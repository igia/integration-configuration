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
import io.igia.integration.configuration.service.dto.FilterDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Filter} and its DTO {@link FilterDTO}.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class})
public interface FilterMapper extends EntityMapper<FilterDTO, Filter> {

    @Mapping(source = "endpoint.id", target = "endpointId")
    FilterDTO toDto(Filter filter);

    @Mapping(source = "endpointId", target = "endpoint")
    Filter toEntity(FilterDTO filterDTO);

    default Filter fromId(Long id) {
        if (id == null) {
            return null;
        }
        Filter filter = new Filter();
        filter.setId(id);
        return filter;
    }
}
