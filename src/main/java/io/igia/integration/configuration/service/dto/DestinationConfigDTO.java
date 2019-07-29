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
 * A DTO for the DestinationConfig entity.
 */
public class DestinationConfigDTO implements Serializable {

    private static final long serialVersionUID = 3678299724234408061L;

    private Long id;

    @NotNull
    @NotEmpty
    private String key;

    @NotNull
    @NotEmpty
    private String value;

    @JsonIgnore
    private Long destinationEndpointId;

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
    
    public Long getDestinationEndpointId() {
        return destinationEndpointId;
    }

    public void setDestinationEndpointId(Long destinationEndpointId) {
        this.destinationEndpointId = destinationEndpointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DestinationConfigDTO destinationConfigDTO = (DestinationConfigDTO) o;
        if (destinationConfigDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), destinationConfigDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DestinationConfigDTO{" + "id=" + getId() + ", configKey='" + getKey() + "'" + ", configValue='"
                + getValue() + "'" + ", destinationEndpoint=" + getDestinationEndpointId() + "}";
    }
}
