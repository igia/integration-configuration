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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.integration.configuration.domain.EndpointMetadata;
import io.igia.integration.configuration.domain.enumeration.Category;
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.repository.EndpointMetadataRepository;
import io.igia.integration.configuration.service.dto.EndpointMetadataDTO;
import io.igia.integration.configuration.service.impl.EndpointMetadataServiceImpl;
import io.igia.integration.configuration.service.mapper.EndpointMetadataMapper;

public class EndpointMetadataServiceImplTest {

    @InjectMocks
    private EndpointMetadataServiceImpl endpointMetadataServiceImpl;

    @Mock
    private EndpointMetadataRepository endpointMetadataRepository;

    @Mock
    private EndpointMetadataMapper endpointMetadataMapper;

    private static final String CURRENT_USER = "SYSTEM";

    private static final Instant CURRENT_DATE = Instant.now();

    private static final String PROPERTY_HOSTNAME = "hostname";

    private static final String PROPERTY_DIRECTORY_NAME = "directoryName";

    private EndpointMetadataDTO getDestinationEndpointMetadataDTO() {
        EndpointMetadataDTO endpointMetadataDTO = new EndpointMetadataDTO();
        endpointMetadataDTO.setId(1L);
        endpointMetadataDTO.setCreatedBy(CURRENT_USER);
        endpointMetadataDTO.setModifiedBy(CURRENT_USER);
        endpointMetadataDTO.setCreatedDate(CURRENT_DATE);
        endpointMetadataDTO.setModifiedDate(CURRENT_DATE);
        endpointMetadataDTO.setCategory(Category.DESTINATION.name());
        endpointMetadataDTO.setIsMandatory(true);
        endpointMetadataDTO.setIsEncrypted(true);
        endpointMetadataDTO.setProperty(PROPERTY_DIRECTORY_NAME);
        endpointMetadataDTO.setType(EndpointType.FILE.name());
        return endpointMetadataDTO;
    }

    private EndpointMetadata getDestinationEndpointMetadata() {
        EndpointMetadata endpointMetadata = new EndpointMetadata();
        endpointMetadata.setId(1L);
        endpointMetadata.setCategory(Category.DESTINATION.name());
        endpointMetadata.setIsMandatory(true);
        endpointMetadata.setIsEncrypted(true);
        endpointMetadata.setProperty(PROPERTY_DIRECTORY_NAME);
        endpointMetadata.setType(EndpointType.FILE.name());
        endpointMetadata.setCreatedBy(CURRENT_USER);
        endpointMetadata.setModifiedBy(CURRENT_USER);
        endpointMetadata.setCreatedDate(CURRENT_DATE);
        endpointMetadata.setModifiedDate(CURRENT_DATE);
        return endpointMetadata;
    }

    private EndpointMetadataDTO getSourceEndpointMetadaDTO() {
        EndpointMetadataDTO endpointMetadataDTO = new EndpointMetadataDTO();
        endpointMetadataDTO.setId(1L);
        endpointMetadataDTO.setCreatedBy(CURRENT_USER);
        endpointMetadataDTO.setModifiedBy(CURRENT_USER);
        endpointMetadataDTO.setCreatedDate(CURRENT_DATE);
        endpointMetadataDTO.setModifiedDate(CURRENT_DATE);
        endpointMetadataDTO.setCategory(Category.SOURCE.name());
        endpointMetadataDTO.setIsMandatory(true);
        endpointMetadataDTO.setIsEncrypted(true);
        endpointMetadataDTO.setProperty(PROPERTY_HOSTNAME);
        endpointMetadataDTO.setType(EndpointType.MLLP.name());
        return endpointMetadataDTO;
    }

