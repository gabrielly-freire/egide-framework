package br.imd.ufrn.conflict;

import br.imd.ufrn.conflict.dto.AccusationResponse;
import java.util.List;

public interface AccusationService {

    AccusationResponse register(Long manifestationId, Long accusedUserId);

    List<AccusationResponse> findByManifestation(Long manifestationId);
}
