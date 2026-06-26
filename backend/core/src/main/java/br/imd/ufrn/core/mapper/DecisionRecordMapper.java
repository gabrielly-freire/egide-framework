package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.DecisionRecord;
import br.imd.ufrn.core.dto.DecisionRecordRequest;
import br.imd.ufrn.core.dto.DecisionRecordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DecisionRecordMapper {

    DecisionRecordResponse toResponse(DecisionRecord entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DecisionRecord toEntity(DecisionRecordRequest request);
}
