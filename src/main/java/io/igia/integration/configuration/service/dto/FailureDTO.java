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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.integration.configuration.web.rest.errors.CustomErrorMessage;

public class FailureDTO {
    
    @JsonIgnoreProperties(value = { "id","createdOn","createdBy","modifiedOn","modifiedBy" ,"state", "reason"})
    private DataPipelineDTO dataPipelineDTO;
    
    private List<CustomErrorMessage> errors;

    public DataPipelineDTO getDataPipelineDTO() {
        return dataPipelineDTO;
    }

    public void setDataPipelineDTO(DataPipelineDTO dataPipelineDTO) {
        this.dataPipelineDTO = dataPipelineDTO;
    }

    public List<CustomErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<CustomErrorMessage> errors) {
        this.errors = errors;
    }
    
    
}
