/*
 * Copyright (c) 2021-2024. NICE Ltd. All rights reserved.
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

package com.nice.cxonechat.event

import com.nice.cxonechat.internal.model.CustomFieldModel
import com.nice.cxonechat.internal.model.network.ActionSetCustomerCustomFields
import com.nice.cxonechat.state.Connection
import com.nice.cxonechat.storage.ValueStorage

internal class SetCustomerCustomFieldEvent(
    private val fields: Map<String, String>,
) : ChatEvent<ActionSetCustomerCustomFields>() {

    override fun getModel(
        connection: Connection,
        storage: ValueStorage,
    ) = ActionSetCustomerCustomFields(
        connection = connection,
        fields = fields.map(::CustomFieldModel)
    )
}
