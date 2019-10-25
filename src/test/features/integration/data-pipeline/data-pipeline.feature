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

@TestMLLP
Scenario: Create MLLP to FILE data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-mllp-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" +now()
        * set reqCreate.destinations[0].name = "Destination-" +now()
        
        * set reqCreate.source.configurations[0].value = mllpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = mllpConfigs.inputPort
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputFileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-mllp-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-mllp-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        
        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 
    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
        
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate
        

        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"
        
   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

    * def resExport = read('classpath:integration/data-pipeline/export-response-not-found.json')
    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resExport

@TestFILE
 Scenario: Create FILE to FILE data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
       * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" +now()
        * set reqCreate.destinations[0].name = "Destination-" +now()
        
        * set reqCreate.source.configurations[0].value = fileConfigs.inputDirectoryPath
        * set reqCreate.source.configurations[1].value = fileConfigs.inputCSVFileName
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputHL7FileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
        

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate


        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;
 
   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

    * def resExport = read('classpath:integration/data-pipeline/export-response-not-found.json')
    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resExport

@TestFILE
 Scenario: Create FILE to MLLP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
         * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-mllp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = fileConfigs.inputFileName
        * set reqCreate.source.configurations[1].value = fileConfigs.inputDirectoryPath
        * set reqCreate.destinations[0].configurations[0].value = mllpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = mllpConfigs.outputPort
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-mllp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-mllp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate


        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

   * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;
 
   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

    * def resExport = read('classpath:integration/data-pipeline/export-response-not-found.json')
    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resExport

@TestFILE
 Scenario: Create FILE to HTTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
         * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-http-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = fileConfigs.inputFileName
        * set reqCreate.source.configurations[1].value = fileConfigs.inputDirectoryPath
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-http-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-http-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate

        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

  * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

    * def resExport = read('classpath:integration/data-pipeline/export-response-not-found.json')
    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resExport

@TestFILE
 Scenario: Create FILE to HTTPS data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
         * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-https-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = fileConfigs.inputFileName
        * set reqCreate.source.configurations[1].value = fileConfigs.inputDirectoryPath
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-https-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-https-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate

        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

  * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

    * def resExport = read('classpath:integration/data-pipeline/export-response-not-found.json')
    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resExport


@TestHTTP
 Scenario: Create HTTPS to FILE data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.
        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputFileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-https-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-https-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound
        
@TestHTTP
 Scenario: Create HTTPS to HTTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-http-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
        
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-https-http-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-https-http-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound


@TestHTTP
 Scenario: Create HTTPS to HTTP data pipeline fails that has isSecure property missing

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-http-deploy-with-missing-property.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
        * def resCreate =  read('classpath:integration/data-pipeline/missing-property.json')

        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 400
        Then match response == resCreate

@TestHTTP
 Scenario: Create HTTPS to HTTP data pipeline fails that has isSecure property invalid in the source configuration

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-http-deploy-with-isSecure-false.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
        * def resCreate =  read('classpath:integration/data-pipeline/invalid-isSecure-value.json')

        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 400
        Then match response == resCreate

@TestHTTP
 Scenario: Create HTTPS to HTTPS data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

 * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-https-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
        
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-https-https-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-https-https-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound


