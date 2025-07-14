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
                    authorizeRequests.requestMatchers("/api/auth/get-current").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/auth/recovery-password").permitAll();
                    authorizeRequests.requestMatchers("/api/auth/confirm-code/**").permitAll();

                    authorizeRequests.requestMatchers("/api/users/register").permitAll();
                    authorizeRequests.requestMatchers("/api/users/get-by-id/**").permitAll();
                    authorizeRequests.requestMatchers("/api/users/get-all").permitAll();
                    authorizeRequests.requestMatchers("/api/users/subscribe").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/users/get-subscription-channels").hasAnyAuthority("ACTIVE", "MUTED");

                    authorizeRequests.requestMatchers("/api/profile-picture/upload-to-user").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/profile-picture/get-by-file-name").permitAll();
                    authorizeRequests.requestMatchers("/api/profile-picture/delete-by-user").hasAnyAuthority("ACTIVE", "MUTED");

                    authorizeRequests.requestMatchers("/api/video/upload-video").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-by-id/**").permitAll();
                    authorizeRequests.requestMatchers("/api/video/get-all").permitAll();
                    authorizeRequests.requestMatchers("/api/video/get-all-by-channel-id").permitAll();
                    authorizeRequests.requestMatchers("/api/video/stream-video-by-file-name").permitAll();
                    authorizeRequests.requestMatchers("/api/video/stream-preview-by-file-name").permitAll();
                    authorizeRequests.requestMatchers("/api/video/add-comment-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/like-video-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/delete-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/dislike-video-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/add-to-play-list-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/remove-from-play-list-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-liked-videos").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-play-list").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/video/get-all-comments-by-video-id").permitAll();
                    authorizeRequests.requestMatchers("/api/video/get-videos-by-title").permitAll();

                    authorizeRequests.requestMatchers("/api/comment/delete-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/comment/add-comment-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/comment/update-comment-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/comment/like-by-id").hasAnyAuthority("ACTIVE", "MUTED");
                    authorizeRequests.requestMatchers("/api/comment/dislike-by-id").hasAnyAuthority("ACTIVE", "MUTED");

                    authorizeRequests.requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/v3/api-docs",
                            "/swagger-resources/**",
                            "/swagger-resources",
                            "/webjars/**"
                    ).permitAll();

                    authorizeRequests.anyRequest().authenticated();
                }).sessionManagement(sessionManagement -> {
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });
        return http.build();
    }
}
