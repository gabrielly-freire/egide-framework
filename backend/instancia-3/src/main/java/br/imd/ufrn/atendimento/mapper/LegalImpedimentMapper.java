package br.imd.ufrn.atendimento.mapper;

import br.imd.ufrn.atendimento.domain.LegalImpediment;
import br.imd.ufrn.atendimento.dto.LegalImpedimentRequest;
import br.imd.ufrn.atendimento.dto.LegalImpedimentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LegalImpedimentMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    LegalImpedimentResponse toResponse(LegalImpediment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LegalImpediment toEntity(LegalImpedimentRequest request);
}
