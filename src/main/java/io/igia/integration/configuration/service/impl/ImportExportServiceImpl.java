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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.helper.DataPipelineHelper;
import io.igia.integration.configuration.repository.DataPipelineRepository;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.ImportExportService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.FailureDTO;
import io.igia.integration.configuration.service.dto.ImportResponseDTO;
import io.igia.integration.configuration.service.dto.SuccessDTO;
import io.igia.integration.configuration.validator.DataPipelineValidator;
import io.igia.integration.configuration.web.rest.errors.CustomErrorMessage;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

@Service
@Transactional
public class ImportExportServiceImpl implements ImportExportService{
    
    private final Logger log = LoggerFactory.getLogger(ImportExportServiceImpl.class);
    
    @Autowired
    private DataPipelineService dataPipelineService;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private EndpointMetadataService endpointMetadataService;
    
    @Autowired
    private DataPipelineRepository dataPipelineRepository;
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    DataPipelineHelper dataPipelineHelper;
    

    @Override
    public Resource exportAll() {
        try {
            List<DataPipelineDTO> dataPipelineDTOs  = dataPipelineService.findAll();
            return convertToJSON(dataPipelineDTOs);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            log.error("Exception while encryption {}", e);
            throw new CustomParameterizedException(ErrorConstants.ENCRYPTION_EXCEPTION, e.getMessage());
        }
        
    }

    @Override
    public Resource exportById(Long id) {
        log.info("Export data pipeline by id {} ", id);
        Optional<DataPipelineDTO> optionalDataPipelineDTO =  dataPipelineService.findOne(id);
        
        if (optionalDataPipelineDTO.isPresent()) {
            DataPipelineDTO dataPipelineDTO = optionalDataPipelineDTO.get();
            List<DataPipelineDTO> dataPipelineDTOs = new ArrayList<>();
            dataPipelineDTOs.add(dataPipelineDTO);
            return convertToJSON(dataPipelineDTOs);
        }
        
        log.error("Pipeline not found {}", id);
        throw new CustomParameterizedException(ErrorConstants.EXPORT_EXCEPTION, "Pipeline not found");
    }

    private Resource convertToJSON(Object object) {
        JavaTimeModule module = new JavaTimeModule();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        mapper.registerModule(module);
       
        try {
            String data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            return new ByteArrayResource(data.getBytes());
        } catch (JsonProcessingException e) {
            log.error("Error while converting to JSON {}",e);
            throw new CustomParameterizedException(ErrorConstants.EXPORT_EXCEPTION, e.getMessage());
        }
    }
    
    private List<DataPipelineDTO> converToObject(MultipartFile file){
        JavaTimeModule module = new JavaTimeModule();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(module);
        try {
            return Arrays.asList(mapper.readValue(file.getBytes(), DataPipelineDTO[].class));
        } catch (Exception e) {
            log.error("Error while converting to Object {}",e);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.MESSAGE_KEY, ErrorConstants.ERR_INVALID_JSON);
            throw new CustomParameterizedException(ErrorConstants.DESERIALIZE_EXCEPTION, paramMap);
        }
    }
    
    @Override
    public ImportResponseDTO importInterfaces(MultipartFile file) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        List<DataPipelineDTO> validDataPipelineDTOs = new ArrayList<>();
        List<CustomErrorMessage> errors = null;
        ImportResponseDTO  importResponseDTO = new ImportResponseDTO();
        
        List<FailureDTO> failRecords = new ArrayList<>();
        List<SuccessDTO> successfulRecords = new ArrayList<>();
        
        List<DataPipelineDTO> dataPipelineDTOs  =  converToObject(file);
        
        Map<String, Object> error = DataPipelineValidator.validateDataPipelineImportJSON(dataPipelineDTOs);
        if(!error.isEmpty()){
            throw new CustomParameterizedException(ErrorConstants.ERR_INVALID_JSON, error);
        }
       
        for(DataPipelineDTO dataPipelineDTO :dataPipelineDTOs){
            dataPipelineDTO.setId(null);
            dataPipelineHelper.removeIds(dataPipelineDTO);
            errors = DataPipelineValidator.validateDataPipeline(dataPipelineDTO,validator,endpointMetadataService, dataPipelineRepository,discoveryClient, applicationProperties);
            if(errors.isEmpty()){
                validDataPipelineDTOs.add(dataPipelineDTO);
            }else{
                FailureDTO failuerDTO = new FailureDTO();
                failuerDTO.setDataPipelineDTO(dataPipelineDTO);
                failuerDTO.setErrors(errors);
                failRecords.add(failuerDTO);
            }
        }
        for(DataPipelineDTO dataPipelineDTO : validDataPipelineDTOs){
            SuccessDTO successDTO = new SuccessDTO();
            DataPipelineDTO updatedDataPipelineDTO  = dataPipelineService.save(dataPipelineDTO);
            successDTO.setDataPipeline(updatedDataPipelineDTO);
            successfulRecords.add(successDTO);
        }
        
        importResponseDTO.setFailure(failRecords);
        
        importResponseDTO.setSuccess(successfulRecords);
            
        return importResponseDTO;
    }
}
