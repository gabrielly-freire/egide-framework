package br.imd.ufrn.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.dto.AcademicMemberRequest;
import br.imd.ufrn.dto.AcademicMemberResponse;
import br.imd.ufrn.exception.AcademicMemberNotFoundException;
import br.imd.ufrn.mapper.AcademicMemberMapper;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcademicMemberServiceTest {

    @Mock
    private AcademicMemberRepository repository;

    @Mock
    private AcademicMemberMapper mapper;

    @InjectMocks
    private AcademicMemberServiceImpl service;

    @Test
    void create_devePersistirMembroAtivoERetornarResponse() {
        AcademicMemberRequest request = new AcademicMemberRequest("Ana", "DIMAP");
        AcademicMember entity = new AcademicMember();
        AcademicMember saved = new AcademicMember();
        saved.setId(1L);
        AcademicMemberResponse response = new AcademicMemberResponse(1L, "Ana", "DIMAP");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any(AcademicMember.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        AcademicMemberResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
        assertThat(entity.getActive()).isTrue();
    }

    @Test
    void findById_deveRetornarMembro_quandoExiste() {
        AcademicMember entity = new AcademicMember();
        entity.setId(1L);
        AcademicMemberResponse response = new AcademicMemberResponse(1L, "Ana", "DIMAP");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        assertThat(service.findById(1L)).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(AcademicMemberNotFoundException.class);
    }
}
