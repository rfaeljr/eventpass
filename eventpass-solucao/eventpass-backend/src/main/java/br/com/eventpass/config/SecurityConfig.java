package br.com.eventpass.config;

import br.com.eventpass.infrastructure.security.JwtAuthFilter;
import br.com.eventpass.infrastructure.security.DeviceTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final DeviceTokenFilter deviceTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas
                .requestMatchers(HttpMethod.POST, "/auth/cadastro/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/verificar-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/reenviar-otp").permitAll()
                .requestMatchers(HttpMethod.GET,  "/planos").permitAll()
                .requestMatchers(HttpMethod.GET,  "/convites/{uuid}").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Rotas do dispositivo (app portaria)
                .requestMatchers("/portaria/**").hasAuthority("DEVICE")
                .requestMatchers("/dispositivos/pareamento/**").permitAll()
                // Tudo mais exige auth de usuário
                .anyRequest().hasAuthority("CLIENTE")
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(deviceTokenFilter, JwtAuthFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
