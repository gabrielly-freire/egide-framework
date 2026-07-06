package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.Party;
import br.imd.ufrn.core.dto.PartyRequest;
import br.imd.ufrn.core.dto.PartyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyMapper {

    PartyResponse toResponse(Party entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Party toEntity(PartyRequest request);
}
