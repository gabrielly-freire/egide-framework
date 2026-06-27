package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManifestationMapper {

    ManifestationResponse toResponse(Manifestation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocolNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "description", ignore = true)
    Manifestation toEntity(ManifestationRequest request);
}
