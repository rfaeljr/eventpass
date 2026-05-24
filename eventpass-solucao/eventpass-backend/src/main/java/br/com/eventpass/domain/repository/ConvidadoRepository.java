package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Convidado;
import br.com.eventpass.domain.enums.StatusConvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvidadoRepository extends JpaRepository<Convidado, Long> {
    Optional<Convidado> findByUuid(String uuid);
    Optional<Convidado> findByIdAndEventoId(Long id, Long eventoId);
    Page<Convidado> findByEventoId(Long eventoId, Pageable pageable);
    long countByEventoId(Long eventoId);
    List<Convidado> findByEventoIdAndStatusConvite(Long eventoId, StatusConvite status);

    @Query("SELECT c FROM Convidado c WHERE c.evento.id = :eventoId AND LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Convidado> buscarPorNome(Long eventoId, String nome);
}
