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

import static io.igia.integration.configuration.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.igia.integration.configuration.IntegrationconfigurationApp;
import io.igia.integration.configuration.deploy.Deployer;
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
import io.igia.integration.configuration.domain.enumeration.EndpointType;
import io.igia.integration.configuration.domain.enumeration.FilterType;
import io.igia.integration.configuration.domain.enumeration.InDataType;
import io.igia.integration.configuration.domain.enumeration.OutDataType;
import io.igia.integration.configuration.domain.enumeration.State;
import io.igia.integration.configuration.domain.enumeration.TransformerType;
import io.igia.integration.configuration.service.ActiveMQTopicService;
import io.igia.integration.configuration.service.DataPipelineService;
import io.igia.integration.configuration.service.dto.DataPipelineDTO;
import io.igia.integration.configuration.service.mapper.DataPipelineMapper;
import io.igia.integration.configuration.web.rest.DataPipelineResource;
import io.igia.integration.configuration.web.rest.errors.ExceptionTranslator;


/**
 * Test class for the DataPipelineResource REST controller.
 *
 * @see DataPipelineResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationconfigurationApp.class)
@WithMockUser(username = "SYSTEM", authorities = { "ROLE_USR" }, password = "SYSTEM")
public class DataPipelineResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";

    private static final Boolean DEFAULT_DEPLOY = false;

    private static final Long DEFAULT_VERSION = new Long(0);

    private static final State DEFAULT_STATE = State.READY;

    private static final String DEFAULT_WORKER_SERVICE = "INTEGRATIONWORKER-I";

    private static final Instant DEFAULT_CREATED_ON = Instant.now();

    private static final String DEFAULT_CREATED_BY = "SYSTEM";

    private static final Instant DEFAULT_MODIFIED_ON = Instant.now();

    private static final String DEFAULT_MODIFIED_BY = "SYSTEM";

    private static final String KEY_HOSTNAME = "hostname";

    private static final String VALUE_HOSTNAME = "hostname";

    private static final String KEY_PORT = "port";

    private static final String VALUE_PORT = "9080";

    private static final String KEY_DIRECTORY_NAME = "directoryName";

    private static final String VALUE_DIRECTORY_NAME = "directoryName";

    private static final String KEY_FILE_NAME = "fileName";

    private static final String VALUE_FILE_NAME = "fileName";
    
    private static final String DEFAULT_DATA = "JAVA SCRIPT";
    
    private static final Integer DEFAULT_ORDER = 1;
    
    private static final String UPDATED_NAME = "AAAAAAAAAA";
    
    private static final String UPDATED_DESCRIPTION = "BBBBBBB";

    @Autowired
    private DataPipelineMapper dataPipelineMapper;

    @Autowired
    private DataPipelineService dataPipelineService;
    
    @Autowired
    private Deployer deployer;
    
    @Autowired
    private ActiveMQTopicService activeMQService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDataPipelineMockMvc;

    private DataPipeline dataPipeline;
    
    @MockBean(name = "simpleDiscoveryClient")
    private DiscoveryClient discoveryClient;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DataPipelineResource dataPipelineResource = new DataPipelineResource(dataPipelineService,deployer,activeMQService);
        this.restDataPipelineMockMvc = MockMvcBuilders.standaloneSetup(dataPipelineResource)
                .setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
                .build();
        
        List<String> workerServices = new ArrayList<>();
        workerServices.add("INTEGRATIONWORKER-I");
        when(discoveryClient.getServices()).thenReturn(workerServices);
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataPipeline createEntity(EntityManager em) {

        SourceConfig sourceConfig = new SourceConfig().key(KEY_HOSTNAME)
                .value(VALUE_HOSTNAME);

        Set<SourceConfig> sourceConfigs = new HashSet<>();
        sourceConfigs.add(sourceConfig);

        sourceConfig = new SourceConfig().key(KEY_PORT).value(VALUE_PORT);
        sourceConfigs.add(sourceConfig);
        
        Set<SourceFilter> sourceFilters = new HashSet<>();
        SourceFilter sourceFilter = new SourceFilter().data(DEFAULT_DATA).description(DEFAULT_DESCRIPTION)
                .order(DEFAULT_ORDER).type(FilterType.JAVASCRIPT);
        
        sourceFilters.add(sourceFilter);
        
        Set<SourceTransformer> sourceTransformers = new HashSet<>();
        SourceTransformer sourceTransformer = new SourceTransformer().data(DEFAULT_DATA).description(DEFAULT_DESCRIPTION)
                .order(DEFAULT_ORDER).type(TransformerType.JAVASCRIPT);
        sourceTransformers.add(sourceTransformer);
                
        SourceEndpoint sourceEndpoint = new SourceEndpoint().inDataType(InDataType.HL7_V2)
                .outDataType(OutDataType.HL7_V2).name(DEFAULT_NAME).configurations(sourceConfigs)
                .type(EndpointType.MLLP).filters(sourceFilters).transformers(sourceTransformers);

        DestinationConfig destinationConfig = new DestinationConfig()
                .key(KEY_DIRECTORY_NAME).value(VALUE_DIRECTORY_NAME);

        Set<DestinationConfig> destinationConfigs = new HashSet<>();
        destinationConfigs.add(destinationConfig);

        destinationConfig = new DestinationConfig().key(KEY_FILE_NAME)
                .value(VALUE_FILE_NAME);
        destinationConfigs.add(destinationConfig);
        
        Set<DestinationFilter> destinationFilters = new HashSet<>();
        DestinationFilter destinationFilter = new DestinationFilter().data(DEFAULT_DATA).description(DEFAULT_DESCRIPTION)
                .order(DEFAULT_ORDER).type(FilterType.JAVASCRIPT);
        destinationFilters.add(destinationFilter);
        
        Set<DestinationTransformer> destinationTransformers = new HashSet<>();
        DestinationTransformer destinationTransformer = new DestinationTransformer().data(DEFAULT_DATA).description(DEFAULT_DESCRIPTION)
                .order(DEFAULT_ORDER).type(TransformerType.JAVASCRIPT);
        
        destinationTransformers.add(destinationTransformer);
        
        
        Set<ResponseTransformer> responseTransformers = new HashSet<>();
        ResponseTransformer rt = new ResponseTransformer().data(DEFAULT_DATA).description(DEFAULT_DESCRIPTION)
                .order(DEFAULT_ORDER).type(TransformerType.JAVASCRIPT);
        
        responseTransformers.add(rt);

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint()
                .inDataType(InDataType.HL7_V2).outDataType(OutDataType.HL7_V2).name(DEFAULT_NAME)
                .configurations(destinationConfigs).type(EndpointType.FILE).filters(destinationFilters)
                .transformers(destinationTransformers).responseTransformers(responseTransformers);

        Set<DestinationEndpoint> destinationEndpoints = new HashSet<>();
        destinationEndpoints.add(destinationEndpoint);

        DataPipeline dataPipeline = new DataPipeline().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION)
                .deploy(DEFAULT_DEPLOY).version(DEFAULT_VERSION).state(DEFAULT_STATE)
                .workerService(DEFAULT_WORKER_SERVICE).createdOn(DEFAULT_CREATED_ON).createdBy(DEFAULT_CREATED_BY)
                .modifiedOn(DEFAULT_MODIFIED_ON).modifiedBy(DEFAULT_MODIFIED_BY).source(sourceEndpoint)
                .destinations(destinationEndpoints);
        return dataPipeline;
    }

    @Before
    public void initTest() {
        dataPipeline = createEntity(em);
    }

    private DataPipelineDTO getDataPipelineDTOFromResult(MvcResult result) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<DataPipelineDTO>() {
        });
    }
    
    private List<DataPipelineDTO> getDataPipelineDTOsFromResult(MvcResult result) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<DataPipelineDTO>>() {
        });
    }
    
    private void compareDataPipelineDTOs(DataPipelineDTO expectedDTO, Long expectedVersion, DataPipelineDTO actualDTO) {
        assertThat(expectedDTO.getName()).isEqualTo(actualDTO.getName());
        assertThat(expectedDTO.getDescription()).isEqualTo(actualDTO.getDescription());
        assertThat(expectedDTO.getState()).isEqualTo(actualDTO.getState());
        assertThat(expectedDTO.isDeploy()).isEqualTo(actualDTO.isDeploy());
        assertThat(expectedDTO.getWorkerService()).isEqualTo(actualDTO.getWorkerService());
        assertThat(expectedDTO.isDeploy()).isEqualTo(actualDTO.isDeploy());
        
        assertThat(expectedVersion).isEqualTo(actualDTO.getVersion());
        
        assertThat(actualDTO.getSource()).isNotNull();
        assertThat(actualDTO.getSource().getConfigurations().size()).isEqualTo(2);
        assertThat(actualDTO.getSource().getFilters().size()).isEqualTo(1);
        assertThat(actualDTO.getSource().getTransformers().size()).isEqualTo(1);
        
        assertThat(actualDTO.getDestinations()).isNotNull();
        assertThat(actualDTO.getDestinations().size()).isEqualTo(1);
    }
    
    @Test
    public void createDataPipeline() throws Exception {
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline1");
        
        MvcResult result = restDataPipelineMockMvc.perform(post("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO))).andExpect(status().isCreated()).andReturn();
        
        DataPipelineDTO actualDTO = getDataPipelineDTOFromResult(result);
        
        compareDataPipelineDTOs(dataPipelineDTO, new Long(0), actualDTO);
    }    
    
    @Test
    public void updateDataPipeline() throws Exception {
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline2");
        
        //Create pipeline
        MvcResult result = restDataPipelineMockMvc.perform(post("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO))).andExpect(status().isCreated()).andReturn();
        
        DataPipelineDTO actualDTO = getDataPipelineDTOFromResult(result);
        
        compareDataPipelineDTOs(dataPipelineDTO, new Long(0), actualDTO);
        
        //Update pipeline
        actualDTO.setName(UPDATED_NAME);
        actualDTO.setDescription(UPDATED_DESCRIPTION);
        result = restDataPipelineMockMvc.perform(put("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(actualDTO))).andExpect(status().isOk()).andReturn();
        
        DataPipelineDTO updatedDTO = getDataPipelineDTOFromResult(result);
        
        compareDataPipelineDTOs(actualDTO, new Long(1), updatedDTO);
        
        result = restDataPipelineMockMvc.perform(put("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(actualDTO))).andExpect(status().isOk()).andReturn();
        
        updatedDTO = getDataPipelineDTOFromResult(result);
        
        
        compareDataPipelineDTOs(actualDTO, new Long(2), updatedDTO);
   }
    
    @Test
    public void getAllDataPipelines() throws Exception {
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline3");
        
        //Create pipeline
        MvcResult result = restDataPipelineMockMvc.perform(post("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO))).andExpect(status().isCreated()).andReturn();
        
        DataPipelineDTO actualDTO = getDataPipelineDTOFromResult(result);
        
        System.out.println("In get all data pipeline id is " + actualDTO.getId());
        System.out.println("In get all data pipeline version is " + actualDTO.getVersion());
        
       //Get all data pipelines
        result = restDataPipelineMockMvc.perform(get("/api/data-pipelines?sort=id,desc"))
                .andExpect(status().isOk())
                .andReturn();
            
        List<DataPipelineDTO> dataPipelineDTOs = getDataPipelineDTOsFromResult(result);
        assertThat(dataPipelineDTOs.size()).isGreaterThan(0);
        
        compareDataPipelineDTOs(dataPipelineDTO, new Long(0), dataPipelineDTOs.get(0)); 
    }
    
    @Test
    public void getOneDataPipeline() throws Exception {
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline4");
        
        //Create pipeline
        MvcResult result = restDataPipelineMockMvc.perform(post("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO))).andExpect(status().isCreated()).andReturn();
        
        DataPipelineDTO actualDTO = getDataPipelineDTOFromResult(result);
        
        //Find data pipeline by id
        result = restDataPipelineMockMvc.perform(get("/api/data-pipelines/{id}", actualDTO.getId()))
                .andExpect(status().isOk()).andReturn();
            
        actualDTO = getDataPipelineDTOFromResult(result);
        compareDataPipelineDTOs(dataPipelineDTO, new Long(0), actualDTO); 
    }
    
    @Test
    public void deleteDataPipeline() throws Exception {
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline5");
        
        //Create pipeline
        MvcResult result = restDataPipelineMockMvc.perform(post("/api/data-pipelines").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO))).andExpect(status().isCreated()).andReturn();
        
        DataPipelineDTO actualDTO = getDataPipelineDTOFromResult(result);
        compareDataPipelineDTOs(dataPipelineDTO, new Long(0), actualDTO); 
        
        //Delete the pipeline
        restDataPipelineMockMvc.perform(delete("/api/data-pipelines/{id}", actualDTO.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        
        //Assert that the deleted pipeline does not exist
        restDataPipelineMockMvc.perform(get("/api/data-pipelines/{id}", actualDTO.getId()))
        .andExpect(status().isBadRequest()).andReturn();
    }
    
    @Test
    public void updateNonExistingDataPipeline() throws Exception {
        // Create the DataPipeline
        DataPipelineDTO dataPipelineDTO = dataPipelineMapper.toDto(dataPipeline);
        dataPipelineDTO.setName("DataPipeline6");

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataPipelineMockMvc.perform(put("/api/data-pipelines")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataPipelineDTO)))
            .andExpect(status().isBadRequest());
    }
}
