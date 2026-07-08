package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.PartyRequest;
import br.imd.ufrn.core.dto.PartyResponse;

/**
 * Registro genérico das partes (analistas e acusados) e suas unidades organizacionais.
 * Ponto fixo de infraestrutura que fornece o dado consumido pelo ponto variável de conflito.
 */
public interface PartyService {

    PartyResponse create(PartyRequest request);

    PartyResponse findById(Long id);
}
