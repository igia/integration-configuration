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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.integration.configuration.domain.enumeration.FilterType;

/**
 * A SourceFilter.
 */
@Entity
@Table(name = "source_filter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@SequenceGenerator(name = "sourceFiter", sequenceName = "source_filter_sequence" )
public class SourceFilter implements Serializable {

    private static final long serialVersionUID = 2305589102561339379L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sourceFiter")
    private Long id;

    @Column(name = "filter_order")
    private Integer order;

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type")
    private FilterType type;

    @Lob
    private String data;

    private String description;

    @ManyToOne
    @JsonIgnoreProperties("filters")
    private SourceEndpoint sourceEndpoint;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public SourceFilter order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public FilterType getType() {
        return type;
    }

    public SourceFilter type(FilterType type) {
        this.type = type;
        return this;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public SourceFilter data(String data) {
        this.data = data;
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public SourceFilter description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SourceEndpoint getSourceEndpoint() {
        return sourceEndpoint;
    }

    public SourceFilter sourceEndpoint(SourceEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
        return this;
    }

    public void setSourceEndpoint(SourceEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
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
        SourceFilter sourceFilter = (SourceFilter) o;
        if (sourceFilter.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceFilter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceFilter [id=" + id + ", order=" + order + ", type=" + type + ", data=" + data + ", description="
                + description + ", sourceEndpoint=" + sourceEndpoint + "]";
    }

    
}
