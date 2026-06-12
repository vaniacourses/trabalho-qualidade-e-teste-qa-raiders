package integration;

import DAO.DaoStatusLanchonete;
import DAO.DaoUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class DaoStatusLanchoneteIntegrationIT {

    @Test
    public void alterarELerStatusDaLanchonete() throws Exception {
        DaoStatusLanchonete dao = new DaoStatusLanchonete();
        int idAntes = ultimoIdStatus();

        try {
            dao.alterarStatus("ABERTO");
            String status = dao.getStatus();
            assertEquals("ABERTO", status, "Status deve ser ABERTO após alteração");

            dao.alterarStatus("FECHADO");
            String statusFechado = dao.getStatus();
            assertEquals("FECHADO", statusFechado, "Status deve ser FECHADO após nova alteração");

        } finally { 
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM tb_status_lanchonete WHERE id_status > ?");
                ps.setInt(1, idAntes);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) { 
            }
        }
    }
 
    private int ultimoIdStatus() {
        try (Connection conn = new DaoUtil().conecta()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(MAX(id_status), 0) AS ultimo FROM tb_status_lanchonete");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ultimo");
                rs.close();
                ps.close();
                return id;
            }
        } catch (Exception ex) { 
        }
        return 0;
    }
}
