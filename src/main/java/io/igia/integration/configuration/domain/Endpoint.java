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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;

/**
 * A Endpoint.
 */
@Entity
@Table(name = "endpoint")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "category" , discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "endpoint" , sequenceName = "endpoint_sequence")
@Audited

public class Endpoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endpoint")
   
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EndpointType type;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_data_type")
    private InDataType inDataType;

    @Enumerated(EnumType.STRING)
    @Column(name = "out_data_type")
    private OutDataType outDataType;

    @ManyToOne
    @JsonIgnoreProperties("endpoints")
    private DataPipeline dataPipeline;
    
    @OneToMany(mappedBy = "endpoint", cascade =  CascadeType.ALL , fetch = FetchType.EAGER , orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Filter> filters = new HashSet<>();

    @OneToMany(mappedBy = "endpoint", cascade =  CascadeType.ALL , fetch = FetchType.EAGER , orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Transformer> transformers = new HashSet<>();

    @OneToMany(mappedBy = "endpoint", cascade =  CascadeType.ALL , fetch = FetchType.EAGER , orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<EndpointConfig> configurations = new HashSet<>();

    @OneToMany(mappedBy = "endpoint", cascade =  CascadeType.ALL , fetch = FetchType.EAGER , orphanRemoval = true)
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

    public Endpoint type(EndpointType type) {
        this.type = type;
        return this;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Endpoint name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InDataType getInDataType() {
        return inDataType;
    }

    public Endpoint inDataType(InDataType inDataType) {
        this.inDataType = inDataType;
        return this;
    }

    public void setInDataType(InDataType inDataType) {
        this.inDataType = inDataType;
    }

    public OutDataType getOutDataType() {
        return outDataType;
    }

    public Endpoint outDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
        return this;
    }

    public void setOutDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
    }

    public DataPipeline getDataPipeline() {
        return dataPipeline;
    }

    public Endpoint dataPipeline(DataPipeline dataPipeline) {
        this.dataPipeline = dataPipeline;
        return this;
    }

    public void setDataPipeline(DataPipeline dataPipeline) {
        this.dataPipeline = dataPipeline;
    }

    public Set<Filter> getFilters() {
        return filters;
    }

    public Endpoint filters(Set<Filter> filters) {
        this.filters = filters;
        return this;
    }

    public Endpoint addFilter(Filter filter) {
        this.filters.add(filter);
        filter.setEndpoint(this);
        return this;
    }

    public Endpoint removeFilter(Filter filter) {
        this.filters.remove(filter);
        filter.setEndpoint(null);
        return this;
    }

    public void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    public Set<Transformer> getTransformers() {
        return transformers;
    }

    public Endpoint transformers(Set<Transformer> transformers) {
        this.transformers = transformers;
        return this;
    }

    public Endpoint addTransformer(Transformer transformer) {
        this.transformers.add(transformer);
        transformer.setEndpoint(this);
        return this;
    }

    public Endpoint removeTransformer(Transformer transformer) {
        this.transformers.remove(transformer);
        transformer.setEndpoint(null);
        return this;
    }

    public void setTransformers(Set<Transformer> transformers) {
        this.transformers = transformers;
    }

    public Set<EndpointConfig> getConfigurations() {
        return configurations;
    }

    public Endpoint configurations(Set<EndpointConfig> endpointConfigs) {
        this.configurations = endpointConfigs;
        return this;
    }

    public Endpoint addConfiguration(EndpointConfig endpointConfig) {
        this.configurations.add(endpointConfig);
        endpointConfig.setEndpoint(this);
        return this;
    }

    public Endpoint removeConfiguration(EndpointConfig endpointConfig) {
        this.configurations.remove(endpointConfig);
        endpointConfig.setEndpoint(null);
        return this;
    }

    public void setConfigurations(Set<EndpointConfig> endpointConfigs) {
        this.configurations = endpointConfigs;
    }

    public Set<ResponseTransformer> getResponseTransformers() {
        return responseTransformers;
    }

    public Endpoint responseTransformers(Set<ResponseTransformer> responseTransformers) {
        this.responseTransformers = responseTransformers;
        return this;
    }

    public Endpoint addResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformers.add(responseTransformer);
        responseTransformer.setEndpoint(this);
        return this;
    }

    public Endpoint removeResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformers.remove(responseTransformer);
        responseTransformer.setEndpoint(null);
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
        if (!(o instanceof Endpoint)) {
            return false;
        }
        return id != null && id.equals(((Endpoint) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Endpoint{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", inDataType='" + getInDataType() + "'" +
            ", outDataType='" + getOutDataType() + "'" +
            "}";
    }
}
