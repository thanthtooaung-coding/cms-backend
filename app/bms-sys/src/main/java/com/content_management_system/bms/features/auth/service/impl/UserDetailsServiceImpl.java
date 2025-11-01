package com.content_management_system.bms.features.auth.service.impl;

import com.content_management_system.bms.common.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return Mono.justOrEmpty(userJpaRepository.findByEmail(email))
                .map(user -> (UserDetails) new User(user.getEmail(), user.getPassword(), Collections.emptyList()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + email)));
    }
}