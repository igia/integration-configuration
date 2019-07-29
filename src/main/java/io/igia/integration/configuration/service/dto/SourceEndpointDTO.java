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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;

/**
 * A DTO for the SourceEndpoint entity.
 */
public class SourceEndpointDTO implements Serializable {

    private static final long serialVersionUID = -1240821091666614061L;

    private Long id;

    @NotNull
    private EndpointType type;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private InDataType inDataType;

    @NotNull
    private OutDataType outDataType;

    @NotEmpty
    @Valid
    private Set<SourceConfigDTO> configurations = new HashSet<>();

    @Valid
    private Set<SourceFilterDTO> filters = new HashSet<>();

    @Valid
    private Set<SourceTransformerDTO> transformers = new HashSet<>();

    public Set<SourceFilterDTO> getFilters() {
        return filters;
    }

    public void setFilters(Set<SourceFilterDTO> filters) {
        this.filters = filters;
    }

    public Set<SourceTransformerDTO> getTransformers() {
        return transformers;
    }

    public void setTransformers(Set<SourceTransformerDTO> transformers) {
        this.transformers = transformers;
    }

    public Set<SourceConfigDTO> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<SourceConfigDTO> configurations) {
        this.configurations = configurations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InDataType getInDataType() {
        return inDataType;
    }

    public void setInDataType(InDataType inDataType) {
        this.inDataType = inDataType;
    }

    public OutDataType getOutDataType() {
        return outDataType;
    }

    public void setOutDataType(OutDataType outDataType) {
        this.outDataType = outDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SourceEndpointDTO sourceEndpointDTO = (SourceEndpointDTO) o;
        if (sourceEndpointDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceEndpointDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceEndpointDTO{" + "id=" + getId() + ", type='" + getType() + "'" + ", name='" + getName() + "'"
                + ", inDataType='" + getInDataType() + "'" + ", outDataType='" + getOutDataType() + "'}";
    }
}
