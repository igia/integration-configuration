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
package io.igia.integration.configuration.service;

import java.util.Set;

import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;
import io.igia.integration.configuration.service.dto.DataPipelineAuditMessageEventTypeDTO;

/**
 * Service Interface for managing DataPipelineAuditMessageEventType.
 */
public interface DataPipelineAuditMessageEventTypeService {

    /**
     * Save a dataPipelineAuditMessageEventType.
     *
     * @param dataPipelineAuditMessageEventTypeDTO the entity to save
     * @return the persisted entity
     */
    DataPipelineAuditMessageEventTypeDTO save(DataPipelineAuditMessageEventTypeDTO dataPipelineAuditMessageEventTypeDTO);

    /**
     * Delete the "id" dataPipelineAuditMessageEventType.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Save the auditMessages for given DataPipeline Id
     * @param id
     * @param auditMessages
     */
    void save(Long dataPipelineId, Set<AuditMessageEventType> auditMessages);
    
    /**
     * Find AuditMessageEventType for given DataPipeline Id 
     * @param dataPipelineId
     * @return
     */
    Set<AuditMessageEventType> findByDataPipelineId(Long dataPipelineId);

    /**
     * Delete dataPipelineAuditMessageEventType by DataPipelineId
     * @param dataPipelineId
     */
    void deleteByDataPipelineId(Long dataPipelineId);
}
