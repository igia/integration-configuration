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

import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A DTO for the EndpointMetadata entity.
 */
@JsonInclude(Include.NON_EMPTY)
public class EndpointMetadataDTO implements Serializable {

    private static final long serialVersionUID = -5583068461070914192L;

    private Long id;

    private String type;

    private String category;

    private String property;

    private Boolean isEncrypted;

    private Boolean isMandatory;

    private Instant createdDate = Instant.now();

    private String createdBy;

    private Instant modifiedDate = Instant.now();

    private String modifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Boolean isIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public Boolean isIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EndpointMetadataDTO endpointMetadataDTO = (EndpointMetadataDTO) o;
        if (endpointMetadataDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), endpointMetadataDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EndpointMetadataDTO{" + "id=" + getId() + ", type='" + getType() + "'" + ", category='" + getCategory()
                + "'" + ", property='" + getProperty() + "'" + ", isEncrypted='" + isIsEncrypted() + "'"
                + ", isMandatory='" + isIsMandatory() + "'" + ", createdDate='" + getCreatedDate() + "'"
                + ", createdBy='" + getCreatedBy() + "'" + ", modifiedDate='" + getModifiedDate() + "'"
                + ", modifiedBy='" + getModifiedBy() + "'" + "}";
    }
}
