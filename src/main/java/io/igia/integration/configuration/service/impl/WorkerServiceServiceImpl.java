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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.integration.configuration.config.ApplicationProperties;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.WorkerService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.WorkerServiceDTO;

@Service
@Transactional
public class WorkerServiceServiceImpl implements WorkerService {

    private final Logger log = LoggerFactory.getLogger(WorkerServiceServiceImpl.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private DataPipelineService dataPipelineService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public List<WorkerServiceDTO> getWorkerServices() throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        List<String> applications = discoveryClient.getServices().stream()
                .filter(s ->s.toUpperCase().startsWith(applicationProperties.getWorkerServiceNamePrefix().toUpperCase(Locale.ENGLISH))).collect(Collectors.toList());
        List<WorkerServiceDTO> workerServices = new ArrayList<>();

        for (String serviceName : applications) {
            List<DataPipelineDTO> dataPipelineDTOs = dataPipelineService.findAllByWorkerService(serviceName);
            log.info("Get All Datapipelines with workerservice name {} :" ,serviceName);
            Set<DataPipelineDTO> dataPipelines = new HashSet<>(dataPipelineDTOs);
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
            for (ServiceInstance serviceInstance : serviceInstances) {
                WorkerServiceDTO workerServiceDTO = new WorkerServiceDTO();
                workerServiceDTO.setName(serviceInstance.getServiceId());
                workerServiceDTO.setPort(serviceInstance.getPort());
                workerServiceDTO.setDataPipelines(dataPipelines);
                workerServices.add(workerServiceDTO);
            }
        }
        return workerServices;
    }

}
