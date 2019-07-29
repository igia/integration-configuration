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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A DTO for the SourceConfig entity.
 */
public class SourceConfigDTO implements Serializable {

    private static final long serialVersionUID = -4726601906334369683L;

    private Long id;

    @NotNull
    @NotEmpty
    private String key;

    @NotNull
    @NotEmpty
    private String value;

    @JsonIgnore
    private Long sourceEndpointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String configKey) {
        this.key = configKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String configValue) {
        this.value = configValue;
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

        SourceConfigDTO sourceConfigDTO = (SourceConfigDTO) o;
        if (sourceConfigDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceConfigDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceConfigDTO{" + "id=" + getId() + ", configKey='" + getKey() + "'" + ", configValue='" + getValue()
                + "'" + ", sourceEndpoint=" + getSourceEndpointId() + "}";
    }
}
