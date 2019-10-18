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
package io.igia.integration.configuration.constants;

public final class MessageConstants {

    private MessageConstants(){
        
    }

    public static final String DEFAULT_USER = "SYSTEM";
    public static final String SOURCE_CONFIGURATIONS = "source.configurations";
    public static final String DESTINATION_CONFIGURATIONS = "destination.configurations";
    public static final String MESSAGE_KEY = "message";
    public static final String SOURCE_FILTERS = "source.filters";
    public static final String DESTINATION_FILTERS = "destination.filters";
    public static final String SOURCE_TRANSFORMERS = "source.transformers";
    public static final String DESTINATION_TRANSFORMERS = "destination.transformers";
    public static final String PARAMS_KEY = "params";
    public static final String DATAPIPELINE_NAME = "dataPipeline.name";
    public static final String DUPLICATE_DESTINATION_NAME = "dataPipeline.destnation";
    public static final Integer MIN_PORT_NUMBER = 0;
    public static final Integer MAX_PORT_NUMBER = 65535;
    public static final String PORT = "port";
    public static final String FIELD = "field";
    public static final String FIELD_VALUE = "value";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String NAME = "name";
    public static final String ORDER = "order";
    public static final String WORKERSERVICE = "workerservice";
    public static final String INVALID_WORKERSERVICE = "Invalid Workerservice";
    public static final String ERR_MISSTING_REQUIRED_PROPERTY = "Missing Property";
    public static final String ERR_DUPLICATE_ORDER_VALUE = "Duplicate Order Value";
    public static final String UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE = "Duplicate Data Pipeline Name";
    public static final String ERR_DUPLICATE_DESTINATION_NAME = "Duplicate Destination Name";
    public static final String ERR_INVALIDATION_PORT_RANGE = "Invalid Port Range";
    public static final String ERR_PORT_VALIDATION = "Invalid port number";
    public static final String DEPLOY = "deploy";  
    public static final String ERR_INVALID_DEPLOY_FLAG = "Deploy flag should be false";
    public static final String HEADER_CONSTANT_FILE_ATTACHMENT = "attachment; filename=\"";
    public static final String HTTP_SECURE = "isSecure";
    public static final String ERR_INVALID_SECURE_FLAG = "Secure field should be true";
    public static final String HOSTNAME = "hostname";
    public static final String RESOURCE_URI = "resourceUri";
    public static final String DATAPIPELINE_SOURCE ="datapipeline.source";
    public static final String DATAPIPELINE_DESTINATION = "datapipeline.destination";
    public static final String ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT = "Duplicate Source and Destination Endpoint";
    
}
