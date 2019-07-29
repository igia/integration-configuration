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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.service.EndpointMetadataService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;

public class DataEncrypt {

    private DataEncrypt() {
    }

    public static SourceEndpointDTO encryptSourceConfiguration(SourceEndpointDTO sourceEndpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(sourceEndpointDTO.getType().name(), Category.SOURCE.name(),
                        true);

        if (!sourceEndpointMetadataDTOs.isEmpty()) {
            Set<SourceConfigDTO> configs = sourceEndpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (SourceConfigDTO sourceConfigDTO : configs) {
                if (encryptedProperties.contains(sourceConfigDTO.getKey())) {
                    String value = sourceConfigDTO.getValue();
                    String encyptedValue = encryptionUtility.encrypt(value);
                    sourceConfigDTO.setValue(encyptedValue);
                }
            }
        }
        return sourceEndpointDTO;
    }

    public static SourceEndpointDTO decryptSourceConfiguration(SourceEndpointDTO sourceEndpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(sourceEndpointDTO.getType().name(), Category.SOURCE.name(),
                        true);
        if (!sourceEndpointMetadataDTOs.isEmpty()) {
            Set<SourceConfigDTO> configs = sourceEndpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }

            for (SourceConfigDTO sourceConfigDTO : configs) {
                if (encryptedProperties.contains(sourceConfigDTO.getKey())) {
                    String value = sourceConfigDTO.getValue();
                    String dcyptedValue = encryptionUtility.decrypt(value);
                    sourceConfigDTO.setValue(dcyptedValue);
                }
            }
        }
        return sourceEndpointDTO;
    }

    public static DestinationEndpointDTO encryptDestinationConfiguration(DestinationEndpointDTO destinationEndpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> destinationEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(destinationEndpointDTO.getType().name(),
                        Category.DESTINATION.name(), true);
        if (!destinationEndpointMetadataDTOs.isEmpty()) {
            Set<DestinationConfigDTO> configs = destinationEndpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (DestinationConfigDTO destinationConfigDTO : configs) {
                if (encryptedProperties.contains(destinationConfigDTO.getKey())) {
                    String value = destinationConfigDTO.getValue();
                    String encyptedValue = encryptionUtility.encrypt(value);
                    destinationConfigDTO.setValue(encyptedValue);
                }
            }
        }
        return destinationEndpointDTO;
    }

    public static DestinationEndpointDTO decryptDestinationConfiguration(DestinationEndpointDTO destinationEndpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> destinationEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(destinationEndpointDTO.getType().name(),
                        Category.DESTINATION.name(), true);
        if (!destinationEndpointMetadataDTOs.isEmpty()) {
            Set<DestinationConfigDTO> configs = destinationEndpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (DestinationConfigDTO destinationConfigDTO : configs) {
                if (encryptedProperties.contains(destinationConfigDTO.getKey())) {
                    String value = destinationConfigDTO.getValue();
                    String decyptedValue = encryptionUtility.decrypt(value);
                    destinationConfigDTO.setValue(decyptedValue);
                }
            }
        }
        return destinationEndpointDTO;
    }

    public static DataPipelineDTO encrypt(DataPipelineDTO dataPipelineDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        SourceEndpointDTO sourceEndpointDTO = encryptSourceConfiguration(dataPipelineDTO.getSource(),
                endpointMetadataService, encryptionUtility);
        dataPipelineDTO.setSource(sourceEndpointDTO);
        Set<DestinationEndpointDTO> endpointDTOs = new HashSet<>();
        for (DestinationEndpointDTO destinationEndpointDTO : dataPipelineDTO.getDestinations()) {
            DestinationEndpointDTO encDestinationEndpointDTO = encryptDestinationConfiguration(destinationEndpointDTO,
                    endpointMetadataService, encryptionUtility);
            endpointDTOs.add(encDestinationEndpointDTO);
        }
        dataPipelineDTO.setDestinations(endpointDTOs);
        return dataPipelineDTO;
    }

    public static DataPipelineDTO decrypt(DataPipelineDTO dataPipelineDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        SourceEndpointDTO sourceEndpointDTO = decryptSourceConfiguration(dataPipelineDTO.getSource(),
                endpointMetadataService, encryptionUtility);
        dataPipelineDTO.setSource(sourceEndpointDTO);
        Set<DestinationEndpointDTO> endpointDTOs = new HashSet<>();
        for (DestinationEndpointDTO destinationEndpointDTO : dataPipelineDTO.getDestinations()) {
            DestinationEndpointDTO encDestinationEndpointDTO = decryptDestinationConfiguration(destinationEndpointDTO,
                    endpointMetadataService, encryptionUtility);
            endpointDTOs.add(encDestinationEndpointDTO);
        }
        dataPipelineDTO.setDestinations(endpointDTOs);
        return dataPipelineDTO;
    }

}
