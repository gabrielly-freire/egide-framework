package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

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

    /**
     * Aplica os campos do request sobre uma entidade existente (semântica PATCH).
     * Campos imutáveis (id, protocolo, status, controle) são preservados; a descrição
     * é tratada à parte no serviço por passar pela estratégia de anonimização.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocolNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "description", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ManifestationRequest request, @MappingTarget Manifestation entity);
}