@TestHTTP
 Scenario: Create HTTPS to SFTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-sftp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = sftpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = sftpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = sftpConfigs.outputUsername
        * set reqCreate.destinations[0].configurations[3].value = sftpConfigs.outputPassword
        * set reqCreate.destinations[0].configurations[4].value = sftpConfigs.outputDirectory
        * set reqCreate.destinations[0].configurations[5].value = sftpConfigs.outputFileName
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-https-sftp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-https-sftp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value
        * set tempJSON.destinations[0].configurations[3].value = reqCreate.destinations[0].configurations[3].value
        * set tempJSON.destinations[0].configurations[4].value = reqCreate.destinations[0].configurations[4].value
        * set tempJSON.destinations[0].configurations[5].value = reqCreate.destinations[0].configurations[5].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
        

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestHTTP
 Scenario: Create HTTPS to MLLP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-https-mllp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = httpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = httpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = httpConfigs.inputUri
        * set reqCreate.destinations[0].configurations[0].value = mllpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = mllpConfigs.outputPort
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-https-mllp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-https-mllp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestFILE
 Scenario: Create FILE to SFTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-sftp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = fileConfigs.inputFileName
        * set reqCreate.source.configurations[1].value = fileConfigs.inputDirectoryPath
        * set reqCreate.destinations[0].configurations[0].value = sftpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = sftpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = sftpConfigs.outputUsername
        * set reqCreate.destinations[0].configurations[3].value = sftpConfigs.outputPassword
        * set reqCreate.destinations[0].configurations[4].value = sftpConfigs.outputDirectory
        * set reqCreate.destinations[0].configurations[5].value = sftpConfigs.outputFileName
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-sftp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-sftp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value
        * set tempJSON.destinations[0].configurations[3].value = reqCreate.destinations[0].configurations[3].value
        * set tempJSON.destinations[0].configurations[4].value = reqCreate.destinations[0].configurations[4].value
        * set tempJSON.destinations[0].configurations[5].value = reqCreate.destinations[0].configurations[5].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestMLLP
 Scenario: Create MLLP to SFTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-mllp-sftp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = mllpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = mllpConfigs.inputPort
        * set reqCreate.destinations[0].configurations[0].value = sftpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = sftpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = sftpConfigs.outputUsername
        * set reqCreate.destinations[0].configurations[3].value = sftpConfigs.outputPassword
        * set reqCreate.destinations[0].configurations[4].value = sftpConfigs.outputDirectory
        * set reqCreate.destinations[0].configurations[5].value = sftpConfigs.outputFileName
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-mllp-sftp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-mllp-sftp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value
        * set tempJSON.destinations[0].configurations[3].value = reqCreate.destinations[0].configurations[3].value
        * set tempJSON.destinations[0].configurations[4].value = reqCreate.destinations[0].configurations[4].value
        * set tempJSON.destinations[0].configurations[5].value = reqCreate.destinations[0].configurations[5].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestMLLP
 Scenario: Create MLLP to HTTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-mllp-http-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = mllpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = mllpConfigs.inputPort
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-mllp-http-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-mllp-http-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound


@TestMLLP
 Scenario: Create MLLP to HTTPS data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-mllp-https-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = mllpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = mllpConfigs.inputPort
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-mllp-https-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-mllp-https-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound


@TestMLLP
 Scenario: Create MLLP to MLLP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-mllp-mllp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = mllpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = mllpConfigs.inputPort
        * set reqCreate.destinations[0].configurations[0].value = mllpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = mllpConfigs.outputPort
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-mllp-mllp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-mllp-mllp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 30

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound


