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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.repository.DataPipelineRepository;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.FilterDTO;
import io.igia.integration.configuration.service.dto.ResponseTransformerDTO;
import io.igia.integration.configuration.service.dto.TransformerDTO;
import io.igia.integration.configuration.web.rest.errors.CustomErrorMessage;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

public class DataPipelineValidator {

    private DataPipelineValidator() {
    }

    public static void validate(DataPipelineDTO oldPipelineDTO, DataPipelineDTO dataPipelineDTO,
            EndpointMetadataService endpointMetadataService, DataPipelineRepository dataPipelineRepository,
            DiscoveryClient discoveryClient, ApplicationProperties applicationProperties) {

        validateWorkerService(dataPipelineDTO.getWorkerService(),discoveryClient,applicationProperties);

        validateDataPipelineName(oldPipelineDTO, dataPipelineDTO, dataPipelineRepository);

        validateSource(dataPipelineDTO.getSource(), endpointMetadataService);

        validateDestination(dataPipelineDTO.getDestinations(), endpointMetadataService);

        validateSourceAndDestinationEndpoints(dataPipelineDTO);

    }

    private static void validateWorkerService(String workerService, DiscoveryClient discoveryClient,
            ApplicationProperties applicationProperties) {
        List<String> workerServices = discoveryClient.getServices().stream()
                        .filter(s -> s.toUpperCase()
                        .startsWith(applicationProperties.getWorkerServiceNamePrefix().toUpperCase(Locale.ENGLISH)))
             .collect(Collectors.toList());

        if (workerServices.stream().noneMatch(serviceName -> serviceName.equalsIgnoreCase(workerService))) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.FIELD, MessageConstants.WORKERSERVICE);
            paramMap.put(MessageConstants.FIELD_VALUE, workerService);
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.INVALID_WORKERSERVICE);
            throw new CustomParameterizedException(ErrorConstants.INVALID_WORKERSERVICE, paramMap);
        }
    }

    private static void validateSource(EndpointDTO sourceEndpointDTO, EndpointMetadataService endpointMetadataService) {
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

    private static void validateDestination(Set<EndpointDTO> destinationEndpointDTOs,
            EndpointMetadataService endpointMetadataService) {
        validateDestinationNames(destinationEndpointDTOs);
        for (EndpointDTO destinationEndpointDTO : destinationEndpointDTOs) {
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

    private static void validateSourceConfigurations(EndpointDTO sourceEndpointDTO,
            List<EndpointMetadataDTO> sourceEndpointMetadataDTO) {

        if (!sourceEndpointMetadataDTO.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            Set<EndpointConfigDTO> configs = sourceEndpointDTO.getConfigurations();
            for (EndpointConfigDTO configDTO : configs) {
                map.put(configDTO.getKey(), configDTO.getValue());
            }
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTO) {
                if (!map.containsKey(endpointMetadataDTO.getProperty())) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put(MessageConstants.FIELD, MessageConstants.SOURCE_CONFIGURATIONS);
                    paramMap.put(MessageConstants.FIELD_VALUE, endpointMetadataDTO.getProperty());
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_MISSTING_REQUIRED_PROPERTY);
                    throw new CustomParameterizedException(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY, paramMap);
                } else {
                    if (endpointMetadataDTO.getProperty().equalsIgnoreCase(MessageConstants.PORT)) {
                        String port = map.get(endpointMetadataDTO.getProperty());
                        validatePortNumber(port);
                    }else if (endpointMetadataDTO.getProperty().equalsIgnoreCase(MessageConstants.HTTP_SECURE)) {
                        String isSecure = map.get(endpointMetadataDTO.getProperty());
                        validateHTTPSecureField(isSecure);
                    }
                }
            }
        }
    }

    private static void validateDestinationConfigurations(EndpointDTO destinationEndpointDTO,
            List<EndpointMetadataDTO> destinationEndpointMetadataDTO) {
        if (!destinationEndpointMetadataDTO.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            Set<EndpointConfigDTO> configs = destinationEndpointDTO.getConfigurations();
            for (EndpointConfigDTO configDTO : configs) {
                map.put(configDTO.getKey(), configDTO.getValue());
            }
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTO) {
                if (!map.containsKey(endpointMetadataDTO.getProperty())) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put(MessageConstants.FIELD, MessageConstants.DESTINATION_CONFIGURATIONS);
                    paramMap.put(MessageConstants.FIELD_VALUE, endpointMetadataDTO.getProperty());
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_MISSTING_REQUIRED_PROPERTY);
                    throw new CustomParameterizedException(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY, paramMap);
                } else {
                    if (endpointMetadataDTO.getProperty().equalsIgnoreCase(MessageConstants.PORT)) {
                        String port = map.get(endpointMetadataDTO.getProperty());
                        validatePortNumber(port);
                    }
                }
            }
        }
    }

    private static void validateSourceFilterOrder(Set<FilterDTO> sourceFilterDTOs) {
        Map<Integer, FilterDTO> map = new HashMap<>();
        for (FilterDTO sourceFilterDTO : sourceFilterDTOs) {
            if (map.containsKey(sourceFilterDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD, MessageConstants.SOURCE_FILTERS + "." + MessageConstants.ORDER);
                paramMap.put(MessageConstants.FIELD_VALUE, sourceFilterDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(sourceFilterDTO.getOrder(), sourceFilterDTO);
            }
        }
    }

    private static void validateDestinationFilterOrder(Set<FilterDTO> destinationFilterDTOs) {
        Map<Integer, FilterDTO> map = new HashMap<>();
        for (FilterDTO destinationFilterDTO : destinationFilterDTOs) {
            if (map.containsKey(destinationFilterDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,
                        MessageConstants.DESTINATION_FILTERS + "." + MessageConstants.ORDER);
                paramMap.put(MessageConstants.FIELD_VALUE, destinationFilterDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(destinationFilterDTO.getOrder(), destinationFilterDTO);
            }
        }
    }

    private static void validateSourceTransformerOrder(Set<TransformerDTO> sourceTransformerDTOs) {
        Map<Integer, TransformerDTO> map = new HashMap<>();
        for (TransformerDTO sourceTransformerDTO : sourceTransformerDTOs) {
            if (map.containsKey(sourceTransformerDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,
                        MessageConstants.SOURCE_TRANSFORMERS + "." + MessageConstants.ORDER);
                paramMap.put(MessageConstants.FIELD_VALUE, sourceTransformerDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE, paramMap);
            } else {
                map.put(sourceTransformerDTO.getOrder(), sourceTransformerDTO);
            }
        }
    }

    private static void validateDestinationTransformerOrder(Set<TransformerDTO> destinationTransformerDTOs) {
        Map<Integer, TransformerDTO> map = new HashMap<>();
        for (TransformerDTO destinationTransformerDTO : destinationTransformerDTOs) {
            if (map.containsKey(destinationTransformerDTO.getOrder())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,
                        MessageConstants.DESTINATION_TRANSFORMERS + "." + MessageConstants.ORDER);
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
        for (ResponseTransformerDTO responseTransformerDTO :responseTransformerDTOs) { 
            if(map.containsKey(responseTransformerDTO.getOrder())) { 
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD,
                        MessageConstants.DESTINATION_TRANSFORMERS+"."+MessageConstants.ORDER); 
                paramMap.put(MessageConstants.FIELD_VALUE,responseTransformerDTO.getOrder());
                paramMap.put(MessageConstants.MESSAGE_KEY,MessageConstants.ERR_DUPLICATE_ORDER_VALUE); 
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE,paramMap); 
            } else { 
                map.put(responseTransformerDTO.getOrder(),responseTransformerDTO); 
                } 
            } 
        }

    private static void validateDataPipelineName(DataPipelineDTO oldPipelineDTO, DataPipelineDTO dataPipelineDTO,
            DataPipelineRepository dataPipelineRepository) {

        if (oldPipelineDTO != null && oldPipelineDTO.getName().equalsIgnoreCase(dataPipelineDTO.getName())) {
            return;
        }

        if (dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.FIELD, MessageConstants.NAME);
            paramMap.put(MessageConstants.FIELD_VALUE, dataPipelineDTO.getName());
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE);
            throw new CustomParameterizedException(ErrorConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE, paramMap);
        }
    }

    private static void validateDestinationNames(Set<EndpointDTO> destinationEndpointDTOs) {
        Map<String, EndpointDTO> map = new HashMap<>();
        for (EndpointDTO destinationEndpointDTO : destinationEndpointDTOs) {
            if (map.containsKey(destinationEndpointDTO.getName())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD, MessageConstants.NAME);
                paramMap.put(MessageConstants.FIELD_VALUE, destinationEndpointDTO.getName());
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_DESTINATION_NAME);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_DESTINATION_NAME, paramMap);
            } else {
                map.put(destinationEndpointDTO.getName(), destinationEndpointDTO);
            }
        }
    }

    private static void validatePortNumber(String port) {
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNumeric(port)) {
            try {
                Integer iPort = Integer.parseInt(port);
                if (iPort < MessageConstants.MIN_PORT_NUMBER || iPort > MessageConstants.MAX_PORT_NUMBER) {
                    paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
                    paramMap.put(MessageConstants.FIELD_VALUE, port);
                    paramMap.put(MessageConstants.MIN, MessageConstants.MIN_PORT_NUMBER);
                    paramMap.put(MessageConstants.MAX, MessageConstants.MAX_PORT_NUMBER);
                    paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_INVALIDATION_PORT_RANGE);
                    throw new CustomParameterizedException(ErrorConstants.ERR_INVALIDATION_PORT_RANGE, paramMap);
                }
            } catch (Exception e) {
                paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
                paramMap.put(MessageConstants.FIELD_VALUE, port);
                paramMap.put(MessageConstants.MESSAGE_KEY, e.getMessage());
                throw new CustomParameterizedException(ErrorConstants.ERR_PORT_VALIDATION, paramMap);
            }
        } else {
            paramMap.put(MessageConstants.FIELD, MessageConstants.PORT);
            paramMap.put(MessageConstants.FIELD_VALUE, port);
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_PORT_VALIDATION);
            throw new CustomParameterizedException(ErrorConstants.ERR_PORT_VALIDATION, paramMap);
        }
    }

    public static Map<String, Object> validateDataPipelineImportJSON(List<DataPipelineDTO> dataPipelineDTOs) {
        Map<String, DataPipelineDTO> dataPipelineNames = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        for (DataPipelineDTO dataPipelineDTO : dataPipelineDTOs) {
            if (dataPipelineNames.containsKey(dataPipelineDTO.getName())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD, MessageConstants.NAME);
                paramMap.put(MessageConstants.FIELD_VALUE, dataPipelineDTO.getName());
                paramMap.put(MessageConstants.MESSAGE_KEY, ErrorConstants.ERR_DUPLICATE_PIPELINE_NAME);
                params.put("error", paramMap);
                return params;
            } else {
                dataPipelineNames.put(dataPipelineDTO.getName(), dataPipelineDTO);
            }
        }
        return params;
    }

    public static List<CustomErrorMessage> validateDataPipeline(DataPipelineDTO dataPipelineDTO, Validator validator,
            EndpointMetadataService endpointMetadataService, DataPipelineRepository dataPipelineRepository,
            DiscoveryClient discoveryClient, ApplicationProperties applicationProperties) {
        List<CustomErrorMessage> errors = new ArrayList<>();
        Set<ConstraintViolation<DataPipelineDTO>> violations = validator.validate(dataPipelineDTO);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<DataPipelineDTO> d : violations) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put(MessageConstants.FIELD, d.getPropertyPath().toString());
                paramMap.put(MessageConstants.FIELD_VALUE, "");
                paramMap.put(MessageConstants.MESSAGE_KEY, d.getMessage());
                CustomErrorMessage error = new CustomErrorMessage(ErrorConstants.ERR_VALIDATION, paramMap);
                errors.add(error);
            }
        }
        if (errors.isEmpty()) {
            try {
                validate(null, dataPipelineDTO, endpointMetadataService, dataPipelineRepository, discoveryClient,
                        applicationProperties);
                validateDeployFlagForImport(dataPipelineDTO);
            } catch (CustomParameterizedException e) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramMap = (Map<String, Object>) e.getParameters().get("params");
                String errorMessage = (String) e.getParameters().get("message");
                CustomErrorMessage error = new CustomErrorMessage(errorMessage, paramMap);
                errors.add(error);
            }
        }
        return errors;
    }

    public static void validateDeployFlagForImport(DataPipelineDTO dataPipelineDTO) {
        Map<String, Object> paramMap = new HashMap<>();
        if (dataPipelineDTO.isDeploy()) {
            paramMap.put(MessageConstants.FIELD, MessageConstants.DEPLOY);
            paramMap.put(MessageConstants.FIELD_VALUE, dataPipelineDTO.isDeploy());
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_INVALID_DEPLOY_FLAG);
            throw new CustomParameterizedException(ErrorConstants.ERR_INVALID_DEPLOY_FLAG, paramMap);
        }
    }

    private static void validateHTTPSecureField(String isSecure){
        Map<String, Object> paramMap = new HashMap<>();
        if(!isSecure.equalsIgnoreCase(Boolean.TRUE.toString())) {
            paramMap.put(MessageConstants.FIELD, MessageConstants.SOURCE_CONFIGURATIONS+"."+MessageConstants.HTTP_SECURE);
            paramMap.put(MessageConstants.FIELD_VALUE, isSecure);
            paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_INVALID_SECURE_FLAG);
            throw new CustomParameterizedException(ErrorConstants.ERR_INVALID_SECURE_FLAG, paramMap);
        }
    }
    
    private static void validateSourceAndDestinationEndpoints(DataPipelineDTO dataPipelineDTO){
        Map<String, Object> paramMap = new HashMap<>();
        EndpointDTO sourceEndpoint = dataPipelineDTO.getSource();
        if(sourceEndpoint.getType().equals(EndpointType.HTTP) || sourceEndpoint.getType().equals(EndpointType.MLLP)){
            String srcEndpointURL = getSourceUrl(sourceEndpoint);

            List<String> destEndpointUrls = getDestinationUrl(dataPipelineDTO);

            if(destEndpointUrls.contains(srcEndpointURL)){
                paramMap.put(MessageConstants.FIELD, MessageConstants.DATAPIPELINE_SOURCE+" & "+MessageConstants.DATAPIPELINE_DESTINATION);
                paramMap.put(MessageConstants.FIELD_VALUE, srcEndpointURL);
                paramMap.put(MessageConstants.MESSAGE_KEY, MessageConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT);
                throw new CustomParameterizedException(ErrorConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT, paramMap);
            }
        }
    }
    
    private static String getSourceUrl(EndpointDTO sourceEndpoint){
        Set<EndpointConfigDTO> sourceConfigs = sourceEndpoint.getConfigurations();
        Map<String,String> configs = new HashMap<>();
        for(EndpointConfigDTO config : sourceConfigs){
            configs.put(config.getKey(), config.getValue());
        }

        String srcHostname = configs.get(MessageConstants.HOSTNAME);
        String srcPort =  configs.get(MessageConstants.PORT);
        String srcEndpointUrl = srcHostname.concat(":").concat(srcPort);
        
        if(sourceEndpoint.getType().equals(EndpointType.HTTP)){
            String srcResourceUri =  configs.get(MessageConstants.RESOURCE_URI);
            srcEndpointUrl = srcEndpointUrl.concat(srcResourceUri);
        }
        return srcEndpointUrl;
    }

    private static List<String> getDestinationUrl(DataPipelineDTO dataPipelineDTO) {
        Set<EndpointDTO> destinations = dataPipelineDTO.getDestinations();
        List<String> destinationEndpointURLs = new ArrayList<>();
        for(EndpointDTO destination : destinations){
            if(destination.getType().equals(EndpointType.HTTP) || destination.getType().equals(EndpointType.MLLP)){
                Set<EndpointConfigDTO> destinationConfigs = destination.getConfigurations();
                Map<String,String> configMap = new HashMap<>();
                for(EndpointConfigDTO configObj : destinationConfigs){
                    configMap.put(configObj.getKey(), configObj.getValue());
                }

                String hostname = configMap.get(MessageConstants.HOSTNAME);
                String port =  configMap.get(MessageConstants.PORT);
                String destinationEndpointURL = hostname.concat(":").concat(port);
                if(destination.getType().equals(EndpointType.HTTP)){
                    String resourceUri =  configMap.get(MessageConstants.RESOURCE_URI);
                    destinationEndpointURL = destinationEndpointURL.concat(resourceUri);
                }
                destinationEndpointURLs.add(destinationEndpointURL);
            }
        }
        return destinationEndpointURLs;
    }
}
