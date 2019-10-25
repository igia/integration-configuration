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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.FilterType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.domain.enumeration.TransformerType;
import io.igia.integration.configuration.repository.DataPipelineRepository;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.FilterDTO;
import io.igia.integration.configuration.service.dto.TransformerDTO;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class DataPipelineValidatorTest {

    @InjectMocks
    private DataPipelineValidator dataPipelineValidator;

    @Mock
    private EndpointMetadataService endpointMetadataService;
    
    @Mock
    private DataPipelineRepository dataPipelineRepository;
    
    @Mock
    private DiscoveryClient discoveryClient;
    
    @Mock
    private ApplicationProperties applicationProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        when(applicationProperties.getWorkerServiceNamePrefix()).thenReturn("INTEGRATIONWORKER");
        
        List<String> workerServices = new ArrayList<>();
        workerServices.add("INTEGRATIONWORKER");
        when(discoveryClient.getServices()).thenReturn(workerServices);

    }

    private DataPipelineDTO createEntity() {

        Set<EndpointConfigDTO> sourceConfigs = createHttpSourceConfigs();

        Set<FilterDTO> sourceFilters = createSourceFilters();

        Set<TransformerDTO> sourceTransformers = createSourceTransformers();

        EndpointDTO sourceEndpoint = createMllpEndpoint(sourceConfigs, sourceFilters, sourceTransformers);

        Set<EndpointConfigDTO> destinationConfigs = createFileDestinationConfig();

        Set<FilterDTO> destinationFilters = createDestinationFilter();

        Set<TransformerDTO> destinationTransformers = createDestinationTransformer();

        Set<EndpointDTO> destinationEndpoints = createFileDestinationEndpoint(destinationConfigs,
                destinationFilters, destinationTransformers);

        DataPipelineDTO dataPipeline = createDataPipeline(sourceEndpoint, destinationEndpoints);

        return dataPipeline;
    }

    private DataPipelineDTO createDataPipelineWithHttpEndpoint() {
        EndpointDTO sourceEndpoint = createHttpSourceEndpoint();
        
        Set<EndpointConfigDTO> destinationConfigs = createFileDestinationConfig();
        Set<FilterDTO> destinationFilters = new HashSet<>();
        Set<TransformerDTO> destinationTransformers = new HashSet<>();
        Set<EndpointDTO> destinationEndpoints = createFileDestinationEndpoint(destinationConfigs,destinationFilters, destinationTransformers);
        DataPipelineDTO dataPipeline = createDataPipeline(sourceEndpoint, destinationEndpoints);
        return dataPipeline;
    }

    private DataPipelineDTO createDataPipeline(EndpointDTO sourceEndpoint,
            Set<EndpointDTO> destinationEndpoints) {
        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName("MLLP to Source");
        dataPipeline.setDescription("MLLP to Source");
        dataPipeline.setWorkerService("integrationworker");
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpoints);
        return dataPipeline;
    }

    // FILE 
    private Set<EndpointDTO> createFileDestinationEndpoint(Set<EndpointConfigDTO> destinationConfigs,
            Set<FilterDTO> destinationFilters, Set<TransformerDTO> destinationTransformers) {
        Set<EndpointDTO> destinationEndpoints = new HashSet<>();
        EndpointDTO destinationEndpoint = new EndpointDTO();
        destinationEndpoint.setInDataType(InDataType.HL7_V2);
        destinationEndpoint.setOutDataType(OutDataType.HL7_V2);
        destinationEndpoint.setName("FILE Destiantion");
        destinationEndpoint.setConfigurations(destinationConfigs);
        destinationEndpoint.setType(EndpointType.FILE);
        destinationEndpoint.setFilters(destinationFilters);
        destinationEndpoint.setTransformers(destinationTransformers);
        destinationEndpoints.add(destinationEndpoint);
        return destinationEndpoints;
    }
    
    private Set<EndpointConfigDTO> createFileDestinationConfig() {
        Set<EndpointConfigDTO> destinationConfigs = new HashSet<>();
        EndpointConfigDTO destinationConfig = new EndpointConfigDTO();
        destinationConfig.setKey("directoryName");
        destinationConfig.setValue("directoryName");
        destinationConfigs.add(destinationConfig);

        destinationConfig = new EndpointConfigDTO();
        destinationConfig.setKey("fileName");
        destinationConfig.setValue("fileName");
        destinationConfigs.add(destinationConfig);
        return destinationConfigs;
    }
    
    private List<EndpointMetadataDTO> getFileDestinationMetadaForMandatoryFields() {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.DESTINATION.name());
        sourceEndpointMetadataDTO.setType(EndpointType.FILE.name());
        sourceEndpointMetadataDTO.setProperty("directoryName");
        sourceEndpointMetadataDTO.setIsMandatory(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.DESTINATION.name());
        sourceEndpointMetadataDTO.setType(EndpointType.FILE.name());
        sourceEndpointMetadataDTO.setProperty("fileName");
        sourceEndpointMetadataDTO.setIsMandatory(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        return sourceEndpointMetadataDTOs;
    }

    //MLLP
    private EndpointDTO createMllpEndpoint(Set<EndpointConfigDTO> sourceConfigs,
            Set<FilterDTO> sourceFilters, Set<TransformerDTO> sourceTransformers) {
        EndpointDTO sourceEndpoint = new EndpointDTO();
        sourceEndpoint.setInDataType(InDataType.HL7_V2);
        sourceEndpoint.setOutDataType(OutDataType.HL7_V2);
        sourceEndpoint.setName("MLLP Source");
        sourceEndpoint.setConfigurations(sourceConfigs);
        sourceEndpoint.setType(EndpointType.MLLP);
        sourceEndpoint.setFilters(sourceFilters);
        sourceEndpoint.setTransformers(sourceTransformers);
        return sourceEndpoint;
    }
    
    private List<EndpointMetadataDTO> getMllpSourceMetadaForMandatoryFields() {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.MLLP.name());
        sourceEndpointMetadataDTO.setProperty("hostname");
        sourceEndpointMetadataDTO.setIsMandatory(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.MLLP.name());
        sourceEndpointMetadataDTO.setProperty("port");
        sourceEndpointMetadataDTO.setIsMandatory(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        return sourceEndpointMetadataDTOs;
    }
    
    private Set<EndpointConfigDTO> createMLLPConfig(){
        Set<EndpointConfigDTO> configs = new HashSet<EndpointConfigDTO>();
        
        EndpointConfigDTO config = new EndpointConfigDTO();
        config.setKey("hostname"); config.setValue("localhost");
        configs.add(config);
        
        config = new EndpointConfigDTO();
        config.setKey("port"); config.setValue("7000");
        configs.add(config);
        return configs;
     }
    
    //HTTP
    private Set<EndpointConfigDTO> createHttpSourceConfigs() {
        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        
        EndpointConfigDTO sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("hostname");
        sourceConfig.setValue("hostname");
        sourceConfigs.add(sourceConfig);

        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("port");
        sourceConfig.setValue("9080");
        sourceConfigs.add(sourceConfig);
        
        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("isSecure");
        sourceConfig.setValue("true");
        sourceConfigs.add(sourceConfig);
        
        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("username");
        sourceConfig.setValue("admin");
        sourceConfigs.add(sourceConfig);
        
        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("password");
        sourceConfig.setValue("password");
        sourceConfigs.add(sourceConfig);
        
        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("resourceUri");
        sourceConfig.setValue("/test");
        sourceConfigs.add(sourceConfig);
        
        return sourceConfigs;
    }
    
    private Set<EndpointConfigDTO> createHttpDestinationConfigs() {
        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        
        EndpointConfigDTO sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("hostname");
        sourceConfig.setValue("hostname");
        sourceConfigs.add(sourceConfig);

        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("port");
        sourceConfig.setValue("9080");
        sourceConfigs.add(sourceConfig);

        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("resourceUri");
        sourceConfig.setValue("/test");
        sourceConfigs.add(sourceConfig);

        return sourceConfigs;
    }
    
    private List<EndpointMetadataDTO> getHttpSourceMetadaForMandatoryFields() {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.HTTP.name());
        sourceEndpointMetadataDTO.setProperty("hostname");
        sourceEndpointMetadataDTO.setIsMandatory(true);
        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.HTTP.name());
        sourceEndpointMetadataDTO.setProperty("port");
        sourceEndpointMetadataDTO.setIsMandatory(true);
        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.HTTP.name());
        sourceEndpointMetadataDTO.setProperty("username");
        sourceEndpointMetadataDTO.setIsMandatory(true);
        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);
        
        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.HTTP.name());
        sourceEndpointMetadataDTO.setProperty("password");
        sourceEndpointMetadataDTO.setIsMandatory(true);
        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);
        
        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.HTTP.name());
        sourceEndpointMetadataDTO.setProperty("isSecure");
        sourceEndpointMetadataDTO.setIsMandatory(true);
        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        return sourceEndpointMetadataDTOs;
    }
    
    private EndpointDTO createHttpSourceEndpoint(){
        EndpointDTO sourceEndpoint = new EndpointDTO();
        sourceEndpoint.setInDataType(InDataType.JSON);
        sourceEndpoint.setName("HTTP Source");
        sourceEndpoint.setType(EndpointType.HTTP);
        sourceEndpoint.setOutDataType(OutDataType.JSON);
        Set<EndpointConfigDTO> configs =  createHttpSourceConfigs();
        sourceEndpoint.setConfigurations(configs);
        return sourceEndpoint;
     }
    
    private EndpointDTO createHttpDestinationEndpoint(){
        EndpointDTO destEndpoint = new EndpointDTO();
        destEndpoint.setInDataType(InDataType.JSON);
        destEndpoint.setName("HTTP Source");
        destEndpoint.setType(EndpointType.HTTP);
        destEndpoint.setOutDataType(OutDataType.JSON);
        
        Set<EndpointConfigDTO> configs = createHttpDestinationConfigs();
        destEndpoint.setConfigurations(configs);
        return destEndpoint;
     }
    
    
    //Transformer
    private Set<TransformerDTO> createDestinationTransformer() {
        Set<TransformerDTO> destinationTransformers = new HashSet<>();
        TransformerDTO destinationTransformerDTO = new TransformerDTO();
        destinationTransformerDTO.setOrder(0);
        destinationTransformerDTO.setData("Write javascript here");
        destinationTransformerDTO.setDescription("Destination Transformer");
        destinationTransformerDTO.setType(TransformerType.JAVASCRIPT);
        destinationTransformers.add(destinationTransformerDTO);
        return destinationTransformers;
    }
    
    private Set<TransformerDTO> createSourceTransformers() {
        Set<TransformerDTO> sourceTransformers = new HashSet<>();
        TransformerDTO transformerDTO = new TransformerDTO();
        transformerDTO.setOrder(0);
        transformerDTO.setData("Write java script here");
        transformerDTO.setType(TransformerType.JAVASCRIPT);
        transformerDTO.setDescription("This is souce transformer");
        sourceTransformers.add(transformerDTO);
        return sourceTransformers;
    }

    //Filter
    private Set<FilterDTO> createDestinationFilter() {
        Set<FilterDTO> destinationFilters = new HashSet<>();
        FilterDTO destinationFilterDTO = new FilterDTO();
        destinationFilterDTO.setOrder(0);
        destinationFilterDTO.setDescription("Destination Filter");
        destinationFilterDTO.setData("Write javascript here");
        destinationFilterDTO.setType(FilterType.JAVASCRIPT);
        destinationFilters.add(destinationFilterDTO);
        return destinationFilters;
    }

    private Set<FilterDTO> createSourceFilters() {
        Set<FilterDTO> sourceFilters = new HashSet<>();
        FilterDTO sourceFilterDTO = new FilterDTO();
        sourceFilterDTO.setData("Write java script here");
        sourceFilterDTO.setOrder(0);
        sourceFilterDTO.setType(FilterType.JAVASCRIPT);
        sourceFilterDTO.setDescription("This is source filter");
        sourceFilters.add(sourceFilterDTO);
        return sourceFilters;
    }

    // Test cases 
    @Test(expected = CustomParameterizedException.class)
    public void testValidateSourceConfigs() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);
        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
    }

    @Test
    public void testValidateSourceConfigsExceptionMessage() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY);
        }
    }

    @Test(expected = CustomParameterizedException.class)
    public void testValidateDestinationConfigs() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<EndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (EndpointDTO destination : destinations) {
            Set<EndpointConfigDTO> destinationConfigs = new HashSet<>();
            destination.setConfigurations(destinationConfigs);
        }

        dataPipelineDTO.setDestinations(destinations);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getFileDestinationMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(destinationEndpointMetadataDTO);

        DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
    }

    @Test
    public void testValidateDestinationConfigsExceptionMessage() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<EndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (EndpointDTO destination : destinations) {
            Set<EndpointConfigDTO> destinationConfigs = new HashSet<>();
            destination.setConfigurations(destinationConfigs);
        }

        dataPipelineDTO.setDestinations(destinations);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getFileDestinationMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(destinationEndpointMetadataDTO);
        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY);
        }
    }

    @Test
    public void testSourceFilterValidation() {
        DataPipelineDTO dataPipelineDTO = createEntity();

        FilterDTO sourceFilterDTO = new FilterDTO();
        sourceFilterDTO.setData("Write java script here");
        sourceFilterDTO.setOrder(0);
        sourceFilterDTO.setType(FilterType.JAVASCRIPT);
        sourceFilterDTO.setDescription("This is source filter");

        dataPipelineDTO.getSource().getFilters().add(sourceFilterDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
        }
    }

    @Test
    public void testSourceTransformerValidation() {
        DataPipelineDTO dataPipelineDTO = createEntity();

        TransformerDTO transformerDTO = new TransformerDTO();
        transformerDTO.setOrder(0);
        transformerDTO.setData("Write java script here");
        transformerDTO.setType(TransformerType.JAVASCRIPT);
        transformerDTO.setDescription("This is souce transformer");

        dataPipelineDTO.getSource().getTransformers().add(transformerDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
        }
    }

    @Test
    public void testDestinationFilterValidation() {
        DataPipelineDTO dataPipelineDTO = createEntity();

        FilterDTO destinationFilterDTO = new FilterDTO();
        destinationFilterDTO.setOrder(0);
        destinationFilterDTO.setDescription("Destination Filter");
        destinationFilterDTO.setData("Write javascript here");
        destinationFilterDTO.setType(FilterType.JAVASCRIPT);

        Set<EndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (EndpointDTO destinationEndpointDTO : destinations) {

            destinationEndpointDTO.getFilters().add(destinationFilterDTO);
        }

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getFileDestinationMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(destinationEndpointMetadataDTO);

        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_ORDER_VALUE);;
        }
    }

    @Test
    public void testDestinationTransformerValidation() {
        DataPipelineDTO dataPipelineDTO = createEntity();

        TransformerDTO destinationTransformerDTO = new TransformerDTO();
        destinationTransformerDTO.setOrder(0);
        destinationTransformerDTO.setData("Write javascript here");
        destinationTransformerDTO.setDescription("Destination Transformer");
        destinationTransformerDTO.setType(TransformerType.JAVASCRIPT);

        Set<EndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (EndpointDTO destinationEndpointDTO : destinations) {

            destinationEndpointDTO.getTransformers().add(destinationTransformerDTO);
        }

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getMllpSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getFileDestinationMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(destinationEndpointMetadataDTO);
        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_ORDER_VALUE);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_ORDER_VALUE);
        }
    }
    
    @Test
    public void testValidateDatapipelineName(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        
        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(true);
        
        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE);
        }
    }
    
    @Test
    public void testValidateDestinationName(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<EndpointDTO>  destinations = dataPipelineDTO.getDestinations();
        
        Set<EndpointDTO> newdestinations = createFileDestinationEndpoint(createFileDestinationConfig(),
                createDestinationFilter(), createDestinationTransformer());
        
        destinations.addAll(newdestinations);
        dataPipelineDTO.setDestinations(destinations);
        try {
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_DESTINATION_NAME);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_DESTINATION_NAME);
        }
    }

    @Test
    public void validateHttpConsumerConfigurations(){
        try {
            DataPipelineDTO dataPipelineDTO = createDataPipelineWithHttpEndpoint();
            
            Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
            
            dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
            
            Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

            Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.HTTP.name(),
                    Category.SOURCE.name(), true)).thenReturn(getHttpSourceMetadaForMandatoryFields());
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
            fail("CustomParameterizedException not occured");
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_MISSTING_REQUIRED_PROPERTY);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_MISSTING_REQUIRED_PROPERTY);
        }
    }
    
    @Test
    public void validateHttpSecureFlag(){
        try {
            DataPipelineDTO dataPipelineDTO = createDataPipelineWithHttpEndpoint();

            Set<EndpointConfigDTO> sourceConfigs = dataPipelineDTO.getSource().getConfigurations();
            
            EndpointConfigDTO sourceConfig = new EndpointConfigDTO();
            sourceConfig.setKey("isSecure");
            sourceConfig.setValue("false");
            sourceConfigs.add(sourceConfig);
            
            dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
            
            Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

            Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.HTTP.name(),
                    Category.SOURCE.name(), true)).thenReturn(getHttpSourceMetadaForMandatoryFields());
            
            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
            fail("CustomParameterizedException not occured");
        } catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_INVALID_SECURE_FLAG);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_INVALID_SECURE_FLAG);
        }
    }
    
    @Test
    public void validateMLLPSourceAndDestinationEndpoints(){
        try{
            Set<EndpointConfigDTO> configs = createMLLPConfig();
            Set<FilterDTO> filters = new HashSet<>();
            Set<TransformerDTO> transformers =new HashSet<>();
            EndpointDTO sourceEndpoint = createMllpEndpoint(configs, filters, transformers);
            Set<EndpointDTO> destinationEndpoints = new HashSet<>();

            EndpointDTO destinationEndpoint = createMllpEndpoint(configs,filters ,transformers);
            destinationEndpoints.add(destinationEndpoint);
            DataPipelineDTO dataPipelineDTO = createDataPipeline(sourceEndpoint, destinationEndpoints);
            
            Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);
            Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(getMllpSourceMetadaForMandatoryFields());

            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
            fail("CustomParameterizedException not occured");
        }catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT);
        }
    }
    
    @Test
    public void validateHTTPSourceAndDestinationEndpoints(){
        try{
            EndpointDTO sourceEndpoint = createHttpSourceEndpoint();
            Set<EndpointDTO> destinationEndpoints = new HashSet<>();
            EndpointDTO destinationEndpoint = createHttpDestinationEndpoint();
            destinationEndpoints.add(destinationEndpoint);
            DataPipelineDTO dataPipelineDTO = createDataPipeline(sourceEndpoint, destinationEndpoints);
            
            Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

            Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.HTTP.name(),
                    Category.SOURCE.name(), true)).thenReturn(getHttpSourceMetadaForMandatoryFields());

            DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
            fail("CustomParameterizedException not occured");
        }catch (CustomParameterizedException e) {
            Map<String, Object> params = e.getParameters();
            assertThat(params.get(MessageConstants.MESSAGE_KEY)).isEqualTo(ErrorConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT);
            @SuppressWarnings("unchecked")
            Map<String, String> param = (Map<String, String>) params.get(MessageConstants.PARAMS_KEY);
            assertThat(param.get(MessageConstants.MESSAGE_KEY)).isEqualTo(MessageConstants.ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT);
        }
    }
}
