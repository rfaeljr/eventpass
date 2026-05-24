package br.com.eventpass.domain.repository;

import br.com.eventpass.domain.entity.CodigoOtp;
import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.enums.FinalidadeOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CodigoOtpRepository extends JpaRepository<CodigoOtp, Long> {

    Optional<CodigoOtp> findTopByTelefoneAndFinalidadeAndUsadoEmIsNullOrderByCriadoEmDesc(
        String telefone, FinalidadeOtp finalidade);

    Optional<CodigoOtp> findByCliente(Cliente cliente);

    @Modifying
    @Query("DELETE FROM CodigoOtp c WHERE c.expiraEm < :agora")
    void deletarExpirados(LocalDateTime agora);
}
