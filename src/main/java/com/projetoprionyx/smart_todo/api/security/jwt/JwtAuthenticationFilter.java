package com.projetoprionyx.smart_todo.api.security.jwt;

import com.projetoprionyx.smart_todo.api.security.UserDetailsServiceImpl;
import com.projetoprionyx.smart_todo.api.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro customizado que intercepta todas as requisições para processar o token JWT.
 * Ele é executado uma vez por requisição.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Lógica principal do filtro. Executada para cada requisição.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extrair o token JWT da requisição.
            String jwt = getJwtFromRequest(request);

            // 2. Validar o token e, se válido, configurar a autenticação no contexto do Spring Security.
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 2.1 Extrai o e-mail do token.
                String userEmail = tokenProvider.getEmailFromJWT(jwt);

                // 2.2 Carrega os detalhes do usuário a partir do e-mail.
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 2.3 Cria o objeto de autenticação.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 2.4 Define a autenticação no contexto de segurança do Spring.
                // A partir daqui, o usuário está "logado" para esta requisição.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // 3. Continua a cadeia de filtros.
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para extrair o token do cabeçalho "Authorization".
     *
     * @param request A requisição HTTP.
     * @return O token JWT como uma String, ou null se não for encontrado.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Retorna apenas a parte do token, sem o prefixo "Bearer ".
            return bearerToken.substring(7);
        }
        return null;
    }
}