package integration;

import DAO.DaoStatusLanchonete;
import DAO.DaoUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoStatusLanchoneteIntegrationIT {

    @Test
    public void alterarEObterStatusLanchonete() throws Exception {
        String statusUnico = "IT_STATUS_" + System.currentTimeMillis();

        DaoStatusLanchonete daoStatus = new DaoStatusLanchonete();

        try {
            daoStatus.alterarStatus(statusUnico);

            String statusAtual = daoStatus.getStatus();
            assertEquals(statusUnico, statusAtual, "O status atual deve corresponder ao status alterado");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_status_lanchonete WHERE status = ?");
                ps.setString(1, statusUnico);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
                // Cleanup não deve falhar o teste
            }
        }
    }
}
