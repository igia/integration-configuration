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

import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.igia.integration.configuration.domain.EndpointMetadata}.
 */
public interface EndpointMetadataService {

    /**
     * Save a endpointMetadata.
     *
     * @param endpointMetadataDTO the entity to save.
     * @return the persisted entity.
     */
    EndpointMetadataDTO save(EndpointMetadataDTO endpointMetadataDTO);

    /**
     * Get all the endpointMetadata.
     *
     * @return the list of entities.
     */
    List<EndpointMetadataDTO> findAll();


    /**
     * Get the "id" endpointMetadata.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EndpointMetadataDTO> findOne(Long id);

    /**
     * Delete the "id" endpointMetadata.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);


    List<EndpointMetadataDTO> findAllByTypeAndCategoryAndIsEncrypted(String type, String category, boolean isEncrypted);

    List<EndpointMetadataDTO> findAllByTypeAndCategoryAndIsMandatory(String type, String category, boolean isMandatory);
}
