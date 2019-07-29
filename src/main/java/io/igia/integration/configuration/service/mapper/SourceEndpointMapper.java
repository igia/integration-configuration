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
package io.igia.integration.configuration.service.mapper;

import org.mapstruct.*;

import io.igia.integration.configuration.domain.*;
import io.igia.integration.configuration.service.dto.SourceEndpointDTO;

/**
 * Mapper for the entity SourceEndpoint and its DTO SourceEndpointDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SourceEndpointMapper extends EntityMapper<SourceEndpointDTO, SourceEndpoint> {

    SourceEndpoint toEntity(SourceEndpointDTO sourceEndpointDTO);

    default SourceEndpoint fromId(Long id) {
        if (id == null) {
            return null;
        }
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setId(id);
        return sourceEndpoint;
    }
}
