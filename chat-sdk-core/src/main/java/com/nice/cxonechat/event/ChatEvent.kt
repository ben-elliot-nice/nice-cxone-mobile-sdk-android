package com.nice.cxonechat.event

import com.nice.cxonechat.Public
import com.nice.cxonechat.state.Connection
import com.nice.cxonechat.storage.ValueStorage

/**
 * Definition of all available chat events which can be triggered by the application.
 */
@Public
sealed class ChatEvent {

    internal abstract fun getModel(
        connection: Connection,
        storage: ValueStorage,
    ): Any

    internal class Custom(
        private val factory: (Connection, ValueStorage) -> Any,
    ) : ChatEvent() {
        override fun getModel(connection: Connection, storage: ValueStorage): Any = factory(connection, storage)
    }
}
