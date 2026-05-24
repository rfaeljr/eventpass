package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Evento;
import br.com.eventpass.domain.enums.StatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    Optional<Evento> findByUuid(String uuid);
    @Query(" SELECT e FROM Evento e JOIN FETCH e.cliente WHERE e.id = :id AND e.cliente.id = :clienteId ")
    Optional<Evento> findByIdAndClienteId(@Param("id") Long id, @Param("clienteId") Long clienteId);

    @Query(" SELECT e FROM Evento e JOIN FETCH e.cliente WHERE e.cliente.id = :clienteId ORDER BY e.criadoEm DESC ")
    Page<Evento> findByClienteIdOrderByCriadoEmDesc(@Param("clienteId") Long clienteId, Pageable pageable);
    long countByClienteId(Long clienteId);
    List<Evento> findByClienteIdAndStatus(Long clienteId, StatusEvento status);
}
