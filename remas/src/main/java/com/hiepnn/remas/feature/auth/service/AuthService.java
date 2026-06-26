package com.hiepnn.remas.feature.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hiepnn.remas.common.constant.RoleName;
import com.hiepnn.remas.entity.InvalidatedToken;
import com.hiepnn.remas.entity.Role;
import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.entity.UserRole;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.feature.auth.model.AuthResponse;
import com.hiepnn.remas.feature.auth.model.LoginRequest;
import com.hiepnn.remas.feature.auth.model.LoginResult;
import com.hiepnn.remas.feature.auth.model.RegisterRequest;
import com.hiepnn.remas.feature.auth.model.UserPrincipal;
import com.hiepnn.remas.feature.auth.repository.InvalidatedTokenRepository;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.auth.repository.UserRoleRepository;
import com.hiepnn.remas.feature.auth.util.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // #region Đăng ký tài khoản
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists!");
        }
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty())
                ? request.getEmail().trim()
                : null;

        if (email != null && userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(email)
                .fullName(request.getFullName())
                .isActive(false)
                .build();

        User savedUser = userRepository.save(user);

        Role defaultRole = Role.builder().id(3).name(RoleName.CUSTOMER).build();

        UserRole.UserRoleId userRoleId = new UserRole.UserRoleId(savedUser.getId(), defaultRole.getId());
        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .user(savedUser)
                .role(defaultRole)
                .build();

        userRoleRepository.save(userRole);

        return "Registered successfully! Plase contact admin to activate your account.";
    }
    // #endregion

    // #region Đăng nhập - Sinh token
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password!"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BadRequestException("User is inactive!");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal.getUsername(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal.getUsername());

        AuthResponse authResponse = new AuthResponse(accessToken, userPrincipal.getUsername(), userPrincipal.getUser().getEmail(), roles);
        return new LoginResult(authResponse, refreshToken);
    }
    // #endregion

    // #region Làm mới Token
    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Refresh token is invalid or expired!");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found!"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BadRequestException("User is inactive!");
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        String roles = userRoles.stream()
                .map(ur -> ur.getRole().getName().getValue())
                .collect(Collectors.joining(","));

        String newAccessToken = jwtTokenProvider.generateAccessToken(username, roles);

        return new AuthResponse(newAccessToken, username, user.getEmail(), roles);
    }
    // #endregion

    // #region Đăng xuất
    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token!");
        }
        String token = authHeader.substring(7);

        Date expirationDate = jwtTokenProvider.getExpirationDate(token);

        LocalDateTime expiryTime = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(token)
                .expiryTime(expiryTime)
                .createdAt(LocalDateTime.now())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        return "Logged out successfully!";
    }
    // #endregion
}
