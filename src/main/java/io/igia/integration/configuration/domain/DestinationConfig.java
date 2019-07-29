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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DestinationConfig.
 */
@Entity
@Table(name = "destination_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@SequenceGenerator(name = "destinationConfig", sequenceName = "destination_config_sequence")
public class DestinationConfig implements Serializable {

    private static final long serialVersionUID = -7969813933072261741L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "destinationConfig")
    private Long id;

    @Column(name = "config_key")
    private String key;

    @Column(name = "config_value")
    private String value;

    @ManyToOne
    @JsonIgnoreProperties("configurations")
    private DestinationEndpoint destinationEndpoint;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DestinationEndpoint getDestinationEndpoint() {
        return destinationEndpoint;
    }

    public DestinationConfig destinationEndpoint(DestinationEndpoint destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
        return this;
    }

    public void setDestinationEndpoint(DestinationEndpoint destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
    }

    public DestinationConfig key(String key) {
        this.key = key;
        return this;
    }

    public DestinationConfig value(String value) {
        this.value = value;
        return this;
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
        DestinationConfig destinationConfig = (DestinationConfig) o;
        if (destinationConfig.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), destinationConfig.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DestinationConfig [id=" + id + ", key=" + key + ", value=" + value 
                + ", destinationEndpoint=" + destinationEndpoint  + "]";
    }

    

}
