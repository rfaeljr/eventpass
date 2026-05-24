package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {
    Optional<Plano> findBySlug(String slug);
    List<Plano> findByAtivoTrue();
}
