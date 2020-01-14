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

Feature: Validations on Data pipeline resource

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



@Validation
Scenario: Create data pipeline - validate for blank hostname in Source
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-source-config-hostname.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank data pipeline name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-config-datapipelinename.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank source name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-config-sourcename.json')
        When method post
        Then status 400

@Validation
Scenario: Create data pipeline - validate for blank destination name
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-config-destinationname.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank port in source
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-source-config-port.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank directoryName in Destination
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-destination-config-directoryName.json')
        When method post
        Then status 400
        
@Validation
Scenario: Create data pipeline - validate for blank fileName in Destination
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-destination-config-fileName.json')
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
        And request read('classpath:integration/data-pipeline/validation/create-request-missing-worker.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Name Missing
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/name_missing.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Source Type Validation
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-validate-source-type.json')
        When method post
        Then status 400
        
@Validation
Scenario: Validate Create data pipeline - Destination Type Validation
        Given path '/data-pipelines'
        And header Authorization = 'Bearer ' + accessToken
        And request read('classpath:integration/data-pipeline/validation/create-request-validate-destination-type.json')
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
 * def reqPipeline = read('classpath:integration/data-pipeline/mllp/create-request-mllp-file-deploy-with-FT.json')
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
        * def reqCreate =  read('classpath:integration/data-pipeline/file/create-request-file-file-deploy-with-FT.json')
        * set reqCreate.name = "Test-" + now()
        * set reqCreate.source.name = "Source-" +now()
        * set reqCreate.destinations[0].name = "Destination-" +now()

        * set reqCreate.source.configurations[0].value = fileConfigs.inputDirectoryPath
        * set reqCreate.source.configurations[1].value = fileConfigs.inputCSVFileName
        * set reqCreate.destinations[0].configurations[0].value = fileConfigs.outputHL7FileName
        * set reqCreate.destinations[0].configurations[1].value = fileConfigs.outputDirectoryPath

        * def tempJSON = read('classpath:integration/data-pipeline/file/create-response-file-file-deploy-with-FT.json')
        * def resCreate = read('classpath:integration/data-pipeline/file/create-response-file-file-deploy-with-FT.json')
        * def resDeleteNotFound = read('classpath:integration/data-pipeline/common/delete-response-not-found.json')
        * def resGetByIdNotFound = read('classpath:integration/data-pipeline/common/get-response-not-found.json')

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
     And request read('classpath:integration/data-pipeline/validation/same_endpoint_for_source_destination.json')
     When method post
     Then status 400
     Then match response.message contains "error.duplicate.source.destination.endpoint"

