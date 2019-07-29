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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.domain.DataPipelineAuditMessageEventType;
import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;
import io.igia.integration.configuration.repository.DataPipelineAuditMessageEventTypeRepository;
import io.igia.integration.configuration.service.DataPipelineAuditMessageEventTypeService;
import io.igia.integration.configuration.service.dto.DataPipelineAuditMessageEventTypeDTO;
import io.igia.integration.configuration.service.mapper.DataPipelineAuditMessageEventTypeMapper;

/**
 * Service Implementation for managing DataPipelineAuditMessageEventType.
 */
@Service
@Transactional
public class DataPipelineAuditMessageEventTypeServiceImpl implements DataPipelineAuditMessageEventTypeService {

    private final Logger log = LoggerFactory.getLogger(DataPipelineAuditMessageEventTypeServiceImpl.class);

    private final DataPipelineAuditMessageEventTypeRepository dataPipelineAuditMessageEventTypeRepository;

    private final DataPipelineAuditMessageEventTypeMapper dataPipelineAuditMessageEventTypeMapper;

    public DataPipelineAuditMessageEventTypeServiceImpl(DataPipelineAuditMessageEventTypeRepository dataPipelineAuditMessageEventTypeRepository, DataPipelineAuditMessageEventTypeMapper dataPipelineAuditMessageEventTypeMapper) {
        this.dataPipelineAuditMessageEventTypeRepository = dataPipelineAuditMessageEventTypeRepository;
        this.dataPipelineAuditMessageEventTypeMapper = dataPipelineAuditMessageEventTypeMapper;
    }

    /**
     * Save a dataPipelineAuditMessageEventType.
     *
     * @param dataPipelineAuditMessageEventTypeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public DataPipelineAuditMessageEventTypeDTO save(DataPipelineAuditMessageEventTypeDTO dataPipelineAuditMessageEventTypeDTO) {
        log.info("Request to save DataPipelineAuditMessageEventType : {}", dataPipelineAuditMessageEventTypeDTO);

        DataPipelineAuditMessageEventType dataPipelineAuditMessageEventType = dataPipelineAuditMessageEventTypeMapper.toEntity(dataPipelineAuditMessageEventTypeDTO);
        dataPipelineAuditMessageEventType = dataPipelineAuditMessageEventTypeRepository.save(dataPipelineAuditMessageEventType);
        return dataPipelineAuditMessageEventTypeMapper.toDto(dataPipelineAuditMessageEventType);
    }

    /**
     * Delete the dataPipelineAuditMessageEventType by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.info("Request to delete DataPipelineAuditMessageEventType : {}", id);
        dataPipelineAuditMessageEventTypeRepository.deleteById(id);
    }
    
    @Override
    public void save(Long dataPipelineId, Set<AuditMessageEventType> auditMessages){
        log.info("Delete old data before saving for Datapipeline id : {}" ,dataPipelineId);
        deleteByDataPipelineId(dataPipelineId);

        for(AuditMessageEventType type : auditMessages){
            DataPipelineAuditMessageEventTypeDTO dto = new DataPipelineAuditMessageEventTypeDTO();
            dto.setDataPipelineId(dataPipelineId);
            dto.setEventType(type);
            save(dto);
        }
        log.info("Successfully saved AuditMessageEventTypes for Datapipeline id : {}" ,dataPipelineId);
    }

    @Override
    public Set<AuditMessageEventType> findByDataPipelineId(Long dataPipelineId) {
        log.info("Request to get All  AuditMessageEventTypes for Datapipeline id : {}", dataPipelineId);
        Set<AuditMessageEventType> auditMessages = new HashSet<>();
        List<DataPipelineAuditMessageEventTypeDTO> auditMessagesEventTypes =  dataPipelineAuditMessageEventTypeRepository.findAllByDataPipelineId(dataPipelineId).stream()
                .map(dataPipelineAuditMessageEventTypeMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        for(DataPipelineAuditMessageEventTypeDTO dto : auditMessagesEventTypes){
            auditMessages.add(dto.getEventType());
        }
        return auditMessages;
    }
    
    @Override
    public void deleteByDataPipelineId(Long dataPipelineId){
        log.info("Request to get delete  AuditMessageEventTypes for Datapipeline id : {}", dataPipelineId);
        List<DataPipelineAuditMessageEventType> auditMessagesEventTypes = dataPipelineAuditMessageEventTypeRepository.findAllByDataPipelineId(dataPipelineId);
        for(DataPipelineAuditMessageEventType eventType : auditMessagesEventTypes){
            delete(eventType.getId());
        }
    }
}
