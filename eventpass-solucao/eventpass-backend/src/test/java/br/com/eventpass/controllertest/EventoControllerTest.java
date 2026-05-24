package br.com.eventpass.controllertest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("EventoController - Testes de Endpoint")
@Sql(scripts = {"/db/limpardados-db.sql","/db/import.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EventoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("GET /eventos/2 deve retornar o evento e o cliente sem erro de Lazy")
    void deveRetornarEventoComSucesso() throws Exception {
        mockMvc.perform(get("/eventos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.cliente.id").value(2));
    }

    @Test
    @DisplayName("GET /eventos/2 sem token deve retornar 403 (ou 401)")
    void deveRetornarErroSemAutenticacao() throws Exception {
        // Aqui não usamos @WithMockUser para testar a segurança
        mockMvc.perform(get("/eventos/2"))
                .andExpect(status().isForbidden());
    }
}
