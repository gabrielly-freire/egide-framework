package br.imd.ufrn.user;

import br.imd.ufrn.user.dto.CreateUserRequest;
import br.imd.ufrn.user.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    // Sem restrição de papel: qualquer autenticado pode listar (usado pelos dropdowns de
    // responsável/acusado na tela de detalhe da manifestação). Criação continua só ADMIN.
    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }
}
