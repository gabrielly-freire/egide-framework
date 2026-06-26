package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.ServiceEvaluation;
import br.imd.ufrn.core.dto.ServiceEvaluationRequest;
import br.imd.ufrn.core.dto.ServiceEvaluationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceEvaluationMapper {

    ServiceEvaluationResponse toResponse(ServiceEvaluation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceEvaluation toEntity(ServiceEvaluationRequest request);
}
