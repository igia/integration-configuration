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

import io.igia.integration.configuration.domain.*;
import io.igia.integration.configuration.service.dto.EndpointDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Endpoint} and its DTO {@link EndpointDTO}.
 */
@Mapper(componentModel = "spring", uses = {DataPipelineMapper.class})
public interface EndpointMapper extends EntityMapper<EndpointDTO, Endpoint> {

    @Mapping(source = "filters", target = "filters")
    @Mapping(source = "transformers", target = "transformers")
    @Mapping(source = "configurations" ,target = "configurations")
    EndpointDTO toDto(Endpoint endpoint);

    @Mapping(source = "filters", target = "filters")
    @Mapping(source = "transformers", target = "transformers")
    @Mapping(source = "configurations" ,target = "configurations")
    Endpoint toEntity(EndpointDTO endpointDTO);

    default Endpoint fromId(Long id) {
        if (id == null) {
            return null;
        }
        Endpoint endpoint = new Endpoint();
        endpoint.setId(id);
        return endpoint;
    }
}
