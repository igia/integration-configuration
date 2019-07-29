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
import io.igia.integration.configuration.service.dto.DestinationConfigDTO;

/**
 * Mapper for the entity DestinationConfig and its DTO DestinationConfigDTO.
 */
@Mapper(componentModel = "spring", uses = {DestinationEndpointMapper.class})
public interface DestinationConfigMapper extends EntityMapper<DestinationConfigDTO, DestinationConfig> {

    @Mapping(source = "destinationEndpoint.id", target = "destinationEndpointId")
    DestinationConfigDTO toDto(DestinationConfig destinationConfig);

    @Mapping(source = "destinationEndpointId", target = "destinationEndpoint")
    DestinationConfig toEntity(DestinationConfigDTO destinationConfigDTO);

    default DestinationConfig fromId(Long id) {
        if (id == null) {
            return null;
        }
        DestinationConfig destinationConfig = new DestinationConfig();
        destinationConfig.setId(id);
        return destinationConfig;
    }
}
