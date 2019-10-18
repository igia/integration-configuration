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
package io.igia.integration.configuration.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");
    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI EMAIL_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-found");
    public static final String CAMEL_EXCEPTION = "camel.exception";
    public static final String ENCRYPTION_EXCEPTION = "error.encryption.exception";
    public static final String UNIQUE_NAME_CONSTRAINT_ERROR_MESSAGE = "error.duplicate.datapipeline.name";
    public static final String DATA_NOT_FOUND = "Data not found";
    public static final String INVALID_WORKERSERVICE = "error.invalid.workerservice";
    public static final String EXPORT_EXCEPTION = "error.invalid.export";
    public static final String DESERIALIZE_EXCEPTION = "error.deserialization.exception";
    public static final String IMPORT_EXCEPTION = "Error while importing";
    public static final String ERR_INVALIDATION_PORT_RANGE = "error.invalid.port.range";
    public static final String ERR_PORT_VALIDATION = "error.invalid.port";
    public static final String ERR_DUPLICATE_DESTINATION_NAME = "error.duplicate.destination.name";
    public static final String ERR_DUPLICATE_ORDER_VALUE = "error.duplicate.order.value";
    public static final String ERR_MISSTING_REQUIRED_PROPERTY = "error.missing.property";
    public static final String ERR_IMPORT = "Erros while Importing";
    public static final String ERR_INVALID_JSON = "error.invalid.json";
    public static final String ERR_DUPLICATE_PIPELINE_NAME = "error.duplicate.name";
    public static final String VALID_IMPORT = "valid.import";
    public static final String ERR_INVALID_DEPLOY_FLAG = "error.invalid.deploy.flag";
    public static final String ERR_UPDATE_DEPLOYED_PIPELINE = "error.update.deployed.pipeline";
    public static final String ERR_INVALID_SECURE_FLAG = "error.invalid.secure.flag";
    public static final String ERR_DUPLICATE_SOURCE_DESTINATION_ENDPOINT = "error.duplicate.source.destination.endpoint";
    private ErrorConstants() {
    }
}
