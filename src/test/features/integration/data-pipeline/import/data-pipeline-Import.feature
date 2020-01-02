#
# This Source Code Form is subject to the terms of the Mozilla Public License, v.
# 2.0 with a Healthcare Disclaimer.
# A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
# be found under the top level directory, named LICENSE.
# If a copy of the MPL was not distributed with this file, You can obtain one at
# http://mozilla.org/MPL/2.0/.
# If a copy of the Healthcare Disclaimer was not distributed with this file, You
# can obtain one at the project website https://github.com/igia.
#
# Copyright (C) 2018-2019 Persistent Systems, Inc.
#

Feature: CRUD operation on Data pipeline resource

    Background:
        * def signIn = call read('classpath:login.feature')
        * def accessToken = signIn.accessToken
        * url baseUrl
        * def now = 
          """
        function() {
                var text = Math.random().toString(36).substring(7);
                return text;
        }
         """
        * def sleep =
        """
        function(seconds){
            for(i = 0; i <= seconds; i++)
            {
            java.lang.Thread.sleep(1*1000);
            karate.log(i);
            }
        }
        """

@Import
Scenario: Import the undeployed pipelines with filters and transformers.
         * def tempJSON = read('classpath:integration/data-pipeline/import/import-response-undeploy-with-FT.json')
         * def resImport = read('classpath:integration/data-pipeline/import/import-response-undeploy-with-FT.json')
         
         * set resImport.success[0].dataPipeline.source.configurations = '#(^^tempJSON.success[0].dataPipeline.source.configurations)'
         * set resImport.success[0].dataPipeline.source.filters = '#(^^tempJSON.success[0].dataPipeline.source.filters)'
         * set resImport.success[0].dataPipeline.source.transformers = '#(^^tempJSON.success[0].dataPipeline.source.transformers)'
         * set resImport.success[0].dataPipeline.destinations[0].configurations = '#(^^tempJSON.success[0].dataPipeline.destinations[0].configurations)'
         * set resImport.success[0].dataPipeline.destinations[0].filters = '#(^^tempJSON.success[0].dataPipeline.destinations[0].filters)'
         * set resImport.success[0].dataPipeline.destinations[0].transformers = '#(^^tempJSON.success[0].dataPipeline.destinations[0].transformers)'
         * set resImport.success[0].dataPipeline.destinations[0].responseTransformers = '#(^^tempJSON.success[0].dataPipeline.destinations[0].responseTransformers)'
         * set resImport.success[0].dataPipeline.auditMessages = '#(^^tempJSON.success[0].dataPipeline.auditMessages)'
         
         * set resImport.success[1].dataPipeline.source.configurations = '#(^^tempJSON.success[1].dataPipeline.source.configurations)'
         * set resImport.success[1].dataPipeline.source.filters = '#(^^tempJSON.success[1].dataPipeline.source.filters)'
         * set resImport.success[1].dataPipeline.source.transformers = '#(^^tempJSON.success[1].dataPipeline.source.transformers)'
         * set resImport.success[1].dataPipeline.destinations[0].configurations = '#(^^tempJSON.success[1].dataPipeline.destinations[0].configurations)'
         * set resImport.success[1].dataPipeline.destinations[0].filters = '#(^^tempJSON.success[1].dataPipeline.destinations[0].filters)'
         * set resImport.success[1].dataPipeline.destinations[0].transformers = '#(^^tempJSON.success[1].dataPipeline.destinations[0].transformers)'
         * set resImport.success[1].dataPipeline.destinations[0].responseTransformers = '#(^^tempJSON.success[1].dataPipeline.destinations[0].responseTransformers)'
         * set resImport.success[1].dataPipeline.auditMessages = '#(^^tempJSON.success[0].dataPipeline.auditMessages)'
         
         
    Given path '/interfaces/import'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:integration/data-pipeline/import/pipeline-undeploy-FT.json', filename: 'pipeline-undeploy-FT.json', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response == resImport
        * def pipelineId_1 = response.success[0].dataPipeline.id;
        * def pipelineId_2 = response.success[1].dataPipeline.id; 

        Given path '/data-pipelines/' + pipelineId_1
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200
   
        Given path '/data-pipelines/' + pipelineId_1
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400

        Given path '/data-pipelines/' + pipelineId_2
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200
   
        Given path '/data-pipelines/' + pipelineId_2
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400

