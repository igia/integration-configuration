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
import java.util.Objects;

import javax.persistence.Lob;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.igia.integration.configuration.domain.enumeration.TransformerType;

/**
 * A DTO for the SourceTransformer entity.
 */
public class SourceTransformerDTO implements Serializable {

    private static final long serialVersionUID = 3031076087324795478L;

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer order;

    @NotNull
    private TransformerType type;

    @NotNull
    @NotEmpty
    @Lob
    private String data;

    private String description;

    @JsonIgnore
    private Long sourceEndpointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public TransformerType getType() {
        return type;
    }

    public void setType(TransformerType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSourceEndpointId() {
        return sourceEndpointId;
    }

    public void setSourceEndpointId(Long sourceEndpointId) {
        this.sourceEndpointId = sourceEndpointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SourceTransformerDTO sourceTransformerDTO = (SourceTransformerDTO) o;
        if (sourceTransformerDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceTransformerDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceTransformerDTO{" + "id=" + getId() + ", transformerOrder=" + getOrder() + ", transformerType='"
                + getType() + "'" + ", data='" + getData() + "'" + ", description='" + getDescription() + "'"
                + ", sourceEndpoint=" + getSourceEndpointId() + "}";
    }
}
