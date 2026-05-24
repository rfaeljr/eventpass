package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.enums.StatusCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findBySlug(String slug);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM Cliente c WHERE c.status = 'ATIVO' AND c.trialExpiraEm < :agora AND c.trialExpiraEm IS NOT NULL")
    List<Cliente> findTrialsExpirados(LocalDateTime agora);
}
