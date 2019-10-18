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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.domain.DataPipeline;
import io.igia.integration.configuration.domain.enumeration.AuditMessageEventType;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.encrypt.DataEncrypt;
import io.igia.integration.configuration.encrypt.EncryptionUtility;
import io.igia.integration.configuration.helper.DataPipelineHelper;
import io.igia.integration.configuration.repository.DataPipelineRepository;
import io.igia.integration.configuration.security.SecurityUtils;
import io.igia.integration.configuration.service.DataPipelineAuditMessageEventTypeService;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.mapper.DataPipelineMapper;
import io.igia.integration.configuration.validator.DataPipelineValidator;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

/**
 * Service Implementation for managing DataPipeline.
 */
@Service
@Transactional
public class DataPipelineServiceImpl implements DataPipelineService {

    private final Logger log = LoggerFactory.getLogger(DataPipelineServiceImpl.class);

    @Autowired
    private DataPipelineRepository dataPipelineRepository;
    
    @Autowired
    private DataPipelineMapper dataPipelineMapper;

    @Autowired
    private EndpointMetadataService endpointMetadataService;

    @Autowired
    private EncryptionUtility encryptionUtility;
    
    @Autowired
    private DataPipelineHelper dataPipelineHelper;
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private DataPipelineAuditMessageEventTypeService auditMessageEventTypeService;
    /**
     * Save a dataPipeline.
     *
     * @param dataPipelineDTO
     *            the entity to save
     * @return the persisted entity
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @Override
    public DataPipelineDTO save(DataPipelineDTO dataPipelineDTO) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        log.info("Request to save DataPipeline : {}", dataPipelineDTO.getName());
        
        Set<AuditMessageEventType> auditMessages = dataPipelineDTO.getAuditMessages();
        
        dataPipelineHelper.removeIds(dataPipelineDTO);

        DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService, dataPipelineRepository,discoveryClient,applicationProperties);

        Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();

        String userName = currentUser.isPresent() ? currentUser.get() : MessageConstants.DEFAULT_USER;

        DataEncrypt.encrypt(dataPipelineDTO, endpointMetadataService, encryptionUtility);

        DataPipeline dataPipeline = dataPipelineMapper.toEntity(dataPipelineDTO);
        
        DataPipeline newDataPipeline = dataPipelineHelper.configDataPipeline(userName, dataPipeline);
        
        newDataPipeline = dataPipelineRepository.save(newDataPipeline);
        
        auditMessageEventTypeService.save(newDataPipeline.getId(), auditMessages);
        
        DataPipelineDTO decryptedDataPipelineDTO = dataPipelineMapper.toDto(newDataPipeline);
        
        decryptedDataPipelineDTO.setAuditMessages(auditMessages);
        
        DataEncrypt.decrypt(decryptedDataPipelineDTO, endpointMetadataService, encryptionUtility);
        
        return decryptedDataPipelineDTO;

    }

    /**
     * Get all the dataPipelines.
     *
     * @param pageable
     *            the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataPipelineDTO> findAll() throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        List<DataPipelineDTO> dataPipelineDTOs = dataPipelineMapper.toDto(dataPipelineRepository.findAll());
        for (DataPipelineDTO dataPipeline : dataPipelineDTOs) {
            DataEncrypt.decrypt(dataPipeline, endpointMetadataService, encryptionUtility);
            dataPipeline.setAuditMessages(auditMessageEventTypeService.findByDataPipelineId(dataPipeline.getId()));
        }
        return dataPipelineDTOs;
    }

    @Override
    public List<DataPipelineDTO> findAllByWorkerService(String workerService) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        log.info("Request to get all DataPipelines for workerservice: {} ", workerService);
        List<DataPipelineDTO> dataPipelines = dataPipelineMapper.toDto(dataPipelineRepository.findAllByWorkerServiceIgnoreCase(workerService));
        for (DataPipelineDTO dataPipeline : dataPipelines) {
            DataEncrypt.decrypt(dataPipeline, endpointMetadataService, encryptionUtility);
            dataPipeline.setAuditMessages(auditMessageEventTypeService.findByDataPipelineId(dataPipeline.getId()));
        }
        return dataPipelines;
    }

    @Override
    public List<DataPipelineDTO> findAllByStateAndWorkerServiceIgnoreCase(State state, String workerService) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        log.info("Request to get all DataPipelines for workerservice: {} ", workerService);
        List<DataPipelineDTO> dataPipelines = dataPipelineMapper.toDto(dataPipelineRepository.findAllByStateAndWorkerServiceIgnoreCase(State.STARTED, workerService));
        for (DataPipelineDTO dataPipeline : dataPipelines) {
            DataEncrypt.decrypt(dataPipeline, endpointMetadataService, encryptionUtility);
            dataPipeline.setAuditMessages(auditMessageEventTypeService.findByDataPipelineId(dataPipeline.getId()));
        }
        return dataPipelines;
    }
    
    @Override
    public DataPipelineDTO update(DataPipelineDTO dataPipelineDTO) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        log.info("Request to update DataPipeline : {}", dataPipelineDTO.getName());
        
        Set<AuditMessageEventType> auditMessages = dataPipelineDTO.getAuditMessages();
        
        dataPipelineHelper.removeIds(dataPipelineDTO);
        
        Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();

        String userName = currentUser.isPresent() ? currentUser.get() : MessageConstants.DEFAULT_USER;
        
        Optional<DataPipeline> optionalOldDataPipeline = dataPipelineRepository.findById(dataPipelineDTO.getId());
        
        if(optionalOldDataPipeline.isPresent()){
            DataPipeline oldDataPipeline = optionalOldDataPipeline.get();
            
            DataPipelineValidator.validate(dataPipelineMapper.toDto(oldDataPipeline), dataPipelineDTO, endpointMetadataService, dataPipelineRepository,discoveryClient,applicationProperties);

            DataEncrypt.encrypt(dataPipelineDTO, endpointMetadataService, encryptionUtility);
            dataPipelineDTO.setVersion(oldDataPipeline.getVersion());
            
            DataPipeline newDataPipeline = dataPipelineMapper.toEntity(dataPipelineDTO);
            
            oldDataPipeline = dataPipelineHelper.createNewDataPipelineWithUpdatedValues(newDataPipeline, oldDataPipeline, userName);
            
            dataPipelineRepository.saveAndFlush(oldDataPipeline);
            
            auditMessageEventTypeService.save(oldDataPipeline.getId(), auditMessages);
            
            DataPipelineDTO decryptedDataPipelineDTO = dataPipelineMapper.toDto(oldDataPipeline);
            
            decryptedDataPipelineDTO.setAuditMessages(auditMessages);
            
            DataEncrypt.decrypt(decryptedDataPipelineDTO, endpointMetadataService, encryptionUtility);
            
            return decryptedDataPipelineDTO;
        }else{
            log.error("DataPipeline not found {} ",dataPipelineDTO.getName());
            throw new CustomParameterizedException(ErrorConstants.DATA_NOT_FOUND, ErrorConstants.DATA_NOT_FOUND); 
        }
    }

    @Override
    public Optional<DataPipelineDTO> findOne(Long id) {
        log.info("Request to get DataPipeline : {}", id);
        
        Optional<DataPipelineDTO> dataPipelineDTO = dataPipelineRepository.findById(id).map(dataPipelineMapper::toDto);
        try {
            if(dataPipelineDTO.isPresent()){
                DataPipelineDTO decryptedDataPipelineDTO = DataEncrypt.decrypt(dataPipelineDTO.get(),endpointMetadataService, encryptionUtility);
                decryptedDataPipelineDTO.setAuditMessages(auditMessageEventTypeService.findByDataPipelineId(decryptedDataPipelineDTO.getId()));
                return Optional.of(decryptedDataPipelineDTO);
            }else{
                log.error("DataPipeline not found {} ",id);
                throw new CustomParameterizedException(ErrorConstants.DATA_NOT_FOUND, ErrorConstants.DATA_NOT_FOUND); 
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            log.error("Encryption error {} ",e);
            throw new CustomParameterizedException(ErrorConstants.ENCRYPTION_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Request to delete DataPipeline : {}", id);
        dataPipelineRepository.deleteById(id);
        auditMessageEventTypeService.deleteByDataPipelineId(id);
    }
    
    
    /**
     * Get all the dataPipelines.
     *
     * @param pageable
     *            the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DataPipelineDTO> findAll(Pageable pageable) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        Page<DataPipelineDTO> dataPipelineDTOs = dataPipelineRepository.findAll(pageable).map(dataPipelineMapper::toDto);
        List<DataPipelineDTO> dList = new ArrayList<>();
        for (DataPipelineDTO dataPipeline : dataPipelineDTOs) {
            dataPipeline.setAuditMessages(auditMessageEventTypeService.findByDataPipelineId(dataPipeline.getId()));
            dList.add(DataEncrypt.decrypt(dataPipeline, endpointMetadataService, encryptionUtility));
        }
        return new PageImpl<>(dList, pageable, dataPipelineDTOs.getTotalElements());
    }
    
    @Override
    @Transactional
    public void updateState(DataPipelineDTO newDataPipelineDTO, State state, Boolean deploy, String reason) {
        log.info("Update DataPipeline: {} with  state : {},Deploy: {} and Reason: {}",newDataPipelineDTO.getName(),state,deploy,reason);
        Optional<DataPipeline> optionalDataPipeline = dataPipelineRepository.findById(newDataPipelineDTO.getId());
        if(optionalDataPipeline.isPresent()){
            DataPipeline oldDataPipeline = optionalDataPipeline.get();
            oldDataPipeline.setState(state);
            oldDataPipeline.setDeploy(deploy);
            oldDataPipeline.setReason(reason);
            oldDataPipeline.setModifiedOn(Instant.now());
            
            dataPipelineRepository.saveAndFlush(oldDataPipeline);
        }else{
            log.error("Data Pipeline not found {}",newDataPipelineDTO.getName());
        }
    }
    
}
