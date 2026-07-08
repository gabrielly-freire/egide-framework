package br.imd.ufrn.atendimento.web;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.atendimento.dto.AuthenticatedAnalystResponse;
import br.imd.ufrn.atendimento.dto.LoginRequest;
import br.imd.ufrn.atendimento.dto.LoginResponse;
import br.imd.ufrn.atendimento.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        Analyst analyst = (Analyst) authentication.getPrincipal();
        String token = jwtService.generateToken(analyst);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", jwtService.getExpirationInSeconds()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticatedAnalystResponse> me(@AuthenticationPrincipal Analyst analyst) {
        return ResponseEntity.ok(new AuthenticatedAnalystResponse(
                analyst.getId(),
                analyst.getName(),
                analyst.getEmail(),
                analyst.getRole()
        ));
    }
}
