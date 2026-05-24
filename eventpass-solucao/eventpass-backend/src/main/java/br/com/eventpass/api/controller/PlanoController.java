package br.com.eventpass.api.controller;

import br.com.eventpass.domain.entity.Plano;
import br.com.eventpass.domain.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoRepository planoRepository;

    @GetMapping
    public List<Plano> listar() {
        return planoRepository.findByAtivoTrue();
    }
}
