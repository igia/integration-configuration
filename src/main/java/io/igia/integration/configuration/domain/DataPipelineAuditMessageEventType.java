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
package io.igia.integration.configuration.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;

/**
 * A DataPipelineAuditMessageEventType.
 */
@Entity
@Table(name = "data_pipeline_audit_message_event_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class DataPipelineAuditMessageEventType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator( name = "sequenceGenerator", sequenceName = "event_sequence_generator")
    private Long id;

    @Column(name = "data_pipeline_id")
    private Long dataPipelineId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private AuditMessageEventType eventType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataPipelineId() {
        return dataPipelineId;
    }

    public DataPipelineAuditMessageEventType dataPipelineId(Long dataPipelineId) {
        this.dataPipelineId = dataPipelineId;
        return this;
    }

    public void setDataPipelineId(Long dataPipelineId) {
        this.dataPipelineId = dataPipelineId;
    }

    public AuditMessageEventType getEventType() {
        return eventType;
    }

    public DataPipelineAuditMessageEventType eventType(AuditMessageEventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public void setEventType(AuditMessageEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "DataPipelineAuditMessageEventType{" +
            "id=" + getId() +
            ", dataPipelineId=" + getDataPipelineId() +
            ", eventType='" + getEventType() + "'" + "}";
    }
}
