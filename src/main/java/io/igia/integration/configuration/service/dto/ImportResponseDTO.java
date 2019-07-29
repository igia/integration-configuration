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

public class ImportResponseDTO {
    
    private List<FailureDTO> failure;
    
    private List<SuccessDTO> success;

    public List<SuccessDTO> getSuccess() {
        return success;
    }

    public void setSuccess(List<SuccessDTO> success) {
        this.success = success;
    }

    public List<FailureDTO> getFailure() {
        return failure;
    }

    public void setFailure(List<FailureDTO> failure) {
        this.failure = failure;
    }

      

}