@TestSFTP
 Scenario: Create SFTP to SFTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-sftp-sftp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = sftpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = sftpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = sftpConfigs.inputUsername
        * set reqCreate.source.configurations[3].value = sftpConfigs.inputPassword
        * set reqCreate.source.configurations[4].value = sftpConfigs.inputDirectory
        * set reqCreate.source.configurations[5].value = sftpConfigs.inputFileName
        * set reqCreate.destinations[0].configurations[0].value = sftpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = sftpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = sftpConfigs.outputUsername
        * set reqCreate.destinations[0].configurations[3].value = sftpConfigs.outputPassword
        * set reqCreate.destinations[0].configurations[4].value = sftpConfigs.outputDirectory
        * set reqCreate.destinations[0].configurations[5].value = sftpConfigs.outputFileName
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-sftp-sftp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-sftp-sftp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.source.configurations[3].value = reqCreate.source.configurations[3].value
        * set tempJSON.source.configurations[4].value = reqCreate.source.configurations[4].value
        * set tempJSON.source.configurations[5].value = reqCreate.source.configurations[5].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value
        * set tempJSON.destinations[0].configurations[3].value = reqCreate.destinations[0].configurations[3].value
        * set tempJSON.destinations[0].configurations[4].value = reqCreate.destinations[0].configurations[4].value
        * set tempJSON.destinations[0].configurations[5].value = reqCreate.destinations[0].configurations[5].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 120
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 240
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestSFTP
 Scenario: Create SFTP to MLLP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-sftp-mllp-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = sftpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = sftpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = sftpConfigs.inputUsername
        * set reqCreate.source.configurations[3].value = sftpConfigs.inputPassword
        * set reqCreate.source.configurations[4].value = sftpConfigs.inputDirectory
        * set reqCreate.source.configurations[5].value = sftpConfigs.inputFileName
        * set reqCreate.destinations[0].configurations[0].value = mllpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = mllpConfigs.outputPort
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-sftp-mllp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-sftp-mllp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.source.configurations[3].value = reqCreate.source.configurations[3].value
        * set tempJSON.source.configurations[4].value = reqCreate.source.configurations[4].value
        * set tempJSON.source.configurations[5].value = reqCreate.source.configurations[5].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 120
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 240
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestSFTP
 Scenario: Create SFTP to FILE data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-sftp-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = sftpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = sftpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = sftpConfigs.inputUsername
        * set reqCreate.source.configurations[3].value = sftpConfigs.inputPassword
        * set reqCreate.source.configurations[4].value = sftpConfigs.inputDirectory
        * set reqCreate.source.configurations[5].value = sftpConfigs.inputFileName
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputFileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-sftp-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-sftp-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.source.configurations[3].value = reqCreate.source.configurations[3].value
        * set tempJSON.source.configurations[4].value = reqCreate.source.configurations[4].value
        * set tempJSON.source.configurations[5].value = reqCreate.source.configurations[5].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
    
        * def pipelineId = response.id
        * call sleep 120
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 240
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestSFTP
 Scenario: Create SFTP to HTTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-sftp-http-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = sftpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = sftpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = sftpConfigs.inputUsername
        * set reqCreate.source.configurations[3].value = sftpConfigs.inputPassword
        * set reqCreate.source.configurations[4].value = sftpConfigs.inputDirectory
        * set reqCreate.source.configurations[5].value = sftpConfigs.inputFileName
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-sftp-http-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-sftp-http-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.source.configurations[3].value = reqCreate.source.configurations[3].value
        * set tempJSON.source.configurations[4].value = reqCreate.source.configurations[4].value
        * set tempJSON.source.configurations[5].value = reqCreate.source.configurations[5].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
        
    
        * def pipelineId = response.id
        * call sleep 120
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 240
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@TestSFTP
 Scenario: Create SFTP to HTTPS data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-sftp-https-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" + now()
        * set reqCreate.destinations[0].name = "Destination-" + now()
        
        * set reqCreate.source.configurations[0].value = sftpConfigs.inputHostname
        * set reqCreate.source.configurations[1].value = sftpConfigs.inputPort
        * set reqCreate.source.configurations[2].value = sftpConfigs.inputUsername
        * set reqCreate.source.configurations[3].value = sftpConfigs.inputPassword
        * set reqCreate.source.configurations[4].value = sftpConfigs.inputDirectory
        * set reqCreate.source.configurations[5].value = sftpConfigs.inputFileName
        * set reqCreate.destinations[0].configurations[0].value = httpConfigs.outputHostname
        * set reqCreate.destinations[0].configurations[1].value = httpConfigs.outputPort
        * set reqCreate.destinations[0].configurations[2].value = httpConfigs.outputUri
 
        * def tempJSON = read('classpath:integration/data-pipeline/create-response-sftp-https-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-sftp-https-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')
        
        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.source.configurations[2].value = reqCreate.source.configurations[2].value
        * set tempJSON.source.configurations[3].value = reqCreate.source.configurations[3].value
        * set tempJSON.source.configurations[4].value = reqCreate.source.configurations[4].value
        * set tempJSON.source.configurations[5].value = reqCreate.source.configurations[5].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value
        * set tempJSON.destinations[0].configurations[2].value = reqCreate.destinations[0].configurations[2].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'
 
        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name
 

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate
        
    
        * def pipelineId = response.id
        * call sleep 120
        * set resCreate.state = "STARTED"

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateWithNoFT = response
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = false
        * set reqUpdateWithNoFT.state = "STOPPING"
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPING"

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 240
        * set resCreate.deploy = false
        * set resCreate.state = "STOPPED"

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateOtherDetails = response
        * set reqUpdateOtherDetails.name = "Updated_Name-" + now()
        * set reqUpdateOtherDetails.description = "Updated_Description" + now()
        * set resCreate.name = reqUpdateOtherDetails.name
        * set resCreate.description = reqUpdateOtherDetails.description

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateOtherDetails
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateSource = response
        * set reqUpdateSource.source.name = "Updated Source Name"+now()
        * set reqUpdateSource.source.inDataType = "CSV";
        * set reqUpdateSource.source.outDataType = "CSV";
        * set resCreate.source.name = reqUpdateSource.source.name
        * set resCreate.source.inDataType = reqUpdateSource.source.inDataType;
        * set resCreate.source.outDataType = reqUpdateSource.source.outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateSource
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate

        * def reqUpdateDestinations = response
        * set reqUpdateDestinations.destinations[0].name = "Updated Destination Name"+now()
        * set reqUpdateDestinations.destinations[0].inDataType = "CSV";
        * set reqUpdateDestinations.destinations[0].outDataType = "CSV";
        * set resCreate.destinations[0].name = reqUpdateDestinations.destinations[0].name
        * set resCreate.destinations[0].inDataType = reqUpdateDestinations.destinations[0].inDataType;
        * set resCreate.destinations[0].outDataType = reqUpdateDestinations.destinations[0].outDataType;

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateDestinations
        When method put
        Then status 200
        Then match response == resCreate 
 
        * call sleep 120

   Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response == resCreate
      
    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        Then match response == resGetByIdNotFound