@Import
 Scenario: Import the deployed pipelines with filters and transformers.
        * def tempJSON = read('classpath:integration/data-pipeline/import/import-response-deploy-with-FT.json')
        * def resImport = read('classpath:integration/data-pipeline/import/import-response-deploy-with-FT.json')
        
         * set resImport.failure[0].dataPipelineDTO.source.configurations = '#(^^tempJSON.failure[0].dataPipelineDTO.source.configurations)'
         * set resImport.failure[0].dataPipelineDTO.source.filters = '#(^^tempJSON.failure[0].dataPipelineDTO.source.filters)'
         * set resImport.failure[0].dataPipelineDTO.source.transformers = '#(^^tempJSON.failure[0].dataPipelineDTO.source.transformers)'
         * set resImport.failure[0].dataPipelineDTO.destinations[0].configurations = '#(^^tempJSON.failure[0].dataPipelineDTO.destinations[0].configurations)'
         * set resImport.failure[0].dataPipelineDTO.destinations[0].filters = '#(^^tempJSON.failure[0].dataPipelineDTO.destinations[0].filters)'
         * set resImport.failure[0].dataPipelineDTO.destinations[0].transformers = '#(^^tempJSON.failure[0].dataPipelineDTO.destinations[0].transformers)'
         * set resImport.failure[0].dataPipelineDTO.destinations[0].responseTransformers = '#(^^tempJSON.failure[0].dataPipelineDTO.destinations[0].responseTransformers)'
         * set resImport.failure[0].dataPipelineDTO.auditMessages = '#(^^tempJSON.failure[0].dataPipelineDTO.auditMessages)'

         * set resImport.failure[1].dataPipelineDTO.source.configurations = '#(^^tempJSON.failure[1].dataPipelineDTO.source.configurations)'
         * set resImport.failure[1].dataPipelineDTO.source.filters = '#(^^tempJSON.failure[1].dataPipelineDTO.source.filters)'
         * set resImport.failure[1].dataPipelineDTO.source.transformers = '#(^^tempJSON.failure[1].dataPipelineDTO.source.transformers)'
         * set resImport.failure[1].dataPipelineDTO.destinations[0].configurations = '#(^^tempJSON.failure[1].dataPipelineDTO.destinations[0].configurations)'
         * set resImport.failure[1].dataPipelineDTO.destinations[0].filters = '#(^^tempJSON.failure[1].dataPipelineDTO.destinations[0].filters)'
         * set resImport.failure[1].dataPipelineDTO.destinations[0].transformers = '#(^^tempJSON.failure[1].dataPipelineDTO.destinations[0].transformers)'
         * set resImport.failure[1].dataPipelineDTO.destinations[0].responseTransformers = '#(^^tempJSON.failure[1].dataPipelineDTO.destinations[0].responseTransformers)'
         * set resImport.failure[1].dataPipelineDTO.auditMessages = '#(^^tempJSON.failure[0].dataPipelineDTO.auditMessages)'
         
    Given path '/interfaces/import'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:integration/data-pipeline/import/pipeline-deploy-FT.json', filename: 'pipeline-deploy-FT.json', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response == resImport

@Import
Scenario: Import pipelines using empty file and file contains only [].
         * def resImport = read('classpath:integration/data-pipeline/import/import-response-empty-file.json')
    Given path '/interfaces/import'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:integration/data-pipeline/import/empty-file.json', filename: 'empty-file.json', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response == resImport

@ImportRAW
Scenario: Import pipeline with RAW as input and/or output data type
         * def tempJSON = read('classpath:integration/data-pipeline/import/import-datapipeline-with-raw-datatyep.json')
         * def resImport = read('classpath:integration/data-pipeline/import/import-datapipeline-with-raw-datatyep.json')

         * set resImport.success[0].dataPipeline.source.configurations = '#(^^tempJSON.success[0].dataPipeline.source.configurations)'
         * set resImport.success[0].dataPipeline.source.filters = '#(^^tempJSON.success[0].dataPipeline.source.filters)'
         * set resImport.success[0].dataPipeline.source.transformers = '#(^^tempJSON.success[0].dataPipeline.source.transformers)'
         * set resImport.success[0].dataPipeline.destinations[0].configurations = '#(^^tempJSON.success[0].dataPipeline.destinations[0].configurations)'
         * set resImport.success[0].dataPipeline.destinations[0].filters = '#(^^tempJSON.success[0].dataPipeline.destinations[0].filters)'
         * set resImport.success[0].dataPipeline.destinations[0].transformers = '#(^^tempJSON.success[0].dataPipeline.destinations[0].transformers)'
         * set resImport.success[0].dataPipeline.destinations[0].responseTransformers = '#(^^tempJSON.success[0].dataPipeline.destinations[0].responseTransformers)'
         * set resImport.success[0].dataPipeline.auditMessages = '#(^^tempJSON.success[0].dataPipeline.auditMessages)'

          Given path '/interfaces/import'
              And header Authorization = 'Bearer ' + accessToken
              And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
              And multipart file file = { read: 'classpath:integration/data-pipeline/import/pipeline-with-raw.json', filename: 'pipeline-with-raw.json', contentType:'form-data'}
              And request {}
              When method post
              Then status 200
              Then match response == resImport

          * def pipelineId = response.success[0].dataPipeline.id

          Given path '/data-pipelines/' + pipelineId
              And header Authorization = 'Bearer ' + accessToken
              When method delete
              Then status 200

