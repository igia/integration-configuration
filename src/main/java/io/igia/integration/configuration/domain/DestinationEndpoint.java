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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * A DestinationEndpoint.
 */
@Entity
@Audited
@DiscriminatorValue(value = "DESTINATION")
public class DestinationEndpoint  extends Endpoint implements Serializable {

    private static final long serialVersionUID = 4663665307805702319L;
    
    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResponseTransformer> responseTransformers = new HashSet<>();
    
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public Set<ResponseTransformer> getResponseTransformers() {
        return responseTransformers;
    }

    @Override
    public DestinationEndpoint responseTransformers(Set<ResponseTransformer> responseTransformers) {
        this.responseTransformers = responseTransformers;
        return this;
    }

    @Override
    public void setResponseTransformers(Set<ResponseTransformer> responseTransformers) {
        this.responseTransformers = responseTransformers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((responseTransformers == null) ? 0 : responseTransformers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DestinationEndpoint other = (DestinationEndpoint) obj;
        if (responseTransformers == null) {
            if (other.responseTransformers != null)
                return false;
        } else if (!responseTransformers.equals(other.responseTransformers))
            return false;
        return true;
    }
    
}
