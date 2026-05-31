package com.example.campconnect_backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ 1. SKIP AUTH ENDPOINTS (IMPORTANT)
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. SKIP CORS PREFLIGHT REQUESTS (IMPORTANT)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // No token → continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (JwtException | IllegalArgumentException ex) {
            // ✅ Ignore invalid tokens (do NOT block request)
        }

        filterChain.doFilter(request, response);
    }
}