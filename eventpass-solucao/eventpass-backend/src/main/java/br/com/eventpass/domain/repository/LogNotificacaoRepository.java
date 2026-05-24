package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.LogNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogNotificacaoRepository extends JpaRepository<LogNotificacao, Long> {}
