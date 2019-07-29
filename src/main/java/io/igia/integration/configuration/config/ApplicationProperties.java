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
package io.igia.integration.configuration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Integrationconfiguration.
 * <p>
 * Properties are configured in the application.yml file. See
 * {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String secretKey;
    private String workerServiceNamePrefix;
    private String messageBrokerPrefix;
    private String messageInQueue;
    
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getWorkerServiceNamePrefix() {
        return workerServiceNamePrefix;
    }

    public void setWorkerServiceNamePrefix(String workerServiceNamePrefix) {
        this.workerServiceNamePrefix = workerServiceNamePrefix;
    }

    public String getMessageBrokerPrefix() {
        return messageBrokerPrefix;
    }

    public void setMessageBrokerPrefix(String messageBrokerPrefix) {
        this.messageBrokerPrefix = messageBrokerPrefix;
    }

    public String getMessageInQueue() {
        return messageInQueue;
    }

    public void setMessageInQueue(String messageInQueue) {
        this.messageInQueue = messageInQueue;
    }
}
