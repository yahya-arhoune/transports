package com.yahyaarhoune.transports.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException; // For newer JJWT versions
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; // For @NonNull annotation
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Your UserDetailsServiceImpl
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this as a Spring Bean, allowing it to be @Autowired
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil; // Your utility to validate and extract from token

    @Autowired
    private UserDetailsService userDetailsService; // Your UserDetailsServiceImpl

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        logger.debug("JwtAuthFilter: Processing request for URI: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                logger.debug("JwtAuthFilter: JWT Token found in request header.");
                if (jwtUtil.validateJwtToken(jwt)) {
                    String username = jwtUtil.getUsernameFromJwtToken(jwt); // This is the email
                    logger.debug("JwtAuthFilter: JWT Token is valid. Username extracted: {}", username);

                    // Important: Check if authentication already exists in context
                    // This prevents re-authenticating on every filter in the chain if already done
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.debug("JwtAuthFilter: UserDetails loaded for username: {}", username);

                        // If token is valid, configure Spring Security to manually set authentication
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // After setting the Authentication in the context, we specify
                        // that the current user is authenticated. So it passes the
                        // Spring Security Configurations successfully.
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("JwtAuthFilter: User '{}' authenticated successfully. SecurityContext updated.", username);
                    } else {
                        logger.debug("JwtAuthFilter: SecurityContext already contains an Authentication object for {}. Skipping re-authentication.",
                                SecurityContextHolder.getContext().getAuthentication().getName());
                    }
                } else {
                    logger.warn("JwtAuthFilter: JWT Token validation failed for token starting with: {}", jwt.length() > 10 ? jwt.substring(0, 10) + "..." : jwt);
                }
            } else {
                // Using trace as this will happen for many requests (e.g., public endpoints)
                logger.trace("JwtAuthFilter: No JWT token found in Authorization header or not Bearer type for {}", request.getRequestURI());
            }
        } catch (ExpiredJwtException e) {
            logger.warn("JwtAuthFilter: JWT token is expired: {}. Request URI: {}", e.getMessage(), request.getRequestURI());
            // Optionally: You can set response status here for expired token to give specific feedback
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // response.getWriter().write("{\"error\": \"JWT_EXPIRED\", \"message\": \"JWT Token has expired\"}");
            // SecurityContextHolder.clearContext(); // Clear context if token is definitively bad
            // return; // Stop further processing
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // These are different types of invalid token issues
            logger.warn("JwtAuthFilter: Invalid JWT token: {}. Request URI: {}", e.getMessage(), request.getRequestURI());
            // SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // Catch any other unexpected errors during the filter process
            logger.error("JwtAuthFilter: Cannot set user authentication for {}: {}", request.getRequestURI(), e.getMessage(), e);
            // SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response); // IMPORTANT: Always continue the filter chain
    }

    /**
     * Extracts the JWT token from the "Authorization" header.
     * The header should be in the format: "Bearer <token>"
     *
     * @param request The incoming HTTP request.
     * @return The JWT token string, or null if not found or not in correct format.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Extract token part, after "Bearer " (7 characters)
        }
        return null;
    }
}