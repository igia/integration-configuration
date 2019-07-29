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
package io.igia.integration.configuration.validator;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.repository.DataPipelineRepository;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.DestinationFilterDTO;
import io.igia.integration.configuration.service.dto.DestinationTransformerDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.ResponseTransformerDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;
import io.igia.integration.configuration.service.dto.SourceFilterDTO;
import io.igia.integration.configuration.service.dto.SourceTransformerDTO;
import io.igia.integration.configuration.web.rest.errors.CustomErrorMessage;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

public class DataPipelineValidator {

    private DataPipelineValidator() {
    }

    public static void validate(DataPipelineDTO oldPipelineDTO, DataPipelineDTO dataPipelineDTO, EndpointMetadataService endpointMetadataService, DataPipelineRepository dataPipelineRepository, DiscoveryClient discoveryClient,ApplicationProperties applicationProperties) {

        validateWorkerService(dataPipelineDTO.getWorkerService(),discoveryClient,applicationProperties);
        
        validateDataPipelineName(oldPipelineDTO, dataPipelineDTO,dataPipelineRepository);

        validateSource(dataPipelineDTO.getSource(), endpointMetadataService);

        validateDestination(dataPipelineDTO.getDestinations(), endpointMetadataService);

    }
    
    private static void validateWorkerService(String workerService, DiscoveryClient discoveryClient,ApplicationProperties applicationProperties){
        List<String> workerServices = discoveryClient.getServices().stream()
                .filter(s -> s.toUpperCase().startsWith(applicationProperties.getWorkerServiceNamePrefix().toUpperCase(Locale.ENGLISH))).collect(Collectors.toList());
        
        if(workerServices.stream().noneMatch(serviceName -> serviceName.equalsIgnoreCase(workerService))){
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.FIELD, MessageConstants.WORKERSERVICE);
            paramMap.put(MessageConstants.FIELD_VALUE, workerService);
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.INVALID_WORKERSERVICE);
            throw new CustomParameterizedException(ErrorConstants.INVALID_WORKERSERVICE, paramMap);
         }
    }

    private static void validateSource(SourceEndpointDTO sourceEndpointDTO,
            EndpointMetadataService endpointMetadataService) {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = endpointMetadataService
                .findAllByTypeAndCategoryAndIsMandatory(sourceEndpointDTO.getType().name(), Category.SOURCE.name(),
                        true);
        validateSourceConfigurations(sourceEndpointDTO, sourceEndpointMetadataDTO);
        if (!sourceEndpointDTO.getFilters().isEmpty()) {
            validateSourceFilterOrder(sourceEndpointDTO.getFilters());
        }
        if (!sourceEndpointDTO.getTransformers().isEmpty()) {
            validateSourceTransformerOrder(sourceEndpointDTO.getTransformers());
        }
    }

    private static void validateDestination(Set<DestinationEndpointDTO> destinationEndpointDTOs,
            EndpointMetadataService endpointMetadataService) {
        validateDestinationNames(destinationEndpointDTOs);
        for (DestinationEndpointDTO destinationEndpointDTO : destinationEndpointDTOs) {
            List<EndpointMetadataDTO> destinationEndpointMetadataDTO = endpointMetadataService
                    .findAllByTypeAndCategoryAndIsMandatory(destinationEndpointDTO.getType().name(),
                            Category.DESTINATION.name(), true);
            validateDestinationConfigurations(destinationEndpointDTO, destinationEndpointMetadataDTO);
            if (!destinationEndpointDTO.getFilters().isEmpty()) {
                validateDestinationFilterOrder(destinationEndpointDTO.getFilters());
            }
            if (!destinationEndpointDTO.getTransformers().isEmpty()) {
                validateDestinationTransformerOrder(destinationEndpointDTO.getTransformers());
            }
            if (!destinationEndpointDTO.getResponseTransformers().isEmpty()) {
                validateResponseTransformerOrder(destinationEndpointDTO.getResponseTransformers());
            }
        }
    }

    private static void validateSourceConfigurations(SourceEndpointDTO sourceEndpointDTO,
            List<EndpointMetadataDTO> sourceEndpointMetadataDTO) {

        if (!sourceEndpointMetadataDTO.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            Set<SourceConfigDTO> configs = sourceEndpointDTO.getConfigurations();
            for (SourceConfigDTO configDTO : configs) {
                map.put(configDTO.getKey(), configDTO.getValue());
            }
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTO) {
                if (!map.containsKey(endpointMetadataDTO.getProperty())) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put(MessageConstants.FIELD, MessageConstants.SOURCE_CONFIGURATIONS);
                    paramMap.put(MessageConstants.FIELD_VALUE, endpointMetadataDTO.getProperty());
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_MISSTING_REQUIRED_PROPERTY);
                    throw new CustomParameterizedException(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY, paramMap);
                }else{
                    if(endpointMetadataDTO.getProperty().equalsIgnoreCase(MessageConstants.PORT)){
                        String port = map.get(endpointMetadataDTO.getProperty());
                        validatePortNumber(port);
                    }
                }
            }
        }
    }

    private static void validateDestinationConfigurations(DestinationEndpointDTO destinationEndpointDTO,
            List<EndpointMetadataDTO> destinationEndpointMetadataDTO) {
        if (!destinationEndpointMetadataDTO.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            Set<DestinationConfigDTO> configs = destinationEndpointDTO.getConfigurations();
            for (DestinationConfigDTO configDTO : configs) {
                map.put(configDTO.getKey(), configDTO.getValue());
            }
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTO) {
                if (!map.containsKey(endpointMetadataDTO.getProperty())) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put(MessageConstants.FIELD, MessageConstants.DESTINATION_CONFIGURATIONS);
                    paramMap.put(MessageConstants.FIELD_VALUE, endpointMetadataDTO.getProperty());
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_MISSTING_REQUIRED_PROPERTY);
                    throw new CustomParameterizedException(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY, paramMap);
                }else{
                    if(endpointMetadataDTO.getProperty().equalsIgnoreCase(MessageConstants.PORT)){
                        String port = map.get(endpointMetadataDTO.getProperty());
                        validatePortNumber(port);
                    }
                }
            }
        }
    }

    private static void validateSourceFilterOrder(Set<SourceFilterDTO> sourceFilterDTOs) {
        Map<Integer, SourceFilterDTO> map = new HashMap<>();
        for (SourceFilterDTO sourceFilterDTO : sourceFilterDTOs) {
            if (map.containsKey(sourceFilterDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.SOURCE_FILTERS+"."+MessageConstants.ORDER);
                paramMap.put(MessageConstants.FIELD_VALUE, sourceFilterDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(sourceFilterDTO.getOrder(), sourceFilterDTO);
            }
        }
    }

    private static void validateDestinationFilterOrder(Set<DestinationFilterDTO> destinationFilterDTOs) {
        Map<Integer, DestinationFilterDTO> map = new HashMap<>();
        for (DestinationFilterDTO destinationFilterDTO : destinationFilterDTOs) {
            if (map.containsKey(destinationFilterDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.DESTINATION_FILTERS+"."+MessageConstants.ORDER); 
                paramMap.put(MessageConstants.FIELD_VALUE, destinationFilterDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(destinationFilterDTO.getOrder(), destinationFilterDTO);
            }
        }
    }

    private static void validateSourceTransformerOrder(Set<SourceTransformerDTO> sourceTransformerDTOs) {
        Map<Integer, SourceTransformerDTO> map = new HashMap<>();
        for (SourceTransformerDTO sourceTransformerDTO : sourceTransformerDTOs) {
            if (map.containsKey(sourceTransformerDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.SOURCE_TRANSFORMERS+"."+MessageConstants.ORDER); 
                paramMap.put(MessageConstants.FIELD_VALUE, sourceTransformerDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(sourceTransformerDTO.getOrder(), sourceTransformerDTO);
            }
        }
    }

    private static void validateDestinationTransformerOrder(Set<DestinationTransformerDTO> destinationTransformerDTOs) {
        Map<Integer, DestinationTransformerDTO> map = new HashMap<>();
        for (DestinationTransformerDTO destinationTransformerDTO : destinationTransformerDTOs) {
            if (map.containsKey(destinationTransformerDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.DESTINATION_TRANSFORMERS+"."+MessageConstants.ORDER); 
                paramMap.put(MessageConstants.FIELD_VALUE, destinationTransformerDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(destinationTransformerDTO.getOrder(), destinationTransformerDTO);
            }
        }
    }
    
    private static void validateResponseTransformerOrder(Set<ResponseTransformerDTO> responseTransformerDTOs) {
        Map<Integer, ResponseTransformerDTO> map = new HashMap<>();
        for (ResponseTransformerDTO responseTransformerDTO : responseTransformerDTOs) {
            if (map.containsKey(responseTransformerDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.DESTINATION_TRANSFORMERS+"."+MessageConstants.ORDER); 
                paramMap.put(MessageConstants.FIELD_VALUE, responseTransformerDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(responseTransformerDTO.getOrder(), responseTransformerDTO);
            }
        }
    }
    
    private static void validateDataPipelineName(DataPipelineDTO oldPipelineDTO, DataPipelineDTO dataPipelineDTO, DataPipelineRepository dataPipelineRepository){
        
        if (oldPipelineDTO != null && oldPipelineDTO.getName().equalsIgnoreCase(dataPipelineDTO.getName())) {
            return;
        }
        
        if(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())){
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.FIELD,MessageConstants.NAME); 
            paramMap.put(MessageConstants.FIELD_VALUE, dataPipelineDTO.getName());
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE);
            throw new CustomParameterizedException(ErrorConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE, paramMap);
        }
    }
    
    private static void validateDestinationNames(Set<DestinationEndpointDTO> destinationEndpointDTOs){
        Map<String, DestinationEndpointDTO> map = new HashMap<>();
        for (DestinationEndpointDTO destinationEndpointDTO : destinationEndpointDTOs) {
            if(map.containsKey(destinationEndpointDTO.getName())){
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.NAME); 
                paramMap.put(MessageConstants.FIELD_VALUE, destinationEndpointDTO.getName());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_DESTINATION_NAME);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_DESTINATION_NAME, paramMap);
            }else{
                map.put(destinationEndpointDTO.getName(),destinationEndpointDTO);
            }
        }
    }
    
    private static void validatePortNumber(String port){
        Map<String, Object> paramMap = new HashMap<>();
        if(StringUtils.isNumeric(port)){
            try{
                Integer iPort = Integer.parseInt(port);
                if(iPort < MessageConstants.MIN_PORT_NUMBER || iPort > MessageConstants.MAX_PORT_NUMBER){
                    paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
                    paramMap.put(MessageConstants.FIELD_VALUE, port);
                    paramMap.put(MessageConstants.MIN, MessageConstants.MIN_PORT_NUMBER);
                    paramMap.put(MessageConstants.MAX, MessageConstants.MAX_PORT_NUMBER);
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_INVALIDATION_PORT_RANGE);
                    throw new CustomParameterizedException(ErrorConstants.ERR_INVALIDATION_PORT_RANGE, paramMap);
                }
            }catch(Exception e){
                paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
                paramMap.put(MessageConstants.FIELD_VALUE, port);
                paramMap.put(MessageConstants.MESSAGE_KEY,e.getMessage());
                throw new CustomParameterizedException(ErrorConstants.ERR_PORT_VALIDATION, paramMap);
            }
        }else{
            paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
            paramMap.put(MessageConstants.FIELD_VALUE, port);
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_PORT_VALIDATION);
            throw new CustomParameterizedException(ErrorConstants.ERR_PORT_VALIDATION, paramMap);
        }
    }
    
    public static Map<String, Object> validateDataPipelineImportJSON(List<DataPipelineDTO> dataPipelineDTOs){
        Map<String, DataPipelineDTO> dataPipelineNames = new HashMap<>();
        Map<String, Object> params  = new HashMap<>();
        for(DataPipelineDTO dataPipelineDTO : dataPipelineDTOs){
            if(dataPipelineNames.containsKey(dataPipelineDTO.getName())){
                Map<String, Object> paramMap  = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,MessageConstants.NAME);
                paramMap.put(MessageConstants.FIELD_VALUE,dataPipelineDTO.getName());
                paramMap.put(MessageConstants.MESSAGE_KEY, ErrorConstants.ERR_DUPLICATE_PIPELINE_NAME);
                params.put("error", paramMap);
                return params;
            }else{
                dataPipelineNames.put(dataPipelineDTO.getName(), dataPipelineDTO);
            }
        }
       return params;
    }
    
    
    public static List<CustomErrorMessage> validateDataPipeline(DataPipelineDTO dataPipelineDTO,Validator validator, EndpointMetadataService endpointMetadataService,DataPipelineRepository dataPipelineRepository,DiscoveryClient discoveryClient,ApplicationProperties applicationProperties){
        List<CustomErrorMessage> errors = new ArrayList<>();
        Set<ConstraintViolation<DataPipelineDTO>> violations =  validator.validate(dataPipelineDTO);
        if(!violations.isEmpty()){
            for(ConstraintViolation<DataPipelineDTO> d : violations){
                Map<String, Object> paramMap  = new HashMap<>();
                paramMap.put(MessageConstants.FIELD, d.getPropertyPath().toString());
                paramMap.put(MessageConstants.FIELD_VALUE,"");
                paramMap.put(MessageConstants.MESSAGE_KEY, d.getMessage());
                CustomErrorMessage error = new CustomErrorMessage(ErrorConstants.ERR_VALIDATION,paramMap);
                errors.add(error);
            }
        }
        if(errors.isEmpty()){
            try{
                validate(null,dataPipelineDTO, endpointMetadataService, dataPipelineRepository,discoveryClient,applicationProperties);
                validateDeployFlagForImport(dataPipelineDTO);
            }catch(CustomParameterizedException e){
                @SuppressWarnings("unchecked")
                Map<String, Object> paramMap =  (Map<String, Object>) e.getParameters().get("params");
                String errorMessage = (String)e.getParameters().get("message");
                CustomErrorMessage error = new CustomErrorMessage(errorMessage,paramMap);
                errors.add(error);
            }
        }
        return errors;
    }
    
    public static void validateDeployFlagForImport(DataPipelineDTO dataPipelineDTO){
        Map<String, Object> paramMap  = new HashMap<>();
        if(dataPipelineDTO.isDeploy()){
            paramMap.put(MessageConstants.FIELD, MessageConstants.DEPLOY);
            paramMap.put(MessageConstants.FIELD_VALUE, dataPipelineDTO.isDeploy());
            paramMap.put(MessageConstants.MESSAGE_KEY,MessageConstants.ERR_INVALID_DEPLOY_FLAG);
            throw new CustomParameterizedException(ErrorConstants.ERR_INVALID_DEPLOY_FLAG, paramMap); 
        }
        
    }
}
