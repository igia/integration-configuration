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
import io.igia.integration.configuration.service.dto.SourceConfigDTO;

/**
 * Mapper for the entity SourceConfig and its DTO SourceConfigDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceEndpointMapper.class})
public interface SourceConfigMapper extends EntityMapper<SourceConfigDTO, SourceConfig> {

    @Mapping(source = "sourceEndpoint.id", target = "sourceEndpointId")
    SourceConfigDTO toDto(SourceConfig sourceConfig);

    @Mapping(source = "sourceEndpointId", target = "sourceEndpoint")
    SourceConfig toEntity(SourceConfigDTO sourceConfigDTO);

    default SourceConfig fromId(Long id) {
        if (id == null) {
            return null;
        }
        SourceConfig sourceConfig = new SourceConfig();
        sourceConfig.setId(id);
        return sourceConfig;
    }
}
