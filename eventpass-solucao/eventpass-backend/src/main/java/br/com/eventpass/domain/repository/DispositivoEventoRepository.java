package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.DispositivoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoEventoRepository extends JpaRepository<DispositivoEvento, Long> {
    Optional<DispositivoEvento> findByDispositivoIdAndEventoId(Long dispositivoId, Long eventoId);
    List<DispositivoEvento> findByEventoIdAndAtivoTrue(Long eventoId);

    @Modifying
    @Query("UPDATE DispositivoEvento de SET de.ativo = false WHERE de.dispositivo.id = :dispositivoId AND de.ativo = true")
    void desativarPorDispositivo(Long dispositivoId);
}
