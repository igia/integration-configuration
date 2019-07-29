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
package io.igia.integration.configuration.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;

/**
 * Service Interface for managing DataPipeline.
 */
public interface DataPipelineService {

    /**
     * Save a dataPipeline.
     *
     * @param dataPipelineDTO the entity to save
     * @return the persisted entity
     */
    DataPipelineDTO save(DataPipelineDTO dataPipelineDTO)throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;

    /**
     * Get all the dataPipelines.
     *
     * @return the list of entities
     */
    List<DataPipelineDTO> findAll() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
    
    
    List<DataPipelineDTO> findAllByWorkerService(String workerService) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
    
    List<DataPipelineDTO> findAllByStateAndWorkerServiceIgnoreCase(State state, String workerService) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
    
    
    DataPipelineDTO update(DataPipelineDTO dataPipelineDTO)throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
    
    Optional<DataPipelineDTO> findOne(Long id);

    void delete(Long id);

    Page<DataPipelineDTO> findAll(Pageable pageable) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
    
    public void updateState(DataPipelineDTO newDataPipelineDTO, State newState, Boolean deploy, String reason);

}
