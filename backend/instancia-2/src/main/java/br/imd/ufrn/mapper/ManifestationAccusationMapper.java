package br.imd.ufrn.mapper;

import br.imd.ufrn.domain.ManifestationAccusation;
import br.imd.ufrn.dto.AccusationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManifestationAccusationMapper {

    AccusationResponse toResponse(ManifestationAccusation entity);
}
