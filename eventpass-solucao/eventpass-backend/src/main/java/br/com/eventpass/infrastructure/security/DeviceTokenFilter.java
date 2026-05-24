package br.com.eventpass.infrastructure.security;

import br.com.eventpass.domain.repository.DispositivoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceTokenFilter extends OncePerRequestFilter {

    private final DispositivoRepository dispositivoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String deviceToken = request.getHeader("X-Device-Token");
        if (deviceToken == null || deviceToken.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        dispositivoRepository.findByTokenDispositivo(deviceToken)
            .filter(d -> d.estaAtivo())
            .ifPresent(dispositivo -> {
                dispositivo.registrarAcesso(
                    request.getHeader("X-Device-Model"),
                    request.getHeader("X-App-Version")
                );
                var auth = new UsernamePasswordAuthenticationToken(
                    dispositivo.getId(), null,
                    List.of(new SimpleGrantedAuthority("DEVICE"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            });

        chain.doFilter(request, response);
    }
}
