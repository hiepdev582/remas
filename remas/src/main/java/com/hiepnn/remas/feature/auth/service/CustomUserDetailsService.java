package com.hiepnn.remas.feature.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.entity.UserRole;
import com.hiepnn.remas.feature.auth.model.UserPrincipal;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.auth.repository.UserRoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());

        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getName().getValue()))
                .collect(Collectors.toList());

        return new UserPrincipal(user, authorities);
    }
}
