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
package io.igia.integration.configuration.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import io.igia.integration.configuration.deploy.Deployer;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.web.rest.errors.BadRequestAlertException;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;
import io.igia.integration.configuration.web.rest.util.HeaderUtil;
import io.igia.integration.configuration.web.rest.util.PaginationUtil;

/**
 * REST controller for managing DataPipeline.
 */
@RestController
@RequestMapping("/api")
public class DataPipelineResource {

    private final Logger log = LoggerFactory.getLogger(DataPipelineResource.class);

    private static final String ENTITY_NAME = "integrationconfigurationDataPipeline";

    private final DataPipelineService dataPipelineService;
    
    private final Deployer deployer;
    
    private final ActiveMQTopicService activeMQService;

    public DataPipelineResource(DataPipelineService dataPipelineService,Deployer deployer,ActiveMQTopicService activeMQService) {
        this.dataPipelineService = dataPipelineService;
        this.deployer = deployer;
        this.activeMQService = activeMQService;
    }

    /**
     * POST  /data-pipelines : Create a new dataPipeline.
     *
     * @param dataPipelineDTO the dataPipelineDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dataPipelineDTO, or with status 400 (Bad Request) if the dataPipeline has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    @PostMapping("/data-pipelines")
    @Timed
    public ResponseEntity<DataPipelineDTO> createDataPipeline(@Valid @RequestBody DataPipelineDTO dataPipelineDTO) throws URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        log.info("REST request to save DataPipeline : {}", dataPipelineDTO.getName());
        if (dataPipelineDTO.getId() != null) {
            throw new BadRequestAlertException("A new dataPipeline cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        DataPipelineDTO result = dataPipelineService.save(dataPipelineDTO);
        if(result.isDeploy()){
            log.info("Deploy DataPipeline : {}", result.getName());
            deployer.deploy(result, activeMQService);
        }
        
        return ResponseEntity.created(new URI("/api/data-pipelines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * GET  /data-pipelines : get all the dataPipelines.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of dataPipelines in body
     */
    @GetMapping("/data-pipelines")
    @Timed
    public ResponseEntity<List<DataPipelineDTO>> getAllDataPipelines(Pageable pageable)throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Page<DataPipelineDTO> page = dataPipelineService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/data-pipelines");
        return  new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * PUT  /data-pipelines : Updates an existing dataPipeline.
     *
     * @param dataPipelineDTO the dataPipelineDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dataPipelineDTO, 
     * or with status 400 (Bad Request) if the dataPipeline is not valid,
     * or with status 500 (Internal Server Error) if the dataPipelineDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    @PutMapping("/data-pipelines")
    @Timed
    public ResponseEntity<DataPipelineDTO> updateDataPipeline(@Valid @RequestBody DataPipelineDTO dataPipelineDTO) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        log.info("REST request to update DataPipeline : {}", dataPipelineDTO.getName());
        if (dataPipelineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        
        Optional<DataPipelineDTO> optionalDataPipelineDTO = dataPipelineService.findOne(dataPipelineDTO.getId());
        if (optionalDataPipelineDTO.isPresent()) {
            DataPipelineDTO oldDataPipelineDTO = optionalDataPipelineDTO.get();
            if(oldDataPipelineDTO.isDeploy() && dataPipelineDTO.isDeploy()){
                throw new CustomParameterizedException(ErrorConstants.ERR_UPDATE_DEPLOYED_PIPELINE, ErrorConstants.ERR_UPDATE_DEPLOYED_PIPELINE); 
            }
            DataPipelineDTO result = dataPipelineService.update(dataPipelineDTO);
            log.info("Deploy DataPipeline : {}", dataPipelineDTO.getName());

            deployer.deployUpdatedDataPipeline(oldDataPipelineDTO, result, activeMQService);
            return ResponseEntity.ok()
	                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
	                .body(result);
        }
        else {
        	throw new CustomParameterizedException(ErrorConstants.DATA_NOT_FOUND, ErrorConstants.DATA_NOT_FOUND); 
        }
    }
    
    /**
     * GET  /data-pipelines : get all the dataPipelines.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of dataPipelines in body
     */
    @GetMapping("/data-pipelines/{id}")
    @Timed
    public ResponseEntity<DataPipelineDTO> getDataPipelines(@PathVariable Long id) {
        log.info("REST request to get DataPipelines {} :" ,id);
        Optional<DataPipelineDTO> dataPipelineDTO = dataPipelineService.findOne(id);
        return  ResponseUtil.wrapOrNotFound(dataPipelineDTO);
    }
    
    /**
     * DELETE /data-pipelines/:id : delete the "id" dataPipeline.
     *
     * @param id
     *            the id of the dataPipelineDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/data-pipelines/{id}")
    @Timed
    public ResponseEntity<Void> deleteDataPipeline(@PathVariable Long id) {
        log.info("REST request to delete DataPipeline : {}", id);
        
        Optional<DataPipelineDTO> dataPipelineDTO = dataPipelineService.findOne(id);
        if (dataPipelineDTO.isPresent()) {
        dataPipelineService.delete(id);
        log.info("Undeploy Datapipeline {} :",dataPipelineDTO.get().getName());
        deployer.undeploy(dataPipelineDTO.get(), activeMQService);
        }
        else {
        	throw new CustomParameterizedException(ErrorConstants.DATA_NOT_FOUND, ErrorConstants.DATA_NOT_FOUND);
        }
        
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
