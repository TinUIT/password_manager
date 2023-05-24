package com.trongtin.password_manager.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.*;

import java.util.List;
import java.util.stream.Collectors;
import java.security.Key;
import java.util.Base64;

public class JwtAuthenticationFilter implements WebFilter {

    // The necessary methods and fields for JwtAuthenticationFilter, e.g.:
    // - A method to read JWT from the request
    // - A method to validate JWT
    // - A method to create Authentication object from JWT

    private final String jwtSecret = "secret-key";

    private void setSecurityContextFromJwt(String jwt) {
        // Get the Authentication object from the JWT
        Authentication authentication = getAuthenticationFromJwt(jwt);

        // Create a SecurityContextImpl object using the Authentication object
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        // Set the security context in the SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }
    private boolean validateJwt(String jwt) {
        try {
            // Decode the JWT secret key
            byte[] decodedSecretKey = Base64.getDecoder().decode(jwtSecret);
            Key secretKey = Keys.hmacShaKeyFor(decodedSecretKey);

            // Parse and validate the JWT
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);

            // If no exception is thrown, the JWT is valid
            return true;
        } catch (JwtException e) {
            // An exception indicates that the JWT is invalid
            System.err.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    private Authentication getAuthenticationFromJwt(String jwt) {
        // Decode the JWT secret key
        byte[] decodedSecretKey = Base64.getDecoder().decode(jwtSecret);
        Key secretKey = Keys.hmacShaKeyFor(decodedSecretKey);

        // Parse the JWT
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt);

        Claims claims = jws.getBody();

        // Extract the username and authorities from the JWT
        String username = claims.getSubject();
        List<String> authorityStrings = claims.get("authorities", List.class);

        // Convert the list of authority strings to a list of GrantedAuthority objects
        List<SimpleGrantedAuthority> authorities = authorityStrings.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create and return an Authentication object
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        try {
            // Read JWT from the request and validate it
            String jwt = getJwtFromRequest(request);

            if (jwt != null && validateJwt(jwt)) {
                Authentication authentication = getAuthenticationFromJwt(jwt);

                // Set authentication information for ReactiveSecurityContextHolder
                SecurityContext securityContext = new SecurityContextImpl(authentication);
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
            }
        } catch (Exception ex) {
            // Log error if authentication fails
            // Adjust error handling according to your application's requirements
            System.err.println("Failed to authenticate user: " + ex.getMessage());
        }

        // Continue processing the request
        return chain.filter(exchange);
    }

    private String getJwtFromRequest(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}