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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;

/**
 * A SourceEndpoint.
 */
@Entity
@Table(name = "source_endpoint")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@SequenceGenerator(name = "sourceEndpoint", sequenceName = "source_endpoint_sequence")
public class SourceEndpoint implements Serializable {

    private static final long serialVersionUID = 4297669942924604145L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sourceEndpoint")
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

    @OneToMany(mappedBy = "sourceEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SourceFilter> filters = new HashSet<>();

    @OneToMany(mappedBy = "sourceEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SourceTransformer> transformers = new HashSet<>();

    @OneToMany(mappedBy = "sourceEndpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SourceConfig> configurations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EndpointType getType() {
        return type;
    }

    public SourceEndpoint type(EndpointType type) {
        this.type = type;
        return this;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SourceEndpoint name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InDataType getInDataType() {
        return inDataType;
    }

    public SourceEndpoint inDataType(InDataType inDataType) {
        this.inDataType = inDataType;
        return this;
    }

    public void setInDataType(InDataType inDataType) {
        this.inDataType = inDataType;
    }

    public OutDataType getOutDataType() {
        return outDataType;
    }

    public SourceEndpoint outDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
        return this;
    }

    public void setOutDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
    }

    public Set<SourceFilter> getFilters() {
        return filters;
    }

    public SourceEndpoint filters(Set<SourceFilter> sourceFilters) {
        this.filters = sourceFilters;
        return this;
    }

    public SourceEndpoint addFilter(SourceFilter sourceFilter) {
        this.filters.add(sourceFilter);
        sourceFilter.setSourceEndpoint(this);
        return this;
    }

    public SourceEndpoint removeFilter(SourceFilter sourceFilter) {
        this.filters.remove(sourceFilter);
        sourceFilter.setSourceEndpoint(null);
        return this;
    }

    public void setFilters(Set<SourceFilter> sourceFilters) {
        this.filters = sourceFilters;
    }

    public Set<SourceTransformer> getTransformers() {
        return transformers;
    }

    public SourceEndpoint transformers(Set<SourceTransformer> sourceTransformers) {
        this.transformers = sourceTransformers;
        return this;
    }

    public SourceEndpoint addTransformer(SourceTransformer sourceTransformer) {
        this.transformers.add(sourceTransformer);
        sourceTransformer.setSourceEndpoint(this);
        return this;
    }

    public SourceEndpoint removeTransformer(SourceTransformer sourceTransformer) {
        this.transformers.remove(sourceTransformer);
        sourceTransformer.setSourceEndpoint(null);
        return this;
    }

    public void setTransformers(Set<SourceTransformer> sourceTransformers) {
        this.transformers = sourceTransformers;
    }

    public Set<SourceConfig> getConfigurations() {
        return configurations;
    }

    public SourceEndpoint configurations(Set<SourceConfig> sourceConfigs) {
        this.configurations = sourceConfigs;
        return this;
    }

    public SourceEndpoint addConfiguration(SourceConfig sourceConfig) {
        this.configurations.add(sourceConfig);
        sourceConfig.setSourceEndpoint(this);
        return this;
    }

    public SourceEndpoint removeConfiguration(SourceConfig sourceConfig) {
        this.configurations.remove(sourceConfig);
        sourceConfig.setSourceEndpoint(null);
        return this;
    }

    public void setConfigurations(Set<SourceConfig> sourceConfigs) {
        this.configurations = sourceConfigs;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters
    // and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SourceEndpoint sourceEndpoint = (SourceEndpoint) o;
        if (sourceEndpoint.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceEndpoint.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceEndpoint [id=" + id + ", type=" + type + ", name=" + name + ", inDataType=" + inDataType
                + ", outDataType=" + outDataType + ", filters=" + filters
                + ", transformers=" + transformers + ", configurations=" + configurations + "]";
    }

}
