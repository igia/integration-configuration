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
import io.igia.integration.configuration.service.dto.DestinationTransformerDTO;

/**
 * Mapper for the entity DestinationTransformer and its DTO DestinationTransformerDTO.
 */
@Mapper(componentModel = "spring", uses = {DestinationEndpointMapper.class})
public interface DestinationTransformerMapper extends EntityMapper<DestinationTransformerDTO, DestinationTransformer> {

    @Mapping(source = "destinationEndpoint.id", target = "destinationEndpointId")
    DestinationTransformerDTO toDto(DestinationTransformer destinationTransformer);

    @Mapping(source = "destinationEndpointId", target = "destinationEndpoint")
    DestinationTransformer toEntity(DestinationTransformerDTO destinationTransformerDTO);

    default DestinationTransformer fromId(Long id) {
        if (id == null) {
            return null;
        }
        DestinationTransformer destinationTransformer = new DestinationTransformer();
        destinationTransformer.setId(id);
        return destinationTransformer;
    }
}
