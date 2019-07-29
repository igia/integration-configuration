/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.integration.configuration.route.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
        @Type(value = ProvisionRequestMessageData.class, name = "provisionRequestData"), 
        @Type(value = ProvisionResponseMessageData.class, name = "provisionResponseData"),
        @Type(value = HealthCheckResponseMessageData.class, name = "healthCheckResponseData")
})
public abstract class MessageData {
    private MessageStatus status;

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
