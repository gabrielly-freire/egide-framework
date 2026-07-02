package br.imd.ufrn.mapper;

import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.dto.AcademicMemberRequest;
import br.imd.ufrn.dto.AcademicMemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AcademicMemberMapper {

    AcademicMemberResponse toResponse(AcademicMember entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AcademicMember toEntity(AcademicMemberRequest request);
}
