@file:Suppress("MaxLineLength", "TestFunctionName")

package com.nice.cxonechat.server

import com.nice.cxonechat.AbstractChatTestSubstrate.Companion.TestUUIDValue
import com.nice.cxonechat.enums.VisitorEventType
import com.nice.cxonechat.enums.VisitorEventType.ChatWindowOpened
import com.nice.cxonechat.enums.VisitorEventType.VisitorVisit
import com.nice.cxonechat.internal.model.AttachmentModel
import com.nice.cxonechat.internal.model.CustomFieldModel
import com.nice.cxonechat.internal.model.network.ActionArchiveThread
import com.nice.cxonechat.internal.model.network.ActionAuthorizeCustomer
import com.nice.cxonechat.internal.model.network.ActionCustomerTyping
import com.nice.cxonechat.internal.model.network.ActionExecuteTrigger
import com.nice.cxonechat.internal.model.network.ActionFetchThread
import com.nice.cxonechat.internal.model.network.ActionLoadMoreMessages
import com.nice.cxonechat.internal.model.network.ActionLoadThreadMetadata
import com.nice.cxonechat.internal.model.network.ActionMessage
import com.nice.cxonechat.internal.model.network.ActionMessageSeenByCustomer
import com.nice.cxonechat.internal.model.network.ActionOutboundMessage
import com.nice.cxonechat.internal.model.network.ActionReconnectCustomer
import com.nice.cxonechat.internal.model.network.ActionRecoverThread
import com.nice.cxonechat.internal.model.network.ActionRefreshToken
import com.nice.cxonechat.internal.model.network.ActionSetContactCustomFields
import com.nice.cxonechat.internal.model.network.ActionSetCustomerCustomFields
import com.nice.cxonechat.internal.model.network.ActionStoreVisitor
import com.nice.cxonechat.internal.model.network.ActionStoreVisitorEvent
import com.nice.cxonechat.internal.model.network.ActionUpdateThread
import com.nice.cxonechat.internal.model.network.Conversion
import com.nice.cxonechat.internal.model.network.PageViewData
import com.nice.cxonechat.internal.model.network.ProactiveActionInfo
import com.nice.cxonechat.internal.model.network.VisitorEvent
import com.nice.cxonechat.server.ServerRequestAssertions.verifyArchiveThread
import com.nice.cxonechat.server.ServerRequestAssertions.verifyAuthorizeConsumer
import com.nice.cxonechat.server.ServerRequestAssertions.verifyExecuteTrigger
import com.nice.cxonechat.server.ServerRequestAssertions.verifyFetchThreadList
import com.nice.cxonechat.server.ServerRequestAssertions.verifyLoadMore
import com.nice.cxonechat.server.ServerRequestAssertions.verifyLoadThreadMetadata
import com.nice.cxonechat.server.ServerRequestAssertions.verifyMarkThreadRead
import com.nice.cxonechat.server.ServerRequestAssertions.verifyReconnectConsumer
import com.nice.cxonechat.server.ServerRequestAssertions.verifyRecoverThread
import com.nice.cxonechat.server.ServerRequestAssertions.verifyRefreshToken
import com.nice.cxonechat.server.ServerRequestAssertions.verifySendMessage
import com.nice.cxonechat.server.ServerRequestAssertions.verifySendOutbound
import com.nice.cxonechat.server.ServerRequestAssertions.verifySenderTypingEnded
import com.nice.cxonechat.server.ServerRequestAssertions.verifySenderTypingStarted
import com.nice.cxonechat.server.ServerRequestAssertions.verifySetContactCustomFields
import com.nice.cxonechat.server.ServerRequestAssertions.verifySetCustomerCustomFields
import com.nice.cxonechat.server.ServerRequestAssertions.verifyStoreVisitor
import com.nice.cxonechat.server.ServerRequestAssertions.verifyStoreVisitorEvent
import com.nice.cxonechat.server.ServerRequestAssertions.verifyUpdateThread
import com.nice.cxonechat.state.Connection
import com.nice.cxonechat.storage.ValueStorage
import com.nice.cxonechat.thread.ChatThread
import com.nice.cxonechat.tool.serialize
import java.util.Date
import java.util.UUID

internal object ServerRequest {

