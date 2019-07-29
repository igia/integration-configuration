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
import io.igia.integration.configuration.service.dto.SourceFilterDTO;

/**
 * Mapper for the entity SourceFilter and its DTO SourceFilterDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceEndpointMapper.class})
public interface SourceFilterMapper extends EntityMapper<SourceFilterDTO, SourceFilter> {

    @Mapping(source = "sourceEndpoint.id", target = "sourceEndpointId")
    SourceFilterDTO toDto(SourceFilter sourceFilter);

    @Mapping(source = "sourceEndpointId", target = "sourceEndpoint")
    SourceFilter toEntity(SourceFilterDTO sourceFilterDTO);

    default SourceFilter fromId(Long id) {
        if (id == null) {
            return null;
        }
        SourceFilter sourceFilter = new SourceFilter();
        sourceFilter.setId(id);
        return sourceFilter;
    }
}