@Validation
Scenario: Create data pipeline - validate for blank hostname in Source
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-source-config-hostname.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank data pipeline name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-config-datapipelinename.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank source name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-config-sourcename.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank destination name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-config-destinationname.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank port in source
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-source-config-port.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank directoryName in Destination
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-destination-config-directoryName.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank fileName in Destination
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-destination-config-fileName.json')
        When method post
        Then status 400
        
@Validation
Scenario: Get list of paginated data pipeline resources
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match header Content-Type == 'application/json;charset=UTF-8'
        #And match $ != []
        And match response[*].id == "#present"
        
@Validation
Scenario: Validate Create data pipeline - Worker Service Missing
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-missing-worker.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Name Missing
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/name_missing.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Source Type Validation
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-validate-source-type.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Destination Type Validation
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/create-request-validate-destination-type.json')
        When method post
        Then status 400
        
@Validation
Scenario: Get list of Worker Services
        Given path '/worker-services'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        
@Validation
Scenario: Delete the non-exist pipeline

 Given path '/data-pipelines/' + 00
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 400

@Validation
Scenario: Get the non-exist pipeline

 Given path '/data-pipelines/' + 00
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
        
@Validation
Scenario: Delete the pipeline with incorrect datatype valued id

 Given path '/data-pipelines/' + 'ac'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 400
        
@Validation
Scenario: Get the pipeline with incorrect datatype valued id

 Given path '/data-pipelines/' + 'ac'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400

@Validation
Scenario: Get the pipeline that is already deleted and delete the pipeline which is already deleted one.
 * def reqPipeline = read('classpath:integration/data-pipeline/create-request-mllp-file-deploy-with-FT.json')
 * set reqPipeline.name = "Test-" + now()
  Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqPipeline
        When method post
        Then status 201
    
    * def pipelineId = response.id
    * call sleep 30

  Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
       
        Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200
   
  Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 400
  
  Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 400

