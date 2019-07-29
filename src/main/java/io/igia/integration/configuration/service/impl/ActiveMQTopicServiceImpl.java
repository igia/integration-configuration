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

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.config.Constants;
import io.igia.integration.configuration.route.dto.Message;
import io.igia.integration.configuration.route.dto.MessageType;
import io.igia.integration.configuration.route.dto.ProvisionMessageSubType;
import io.igia.integration.configuration.route.dto.ProvisionRequestMessageData;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.web.rest.errors.CustomParameterizedException;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;

@Service
@Transactional
public class ActiveMQTopicServiceImpl implements ActiveMQTopicService {

    private final Logger log = LoggerFactory.getLogger(ActiveMQTopicServiceImpl.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void sendData(DataPipelineDTO dataPipelineDTO){
        try {
            Message message = convertToMessage(dataPipelineDTO);
            log.info("Send  Message: {} to endpoint : {}",dataPipelineDTO.getName(), dataPipelineDTO.getWorkerService());
            producerTemplate.sendBodyAndHeader(Constants.OUT_DIRECT_ENDPOINT, message, "topicName", dataPipelineDTO.getWorkerService());
        } catch(Exception e){
            log.error("Error in while sending DataPipeline to Topic", e);
            throw new CustomParameterizedException(e.getMessage(), ErrorConstants.CAMEL_EXCEPTION);
        }
    }

    private Message convertToMessage(DataPipelineDTO dataPipelineDTO){
        Message msg = new Message();
        ProvisionRequestMessageData data = new ProvisionRequestMessageData();
        data.setDataPipeline(dataPipelineDTO);

        msg.setData(data);
        msg.setType(MessageType.PROVISION);
        if(dataPipelineDTO.isDeploy()){
            msg.setSubType(ProvisionMessageSubType.DEPLOY);
        }else{
            msg.setSubType(ProvisionMessageSubType.UNDEPLOY);
        }
        log.info(" Create Provision Request Message for Datapipline {} with deploy flag {} ",dataPipelineDTO.getName(), dataPipelineDTO.isDeploy() );
        return msg;
    }
}
