package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Assinatura;
import br.com.eventpass.domain.enums.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    Optional<Assinatura> findByClienteIdAndStatus(Long clienteId, StatusAssinatura status);
    Optional<Assinatura> findTopByClienteIdOrderByCriadoEmDesc(Long clienteId);
}
