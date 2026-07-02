package br.imd.ufrn.conflict;

import br.imd.ufrn.conflict.dto.AccusationResponse;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.user.AppUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class AccusationServiceImpl implements AccusationService {

    private final ManifestationAccusationRepository accusationRepository;
    private final ManifestationRepository manifestationRepository;
    private final AppUserRepository userRepository;

    @Override
    public AccusationResponse register(Long manifestationId, Long accusedUserId) {
        if (!manifestationRepository.existsById(manifestationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manifestação não encontrada");
        }
        if (!userRepository.existsById(accusedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário acusado não encontrado");
        }

        ManifestationAccusation accusation = new ManifestationAccusation();
        accusation.setManifestationId(manifestationId);
        accusation.setAccusedUserId(accusedUserId);
        return toResponse(accusationRepository.save(accusation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccusationResponse> findByManifestation(Long manifestationId) {
        return accusationRepository.findByManifestationId(manifestationId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AccusationResponse toResponse(ManifestationAccusation a) {
        return new AccusationResponse(a.getId(), a.getManifestationId(), a.getAccusedUserId());
    }
}