@Validation
Scenario: Update the deployed pipeline.
        * def reqCreate =  read('classpath:integration/data-pipeline/create-request-file-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" +now()
        * set reqCreate.destinations[0].name = "Destination-" +now()

        * set reqCreate.source.configurations[0].value = fileConfigs.inputDirectoryPath
        * set reqCreate.source.configurations[1].value = fileConfigs.inputCSVFileName
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputHL7FileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath

        * def tempJSON = read('classpath:integration/data-pipeline/create-response-file-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/create-response-file-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/get-response-not-found.json')

        * set tempJSON.source.configurations[0].value = reqCreate.source.configurations[0].value
        * set tempJSON.source.configurations[1].value = reqCreate.source.configurations[1].value
        * set tempJSON.destinations[0].configurations[0].value = reqCreate.destinations[0].configurations[0].value
        * set tempJSON.destinations[0].configurations[1].value = reqCreate.destinations[0].configurations[1].value

        * set resCreate.source.configurations = '#(^^tempJSON.source.configurations)'
        * set resCreate.source.filters = '#(^^tempJSON.source.filters)'
        * set resCreate.source.transformers = '#(^^tempJSON.source.transformers)'
        * set resCreate.destinations[0].configurations = '#(^^tempJSON.destinations[0].configurations)'
        * set resCreate.destinations[0].filters = '#(^^tempJSON.destinations[0].filters)'
        * set resCreate.destinations[0].transformers = '#(^^tempJSON.destinations[0].transformers)'
        * set resCreate.destinations[0].responseTransformers = '#(^^tempJSON.destinations[0].responseTransformers)'
        * set resCreate.auditMessages = '#(^^tempJSON.auditMessages)'

        * set resCreate.name = reqCreate.name
        * set resCreate.source.name = reqCreate.source.name
        * set resCreate.destinations[0].name = reqCreate.destinations[0].name

    Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request reqCreate
        When method post
        Then status 201
        Then match response == resCreate

        * def pipelineId = response.id
        * call sleep 30
        * set resCreate.state = "STARTED"

    Given path '/interfaces/' + pipelineId + '/export'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0] == resCreate

        * def reqUpdateWithNoFT = response[0]
        * set reqUpdateWithNoFT.source.filters = []
        * set reqUpdateWithNoFT.source.transformers = []
        * set reqUpdateWithNoFT.deploy = true
        * set resCreate.source.filters = []
        * set resCreate.source.transformers = []
        * def pipelineId = reqUpdateWithNoFT.id

   Given path '/data-pipelines/'
        And header Authorization = 'Bearer ' + accessToken
        And request reqUpdateWithNoFT
        When method put
        Then status 400

    Given path '/data-pipelines/' + pipelineId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

@Validation
Scenario: Validate duplicate source and destination names for HTTP data pipelines

     Given path '/data-pipelines'
     And header Authorization = 'Bearer ' + accessToken
     And request read('classpath:integration/data-pipeline/same_endpoint_for_source_destination.json')
     When method post
     Then status 400
     Then match response.message contains "error.duplicate.source.destination.endpoint"

@Import
Scenario: Import the undeployed pipelines with filters and transformers.
         * def tempJSON = read('classpath:integration/data-pipeline/import-response-undeploy-with-FT.json')
         * def resImport = read('classpath:integration/data-pipeline/import-response-undeploy-with-FT.json')
         
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
        And multipart file file = { read: 'classpath:integration/data-pipeline/pipeline-undeploy-FT.json', filename: 'pipeline-undeploy-FT.json', contentType:'form-data'}
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
        * def tempJSON = read('classpath:integration/data-pipeline/import-response-deploy-with-FT.json')
        * def resImport = read('classpath:integration/data-pipeline/import-response-deploy-with-FT.json')
        
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
        And multipart file file = { read: 'classpath:integration/data-pipeline/pipeline-deploy-FT.json', filename: 'pipeline-deploy-FT.json', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response == resImport

@Import
Scenario: Import pipelines using empty file and file contains only [].
         * def resImport = read('classpath:integration/data-pipeline/import-response-empty-file.json')
    Given path '/interfaces/import'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:integration/data-pipeline/empty-file.json', filename: 'empty-file.json', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response == resImport

@ImportRAW
Scenario: Import pipeline with RAW as input and/or output data type
         * def tempJSON = read('classpath:integration/data-pipeline/import-datapipeline-with-raw-datatyep.json')
         * def resImport = read('classpath:integration/data-pipeline/import-datapipeline-with-raw-datatyep.json')

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
              And multipart file file = { read: 'classpath:integration/data-pipeline/pipeline-with-raw.json', filename: 'pipeline-with-raw.json', contentType:'form-data'}
              And request {}
              When method post
              Then status 200
              Then match response == resImport

          * def pipelineId = response.success[0].dataPipeline.id

          Given path '/data-pipelines/' + pipelineId
              And header Authorization = 'Bearer ' + accessToken
              When method delete
              Then status 200

