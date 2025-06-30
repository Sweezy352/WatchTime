package kg.sweezy.watchtime.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtHandler jwtHandler;
    private final AuthService authService;

    @Autowired
    public JwtAuthenticationFilter(JwtHandler jwtHandler, AuthService authService) {
        this.jwtHandler = jwtHandler;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromHeader(request);

        if(Objects.nonNull(token) && jwtHandler.validateToken(token)) {
            String username = jwtHandler.jwtParseToken(token);
            UserDetails userDetails = authService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(Objects.nonNull(authHeader)){
            if(authHeader.startsWith("Basic")){
                throw new AuthenticationException("error.authentication");
            }
            if(authHeader.startsWith("Bearer")){
                return authHeader.substring(7);
            }
        }
        return null;
    }
}
