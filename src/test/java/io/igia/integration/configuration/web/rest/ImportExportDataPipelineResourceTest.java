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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.igia.integration.configuration.IntegrationconfigurationApp;
import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.service.ImportExportService;
import io.igia.integration.configuration.web.rest.ImportExportDataPipelineResource;
import io.igia.integration.configuration.web.rest.errors.ErrorConstants;
import io.igia.integration.configuration.web.rest.errors.ExceptionTranslator;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationconfigurationApp.class)
@WithMockUser(username = "SYSTEM", authorities = { "ROLE_USR" }, password = "SYSTEM")
public class ImportExportDataPipelineResourceTest {
    
    @Autowired
    private ImportExportService importExportService;
    
    private MockMvc restDataPipelineMockMvc;
    
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    
    @Autowired
    private ExceptionTranslator exceptionTranslator;
    
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    
    @MockBean(name = "simpleDiscoveryClient")
    private DiscoveryClient discoveryClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        jacksonMessageConverter.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final ImportExportDataPipelineResource importExportDataPipelineResource = new ImportExportDataPipelineResource(importExportService);
        this.restDataPipelineMockMvc = MockMvcBuilders.standaloneSetup(importExportDataPipelineResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(new ResourceHttpMessageConverter())
                .build();
        
        List<String> workerServices = new ArrayList<>();
        workerServices.add("INTEGRATIONWORKER-I");
        when(discoveryClient.getServices()).thenReturn(workerServices);
    }
    
    @Test(expected = Exception.class)
    @Transactional
    public void testImport() throws Exception{
        
        File file = new File("src/test/resources/json/import.json");
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile  multipartFile = new MockMultipartFile("file", "import.json","application/json", input);
        
        MvcResult result =     (MvcResult) restDataPipelineMockMvc.perform(MockMvcRequestBuilders.multipart("/api/interfaces/import")
                .file(multipartFile)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        
        JsonParser jsonParser = new JsonParser();
        JsonObject json = (JsonObject) jsonParser.parse(response);
        
        assertThat(json.get(MessageConstants.MESSAGE_KEY).getAsString()).isEqualTo(ErrorConstants.ERR_VALIDATION);
        assertThat(json.get("paramMap")).isNotNull();
    }
    
    @Test
    @Transactional
    public void testExport() throws Exception{
       File file = new File("src/test/resources/json/valid_import.json");
       FileInputStream input = new FileInputStream(file);
       MockMultipartFile  multipartFile = new MockMultipartFile("file", "valid_import.json","application/json", input);
        
       restDataPipelineMockMvc.perform(MockMvcRequestBuilders.multipart("/api/interfaces/import")
                .file(multipartFile)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        
       MvcResult export =     (MvcResult) restDataPipelineMockMvc.perform(get("/api/interfaces/export")).andReturn();
        
       JsonParser jsonParser = new JsonParser();
       String exportResult = export.getResponse().getContentAsString();
       JsonArray jsonArray = (JsonArray) jsonParser.parse(exportResult);
       assertThat(jsonArray.size()).isGreaterThan(0);
    }
}
