package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.DecisionRecordRequest;
import br.imd.ufrn.core.dto.DecisionRecordResponse;
import java.util.List;

public interface DecisionRecordService {

    DecisionRecordResponse create(DecisionRecordRequest request);

    DecisionRecordResponse findById(Long id);

    List<DecisionRecordResponse> findAllByManifestationId(Long manifestationId);

    void delete(Long id);
}
