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
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import io.igia.integration.configuration.domain.enumeration.State;

/**
 * A DataPipeline.
 */
@Entity
@Table(name = "data_pipeline")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@SequenceGenerator(name = "dataPipeline", sequenceName = "datapipeline_sequence")
public class DataPipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataPipeline")
    private Long id;

    private String name;

    private String description;

    private Boolean deploy;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "worker_service")
    private String workerService;

    @Column(name = "created_date")
    private Instant createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private Instant modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval= true,optional = false)
    @JoinColumn(unique = true)
    private SourceEndpoint source;
    
    @OneToMany(mappedBy = "dataPipeline", cascade = CascadeType.ALL, orphanRemoval= true, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DestinationEndpoint> destinations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove
    @Column(name = "reason")
    @Lob
    private String reason;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public DataPipeline name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public DataPipeline description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDeploy() {
        return deploy;
    }

    public DataPipeline deploy(Boolean deploy) {
        this.deploy = deploy;
        return this;
    }

    public void setDeploy(Boolean deploy) {
        this.deploy = deploy;
    }

    public Long getVersion() {
        return version;
    }

    public DataPipeline version(Long version) {
        this.version = version;
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public State getState() {
        return state;
    }

    public DataPipeline state(State state) {
        this.state = state;
        return this;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getWorkerService() {
        return workerService;
    }

    public DataPipeline workerService(String workerService) {
        this.workerService = workerService;
        return this;
    }

    public void setWorkerService(String workerService) {
        this.workerService = workerService;
    }

    public DataPipeline createdOn(Instant createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public DataPipeline createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public DataPipeline modifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public DataPipeline modifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public SourceEndpoint getSource() {
        return source;
    }

    public DataPipeline source(SourceEndpoint sourceEndpoint) {
        this.source = sourceEndpoint;
        return this;
    }

    public void setSource(SourceEndpoint sourceEndpoint) {
        this.source = sourceEndpoint;
    }

    public Set<DestinationEndpoint> getDestinations() {
        return destinations;
    }

    public DataPipeline destinations(Set<DestinationEndpoint> destinationEndpoints) {
        this.destinations = destinationEndpoints;
        return this;
    }

    public DataPipeline addDestination(DestinationEndpoint destinationEndpoint) {
        this.destinations.add(destinationEndpoint);
        destinationEndpoint.setDataPipeline(this);
        return this;
    }

    public DataPipeline removeDestination(DestinationEndpoint destinationEndpoint) {
        this.destinations.remove(destinationEndpoint);
        destinationEndpoint.setDataPipeline(null);
        return this;
    }

    public void setDestinations(Set<DestinationEndpoint> destinationEndpoints) {
        this.destinations = destinationEndpoints;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters
    // and setters here, do not remove

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataPipeline dataPipeline = (DataPipeline) o;
        if (dataPipeline.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dataPipeline.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Override
    public String toString() {
        return "DataPipeline [id=" + id + ", name=" + name + ", description=" + description + ", deploy=" + deploy
                + ", version=" + version + ", state=" + state + ", workerService=" + workerService + ", createdOn="
                + createdOn + ", createdBy=" + createdBy + ", modifiedOn=" + modifiedOn + ", modifiedBy=" + modifiedBy
                + ", source=" + source + ", destinations=" + destinations + ", reason=" + reason + "]";
    }
    
    
}
