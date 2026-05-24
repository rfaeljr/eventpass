package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Entrada;
import br.com.eventpass.domain.enums.StatusEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    List<Entrada> findByEventoIdOrderByRegistradoEmDesc(Long eventoId);
    Optional<Entrada> findTopByConvidadoIdAndStatusOrderByRegistradoEmDesc(Long convidadoId, StatusEntrada status);

    @Query("SELECT COUNT(e) FROM Entrada e WHERE e.evento.id = :eventoId AND e.status = 'LIBERADO'")
    long countEntradasLiberadas(Long eventoId);

    boolean existsByConvidadoIdAndStatus(Long convidadoId, StatusEntrada status);

    @Query("SELECT SUM(e.qtdAcompanhantes) FROM Entrada e WHERE e.evento.id = :eventoId AND e.status = 'LIBERADO'")
    Long somarAcompanhantes(Long eventoId);
}
