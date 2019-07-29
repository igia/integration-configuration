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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;

/**
 * A DestinationEndpoint.
 */
@Entity
@Table(name = "destination_endpoint")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@SequenceGenerator(name = "destinationEndpoint", sequenceName = "destination_endpoint_sequence")
public class DestinationEndpoint implements Serializable {

    private static final long serialVersionUID = 4663665307805702319L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "destinationEndpoint")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "igia_type")
    private EndpointType type;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_data_type")
    private InDataType inDataType;

    @Enumerated(EnumType.STRING)
    @Column(name = "out_data_type")
    private OutDataType outDataType;

    @ManyToOne
    @JsonIgnoreProperties("destinations")
    private DataPipeline dataPipeline;

    @OneToMany(mappedBy = "destinationEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DestinationFilter> filters = new HashSet<>();
    
    @OneToMany(mappedBy = "destinationEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DestinationTransformer> transformers = new HashSet<>();
    
    @OneToMany(mappedBy = "destinationEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DestinationConfig> configurations = new HashSet<>();
    
    @OneToMany(mappedBy = "destinationEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResponseTransformer> responseTransformers = new HashSet<>();
    
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EndpointType getType() {
        return type;
    }

    public DestinationEndpoint type(EndpointType type) {
        this.type = type;
        return this;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DestinationEndpoint name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InDataType getInDataType() {
        return inDataType;
    }

    public DestinationEndpoint inDataType(InDataType inDataType) {
        this.inDataType = inDataType;
        return this;
    }

    public void setInDataType(InDataType inDataType) {
        this.inDataType = inDataType;
    }

    public OutDataType getOutDataType() {
        return outDataType;
    }

    public DestinationEndpoint outDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
        return this;
    }

    public void setOutDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
    }

    public DataPipeline getDataPipeline() {
        return dataPipeline;
    }

    public DestinationEndpoint dataPipeline(DataPipeline dataPipeline) {
        this.dataPipeline = dataPipeline;
        return this;
    }

    public void setDataPipeline(DataPipeline dataPipeline) {
        this.dataPipeline = dataPipeline;
    }

    public Set<DestinationFilter> getFilters() {
        return filters;
    }

    public DestinationEndpoint filters(Set<DestinationFilter> destinationFilters) {
        this.filters = destinationFilters;
        return this;
    }

    public DestinationEndpoint addFilter(DestinationFilter destinationFilter) {
        this.filters.add(destinationFilter);
        destinationFilter.setDestinationEndpoint(this);
        return this;
    }

    public DestinationEndpoint removeFilter(DestinationFilter destinationFilter) {
        this.filters.remove(destinationFilter);
        destinationFilter.setDestinationEndpoint(null);
        return this;
    }

    public void setFilters(Set<DestinationFilter> destinationFilters) {
        this.filters = destinationFilters;
    }

    public Set<DestinationTransformer> getTransformers() {
        return transformers;
    }

    public DestinationEndpoint transformers(Set<DestinationTransformer> destinationTransformers) {
        this.transformers = destinationTransformers;
        return this;
    }

    public DestinationEndpoint addTransformer(DestinationTransformer destinationTransformer) {
        this.transformers.add(destinationTransformer);
        destinationTransformer.setDestinationEndpoint(this);
        return this;
    }

    public DestinationEndpoint removeTransformer(DestinationTransformer destinationTransformer) {
        this.transformers.remove(destinationTransformer);
        destinationTransformer.setDestinationEndpoint(null);
        return this;
    }

    public void setTransformers(Set<DestinationTransformer> destinationTransformers) {
        this.transformers = destinationTransformers;
    }

    public Set<DestinationConfig> getConfigurations() {
        return configurations;
    }

    public DestinationEndpoint configurations(Set<DestinationConfig> destinationConfigs) {
        this.configurations = destinationConfigs;
        return this;
    }

    public DestinationEndpoint addConfiguration(DestinationConfig destinationConfig) {
        this.configurations.add(destinationConfig);
        destinationConfig.setDestinationEndpoint(this);
        return this;
    }

    public DestinationEndpoint removeConfiguration(DestinationConfig destinationConfig) {
        this.configurations.remove(destinationConfig);
        destinationConfig.setDestinationEndpoint(null);
        return this;
    }

    public void setConfigurations(Set<DestinationConfig> destinationConfigs) {
        this.configurations = destinationConfigs;
    }

    public Set<ResponseTransformer> getResponseTransformers() {
        return responseTransformers;
    }

    public DestinationEndpoint responseTransformers(Set<ResponseTransformer> responseTransformers) {
        this.responseTransformers = responseTransformers;
        return this;
    }

    public DestinationEndpoint addResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformers.add(responseTransformer);
        responseTransformer.setDestinationEndpoint(this);
        return this;
    }

    public DestinationEndpoint removeResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformers.remove(responseTransformer);
        responseTransformer.setDestinationEndpoint(null);
        return this;
    }

    public void setResponseTransformers(Set<ResponseTransformer> responseTransformers) {
        this.responseTransformers = responseTransformers;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DestinationEndpoint destinationEndpoint = (DestinationEndpoint) o;
        if (destinationEndpoint.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), destinationEndpoint.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DestinationEndpoint{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", inDataType='" + getInDataType() + "'" +
            ", outDataType='" + getOutDataType() + "'" +
            "}";
    }
}
