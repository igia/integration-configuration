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
package io.igia.integration.configuration.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.config.Constants;
import io.igia.integration.configuration.encrypt.EncryptionUtility;
import io.igia.integration.configuration.listener.ActiveMQQueueListener;
import io.igia.integration.configuration.route.dto.Message;

@Component
public class ConfigurationServiceRoute extends RouteBuilder{

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private EncryptionUtility encryptionUtility;

    @Override
    public void configure() {
        from(Constants.IN_MESSAGE_QUEUE_PREFIX + applicationProperties.getMessageInQueue())
        .unmarshal(encryptionUtility.getCryptoDataFormat())
        .unmarshal().json(JsonLibrary.Jackson, Message.class)
        .bean(ActiveMQQueueListener.class, "processMessage");
    }
}
