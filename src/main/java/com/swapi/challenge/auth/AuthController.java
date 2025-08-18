package com.swapi.challenge.auth;

import com.swapi.challenge.auth.dto.request.LoginRequest;
import com.swapi.challenge.auth.dto.request.RegisterRequest;
import com.swapi.challenge.auth.dto.response.AuthResponse;
import com.swapi.challenge.user.Role;
import com.swapi.challenge.user.UserEntity;
import com.swapi.challenge.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("username already exists");
        }
        UserEntity u = UserEntity.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .build();
        userRepository.save(u);
        String token = jwtService.generateToken(u.getUsername());
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtService.generateToken(authentication.getName());
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }
}
