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

import io.igia.integration.configuration.deploy.Deployer;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.FilterType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.domain.enumeration.TransformerType;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.DestinationFilterDTO;
import io.igia.integration.configuration.service.dto.DestinationTransformerDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;
import io.igia.integration.configuration.service.dto.SourceFilterDTO;
import io.igia.integration.configuration.service.dto.SourceTransformerDTO;

@RunWith(MockitoJUnitRunner.class)
public class DeployerTest {
    
    @Mock
    private ActiveMQTopicService activeMQService;
    
    @InjectMocks
    private Deployer deployer;
    
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
    
    private DataPipelineDTO createDataPipeline(SourceEndpointDTO sourceEndpoint,
            Set<DestinationEndpointDTO> destinationEndpoints) {
        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName("MLLP to Source");
        dataPipeline.setDescription("MLLP to Source");
        dataPipeline.setWorkerService("Workerservice1");
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
