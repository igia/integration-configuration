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
package io.igia.integration.configuration.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.domain.EndpointMetadata;
import io.igia.integration.configuration.repository.EndpointMetadataRepository;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.mapper.EndpointMetadataMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing EndpointMetadata.
 */
@Service
@Transactional
public class EndpointMetadataServiceImpl implements EndpointMetadataService {

    private final Logger log = LoggerFactory.getLogger(EndpointMetadataServiceImpl.class);

    private final EndpointMetadataRepository endpointMetadataRepository;

    private final EndpointMetadataMapper endpointMetadataMapper;

    public EndpointMetadataServiceImpl(EndpointMetadataRepository endpointMetadataRepository,
            EndpointMetadataMapper endpointMetadataMapper) {
        this.endpointMetadataRepository = endpointMetadataRepository;
        this.endpointMetadataMapper = endpointMetadataMapper;
    }

    /**
     * Save a endpointMetadata.
     *
     * @param endpointMetadataDTO
     *            the entity to save
     * @return the persisted entity
     */
    @Override
    public EndpointMetadataDTO save(EndpointMetadataDTO endpointMetadataDTO) {
        log.info("Request to save EndpointMetadata : {}", endpointMetadataDTO);

        EndpointMetadata endpointMetadata = endpointMetadataMapper.toEntity(endpointMetadataDTO);
        endpointMetadata = endpointMetadataRepository.save(endpointMetadata);
        return endpointMetadataMapper.toDto(endpointMetadata);
    }

    /**
     * Get all the endpointMetadata.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<EndpointMetadataDTO> findAll() {
        return endpointMetadataRepository.findAll().stream().map(endpointMetadataMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one endpointMetadata by id.
     *
     * @param id
     *            the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EndpointMetadataDTO> findOne(Long id) {
        log.info("Request to get EndpointMetadata : {}", id);
        return endpointMetadataRepository.findById(id).map(endpointMetadataMapper::toDto);
    }

    /**
     * Delete the endpointMetadata by id.
     *
     * @param id
     *            the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.info("Request to delete EndpointMetadata : {}", id);
        endpointMetadataRepository.deleteById(id);
    }

    @Override
    public List<EndpointMetadataDTO> findAllByTypeAndCategoryAndIsMandatory(String type, String category,
            boolean isMandatory) {
        log.info("Request to get EndpointMetadata with Type : {} , Category : {} , Mandatory : {}",type,category,isMandatory);
        List<EndpointMetadata> endpointMetadata = endpointMetadataRepository
                .findAllByTypeAndCategoryAndIsMandatory(type, category, isMandatory);
        return endpointMetadata.stream().map(endpointMetadataMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<EndpointMetadataDTO> findAllByTypeAndCategoryAndIsEncrypted(String type, String category,
            boolean isEncrypted) {
        log.info("Request to get EndpointMetadata with Type : {} , Category : {} , Encrypted : {}",type,category,isEncrypted);
        List<EndpointMetadata> endpointMetadata = endpointMetadataRepository
                .findAllByTypeAndCategoryAndIsEncrypted(type, category, isEncrypted);
        return endpointMetadata.stream().map(endpointMetadataMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
