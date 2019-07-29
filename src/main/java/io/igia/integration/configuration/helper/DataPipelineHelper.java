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
import io.igia.integration.configuration.domain.DestinationConfig;
import io.igia.integration.configuration.domain.DestinationEndpoint;
import io.igia.integration.configuration.domain.DestinationFilter;
import io.igia.integration.configuration.domain.DestinationTransformer;
import io.igia.integration.configuration.domain.ResponseTransformer;
import io.igia.integration.configuration.domain.SourceConfig;
import io.igia.integration.configuration.domain.SourceEndpoint;
import io.igia.integration.configuration.domain.SourceFilter;
import io.igia.integration.configuration.domain.SourceTransformer;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;
import io.igia.integration.configuration.service.dto.DestinationEndpointDTO;
import io.igia.integration.configuration.service.dto.DestinationFilterDTO;
import io.igia.integration.configuration.service.dto.DestinationTransformerDTO;
import io.igia.integration.configuration.service.dto.ResponseTransformerDTO;
import io.igia.integration.configuration.service.dto.SourceConfigDTO;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;
import io.igia.integration.configuration.service.dto.SourceFilterDTO;
import io.igia.integration.configuration.service.dto.SourceTransformerDTO;

@Component
public class DataPipelineHelper {
    
    private final Logger log = LoggerFactory.getLogger(DataPipelineHelper.class);
    
    public DataPipeline createNewDataPipelineWithUpdatedValues(DataPipeline newDataPipeline,
            DataPipeline oldDataPipeline, String userName) {
        
        Instant currentTime = Instant.now();
        
        oldDataPipeline.setDescription(newDataPipeline.getDescription());
        oldDataPipeline.setName(newDataPipeline.getName());
        oldDataPipeline.setModifiedBy(userName);
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
        SourceEndpoint sourceEndpoint = dataPipeline.getSource();
        if (sourceEndpoint != null) {
            
            for (SourceFilter sourceFilter : sourceEndpoint.getFilters()) {
                sourceFilter.setSourceEndpoint(sourceEndpoint);
                sourceEndpoint.addFilter(sourceFilter);
            }

            for (SourceTransformer sourceTransformer : sourceEndpoint.getTransformers()) {
                sourceTransformer.setSourceEndpoint(sourceEndpoint);
                sourceEndpoint.addTransformer(sourceTransformer);
            }

            for (SourceConfig sourceConfig : sourceEndpoint.getConfigurations()) {
                sourceConfig.setSourceEndpoint(sourceEndpoint);
                sourceEndpoint.addConfiguration(sourceConfig);
            }
        }
    }

    private void setDestinationEndpoint(DataPipeline dataPipeline , Set<DestinationEndpoint> destinationEndpoints) {
        for (DestinationEndpoint destinationEndpoint : destinationEndpoints) {
            destinationEndpoint.setDataPipeline(dataPipeline);
            for (DestinationFilter destinationFilter : destinationEndpoint.getFilters()) {
                destinationFilter.setDestinationEndpoint(destinationEndpoint);
                destinationEndpoint.addFilter(destinationFilter);
            }
            for (DestinationTransformer destinationTransformer : destinationEndpoint.getTransformers()) {
                destinationTransformer.setDestinationEndpoint(destinationEndpoint);
                destinationEndpoint.addTransformer(destinationTransformer);
            }
            for (DestinationConfig destinationConfig : destinationEndpoint.getConfigurations()) {
                destinationConfig.setDestinationEndpoint(destinationEndpoint);
                destinationEndpoint.addConfiguration(destinationConfig);
            }
            for(ResponseTransformer responseTransformer : destinationEndpoint.getResponseTransformers()){
                responseTransformer.setDestinationEndpoint(destinationEndpoint);
                destinationEndpoint.addResponseTransformer(responseTransformer);
            }
        }
    }
    
    public void removeIds(DataPipelineDTO dataPipelineDTO){
        removeSourceIds(dataPipelineDTO);
        removeDestinationIds(dataPipelineDTO);
    }

    private void removeDestinationIds(DataPipelineDTO dataPipelineDTO) {
        for (DestinationEndpointDTO de :dataPipelineDTO.getDestinations()){
            de.setId(null);
            
            for (DestinationFilterDTO df : de.getFilters()) {
                df.setId(null);
            }
            
            for (DestinationTransformerDTO dt : de.getTransformers()) {
                dt.setId(null);
            }
            
            for (DestinationConfigDTO dc : de.getConfigurations()) {
                dc.setId(null);
            }
            
            for (ResponseTransformerDTO rt : de.getResponseTransformers()) {
                rt.setId(null);
            }
        }
    }

    private void removeSourceIds(DataPipelineDTO dataPipelineDTO) {
        SourceEndpointDTO se =   dataPipelineDTO.getSource();
        if(se != null){
            se.setId(null);
    
            for (SourceConfigDTO sc : se.getConfigurations()) {
                sc.setId(null);
            }
            
            for (SourceTransformerDTO st : se.getTransformers()) {
                st.setId(null);
            }
            
            for (SourceFilterDTO sf : se.getFilters()) {
                sf.setId(null);
            }
        }
    }
    
}
