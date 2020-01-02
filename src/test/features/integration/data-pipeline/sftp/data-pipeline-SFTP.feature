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

Feature: CRUD operation on SFTP Data pipeline resource

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




@TestSFTP
 Scenario: Create SFTP to SFTP data pipeline that is deployed and it has 2 filters and 2 transformers and get data pipeline resources and delete the pipeline and get that pipeline again.

        * def reqCreate =  read('classpath:integration/data-pipeline/sftp/create-request-sftp-sftp-deploy-with-FT.json')
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
 
        * def tempJSON = read('classpath:integration/data-pipeline/sftp/create-response-sftp-sftp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/sftp/create-response-sftp-sftp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')
        
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
        * call sleep 240
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
 
        * call sleep 240

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
 
        * call sleep 240

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
 
        * call sleep 240

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

        * def reqCreate =  read('classpath:integration/data-pipeline/sftp/create-request-sftp-mllp-deploy-with-FT.json')
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
 
        * def tempJSON = read('classpath:integration/data-pipeline/sftp/create-response-sftp-mllp-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/sftp/create-response-sftp-mllp-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')
        
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
        * call sleep 240
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
 
        * call sleep 240

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
 
        * call sleep 240

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
 
        * call sleep 240

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

        * def reqCreate =  read('classpath:integration/data-pipeline/sftp/create-request-sftp-file-deploy-with-FT.json')
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
 
        * def tempJSON = read('classpath:integration/data-pipeline/sftp/create-response-sftp-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/sftp/create-response-sftp-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')
        
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
        * call sleep 240
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
 
        * call sleep 240

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
 
        * call sleep 240

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
 
        * call sleep 240

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

        * def reqCreate =  read('classpath:integration/data-pipeline/sftp/create-request-sftp-http-deploy-with-FT.json')
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
 
        * def tempJSON = read('classpath:integration/data-pipeline/sftp/create-response-sftp-http-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/sftp/create-response-sftp-http-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')
        
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
        * call sleep 240
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
 
        * call sleep 240

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
 
        * call sleep 240

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
 
        * call sleep 240

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

        * def reqCreate =  read('classpath:integration/data-pipeline/sftp/create-request-sftp-https-deploy-with-FT.json')
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
 
        * def tempJSON = read('classpath:integration/data-pipeline/sftp/create-response-sftp-https-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/sftp/create-response-sftp-https-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')
        
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
        * call sleep 240
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
 
        * call sleep 240

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
 
        * call sleep 240

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
 
        * call sleep 240

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


