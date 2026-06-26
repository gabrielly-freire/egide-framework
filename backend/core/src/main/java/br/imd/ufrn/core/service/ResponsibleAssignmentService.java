package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;

public interface ResponsibleAssignmentService {

    ResponsibleAssignmentResponse assign(ResponsibleAssignmentRequest request);

    ResponsibleAssignmentResponse findById(Long id);

    ResponsibleAssignmentResponse findByManifestationId(Long manifestationId);

    void unassign(Long manifestationId);
}
