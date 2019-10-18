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

import static org.assertj.core.api.Assertions.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.encrypt.EncryptionUtility;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.WorkerServiceDTO;

public class WorkerServiceServiceImplTest {

    @InjectMocks
    private WorkerServiceServiceImpl workerServiceServiceImpl;

    @Mock
    private DiscoveryClient discoveryClient;

    @Mock
    private DataPipelineService dataPipelineService;

    @Mock
    private EndpointMetadataService endpointMetadataService;

    @Mock
    private EncryptionUtility encryptionUtility;

    @Mock
    private ApplicationProperties applicationProperties;

    private static final String DEFAULT_WORKER_SERVICE = "integrationworker";

    private static final int DEFAULT_PORT = 9080;

    private static final String DEFAULT_HOST = "host";

    private static final String DEFAULT_SOURCE_HOSTNAME_KEY = "hostname";

    private static final String DEFAULT_SOURCE_HOSTNAME_KEY_VALUE = "hostname";

    private static final String DEFAULT_SOURCE_PORT_KEY = "port";

    private static final String DEFAULT_SOURCE_PORT_KEY_VALUE = "9080";

    private static final String DEFAULT_SOURCE_ENDPOINT_NAME = "MLLP Source";

    private static final String DEFAULT_DESTINATION_DIRECTORY_NAME_KEY = "directoryName";

    private static final String DEFAULT_DESTINATION_DIRECTORY_NAME_KEY_VALUE = "directoryName";

    private static final String DEFAULT_DESTINATION_FILE_NAME_KEY = "fileName";

    private static final String DEFAULT_DESTINATION_FILE_NAME_KEY_VALUE = "fileName";

    private static final String DEFAULT_DESTINATION_ENDPOINT_NAME = "FILE Destiantion";

    private static final String DEFAULT_DATAPIPELINE_NAME = "MLLP to Source";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    public DataPipelineDTO createEntity() {

        EndpointConfigDTO sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey(DEFAULT_SOURCE_HOSTNAME_KEY);
        sourceConfig.setValue(DEFAULT_SOURCE_HOSTNAME_KEY_VALUE);

        Set<EndpointConfigDTO> sourceConfigs = new HashSet<>();
        sourceConfigs.add(sourceConfig);

        sourceConfig = new EndpointConfigDTO();
        sourceConfig.setKey(DEFAULT_SOURCE_PORT_KEY);
        sourceConfig.setValue(DEFAULT_SOURCE_PORT_KEY_VALUE);
        sourceConfigs.add(sourceConfig);

        EndpointDTO sourceEndpoint = new EndpointDTO();
        sourceEndpoint.setInDataType(InDataType.HL7_V2);
        sourceEndpoint.setOutDataType(OutDataType.HL7_V2);
        sourceEndpoint.setName(DEFAULT_SOURCE_ENDPOINT_NAME);
        sourceEndpoint.setConfigurations(sourceConfigs);
        sourceEndpoint.setType(EndpointType.MLLP);

        EndpointConfigDTO destinationConfig = new EndpointConfigDTO();
        destinationConfig.setKey(DEFAULT_DESTINATION_DIRECTORY_NAME_KEY);
        destinationConfig.setValue(DEFAULT_DESTINATION_DIRECTORY_NAME_KEY_VALUE);

        Set<EndpointConfigDTO> destinationConfigs = new HashSet<>();
        destinationConfigs.add(destinationConfig);

        destinationConfig = new EndpointConfigDTO();
        destinationConfig.setKey(DEFAULT_DESTINATION_FILE_NAME_KEY);
        destinationConfig.setValue(DEFAULT_DESTINATION_FILE_NAME_KEY_VALUE);
        destinationConfigs.add(destinationConfig);

        EndpointDTO destinationEndpoint = new EndpointDTO();
        destinationEndpoint.setInDataType(InDataType.HL7_V2);
        destinationEndpoint.setOutDataType(OutDataType.HL7_V2);
        destinationEndpoint.setName(DEFAULT_DESTINATION_ENDPOINT_NAME);
        destinationEndpoint.setConfigurations(destinationConfigs);
        destinationEndpoint.setType(EndpointType.FILE);

        Set<EndpointDTO> destinationEndpoints = new HashSet<>();
        destinationEndpoints.add(destinationEndpoint);

        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName(DEFAULT_DATAPIPELINE_NAME);
        dataPipeline.setDescription(DEFAULT_DATAPIPELINE_NAME);
        dataPipeline.setWorkerService(DEFAULT_WORKER_SERVICE);
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpoints);

        return dataPipeline;
    }

    public List<String> getApplicationList() {
        List<String> applications = new ArrayList<>();
        applications.add(DEFAULT_WORKER_SERVICE);

        return applications;
    }

    public List<ServiceInstance> getServiceInstances() {
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        ServiceInstance serviceInstance = new DefaultServiceInstance(DEFAULT_WORKER_SERVICE, DEFAULT_HOST, DEFAULT_PORT,
                true);
        serviceInstances.add(serviceInstance);

        return serviceInstances;
    }

    public List<DataPipelineDTO> getWorkerServices() {
        List<DataPipelineDTO> dataPipelineDTOs = new ArrayList<>();
        DataPipelineDTO dataPipelineDTO = createEntity();
        dataPipelineDTOs.add(dataPipelineDTO);

        return dataPipelineDTOs;
    }

    @Test
    public void testGetWorkerServices() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        Mockito.when(applicationProperties.getWorkerServiceNamePrefix()).thenReturn(DEFAULT_WORKER_SERVICE);

        Mockito.when(discoveryClient.getServices()).thenReturn(getApplicationList());

        Mockito.when(dataPipelineService.findAllByWorkerService(DEFAULT_WORKER_SERVICE))
                .thenReturn(getWorkerServices());

        Mockito.when(discoveryClient.getInstances(DEFAULT_WORKER_SERVICE)).thenReturn(getServiceInstances());

        List<WorkerServiceDTO> workerServiceDTOs = workerServiceServiceImpl.getWorkerServices();

        assertThat(workerServiceDTOs.size()).isEqualTo(1);
        assertThat(workerServiceDTOs.get(0).getName()).isEqualTo(DEFAULT_WORKER_SERVICE);
        assertThat(workerServiceDTOs.get(0).getPort()).isEqualTo(DEFAULT_PORT);
        assertThat(workerServiceDTOs.get(0).getDataPipelines().size()).isEqualTo(1);

        for (WorkerServiceDTO workerServiceDTO : workerServiceDTOs) {
            Set<DataPipelineDTO> dataPipelineDTOs = workerServiceDTO.getDataPipelines();
            for (DataPipelineDTO dataPipelineDTO : dataPipelineDTOs) {
                assertThat(dataPipelineDTO.getWorkerService()).isEqualTo(DEFAULT_WORKER_SERVICE);
                assertThat(dataPipelineDTO.getName()).isEqualTo(DEFAULT_DATAPIPELINE_NAME);

            }
        }
    }

}
