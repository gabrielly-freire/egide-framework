package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.ManifestationAccusation;
import br.imd.ufrn.core.dto.AccusationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManifestationAccusationMapper {

    AccusationResponse toResponse(ManifestationAccusation entity);
}
