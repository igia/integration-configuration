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
 * A Filter.
 */
@Entity
@Table(name = "filter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "filter" , sequenceName = "filter_sequence")
@Audited
public class Filter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "filter")
    private Long id;

    @Column(name = "filter_order")
    private Integer order;

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type")
    private FilterType type;

    @Lob
    @Column(name = "data")
    private String data;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties("filters")
    private Endpoint endpoint;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public Filter order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public FilterType getType() {
        return type;
    }

    public Filter type(FilterType type) {
        this.type = type;
        return this;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public Filter data(String data) {
        this.data = data;
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public Filter description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public Filter endpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Filter)) {
            return false;
        }
        return id != null && id.equals(((Filter) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Filter{" +
            "id=" + getId() +
            ", filterOrder=" + getOrder() +
            ", filterType='" + getType() + "'" +
            ", data='" + getData() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
