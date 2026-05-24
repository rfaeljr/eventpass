package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Dispositivo;
import br.com.eventpass.domain.enums.StatusDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    Optional<Dispositivo> findByTokenDispositivo(String token);
    Optional<Dispositivo> findByTokenPareamento(String token);
    Optional<Dispositivo> findByIdAndClienteId(Long id, Long clienteId);
    List<Dispositivo> findByClienteId(Long clienteId);
    long countByClienteIdAndStatus(Long clienteId, StatusDispositivo status);
}
