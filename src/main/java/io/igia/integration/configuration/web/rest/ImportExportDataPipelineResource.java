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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.igia.integration.configuration.constants.MessageConstants;
import io.igia.integration.configuration.service.ImportExportService;
import io.igia.integration.configuration.service.dto.ImportResponseDTO;

@RestController
@RequestMapping("/api")
public class ImportExportDataPipelineResource {
    
    private final Logger log = LoggerFactory.getLogger(ImportExportDataPipelineResource.class);
    
    private final ImportExportService importExportService;
    
    public ImportExportDataPipelineResource(ImportExportService importExportService){
        this.importExportService = importExportService;
    }
    
    private String getFormattedExportFileDate() {
    	Date currentDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sf.format(currentDate);
    }
    
    @GetMapping("/interfaces/export")
    public ResponseEntity<Resource> exportAllInterfaces(){
        Resource resource = importExportService.exportAll();
        
        String date = getFormattedExportFileDate();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,  MessageConstants.HEADER_CONSTANT_FILE_ATTACHMENT + "data_pipelines_"+date+".json" + "\"")
                .body(resource);
        
    }
    
    @GetMapping("/interfaces/{id}/export")
    public ResponseEntity<Resource> exportInterfaceById(@PathVariable Long id ){
        log.info("Request to export datapipeline with id {}" ,id);
        
        Resource resource = importExportService.exportById(id);
        
        String date = getFormattedExportFileDate();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, MessageConstants.HEADER_CONSTANT_FILE_ATTACHMENT + "data_pipeline_"+id+"_"+date+".json" + "\"")
                .body(resource);
        
    }
    
    @PostMapping("/interfaces/import")
    public ResponseEntity<ImportResponseDTO> importInterface(@RequestParam("file") MultipartFile file) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        ImportResponseDTO resource = importExportService.importInterfaces(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                
                .body(resource);
    }
}
