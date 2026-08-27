package com.walletly.walletly_backend.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
// OncePerResquestFilter permet de dire qu'il n'y a qu'un Servlet () est utilisé pour une seule requête, le même  filtre est appliqué à tous les servlet. 
import org.springframework.web.filter.OncePerRequestFilter;

import com.walletly.walletly_backend.service.CustomUserDetailsService;
import com.walletly.walletly_backend.service.JwtService;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component /// Spring l'injecte automatiquement
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Initialiser le constructor
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    };

    @Override /// redéfinis une méthode qui existe déjà dans la classe parent.
    /// doFilterInternal = méthode qui qui sera exécutée à chaque requête HTTP.
    protected void doFilterInternal(
            HttpServletRequest request, // User arrive
            HttpServletResponse response,
            FilterChain filterChain) /// autorise à entrer.
            throws ServletException, IOException {

        try {
            // 1. vérifier s'il y a un "Authorization"
            final String authHeader = request.getHeader("Authorization");
            // 2. vérifie si celui-ci commence par un Bearer
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            // 3. extrait le token
            final String jwt = authHeader.substring(7); /// 7 parce que il y a 7 caractères dans "bearer ".

            // 4. extrait l'email depuis le token
            final String userEmail = jwtService.extractUsername(jwt);

            /// 5. vérifier si l'user est authentifié
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                /// 6. Charger l'user depuis la base
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                /// 7. vérifie si le token est valid
                if (jwtService.validateToken(jwt, userDetails)) {
                    // authToken c'est l'objet dans lequel on stocke
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    // Attacher les détails de la requête
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Mettre l’utilisateur dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log l'erreur sans arrêter le filtre
            System.err.println("JWT Authentication error: " + e.getMessage());
        }
        /// 8. Continuer la chaîne des filtres
        filterChain.doFilter(request, response);
    }
}