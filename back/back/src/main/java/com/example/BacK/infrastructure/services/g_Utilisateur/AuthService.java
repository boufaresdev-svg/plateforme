package com.example.BacK.infrastructure.services.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Command.auth.login.LoginResponse;
import com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken.RefreshTokenResponse;
import com.example.BacK.application.interfaces.g_Utilisateur.auth.IAuthService;
import com.example.BacK.application.models.g_Utilisateur.UserDTO;
import com.example.BacK.domain.g_Utilisateurs.User;
import com.example.BacK.infrastructure.repository.g_Utilisateur.UserRepository;
import com.example.BacK.presentation.security.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AuthService(AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public LoginResponse login(String username, String password) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Get user details
        User user = (User) authentication.getPrincipal();

        // Update last login
        user.setDerniereConnexion(java.time.LocalDateTime.now());
        user.setTentativesConnexionEchouees(0);
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Build response
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // Extract username from token
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByNomUtilisateur(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Token invalide");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userDTO)
                .build();
    }
}
