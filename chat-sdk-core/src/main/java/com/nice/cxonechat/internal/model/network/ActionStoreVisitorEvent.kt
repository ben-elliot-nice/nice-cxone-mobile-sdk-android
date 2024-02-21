/*
 * Copyright (c) 2021-2023. NICE Ltd. All rights reserved.
 *
 * Licensed under the NICE License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/nice-devone/nice-cxone-mobile-sdk-android/blob/main/LICENSE
 *
 * TO THE EXTENT PERMITTED BY APPLICABLE LAW, THE CXONE MOBILE SDK IS PROVIDED ON
 * AN “AS IS” BASIS. NICE HEREBY DISCLAIMS ALL WARRANTIES AND CONDITIONS, EXPRESS
 * OR IMPLIED, INCLUDING (WITHOUT LIMITATION) WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT, AND TITLE.
 */

package com.nice.cxonechat.internal.model.network

import com.google.gson.annotations.SerializedName
import com.nice.cxonechat.enums.EventAction
import com.nice.cxonechat.enums.EventAction.ChatWindowEvent
import com.nice.cxonechat.enums.EventType.StoreVisitorEvents
import com.nice.cxonechat.enums.VisitorEventType
import com.nice.cxonechat.state.Connection
import java.util.Date
import java.util.UUID

internal data class ActionStoreVisitorEvent(
    @SerializedName("action")
    val action: EventAction = ChatWindowEvent,
    @SerializedName("eventId")
    val eventId: UUID = UUID.randomUUID(),
    @SerializedName("payload")
    val payload: LegacyPayload<Data>,
) {

    constructor(
        connection: Connection,
        visitor: UUID,
        destination: UUID,
        vararg events: VisitorEvent,
    ) : this(
        payload = LegacyPayload(
            eventType = StoreVisitorEvents,
            connection = connection,
            data = Data(events.toList()),
            visitor = visitor,
            destination = destination
        )
    )

    constructor(
        connection: Connection,
        visitor: UUID,
        destination: UUID,
        vararg events: Pair<VisitorEventType, Any?>,
        createdAt: Date = Date(),
    ) : this(
        payload = LegacyPayload(
            eventType = StoreVisitorEvents,
            connection = connection,
            data = events
                .map { (event, data) ->
                    VisitorEvent(
                        type = event,
                        data = data,
                        createdAt = createdAt
                    )
                }
                .toList()
                .let(ActionStoreVisitorEvent::Data),
            visitor = visitor,
            destination = destination
        )
    )

    data class Data constructor(
        @SerializedName("visitorEvents")
        val visitorEvents: List<VisitorEvent>,
    )
}
