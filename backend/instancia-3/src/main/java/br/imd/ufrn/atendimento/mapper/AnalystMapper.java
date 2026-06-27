package br.imd.ufrn.atendimento.mapper;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.atendimento.dto.AnalystRequest;
import br.imd.ufrn.atendimento.dto.AnalystResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnalystMapper {

    AnalystResponse toResponse(Analyst entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Analyst toEntity(AnalystRequest request);
}
