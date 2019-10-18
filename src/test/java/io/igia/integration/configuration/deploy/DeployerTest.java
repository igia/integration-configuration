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
package io.igia.integration.configuration.deploy;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.FilterType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.domain.enumeration.TransformerType;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.FilterDTO;
import io.igia.integration.configuration.service.dto.TransformerDTO;

@RunWith(MockitoJUnitRunner.class)
public class DeployerTest {
    
    @Mock
    private ActiveMQTopicService activeMQService;
    
    @InjectMocks
    private Deployer deployer;
    
    private DataPipelineDTO createEntity() {

        Set<EndpointConfigDTO> sourceConfigs = createSourceConfigs();

        Set<FilterDTO> sourceFilters = createSourceFilters();

        Set<TransformerDTO> sourceTransformers = createSourceTransformers();

        EndpointDTO sourceEndpoint = createSourceEndpoint(sourceConfigs, sourceFilters, sourceTransformers);

        Set<EndpointConfigDTO> destinationConfigs = createDestinationConfig();

        Set<FilterDTO> destinationFilters = createDestinationFilter();

        Set<TransformerDTO> destinationTransformers = createDestinationTransformer();

        Set<EndpointDTO> destinationEndpoints = createDestinationEndpoint(destinationConfigs,
                destinationFilters, destinationTransformers);

        DataPipelineDTO dataPipeline = createDataPipeline(sourceEndpoint, destinationEndpoints);

        return dataPipeline;
    }
    
    private DataPipelineDTO createDataPipeline(EndpointDTO sourceEndpoint,
            Set<EndpointDTO> destinationEndpoints) {
        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName("MLLP to Source");
        dataPipeline.setDescription("MLLP to Source");
        dataPipeline.setWorkerService("Workerservice1");
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpoints);
        return dataPipeline;
    }

    private Set<EndpointDTO> createDestinationEndpoint(Set<EndpointConfigDTO> destinationConfigs,
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

    private Set<TransformerDTO> createDestinationTransformer() {
        Set<TransformerDTO> destinationTransformers = new HashSet<>();
        TransformerDTO TransformerDTO = new TransformerDTO();
        TransformerDTO.setOrder(0);
        TransformerDTO.setData("Write javascript here");
        TransformerDTO.setDescription("Destination Transformer");
        TransformerDTO.setType(TransformerType.JAVASCRIPT);
        destinationTransformers.add(TransformerDTO);
        return destinationTransformers;
    }

    private Set<FilterDTO> createDestinationFilter() {
        Set<FilterDTO> destinationFilters = new HashSet<>();
        FilterDTO FilterDTO = new FilterDTO();
        FilterDTO.setOrder(0);
        FilterDTO.setDescription("Destination Filter");
        FilterDTO.setData("Write javascript here");
        FilterDTO.setType(FilterType.JAVASCRIPT);
        destinationFilters.add(FilterDTO);
        return destinationFilters;
    }

    private Set<EndpointConfigDTO> createDestinationConfig() {
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

    private EndpointDTO createSourceEndpoint(Set<EndpointConfigDTO> sourceConfigs,
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

    private Set<FilterDTO> createSourceFilters() {
        Set<FilterDTO> sourceFilters = new HashSet<>();
        FilterDTO FilterDTO = new FilterDTO();
        FilterDTO.setData("Write java script here");
        FilterDTO.setOrder(0);
        FilterDTO.setType(FilterType.JAVASCRIPT);
        FilterDTO.setDescription("This is source filter");
        sourceFilters.add(FilterDTO);
        return sourceFilters;
    }

    private Set<EndpointConfigDTO> createSourceConfigs() {
        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        
        EndpointConfigDTO sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("hostname");
        sourceConfig.setValue("hostname");
        sourceConfigs.add(sourceConfig);

        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey("port");
        sourceConfig.setValue("9080");
        sourceConfigs.add(sourceConfig);
        return sourceConfigs;
    }
    
    @Test
    public void testDeploy(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        
        Deployer deployer = mock(Deployer.class);
        
        doNothing().when(deployer).deploy(dataPipelineDTO, activeMQService);
        
        deployer.deploy(dataPipelineDTO, activeMQService);
        
        verify(deployer).deploy(dataPipelineDTO, activeMQService);
    }
    
    @Test(expected = Exception.class)
    public void testDeployException(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        
        Deployer deployer = mock(Deployer.class);
        
        doThrow().when(deployer).deploy(dataPipelineDTO, activeMQService);
        
        deployer.deploy(dataPipelineDTO, activeMQService);
    }
    
    @Test
    public void testUnDeploy(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        
        Deployer deployer = mock(Deployer.class);
        
        doNothing().when(deployer).undeploy(dataPipelineDTO, activeMQService);
        
        deployer.undeploy(dataPipelineDTO, activeMQService);
        
        verify(deployer).undeploy(dataPipelineDTO, activeMQService);
    }
    
    @Test
    public void testDeployUpdatedDataPipeline(){
        DataPipelineDTO dataPipelineDTO = createEntity();
        
        DataPipelineDTO newDataPipelineDTO = createEntity();
        
        Deployer deployer = mock(Deployer.class);
        
        doNothing().when(deployer).deployUpdatedDataPipeline(dataPipelineDTO, newDataPipelineDTO,activeMQService);
        
        deployer.deployUpdatedDataPipeline(dataPipelineDTO, newDataPipelineDTO,activeMQService);
        
        verify(deployer).deployUpdatedDataPipeline(dataPipelineDTO, newDataPipelineDTO,activeMQService);
    }

}
