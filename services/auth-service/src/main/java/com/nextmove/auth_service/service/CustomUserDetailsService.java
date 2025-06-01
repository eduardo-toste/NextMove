package com.nextmove.auth_service.service;

import com.nextmove.auth_service.exception.ResourceNotFoundException;
import com.nextmove.auth_service.repository.UserRepository;
import com.nextmove.auth_service.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import com.nextmove.auth_service.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        return new CustomUserDetails(user);
    }
}
