package br.imd.ufrn.atendimento.service;

import br.imd.ufrn.atendimento.dto.AnalystRequest;
import br.imd.ufrn.atendimento.dto.AnalystResponse;
import java.util.List;

public interface AnalystService {

    AnalystResponse create(AnalystRequest request);

    AnalystResponse findById(Long id);

    List<AnalystResponse> findAll();

    AnalystResponse update(Long id, AnalystRequest request);

    void delete(Long id);
}
