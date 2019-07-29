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

import java.util.Map;

import javax.annotation.Nullable;

public class CustomErrorVM {
    
    private String type;
    
    private String title;
    
    private String message;
    
    private Map<String, Object> paramMap;

    public CustomErrorVM(String type, String title, String message, @Nullable Map<String, Object> paramMap) {
        super();
        this.type = type;
        this.title = title;
        this.message = message;
        this.paramMap = paramMap;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
    
}
