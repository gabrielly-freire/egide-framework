package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.ResponsibleAssignment;
import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResponsibleAssignmentMapper {

    ResponsibleAssignmentResponse toResponse(ResponsibleAssignment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ResponsibleAssignment toEntity(ResponsibleAssignmentRequest request);
}
