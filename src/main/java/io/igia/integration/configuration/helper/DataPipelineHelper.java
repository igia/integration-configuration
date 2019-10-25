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
package io.igia.integration.configuration.helper;

import java.time.Instant;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.igia.integration.configuration.domain.DataPipeline;
import io.igia.integration.configuration.domain.DestinationEndpoint;
import io.igia.integration.configuration.domain.Endpoint;
import io.igia.integration.configuration.domain.EndpointConfig;
import io.igia.integration.configuration.domain.Filter;
import io.igia.integration.configuration.domain.ResponseTransformer;
import io.igia.integration.configuration.domain.Transformer;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.EndpointConfigDTO;
import io.igia.integration.configuration.service.dto.EndpointDTO;
import io.igia.integration.configuration.service.dto.FilterDTO;
import io.igia.integration.configuration.service.dto.ResponseTransformerDTO;
import io.igia.integration.configuration.service.dto.TransformerDTO;

@Component
public class DataPipelineHelper {
    
    private final Logger log = LoggerFactory.getLogger(DataPipelineHelper.class);
    
    public DataPipeline createNewDataPipelineWithUpdatedValues(DataPipeline newDataPipeline,
            DataPipeline oldDataPipeline, String userName) {
        
        Instant currentTime = Instant.now();
        
        oldDataPipeline.setDescription(newDataPipeline.getDescription());
        oldDataPipeline.setName(newDataPipeline.getName());
        oldDataPipeline.setModifiedBy(userName);
        oldDataPipeline.setVersion(oldDataPipeline.getVersion());
        if(newDataPipeline.isDeploy()){
            oldDataPipeline.setState(State.STARTING);
        }else{
            if(oldDataPipeline.isDeploy()){
                oldDataPipeline.setState(State.STOPPING);
            }
        }
        oldDataPipeline.setDeploy(newDataPipeline.isDeploy());
        oldDataPipeline.setWorkerService(newDataPipeline.getWorkerService());
        oldDataPipeline.setCreatedOn(oldDataPipeline.getCreatedOn());
        oldDataPipeline.setCreatedBy(oldDataPipeline.getCreatedBy());
        oldDataPipeline.setReason(newDataPipeline.getReason());
        oldDataPipeline.setModifiedOn(currentTime);
        
        oldDataPipeline.getDestinations().clear();
        setDestinationEndpoint(oldDataPipeline,newDataPipeline.getDestinations());
        oldDataPipeline.getDestinations().addAll(newDataPipeline.getDestinations());
        
        setSourceEndpoint(newDataPipeline);
        oldDataPipeline.setSource(newDataPipeline.getSource());
        log.debug("DataPipeline state {} in update {} ",oldDataPipeline.getState(),oldDataPipeline.getName());
        return oldDataPipeline;
    }

    public DataPipeline configDataPipeline(String userName, DataPipeline dataPipeline) {
        dataPipeline.setCreatedBy(userName);
        dataPipeline.setModifiedBy(userName);
        if(dataPipeline.isDeploy()){
            dataPipeline.setState(State.STARTING);
        }else{
            dataPipeline.setState(State.READY);
        }
        dataPipeline.setReason(null);
        
        setDestinationEndpoint(dataPipeline,dataPipeline.getDestinations());
        setSourceEndpoint(dataPipeline);
        
        log.debug("DataPipeline state {} in saving {} ",dataPipeline.getState(),dataPipeline.getName());
        return dataPipeline;
    }

    private void setSourceEndpoint(DataPipeline dataPipeline) {
        Endpoint sourceEndpoint = dataPipeline.getSource();
        if (sourceEndpoint != null) {
            sourceEndpoint.setDataPipeline(dataPipeline);
            for (Filter sourceFilter : sourceEndpoint.getFilters()) {
                sourceFilter.setEndpoint(sourceEndpoint);
                sourceEndpoint.addFilter(sourceFilter);
            }

            for (Transformer sourceTransformer : sourceEndpoint.getTransformers()) {
                sourceTransformer.setEndpoint(sourceEndpoint);
                sourceEndpoint.addTransformer(sourceTransformer);
            }

            for (EndpointConfig sourceConfig : sourceEndpoint.getConfigurations()) {
                sourceConfig.setEndpoint(sourceEndpoint);
                sourceEndpoint.addConfiguration(sourceConfig);
            }
        }
    }

    private void setDestinationEndpoint(DataPipeline dataPipeline , Set<DestinationEndpoint> destinationEndpoints) {
        for (Endpoint destinationEndpoint : destinationEndpoints) {
            destinationEndpoint.setDataPipeline(dataPipeline);
            for (Filter destinationFilter : destinationEndpoint.getFilters()) {
                destinationFilter.setEndpoint(destinationEndpoint);
                destinationEndpoint.addFilter(destinationFilter);
            }
            for (Transformer destinationTransformer : destinationEndpoint.getTransformers()) {
                destinationTransformer.setEndpoint(destinationEndpoint);
                destinationEndpoint.addTransformer(destinationTransformer);
            }
            for (EndpointConfig destinationConfig : destinationEndpoint.getConfigurations()) {
                destinationConfig.setEndpoint(destinationEndpoint);
                destinationEndpoint.addConfiguration(destinationConfig);
            }
            for(ResponseTransformer responseTransformer : destinationEndpoint.getResponseTransformers()){
                responseTransformer.setEndpoint(destinationEndpoint);
                destinationEndpoint.addResponseTransformer(responseTransformer);
            }
        }
    }
    
    public void removeIds(DataPipelineDTO dataPipelineDTO){
        removeSourceIds(dataPipelineDTO);
        removeDestinationIds(dataPipelineDTO);
    }

    private void removeDestinationIds(DataPipelineDTO dataPipelineDTO) {
        for (EndpointDTO de :dataPipelineDTO.getDestinations()){
            de.setId(null);
            
            for (FilterDTO df : de.getFilters()) {
                df.setId(null);
            }
            
            for (TransformerDTO dt : de.getTransformers()) {
                dt.setId(null);
            }
            
            for (EndpointConfigDTO dc : de.getConfigurations()) {
                dc.setId(null);
            }
            for (ResponseTransformerDTO rt : de.getResponseTransformers()) {
                rt.setId(null);
                }
        }
    }

    private void removeSourceIds(DataPipelineDTO dataPipelineDTO) {
        EndpointDTO se =   dataPipelineDTO.getSource();
        if(se != null){
            se.setId(null);
    
            for (EndpointConfigDTO sc : se.getConfigurations()) {
                sc.setId(null);
            }
            
            for (TransformerDTO st : se.getTransformers()) {
                st.setId(null);
            }
            
            for (FilterDTO sf : se.getFilters()) {
                sf.setId(null);
            }
        }
    }
    
}
