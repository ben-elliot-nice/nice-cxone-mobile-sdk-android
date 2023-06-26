package com.nice.cxonechat.internal.model.network

import com.google.gson.annotations.SerializedName
import com.nice.cxonechat.enums.EventAction
import com.nice.cxonechat.enums.EventType.AuthorizeCustomer
import com.nice.cxonechat.state.Connection
import java.util.UUID

internal data class ActionAuthorizeCustomer(
    @SerializedName("action")
    val action: EventAction = EventAction.Register,
    @SerializedName("eventId")
    val eventId: UUID = UUID.randomUUID(),
    @SerializedName("payload")
    val payload: Payload<Data>,
) {

    constructor(
        connection: Connection,
        code: String,
        verifier: String,
    ) : this(
        payload = Payload(
            eventType = AuthorizeCustomer,
            connection = connection,
            data = Data(code = code, verifier = verifier)
        )
    )

    data class Data(
        @SerializedName("authorization")
        val authorization: OAuth,
    ) {

        constructor(
            code: String,
            verifier: String,
        ) : this(
            OAuth(
                code = code.ifBlank { null },
                verifier = verifier.ifBlank { null }
            )
        )
    }

    data class OAuth(
        @SerializedName("authorizationCode")
        val code: String?,
        @SerializedName("codeVerifier")
        val verifier: String?,
    )
}
