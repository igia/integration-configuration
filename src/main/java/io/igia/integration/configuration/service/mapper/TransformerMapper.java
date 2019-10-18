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
import io.igia.integration.configuration.service.dto.TransformerDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transformer} and its DTO {@link TransformerDTO}.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class})
public interface TransformerMapper extends EntityMapper<TransformerDTO, Transformer> {

    @Mapping(source = "endpoint.id", target = "endpointId")
    TransformerDTO toDto(Transformer transformer);

    @Mapping(source = "endpointId", target = "endpoint")
    Transformer toEntity(TransformerDTO transformerDTO);

    default Transformer fromId(Long id) {
        if (id == null) {
            return null;
        }
        Transformer transformer = new Transformer();
        transformer.setId(id);
        return transformer;
    }
}
