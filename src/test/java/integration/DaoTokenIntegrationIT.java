package integration;

import DAO.DaoToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DaoTokenIntegrationIT {

    @Test
    public void salvarValidarERemoverToken() throws Exception {
        String token = "IT-token-" + System.currentTimeMillis();
        DaoToken dao = new DaoToken();

        try {
            assertFalse(dao.validar(token), "Token não deve existir antes de ser salvo");

            dao.salvar(token);
            assertTrue(dao.validar(token), "Token deve ser válido após ser salvo");

            dao.remover(token);
            assertFalse(dao.validar(token), "Token não deve mais ser válido após a remoção");

        } finally {
            try {
                dao.remover(token);
            } catch (Exception ex) {
            }
        }
    }

    @Test
    public void validarTokenInexistenteRetornaFalse() throws Exception {
        DaoToken dao = new DaoToken();
        String tokenInexistente = "IT-token-inexistente-" + System.currentTimeMillis();

        assertFalse(dao.validar(tokenInexistente),
                "Validar um token que nunca foi salvo deve retornar false");
    }
}