    private EndpointMetadata getSourceEndpointMetadata() {
        EndpointMetadata endpointMetadata = new EndpointMetadata();
        endpointMetadata.setId(1L);
        endpointMetadata.setCategory(Category.SOURCE.name());
        endpointMetadata.setIsMandatory(true);
        endpointMetadata.setIsEncrypted(true);
        endpointMetadata.setProperty(PROPERTY_HOSTNAME);
        endpointMetadata.setType(EndpointType.MLLP.name());
        endpointMetadata.setCreatedBy(CURRENT_USER);
        endpointMetadata.setModifiedBy(CURRENT_USER);
        endpointMetadata.setCreatedDate(CURRENT_DATE);
        endpointMetadata.setModifiedDate(CURRENT_DATE);
        return endpointMetadata;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllByTypeAndCategoryAndIsMandatoryForSource() {

        List<EndpointMetadata> endpointMetadatas = new ArrayList<>();

        EndpointMetadata endpointMetadata = getSourceEndpointMetadata();

        endpointMetadatas.add(endpointMetadata);

        Mockito.when(endpointMetadataRepository.findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(endpointMetadatas);

        List<EndpointMetadataDTO> endpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO endpointMetadataDTO = getSourceEndpointMetadaDTO();

        endpointMetadataDTOs.add(endpointMetadataDTO);

        Mockito.when(endpointMetadataMapper.toDto(endpointMetadata)).thenReturn(endpointMetadataDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataServiceImpl
                .findAllByTypeAndCategoryAndIsMandatory(EndpointType.MLLP.name(), Category.SOURCE.name(), true);

        assertThat(sourceEndpointMetadataDTOs.size()).isEqualTo(1);

        assertThat(sourceEndpointMetadataDTOs.get(0).getCategory()).isEqualTo(Category.SOURCE.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getType()).isEqualTo(EndpointType.MLLP.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getProperty()).isEqualTo(PROPERTY_HOSTNAME);
        assertThat(sourceEndpointMetadataDTOs.get(0).isIsMandatory()).isTrue();
    }

    @Test
    public void testFindAllByTypeAndCategoryAndIsMandatoryForDestination() {
        List<EndpointMetadata> endpointMetadatas = new ArrayList<>();

        EndpointMetadata endpointMetadata = getDestinationEndpointMetadata();

        endpointMetadatas.add(endpointMetadata);

        Mockito.when(endpointMetadataRepository.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(endpointMetadatas);

        List<EndpointMetadataDTO> endpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO endpointMetadataDTO = getDestinationEndpointMetadataDTO();

        endpointMetadataDTOs.add(endpointMetadataDTO);

        Mockito.when(endpointMetadataMapper.toDto(endpointMetadata)).thenReturn(endpointMetadataDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataServiceImpl
                .findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(), Category.DESTINATION.name(), true);

        assertThat(sourceEndpointMetadataDTOs.size()).isEqualTo(1);

        assertThat(sourceEndpointMetadataDTOs.get(0).getCategory()).isEqualTo(Category.DESTINATION.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getType()).isEqualTo(EndpointType.FILE.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getProperty()).isEqualTo(PROPERTY_DIRECTORY_NAME);
        assertThat(sourceEndpointMetadataDTOs.get(0).isIsMandatory()).isTrue();

    }

    @Test
    public void testFindAllByTypeAndCategoryAndIsEncryptedForSource() {

        List<EndpointMetadata> endpointMetadatas = new ArrayList<>();

        EndpointMetadata endpointMetadata = getSourceEndpointMetadata();

        endpointMetadatas.add(endpointMetadata);

        Mockito.when(endpointMetadataRepository.findAllByTypeAndCategoryAndIsEncrypted(EndpointType.MLLP.name(),
                Category.SOURCE.name(), true)).thenReturn(endpointMetadatas);

        List<EndpointMetadataDTO> endpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO endpointMetadataDTO = getSourceEndpointMetadaDTO();

        endpointMetadataDTOs.add(endpointMetadataDTO);

        Mockito.when(endpointMetadataMapper.toDto(endpointMetadata)).thenReturn(endpointMetadataDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataServiceImpl
                .findAllByTypeAndCategoryAndIsEncrypted(EndpointType.MLLP.name(), Category.SOURCE.name(), true);

        assertThat(sourceEndpointMetadataDTOs.size()).isEqualTo(1);

        assertThat(sourceEndpointMetadataDTOs.get(0).getCategory()).isEqualTo(Category.SOURCE.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getType()).isEqualTo(EndpointType.MLLP.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getProperty()).isEqualTo(PROPERTY_HOSTNAME);
        assertThat(sourceEndpointMetadataDTOs.get(0).isIsEncrypted()).isTrue();
    }

    @Test
    public void testFindAllByTypeAndCategoryAndIsEncryptedForDestination() {
        List<EndpointMetadata> endpointMetadatas = new ArrayList<>();

        EndpointMetadata endpointMetadata = getDestinationEndpointMetadata();

        endpointMetadatas.add(endpointMetadata);

        Mockito.when(endpointMetadataRepository.findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(),
                Category.DESTINATION.name(), true)).thenReturn(endpointMetadatas);

        List<EndpointMetadataDTO> endpointMetadataDTOs = new ArrayList<>();

        EndpointMetadataDTO endpointMetadataDTO = getDestinationEndpointMetadataDTO();

        endpointMetadataDTOs.add(endpointMetadataDTO);

        Mockito.when(endpointMetadataMapper.toDto(endpointMetadata)).thenReturn(endpointMetadataDTO);

        List<EndpointMetadataDTO> sourceEndpointMetadataDTOs = endpointMetadataServiceImpl
                .findAllByTypeAndCategoryAndIsMandatory(EndpointType.FILE.name(), Category.DESTINATION.name(), true);

        assertThat(sourceEndpointMetadataDTOs.size()).isEqualTo(1);

        assertThat(sourceEndpointMetadataDTOs.get(0).getCategory()).isEqualTo(Category.DESTINATION.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getType()).isEqualTo(EndpointType.FILE.name());
        assertThat(sourceEndpointMetadataDTOs.get(0).getProperty()).isEqualTo(PROPERTY_DIRECTORY_NAME);
        assertThat(sourceEndpointMetadataDTOs.get(0).isIsEncrypted()).isTrue();

    }

}
