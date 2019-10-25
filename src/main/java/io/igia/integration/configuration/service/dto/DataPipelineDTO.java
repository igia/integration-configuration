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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Lob;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;
import io.igia.integration.configuration.domain.enumeration.State;

/**
 * A DTO for the DataPipeline entity.
 */
public class DataPipelineDTO implements Serializable {

    private static final long serialVersionUID = -2611204343245164389L;

    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String name;

    private String description;

    private Boolean deploy = false;

    private Long version;

    private State state;

    @NotNull
    @NotEmpty
    private String workerService;

    private Instant createdOn = Instant.now();

    private String createdBy;

    private Instant modifiedOn = Instant.now();

    private String modifiedBy;
    
    @Lob
    private String reason;

    @NotNull
    @Valid
    @JsonIgnoreProperties(value = {"responseTransformers"})
    private EndpointDTO source;

    @NotEmpty
    @Valid
    private Set<EndpointDTO> destinations = new HashSet<>();
    
    private Set<AuditMessageEventType> auditMessages = new HashSet<>();


    public EndpointDTO getSource() {
        return source;
    }

    public void setSource(EndpointDTO source) {
        this.source = source;
    }

    public Set<EndpointDTO> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<EndpointDTO> destinations) {
        this.destinations = destinations;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(Boolean deploy) {
        this.deploy = deploy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getWorkerService() {
        return workerService;
    }

    public void setWorkerService(String workerService) {
        this.workerService = workerService;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Set<AuditMessageEventType> getAuditMessages() {
        return auditMessages;
    }

    public void setAuditMessages(Set<AuditMessageEventType> auditMessages) {
        this.auditMessages = auditMessages;
    }

    @Override
    public String toString() {
        return "DataPipelineDTO{" + "id=" + getId() + ", name='" + getName() + "'" + ", description='"
                + getDescription() + "'" + ", deploy='" + isDeploy() + "'" + ", version='" + getVersion() + "'"
                + ", state='" + getState() + "'" + ", workerService='" + getWorkerService() + "'" + ", createdDate='"
                + getCreatedOn() + "'" + ", createdBy='" + getCreatedBy() + "'" + ", modifiedDate='" + getModifiedOn()
                + "'" + ", modifiedBy='" + getModifiedBy() + "'" + "}";
    }
}
