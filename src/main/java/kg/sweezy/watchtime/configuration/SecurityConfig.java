package kg.sweezy.watchtime.configuration;

import kg.sweezy.watchtime.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOriginPatterns(List.of("*"))
                    .setAllowedMethods(List.of("*"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers("/").permitAll();
                    authorizeRequests.requestMatchers("/api/auth/login").permitAll();
                    authorizeRequests.requestMatchers("/api/users/register").permitAll();
                    authorizeRequests.requestMatchers("/api/auth/get-current").permitAll();
                    authorizeRequests.requestMatchers("/api/users/get-all").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/profile-picture/upload-to-user").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/profile-picture/get-by-file-name").permitAll();
                    authorizeRequests.requestMatchers("/api/profile-picture/delete-by-user").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/upload-video").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-all").permitAll();
                    authorizeRequests.requestMatchers("/api/video/get-all-by-channel-id").permitAll();
                    authorizeRequests.requestMatchers("/api/video/stream-video-by-file-name").permitAll();
                    authorizeRequests.requestMatchers("/api/video/stream-preview-by-file-name").permitAll();
                    authorizeRequests.anyRequest().authenticated();
                }).sessionManagement(sessionManagement -> {
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });
        return http.build();
    }
}
