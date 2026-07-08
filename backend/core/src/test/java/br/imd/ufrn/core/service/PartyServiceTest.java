package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.Party;
import br.imd.ufrn.core.dto.PartyRequest;
import br.imd.ufrn.core.dto.PartyResponse;
import br.imd.ufrn.core.exception.PartyNotFoundException;
import br.imd.ufrn.core.mapper.PartyMapper;
import br.imd.ufrn.core.persistence.PartyRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

    @Mock
    private PartyRepository repository;

    @Mock
    private PartyMapper mapper;

    @InjectMocks
    private PartyServiceImpl service;

    @Test
    void create_devePersistirParteAtivaERetornarResponse() {
        PartyRequest request = new PartyRequest("Ana", "DIMAP");
        Party entity = new Party();
        Party saved = new Party();
        saved.setId(1L);
        PartyResponse response = new PartyResponse(1L, "Ana", "DIMAP");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any(Party.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        PartyResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
        assertThat(entity.getActive()).isTrue();
    }

    @Test
    void findById_deveRetornarParte_quandoExiste() {
        Party entity = new Party();
        entity.setId(1L);
        PartyResponse response = new PartyResponse(1L, "Ana", "DIMAP");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        assertThat(service.findById(1L)).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(PartyNotFoundException.class);
    }
}
