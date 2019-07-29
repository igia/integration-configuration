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
package io.igia.integration.configuration.listener;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.deploy.Deployer;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.route.dto.HealthCheckMessageStatus;
import io.igia.integration.configuration.route.dto.HealthCheckResponseMessageData;
import io.igia.integration.configuration.route.dto.Message;
import io.igia.integration.configuration.route.dto.MessageType;
import io.igia.integration.configuration.route.dto.ProvisionMessageStatus;
import io.igia.integration.configuration.route.dto.ProvisionMessageSubType;
import io.igia.integration.configuration.route.dto.ProvisionResponseMessageData;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.impl.DataPipelineServiceImpl;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

@Component
@Transactional
public class ActiveMQQueueListener {
    
    private final Logger log = LoggerFactory.getLogger(DataPipelineServiceImpl.class);

    @Autowired
    private DataPipelineService dataPipelineService;

    @Autowired
    private Deployer deployer;

    @Autowired
    private ActiveMQTopicService activeMQService;

    public void updateStatus(DataPipelineDTO dataPipelineDTO){
        State state = dataPipelineDTO.getState();
        log.info("State received from Workerservice {} ",state);

        switch(state){
            case STOPPED:
                dataPipelineService.updateState(dataPipelineDTO, State.STOPPED, false, null);
                break;
            case STARTED:
                dataPipelineService.updateState(dataPipelineDTO, State.STARTED, true, null);
                break;
            case FAILED:
                dataPipelineService.updateState(dataPipelineDTO, dataPipelineDTO.getState(), true, dataPipelineDTO.getReason());
                break;
            default:
                log.error("Invalid state found for data pipeline {}", dataPipelineDTO.getName());
                break;
        }
        log.info("Updated datapipeline {} status successfully", dataPipelineDTO.getName());
    }

    public void resumeDataPipelines(String  workerService) {
        try {
            List<DataPipelineDTO> allDataPipeLines = dataPipelineService.findAllByStateAndWorkerServiceIgnoreCase(State.STARTED, workerService);
            for(DataPipelineDTO oldDataPipelineDTO: allDataPipeLines) {
                log.info("Deploye datapipelines for workerservice {}",workerService);
                deployer.deploy(oldDataPipelineDTO, activeMQService);
            }
        }catch(Exception e) {
             log.error("Exception {}",e);
             throw new CustomParameterizedException(e.getMessage(), ErrorConstants.CAMEL_EXCEPTION);
        }
    }

    public void processMessage(Message message){
        log.info("Received {} Message from Workerservice", message.getType());
        if(message.getType().equals(MessageType.HEALTHCHECK)){
            processHealthCheckMessage(message);
        }else if(message.getType().equals(MessageType.PROVISION)){
            processProvisionMessage(message);
        }else {
            log.error("Invalid Message type received {} ",message.getType());
        }
    }

    private void processHealthCheckMessage(Message message) {
        HealthCheckResponseMessageData hcmd = (HealthCheckResponseMessageData)message.getData();
        HealthCheckMessageStatus status = (HealthCheckMessageStatus)hcmd.getStatus();
        log.info("Received HealthCheck {} message",status);
        if(status.equals(HealthCheckMessageStatus.UP)){
            String workerService = hcmd.getWorkerService();
            resumeDataPipelines(workerService);
        }
    }

    private void processProvisionMessage(Message message) {
        ProvisionResponseMessageData prd = (ProvisionResponseMessageData) message.getData();
        Long dataPipelineId = prd.getDataPipelineId();
        ProvisionMessageStatus status = (ProvisionMessageStatus) prd.getStatus();
        ProvisionMessageSubType subType = (ProvisionMessageSubType) message.getSubType();
        String errorMessage = prd.getErrorMessage();

        Optional<DataPipelineDTO> dto  = dataPipelineService.findOne(dataPipelineId);
        if(dto.isPresent()){
            DataPipelineDTO  dataPipelineDTO = dto.get();
            log.info("Received Provision Status {} and Type {}",status,subType);
            if(status.equals(ProvisionMessageStatus.SUCCESS)){
                if(subType.equals(ProvisionMessageSubType.DEPLOY)){
                    dataPipelineDTO.setState(State.STARTED);
                }else if(subType.equals(ProvisionMessageSubType.UNDEPLOY)){
                    dataPipelineDTO.setState(State.STOPPED);
                }
            }else{
                dataPipelineDTO.setState(State.FAILED);
                dataPipelineDTO.setReason(errorMessage);
            }
            updateStatus(dataPipelineDTO);
        }else {
            log.error("Datapipeline {} not found ",dataPipelineId);
        }
    }
}
