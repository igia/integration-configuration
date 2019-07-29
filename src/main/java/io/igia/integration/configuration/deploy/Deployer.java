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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

@Component
@Transactional
public class Deployer {
    
	private final Logger log = LoggerFactory.getLogger(Deployer.class);

    public void deployUpdatedDataPipeline(DataPipelineDTO oldDataPipelineDTO, DataPipelineDTO newDataPipelineDTO,ActiveMQTopicService activeMQService){
        log.info("Deploy flag of updated Datapipeline {}",newDataPipelineDTO.isDeploy());
        if(newDataPipelineDTO.isDeploy()){
            if(oldDataPipelineDTO.isDeploy()){
                log.info("Undeploy DataPipeline {}",oldDataPipelineDTO.getName());
                undeploy(oldDataPipelineDTO,activeMQService);
            }else{
                log.info("Deploy DataPipeline {}",newDataPipelineDTO.getName());
                deploy(newDataPipelineDTO,activeMQService);
            }
        }else {
            if(oldDataPipelineDTO.isDeploy()){
                log.info("Undeploy DataPipeline {}",oldDataPipelineDTO.getName());
                undeploy(oldDataPipelineDTO,activeMQService);
            }
        }
    }

    public void deploy(DataPipelineDTO newDataPipelineDTO,ActiveMQTopicService activeMQService){
        try{
            newDataPipelineDTO.setDeploy(true);
            newDataPipelineDTO.setState(State.STARTING);
            activeMQService.sendData(newDataPipelineDTO);
        }catch(Exception e){
            log.error("Error while deploying DataPipeline {} {}" ,newDataPipelineDTO.getName(), e);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.MESSAGE_KEY, e.getMessage());
            throw new CustomParameterizedException(ErrorConstants.CAMEL_EXCEPTION, paramMap);
        }
    }
    
    public void undeploy(DataPipelineDTO newDataPipelineDTO,ActiveMQTopicService activeMQService){
        try{
            newDataPipelineDTO.setDeploy(false);
            newDataPipelineDTO.setState(State.STOPPING);
            activeMQService.sendData(newDataPipelineDTO);
        }catch(Exception e){
            log.error("Error while undeploying DataPipeline {} {}" ,newDataPipelineDTO.getName(), e);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(MessageConstants.MESSAGE_KEY, e.getMessage());
            throw new CustomParameterizedException(ErrorConstants.CAMEL_EXCEPTION, paramMap);
        }
    }
}
