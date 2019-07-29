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
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.DestinationFilterDTO;
import io.igia.integration.configuration.service.dto.DestinationTransformerDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;
import io.igia.integration.configuration.service.dto.SourceFilterDTO;
import io.igia.integration.configuration.service.dto.SourceTransformerDTO;
import io.igia.integration.configuration.validator.DataPipelineValidator;
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

        Set<SourceConfigDTO> sourceConfigs = createSourceConfigs();

        Set<SourceFilterDTO> sourceFilters = createSourceFilters();

        Set<SourceTransformerDTO> sourceTransformers = createSourceTransformers();

        SourceEndpointDTO sourceEndpoint = createSourceEndpoint(sourceConfigs, sourceFilters, sourceTransformers);

        Set<DestinationConfigDTO> destinationConfigs = createDestinationConfig();

        Set<DestinationFilterDTO> destinationFilters = createDestinationFilter();

        Set<DestinationTransformerDTO> destinationTransformers = createDestinationTransformer();

        Set<DestinationEndpointDTO> destinationEndpoints = createDestinationEndpoint(destinationConfigs,
                destinationFilters, destinationTransformers);

        DataPipelineDTO dataPipeline = createDataPipeline(sourceEndpoint, destinationEndpoints);

        return dataPipeline;
    }

    private DataPipelineDTO createDataPipelineWithHTTPEndpoint() {
        Set<SourceConfigDTO> sourceConfigs = createSourceConfigs();
        Set<SourceFilterDTO> sourceFilters = new HashSet<>();
        Set<SourceTransformerDTO> sourceTransformers = new HashSet<>();
        SourceEndpointDTO sourceEndpoint = new SourceEndpointDTO();
        sourceEndpoint.setInDataType(InDataType.JSON);
        sourceEndpoint.setOutDataType(OutDataType.JSON);
        sourceEndpoint.setName("HTTP Source");
        sourceEndpoint.setConfigurations(sourceConfigs);
        sourceEndpoint.setType(EndpointType.HTTP);
        sourceEndpoint.setFilters(sourceFilters);
        sourceEndpoint.setTransformers(sourceTransformers);

        Set<DestinationConfigDTO> destinationConfigs = createDestinationConfig();
        Set<DestinationFilterDTO> destinationFilters = new HashSet<>();
        Set<DestinationTransformerDTO> destinationTransformers = new HashSet<>();
        Set<DestinationEndpointDTO> destinationEndpoints = createDestinationEndpoint(destinationConfigs,destinationFilters, destinationTransformers);
        DataPipelineDTO dataPipeline = createDataPipeline(sourceEndpoint, destinationEndpoints);
        return dataPipeline;
    }

    private DataPipelineDTO createDataPipeline(SourceEndpointDTO sourceEndpoint,
            Set<DestinationEndpointDTO> destinationEndpoints) {
        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName("MLLP to Source");
        dataPipeline.setDescription("MLLP to Source");
        dataPipeline.setWorkerService("integrationworker");
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpoints);
        return dataPipeline;
    }

    private Set<DestinationEndpointDTO> createDestinationEndpoint(Set<DestinationConfigDTO> destinationConfigs,
            Set<DestinationFilterDTO> destinationFilters, Set<DestinationTransformerDTO> destinationTransformers) {
        Set<DestinationEndpointDTO> destinationEndpoints = new HashSet<>();
        DestinationEndpointDTO destinationEndpoint = new DestinationEndpointDTO();
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

    private Set<DestinationTransformerDTO> createDestinationTransformer() {
        Set<DestinationTransformerDTO> destinationTransformers = new HashSet<>();
        DestinationTransformerDTO destinationTransformerDTO = new DestinationTransformerDTO();
        destinationTransformerDTO.setOrder(0);
        destinationTransformerDTO.setData("Write javascript here");
        destinationTransformerDTO.setDescription("Destination Transformer");
        destinationTransformerDTO.setType(TransformerType.JAVASCRIPT);
        destinationTransformers.add(destinationTransformerDTO);
        return destinationTransformers;
    }

    private Set<DestinationFilterDTO> createDestinationFilter() {
        Set<DestinationFilterDTO> destinationFilters = new HashSet<>();
        DestinationFilterDTO destinationFilterDTO = new DestinationFilterDTO();
        destinationFilterDTO.setOrder(0);
        destinationFilterDTO.setDescription("Destination Filter");
        destinationFilterDTO.setData("Write javascript here");
        destinationFilterDTO.setType(FilterType.JAVASCRIPT);
        destinationFilters.add(destinationFilterDTO);
        return destinationFilters;
    }

    private Set<DestinationConfigDTO> createDestinationConfig() {
        Set<DestinationConfigDTO> destinationConfigs = new HashSet<>();
        DestinationConfigDTO destinationConfig = new DestinationConfigDTO();
        destinationConfig.setKey("directoryName");
        destinationConfig.setValue("directoryName");
        destinationConfigs.add(destinationConfig);

        destinationConfig = new DestinationConfigDTO();
        destinationConfig.setKey("fileName");
        destinationConfig.setValue("fileName");
        destinationConfigs.add(destinationConfig);
        return destinationConfigs;
    }

    private SourceEndpointDTO createSourceEndpoint(Set<SourceConfigDTO> sourceConfigs,
            Set<SourceFilterDTO> sourceFilters, Set<SourceTransformerDTO> sourceTransformers) {
        SourceEndpointDTO sourceEndpoint = new SourceEndpointDTO();
        sourceEndpoint.setInDataType(InDataType.HL7_V2);
        sourceEndpoint.setOutDataType(OutDataType.HL7_V2);
        sourceEndpoint.setName("MLLP Source");
        sourceEndpoint.setConfigurations(sourceConfigs);
        sourceEndpoint.setType(EndpointType.MLLP);
        sourceEndpoint.setFilters(sourceFilters);
        sourceEndpoint.setTransformers(sourceTransformers);
        return sourceEndpoint;
    }

    private Set<SourceTransformerDTO> createSourceTransformers() {
        Set<SourceTransformerDTO> sourceTransformers = new HashSet<>();
        SourceTransformerDTO transformerDTO = new SourceTransformerDTO();
        transformerDTO.setOrder(0);
        transformerDTO.setData("Write java script here");
        transformerDTO.setType(TransformerType.JAVASCRIPT);
        transformerDTO.setDescription("This is souce transformer");
        sourceTransformers.add(transformerDTO);
        return sourceTransformers;
    }

    private Set<SourceFilterDTO> createSourceFilters() {
        Set<SourceFilterDTO> sourceFilters = new HashSet<>();
        SourceFilterDTO sourceFilterDTO = new SourceFilterDTO();
        sourceFilterDTO.setData("Write java script here");
        sourceFilterDTO.setOrder(0);
        sourceFilterDTO.setType(FilterType.JAVASCRIPT);
        sourceFilterDTO.setDescription("This is source filter");
        sourceFilters.add(sourceFilterDTO);
        return sourceFilters;
    }

    private Set<SourceConfigDTO> createSourceConfigs() {
        Set<SourceConfigDTO> sourceConfigs = new HashSet<>();
        
        SourceConfigDTO sourceConfig = new SourceConfigDTO();
        sourceConfig.setKey("hostname");
        sourceConfig.setValue("hostname");
        sourceConfigs.add(sourceConfig);

        sourceConfig = new SourceConfigDTO();
        sourceConfig.setKey("port");
        sourceConfig.setValue("9080");
        sourceConfigs.add(sourceConfig);
        return sourceConfigs;
    }

    private List<EndpointMetadataDTO> getSourceMetadaForMandatoryFields() {
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

    private List<EndpointMetadataDTO> getDestinationMetadaForMandatoryFields() {
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

    private List<EndpointMetadataDTO> getSourceMetadaForMandatoryFieldsHTTP() {
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

        return sourceEndpointMetadataDTOs;
    }

    @Test(expected = CustomParameterizedException.class)
    public void testValidateSourceConfigs() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<SourceConfigDTO> sourceConfigs = new HashSet<>();
        dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();

        Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);
        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);

        DataPipelineValidator.validate(null, dataPipelineDTO, endpointMetadataService,dataPipelineRepository,discoveryClient,applicationProperties);
    }

    @Test
    public void testValidateSourceConfigsExceptionMessage() {

        DataPipelineDTO dataPipelineDTO = createEntity();
        Set<SourceConfigDTO> sourceConfigs = new HashSet<>();
        dataPipelineDTO.getSource().setConfigurations(sourceConfigs);
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();

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
        Set<DestinationEndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (DestinationEndpointDTO destination : destinations) {
            Set<DestinationConfigDTO> destinationConfigs = new HashSet<>();
            destination.setConfigurations(destinationConfigs);
        }

        dataPipelineDTO.setDestinations(destinations);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getDestinationMetadaForMandatoryFields();

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
        Set<DestinationEndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (DestinationEndpointDTO destination : destinations) {
            Set<DestinationConfigDTO> destinationConfigs = new HashSet<>();
            destination.setConfigurations(destinationConfigs);
        }

        dataPipelineDTO.setDestinations(destinations);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getDestinationMetadaForMandatoryFields();

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

        SourceFilterDTO sourceFilterDTO = new SourceFilterDTO();
        sourceFilterDTO.setData("Write java script here");
        sourceFilterDTO.setOrder(0);
        sourceFilterDTO.setType(FilterType.JAVASCRIPT);
        sourceFilterDTO.setDescription("This is source filter");

        dataPipelineDTO.getSource().getFilters().add(sourceFilterDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();

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

        SourceTransformerDTO transformerDTO = new SourceTransformerDTO();
        transformerDTO.setOrder(0);
        transformerDTO.setData("Write java script here");
        transformerDTO.setType(TransformerType.JAVASCRIPT);
        transformerDTO.setDescription("This is souce transformer");

        dataPipelineDTO.getSource().getTransformers().add(transformerDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();

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

        DestinationFilterDTO destinationFilterDTO = new DestinationFilterDTO();
        destinationFilterDTO.setOrder(0);
        destinationFilterDTO.setDescription("Destination Filter");
        destinationFilterDTO.setData("Write javascript here");
        destinationFilterDTO.setType(FilterType.JAVASCRIPT);

        Set<DestinationEndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (DestinationEndpointDTO destinationEndpointDTO : destinations) {

            destinationEndpointDTO.getFilters().add(destinationFilterDTO);
        }

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getDestinationMetadaForMandatoryFields();

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

        DestinationTransformerDTO destinationTransformerDTO = new DestinationTransformerDTO();
        destinationTransformerDTO.setOrder(0);
        destinationTransformerDTO.setData("Write javascript here");
        destinationTransformerDTO.setDescription("Destination Transformer");
        destinationTransformerDTO.setType(TransformerType.JAVASCRIPT);

        Set<DestinationEndpointDTO> destinations = dataPipelineDTO.getDestinations();
        for (DestinationEndpointDTO destinationEndpointDTO : destinations) {

            destinationEndpointDTO.getTransformers().add(destinationTransformerDTO);
        }

        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForMandatoryFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getDestinationMetadaForMandatoryFields();

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
        Set<DestinationEndpointDTO>  destinations = dataPipelineDTO.getDestinations();
        
        Set<DestinationEndpointDTO> newdestinations = createDestinationEndpoint(createDestinationConfig(),
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
            DataPipelineDTO dataPipelineDTO = createDataPipelineWithHTTPEndpoint();
            Mockito.when(dataPipelineRepository.existsByNameIgnoreCase(dataPipelineDTO.getName())).thenReturn(false);

            Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsMandatory(EndpointType.HTTP.name(),
                    Category.SOURCE.name(), true)).thenReturn(getSourceMetadaForMandatoryFieldsHTTP());
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
}
