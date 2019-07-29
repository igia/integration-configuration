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
package io.igia.integration.configuration.service.dto;

import java.io.Serializable;

import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;

/**
 * A DTO for the DataPipelineAuditMessageEventType entity.
 */
public class DataPipelineAuditMessageEventTypeDTO implements Serializable {

    private static final long serialVersionUID = -3961000405671482015L;

    private Long id;

    private Long dataPipelineId;

    private AuditMessageEventType eventType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataPipelineId() {
        return dataPipelineId;
    }

    public void setDataPipelineId(Long dataPipelineId) {
        this.dataPipelineId = dataPipelineId;
    }

    public AuditMessageEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditMessageEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "DataPipelineAuditMessageEventTypeDTO{" +
            "id=" + getId() +
            ", dataPipelineId=" + getDataPipelineId() +
            ", eventType='" + getEventType() + "'" + "}";
    }
}
