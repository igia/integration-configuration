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
package io.igia.integration.configuration.encrypt;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.encrypt.DataEncrypt;
import io.igia.integration.configuration.encrypt.EncryptionUtility;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;

@RunWith(MockitoJUnitRunner.class)
public class DataEncryptTest {

    @Mock
    private EndpointMetadataService endpointMetadataService;

    @Mock
    private EncryptionUtility encryptionUtility;

    @Mock
    private ApplicationProperties applicationProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private DataPipelineDTO createEntity() {

        SourceConfigDTO sourceConfig = new SourceConfigDTO();
        sourceConfig.setKey("hostname");
        sourceConfig.setValue("hostname");

        Set<SourceConfigDTO> sourceConfigs = new HashSet<>();
        sourceConfigs.add(sourceConfig);

        sourceConfig = new SourceConfigDTO();
        sourceConfig.setKey("port");
        sourceConfig.setValue("9080");
        sourceConfigs.add(sourceConfig);

        SourceEndpointDTO sourceEndpoint = new SourceEndpointDTO();
        sourceEndpoint.setInDataType(InDataType.HL7_V2);
        sourceEndpoint.setOutDataType(OutDataType.HL7_V2);
        sourceEndpoint.setName("MLLP Source");
        sourceEndpoint.setConfigurations(sourceConfigs);
        sourceEndpoint.setType(EndpointType.MLLP);

        DestinationConfigDTO destinationConfig = new DestinationConfigDTO();
        destinationConfig.setKey("directoryName");
        destinationConfig.setValue("directoryName");

        Set<DestinationConfigDTO> destinationConfigs = new HashSet<>();
        destinationConfigs.add(destinationConfig);

        destinationConfig = new DestinationConfigDTO();
        destinationConfig.setKey("fileName");
        destinationConfig.setValue("fileName");
        destinationConfigs.add(destinationConfig);

        DestinationEndpointDTO destinationEndpoint = new DestinationEndpointDTO();
        destinationEndpoint.setInDataType(InDataType.HL7_V2);
        destinationEndpoint.setOutDataType(OutDataType.HL7_V2);
        destinationEndpoint.setName("FILE Destiantion");
        destinationEndpoint.setConfigurations(destinationConfigs);
        destinationEndpoint.setType(EndpointType.FILE);

        Set<DestinationEndpointDTO> destinationEndpoints = new HashSet<>();
        destinationEndpoints.add(destinationEndpoint);

        DataPipelineDTO dataPipeline = new DataPipelineDTO();
        dataPipeline.setName("MLLP to Source");
        dataPipeline.setDescription("MLLP to Source");
        dataPipeline.setWorkerService("Workerservice1");
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpoints);

        return dataPipeline;
    }

    private List<EndpointMetadataDTO> getSourceMetadaForEncryptedFields() {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.MLLP.name());
        sourceEndpointMetadataDTO.setProperty("hostname");
        sourceEndpointMetadataDTO.setIsEncrypted(true);
        ;

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.SOURCE.name());
        sourceEndpointMetadataDTO.setType(EndpointType.MLLP.name());
        sourceEndpointMetadataDTO.setProperty("port");
        sourceEndpointMetadataDTO.setIsEncrypted(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        return sourceEndpointMetadataDTOs;
    }

    private List<EndpointMetadataDTO> getDestinationMetadaForEncryptedFields() {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.DESTINATION.name());
        sourceEndpointMetadataDTO.setType(EndpointType.FILE.name());
        sourceEndpointMetadataDTO.setProperty("directoryName");
        sourceEndpointMetadataDTO.setIsEncrypted(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        sourceEndpointMetadataDTO = new EndpointMetadataDTO();
        sourceEndpointMetadataDTO.setCategory(Category.DESTINATION.name());
        sourceEndpointMetadataDTO.setType(EndpointType.FILE.name());
        sourceEndpointMetadataDTO.setProperty("fileName");
        sourceEndpointMetadataDTO.setIsEncrypted(true);

        sourceEndpointMetadataDTOs.add(sourceEndpointMetadataDTO);

        return sourceEndpointMetadataDTOs;
    }

    @Test
    public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        DataPipelineDTO dataPipelineDTO = createEntity();
        List<EndpointMetadataDTO> sourceEndpointMetadataDTO = getSourceMetadaForEncryptedFields();
        List<EndpointMetadataDTO> destinationEndpointMetadataDTO = getDestinationMetadaForEncryptedFields();

        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsEncrypted(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(sourceEndpointMetadataDTO);
        Mockito.when(endpointMetadataService.findAllByTypeAndCategoryAndIsEncrypted(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(destinationEndpointMetadataDTO);
        Mockito.when(encryptionUtility.getKey()).thenReturn("this_is_encrypt_key");
        Mockito.when(encryptionUtility.getSecretKeySpec()).thenCallRealMethod();
        Mockito.when(encryptionUtility.encrypt(ArgumentMatchers.anyString())).thenCallRealMethod();
        Mockito.when(encryptionUtility.decrypt(ArgumentMatchers.anyString())).thenCallRealMethod();

        try {

            SourceEndpointDTO sourceEndpointDTO = dataPipelineDTO.getSource();
            Set<SourceConfigDTO> sourceConfigs = sourceEndpointDTO.getConfigurations();
            Map<String, String> map = new HashMap<>();

            for (SourceConfigDTO config : sourceConfigs) {
                map.put(config.getKey(), config.getValue());
            }

            Set<DestinationEndpointDTO> destinationConfigs = dataPipelineDTO.getDestinations();
            for (DestinationEndpointDTO destinationEndpointDTO : destinationConfigs) {
                Set<DestinationConfigDTO> destinationConfigDTOs = destinationEndpointDTO.getConfigurations();
                for (DestinationConfigDTO destinationConfigDTO : destinationConfigDTOs) {
                    map.put(destinationConfigDTO.getKey(), destinationConfigDTO.getValue());
                }
            }

            DataPipelineDTO encryptedDataPipelineDTO = DataEncrypt.encrypt(dataPipelineDTO, endpointMetadataService,
                    encryptionUtility);

            DataPipelineDTO decryptedDataPipelineDTO = DataEncrypt.decrypt(encryptedDataPipelineDTO,
                    endpointMetadataService, encryptionUtility);

            sourceEndpointDTO = decryptedDataPipelineDTO.getSource();
            sourceConfigs = sourceEndpointDTO.getConfigurations();

            Map<String, String> decryptedMap = new HashMap<>();
            for (SourceConfigDTO config : sourceConfigs) {
                decryptedMap.put(config.getKey(), config.getValue());
            }

            destinationConfigs = decryptedDataPipelineDTO.getDestinations();
            for (DestinationEndpointDTO destinationEndpointDTO : destinationConfigs) {
                Set<DestinationConfigDTO> destinationConfigDTOs = destinationEndpointDTO.getConfigurations();
                for (DestinationConfigDTO destinationConfigDTO : destinationConfigDTOs) {
                    decryptedMap.put(destinationConfigDTO.getKey(), destinationConfigDTO.getValue());
                }
            }

            assertThat(map.get("hostname")).isEqualTo(decryptedMap.get("hostname"));
            assertThat(map.get("port")).isEqualTo(decryptedMap.get("port"));
            assertThat(map.get("directoryName")).isEqualTo(decryptedMap.get("directoryName"));
            assertThat(map.get("fileName")).isEqualTo(decryptedMap.get("fileName"));

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException  e) {
            e.printStackTrace();
        }

    }
}
