package com.nice.cxonechat.internal

import com.nice.cxonechat.Cancellable
import com.nice.cxonechat.ChatThreadHandler
import com.nice.cxonechat.ChatThreadHandler.OnThreadUpdatedListener
import com.nice.cxonechat.enums.EventType.SenderTypingEnded
import com.nice.cxonechat.enums.EventType.SenderTypingStarted
import com.nice.cxonechat.internal.copy.AgentCopyable.Companion.asCopyable
import com.nice.cxonechat.internal.copy.ChatThreadCopyable.Companion.asCopyable
import com.nice.cxonechat.internal.model.network.EventAgentTyping
import com.nice.cxonechat.internal.socket.EventCallback.Companion.addCallback
import com.nice.cxonechat.thread.Agent
import com.nice.cxonechat.thread.ChatThread

internal class ChatThreadHandlerAgentTyping(
    private val origin: ChatThreadHandler,
    private val chat: ChatWithParameters,
) : ChatThreadHandler by origin {

    @Suppress("KotlinConstantConditions")
    override fun get(listener: OnThreadUpdatedListener): Cancellable {
        var isTyping = false
        val cancellableStarted = chat.socketListener.addCallback<EventAgentTyping>(SenderTypingStarted) { event ->
            if (event.inThread(get())) {
                isTyping = true
                listener.onUpdated(
                    updateThread(
                        typing = isTyping,
                        agent = event.agent.copyWithTypingSet(isTyping = isTyping)
                    )
                )
            }
        }
        val cancellableEnded = chat.socketListener.addCallback<EventAgentTyping>(SenderTypingEnded) { event ->
            if (event.inThread(get())) {
                isTyping = false
                listener.onUpdated(
                    updateThread(
                        typing = isTyping,
                        agent = event.agent.copyWithTypingSet(isTyping = isTyping)
                    )
                )
            }
        }
        val cancellableOrigin = origin.get { chatThread ->
            listener.onUpdated(updateThread(isTyping, chatThread))
        }
        return Cancellable(
            cancellableStarted,
            cancellableEnded,
            cancellableOrigin
        )
    }

    private fun updateThread(
        typing: Boolean,
        thread: ChatThread = get(),
        agent: Agent? = null,
    ): ChatThread {
        val threadAgent = agent ?: thread.threadAgent.copyWithTypingSet(isTyping = typing)
        return thread.asCopyable().copy(threadAgent = threadAgent)
    }

    private fun Agent?.copyWithTypingSet(isTyping: Boolean): Agent? {
        if (this == null) return null
        return this.asCopyable().copy(isTyping = isTyping)
    }
}
