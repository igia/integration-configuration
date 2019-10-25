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
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;

public class DataEncrypt {

    private DataEncrypt() {
    }

    public static EndpointDTO encryptSourceConfiguration(EndpointDTO endpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(endpointDTO.getType().name(), Category.SOURCE.name(),
                        true);

        if (!sourceEndpointMetadataDTOs.isEmpty()) {
            Set<EndpointConfigDTO> configs = endpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (EndpointConfigDTO sourceConfigDTO : configs) {
                if (encryptedProperties.contains(sourceConfigDTO.getKey())) {
                    String value = sourceConfigDTO.getValue();
                    String encyptedValue = encryptionUtility.encrypt(value);
                    sourceConfigDTO.setValue(encyptedValue);
                }
            }
        }
        return endpointDTO;
    }

    public static EndpointDTO decryptSourceConfiguration(EndpointDTO endpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(endpointDTO.getType().name(), Category.SOURCE.name(),
                        true);
        if (!sourceEndpointMetadataDTOs.isEmpty()) {
            Set<EndpointConfigDTO> configs = endpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : sourceEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }

            for (EndpointConfigDTO sourceConfigDTO : configs) {
                if (encryptedProperties.contains(sourceConfigDTO.getKey())) {
                    String value = sourceConfigDTO.getValue();
                    String dcyptedValue = encryptionUtility.decrypt(value);
                    sourceConfigDTO.setValue(dcyptedValue);
                }
            }
        }
        return endpointDTO;
    }

    public static EndpointDTO encryptDestinationConfiguration(EndpointDTO endpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> destinationEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(endpointDTO.getType().name(),
                        Category.DESTINATION.name(), true);
        if (!destinationEndpointMetadataDTOs.isEmpty()) {
            Set<EndpointConfigDTO> configs = endpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (EndpointConfigDTO destinationConfigDTO : configs) {
                if (encryptedProperties.contains(destinationConfigDTO.getKey())) {
                    String value = destinationConfigDTO.getValue();
                    String encyptedValue = encryptionUtility.encrypt(value);
                    destinationConfigDTO.setValue(encyptedValue);
                }
            }
        }
        return endpointDTO;
    }

    public static EndpointDTO decryptDestinationConfiguration(EndpointDTO endpointDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        List<EndpointMetadataDTO> destinationEndpointMetadataDTOs = endpointMetadataService
                .findAllByTypeAndCategoryAndIsEncrypted(endpointDTO.getType().name(),
                        Category.DESTINATION.name(), true);
        if (!destinationEndpointMetadataDTOs.isEmpty()) {
            Set<EndpointConfigDTO> configs = endpointDTO.getConfigurations();
            HashSet<String> encryptedProperties = new HashSet<>();
            for (EndpointMetadataDTO endpointMetadataDTO : destinationEndpointMetadataDTOs) {
                encryptedProperties.add(endpointMetadataDTO.getProperty());
            }
            for (EndpointConfigDTO destinationConfigDTO : configs) {
                if (encryptedProperties.contains(destinationConfigDTO.getKey())) {
                    String value = destinationConfigDTO.getValue();
                    String decyptedValue = encryptionUtility.decrypt(value);
                    destinationConfigDTO.setValue(decyptedValue);
                }
            }
        }
        return endpointDTO;
    }

    public static DataPipelineDTO encrypt(DataPipelineDTO dataPipelineDTO,
            EndpointMetadataService endpointMetadataService, EncryptionUtility encryptionUtility)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        EndpointDTO endpointDTO = encryptSourceConfiguration(dataPipelineDTO.getSource(),
                endpointMetadataService, encryptionUtility);
        dataPipelineDTO.setSource(endpointDTO);
        Set<EndpointDTO> endpointDTOs = new HashSet<>();
        for (EndpointDTO destinationEndpointDTO : dataPipelineDTO.getDestinations()) {
            EndpointDTO encDestinationEndpointDTO = encryptDestinationConfiguration(destinationEndpointDTO,
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

        EndpointDTO sourceEndpointDTO = decryptSourceConfiguration(dataPipelineDTO.getSource(),
                endpointMetadataService, encryptionUtility);
        dataPipelineDTO.setSource(sourceEndpointDTO);
        Set<EndpointDTO> endpointDTOs = new HashSet<>();
        for (EndpointDTO destinationEndpointDTO : dataPipelineDTO.getDestinations()) {
            EndpointDTO encDestinationEndpointDTO = decryptDestinationConfiguration(destinationEndpointDTO,
                    endpointMetadataService, encryptionUtility);
            endpointDTOs.add(encDestinationEndpointDTO);
        }
        dataPipelineDTO.setDestinations(endpointDTOs);
        return dataPipelineDTO;
    }

}
