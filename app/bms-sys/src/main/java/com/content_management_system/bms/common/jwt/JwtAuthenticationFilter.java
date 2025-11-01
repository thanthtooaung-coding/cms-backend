//package com.content_management_system.bms.common.jwt;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter implements WebFilter {
//
//    private final ReactiveUserDetailsService userDetailsService;
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Mono<Void> filter(   ServerWebExchange exchange, WebFilterChain chain) {
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String authToken = authHeader.substring(7);
//            String username = jwtUtil.extractUsername(authToken);
//
//            if (username != null && ReactiveSecurityContextHolder.getContext() == null) {
//                Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(username);
//
//                return userDetailsMono.flatMap(userDetails -> {
//                    if (jwtUtil.validateToken(authToken, userDetails)) {
//                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//                        return chain.filter(exchange)
//                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
//                    }
//                    return chain.filter(exchange);
//                });
//            }
//        }
//        return chain.filter(exchange);
//    }
//}