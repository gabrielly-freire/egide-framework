package br.imd.ufrn.service;

import br.imd.ufrn.dto.AcademicMemberRequest;
import br.imd.ufrn.dto.AcademicMemberResponse;

public interface AcademicMemberService {

    AcademicMemberResponse create(AcademicMemberRequest request);

    AcademicMemberResponse findById(Long id);
}
