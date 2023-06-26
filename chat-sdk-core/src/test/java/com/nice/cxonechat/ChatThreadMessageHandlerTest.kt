@file:Suppress("FunctionMaxLength")

package com.nice.cxonechat

import android.util.Base64
import com.nice.cxonechat.ChatThreadMessageHandler.OnMessageTransferListener
import com.nice.cxonechat.api.model.AttachmentUploadResponse
import com.nice.cxonechat.internal.model.AttachmentModel
import com.nice.cxonechat.internal.model.AttachmentUploadModel
import com.nice.cxonechat.message.ContentDescriptor
import com.nice.cxonechat.message.OutboundMessage
import com.nice.cxonechat.model.makeChatThread
import com.nice.cxonechat.model.makeMessage
import com.nice.cxonechat.server.ServerRequest
import com.nice.cxonechat.server.ServerResponse
import com.nice.cxonechat.thread.ChatThread
import com.nice.cxonechat.tool.nextString
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Response
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

internal class ChatThreadMessageHandlerTest : AbstractChatTest() {

    private lateinit var messages: ChatThreadMessageHandler
    private lateinit var thread: ChatThread

    override fun prepare() {
        super.prepare()
        thread = makeChatThread(messages = listOf(makeMessage()), id = TestUUIDValue)
        messages = chat.threads().thread(thread).messages()
    }

    // ---

    @Test
    fun loadMore_sendsExpectedMessage() {
        assertSendText(ServerRequest.LoadMore(connection, thread)) {
            messages.loadMore()
        }
    }

    @Test(expected = AssertionError::class)
    fun loadMore_ignoresLoad() {
        messages = chat.threads().thread(makeChatThread()).messages()
        assertSendText("this is wrong") {
            messages.loadMore()
        }
    }

    @Test
    fun send_text_sendsExpectedMessage() {
        val expected = "hello!"
        assertSendText(ServerRequest.SendMessage(connection, thread, storage, expected)) {
            messages.send(OutboundMessage(expected))
        }
    }

    @Test
    fun send_text_customCustomerFieldsWhenDefined() {
        val fields = mapOf("my-field!" to "my-value?")
        this serverResponds ServerResponse.WelcomeMessage("", fields)
        val expected = "Welcome defined fields!!!"
        assertSendText(
            expected = ServerRequest.SendMessage(connection, thread, storage, expected, fields = fields),
            replaceDate = true,
        ) {
            messages.send(OutboundMessage(expected))
        }
    }

    @Test
    fun send_text_customCustomerFields_merged() {
        val fields1 = mapOf("my-field1" to "my-new-value1")
        val fields2 = mapOf("my-field2" to "my-new-value2")
        this serverResponds ServerResponse.WelcomeMessage("", fields1)
        this serverResponds ServerResponse.WelcomeMessage("", fields2)
        val expected = "Welcome merged fields!!!"
        assertSendText(
            expected = ServerRequest.SendMessage(connection, thread, storage, expected, fields2 + fields1),
            replaceDate = true,
        ) {
            messages.send(OutboundMessage(expected))
        }
    }

    @Test
    fun send_text_customCustomerFields_distinct() {
        val fields1 = mapOf("my-field" to "my-value1")
        val fields2 = mapOf("my-field" to "my-value2")
        this serverResponds ServerResponse.WelcomeMessage("", fields1)
        this serverResponds ServerResponse.WelcomeMessage("", fields2)
        val expected = "Welcome distinct fields!!!"
        assertSendText(
            expected = ServerRequest.SendMessage(connection, thread, storage, expected, fields = fields2),
            replaceDate = true,
        ) {
            messages.send(OutboundMessage(expected))
        }
    }

    @Test
    fun send_text_customCustomerFieldsWhenDefined_once() {
        val fields = mapOf("my-new-field" to "my-value?")
        this serverResponds ServerResponse.WelcomeMessage("", fields)
        val expected = "I seek your presence…"
        assertSendText(
            expected = ServerRequest.SendMessage(connection, thread, storage, expected, fields = fields),
            replaceDate = true,
        ) {
            messages.send(OutboundMessage(expected))
        }
        assertSendText(ServerRequest.SendMessage(connection, thread, storage, expected)) {
            messages.send(OutboundMessage(expected))
        }
    }

    @Test
    fun send_text_with_postback_sendExpectedMessage() {
        val expected = nextString()
        val postback = nextString()
        assertSendText(
            ServerRequest.SendMessage(
                connection = connection,
                thread = thread,
                storage = storage,
                message = expected,
                postback = postback
            )
        ) {
            messages.send(OutboundMessage(expected, postback))
        }
    }

    @Test
    fun send_attachments_sendsExpectedMessage() {
        val expected = "content"
        val postback = nextString()
        val bytes = expected.toByteArray()

        // since android.* classes aren't implemented for unit tests, mock out Base64 conversion
        // to just return a fixed string
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } returns expected

        val attachments = listOf(AttachmentModel("url", "friendlyName", "application/wtf"))
        assertSendText(
            ServerRequest.SendMessage(
                connection = connection,
                thread = thread,
                storage = storage,
                message = expected,
                attachments = attachments,
                postback = postback
            )
        ) {
            val upload = ContentDescriptor(bytes, "application/wtf", "fileName", "friendlyName")
            val call: Call<AttachmentUploadResponse?> = mock()
            whenever(call.execute()).thenReturn(Response.success(AttachmentUploadResponse("url")))
            whenever(service.uploadFile(eq(AttachmentUploadModel(upload)), any(), any())).then {
                assertEquals(connection.brandId.toString(), it.getArgument(1))
                assertEquals(connection.channelId, it.getArgument(2))
                call
            }
            messages.send(OutboundMessage(listOf(upload), expected, postback))
        }

        verify {
            Base64.encodeToString(eq(bytes), eq(0))
        }
    }

    @Test
    fun send_text_respondsWithCallback() {
        val result = testCallback<UUID> { trigger ->
            testSendTextFeedback()
            messages.send(OutboundMessage("message1"), OnMessageTransferListener(onSent = trigger))
        }
        assertNotNull(result)
    }

    @Test
    fun send_text_respondsProcessed_withoutServerInterference() {
        val result = testCallback<UUID> { trigger ->
            messages.send(OutboundMessage("message2"), OnMessageTransferListener(onProcessed = trigger))
        }
        assertNotNull(result)
    }

    @Test
    fun send_text_callbacksRespond_withIdenticalId() {
        var processedId: UUID? = null
        val result = testCallback<UUID> { trigger ->
            testSendTextFeedback()
            val listener = OnMessageTransferListener(
                onProcessed = { processedId = it },
                onSent = trigger
            )
            messages.send(message = OutboundMessage("message3"), listener = listener)
        }
        assertSame(processedId, result)
    }
}