    fun LoadMore(connection: Connection, thread: ChatThread): String = ActionLoadMoreMessages(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyLoadMore()

    fun LoadThreadMetadata(connection: Connection, thread: ChatThread): String = ActionLoadThreadMetadata(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyLoadThreadMetadata()

    fun ArchiveThread(connection: Connection, thread: ChatThread): String = ActionArchiveThread(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyArchiveThread()

    fun MarkThreadRead(connection: Connection, thread: ChatThread): String = ActionMessageSeenByCustomer(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyMarkThreadRead()

    fun SenderTypingStarted(connection: Connection, thread: ChatThread): String = ActionCustomerTyping.started(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySenderTypingStarted()

    fun SenderTypingEnded(connection: Connection, thread: ChatThread): String = ActionCustomerTyping.ended(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySenderTypingEnded()

    fun AuthorizeConsumer(connection: Connection, code: String, verifier: String): String = ActionAuthorizeCustomer(
        connection = connection,
        code = code,
        verifier = verifier
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyAuthorizeConsumer()

    fun ReconnectConsumer(connection: Connection, token: String = "token"): String {
        return ActionReconnectCustomer(connection, TestUUIDValue, token).copy(eventId = TestUUIDValue).serialize().verifyReconnectConsumer()
    }

    fun StoreVisitorEvent(connection: Connection, vararg events: VisitorEvent): String {
        return ActionStoreVisitorEvent(connection, TestUUIDValue, TestUUIDValue, events = events).copy(eventId = TestUUIDValue).serialize().verifyStoreVisitorEvent()
    }

    fun StoreVisitor(connection: Connection, token: String?): String {
        return ActionStoreVisitor(connection, TestUUIDValue, token).copy(eventId = TestUUIDValue).serialize().verifyStoreVisitor()
    }

    fun ExecuteTrigger(connection: Connection, id: UUID): String {
        return ActionExecuteTrigger(connection, TestUUIDValue, TestUUIDValue, id).copy(eventId = TestUUIDValue).serialize().verifyExecuteTrigger()
    }

    fun RefreshToken(connection: Connection, token: String = "token"): String {
        return ActionRefreshToken(connection, token).copy(eventId = TestUUIDValue).serialize().verifyRefreshToken()
    }

    @Suppress("LongParameterList")
    fun SendMessage(
        connection: Connection,
        thread: ChatThread,
        storage: ValueStorage,
        message: String,
        fields: Map<String, String> = emptyMap(),
        attachments: List<AttachmentModel> = emptyList(),
        postback: String? = null,
    ): String = ActionMessage(
        connection = connection,
        thread = thread,
        id = TestUUIDValue,
        message = message,
        attachments = attachments,
        fields = fields.map(::CustomFieldModel),
        token = storage.authToken,
        postback = postback,
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySendMessage()

    fun SetCustomerCustomFields(
        connection: Connection,
        fields: Map<String, String> = emptyMap(),
    ): String = ActionSetCustomerCustomFields(
        connection = connection,
        fields = fields.map(::CustomFieldModel)
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySetCustomerCustomFields()

    fun SetContactCustomFields(
        connection: Connection,
        thread: ChatThread,
        fields: Map<String, String> = emptyMap(),
    ): String = ActionSetContactCustomFields(
        connection = connection,
        thread = thread,
        fields = fields.map(::CustomFieldModel)
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySetContactCustomFields()

    fun UpdateThread(connection: Connection, thread: ChatThread): String = ActionUpdateThread(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyUpdateThread()

    fun FetchThreadList(connection: Connection): String = ActionFetchThread(connection = connection)
        .copy(eventId = TestUUIDValue)
        .serialize()
        .verifyFetchThreadList()

    fun RecoverThread(connection: Connection, thread: ChatThread): String = ActionRecoverThread(
        connection = connection,
        thread = thread
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifyRecoverThread()

    fun SendOutbound(
        connection: Connection,
        thread: ChatThread,
        storage: ValueStorage,
        message: String,
    ): String = ActionOutboundMessage(
        connection = connection,
        thread = thread,
        id = TestUUIDValue,
        message = message,
        attachments = emptyList(),
        fields = emptyList(),
        token = storage.authToken
    ).copy(eventId = TestUUIDValue)
        .serialize()
        .verifySendOutbound()

    object StoreVisitorEvents {

        fun ChatWindowOpenEvent(date: Date = Date(0)): VisitorEvent {
            return VisitorEvent(type = ChatWindowOpened, id = TestUUIDValue, createdAt = date)
        }

        fun VisitorVisit(date: Date = Date(0)): VisitorEvent {
            return VisitorEvent(type = VisitorVisit, id = TestUUIDValue, createdAt = date)
        }

        fun Conversion(
            type: String,
            value: Int,
            createdAt: Date = Date(0),
        ): VisitorEvent {
            val model = Conversion(type, value, timestamp = createdAt)
            return VisitorEvent(VisitorEventType.Conversion, id = TestUUIDValue, createdAt, model)
        }

        fun CustomVisitorEvent(data: String, date: Date = Date(0)): VisitorEvent {
            return VisitorEvent(type = VisitorEventType.ProactiveActionDisplayed, createdAt = date, data = data, id = TestUUIDValue)
        }

        fun ProactiveActionDisplayed(
            actionName: String,
            actionType: String,
            date: Date = Date(0),
        ): VisitorEvent {
            val info = ProactiveActionInfo(TestUUIDValue, actionName, actionType)
            return VisitorEvent(type = VisitorEventType.ProactiveActionDisplayed, createdAt = date, data = info, id = TestUUIDValue)
        }

        fun ProactiveActionClicked(
            actionName: String,
            actionType: String,
            date: Date = Date(0),
        ): VisitorEvent {
            val info = ProactiveActionInfo(TestUUIDValue, actionName, actionType)
            return VisitorEvent(type = VisitorEventType.ProactiveActionClicked, createdAt = date, data = info, id = TestUUIDValue)
        }

        fun ProactiveActionSuccess(
            actionName: String,
            actionType: String,
            date: Date = Date(0),
        ): VisitorEvent {
            val info = ProactiveActionInfo(TestUUIDValue, actionName, actionType)
            return VisitorEvent(type = VisitorEventType.ProactiveActionSuccess, createdAt = date, data = info, id = TestUUIDValue)
        }

        fun ProactiveActionFailed(
            actionName: String,
            actionType: String,
            date: Date = Date(0),
        ): VisitorEvent {
            val info = ProactiveActionInfo(TestUUIDValue, actionName, actionType)
            return VisitorEvent(type = VisitorEventType.ProactiveActionFailed, createdAt = date, data = info, id = TestUUIDValue)
        }

        fun PageView(title: String, uri: String, date: Date = Date(0)): VisitorEvent {
            return VisitorEvent(VisitorEventType.PageView, id = TestUUIDValue, createdAt = date, data = PageViewData(title, uri))
        }
    }
}
