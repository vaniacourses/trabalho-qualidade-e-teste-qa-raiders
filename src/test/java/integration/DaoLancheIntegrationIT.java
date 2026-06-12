package integration;

import DAO.DaoLanche;
import DAO.DaoUtil;
import Model.Lanche;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoLancheIntegrationIT {

    @Test
    public void salvarEPesquisarLanche() throws Exception {
        String nomeLanche = "it_lanche_" + System.currentTimeMillis();

        Lanche lanche = new Lanche();
        lanche.setNome(nomeLanche);
        lanche.setDescricao("Lanche de integração");
        lanche.setValor_venda(25.75);
        lanche.setFg_ativo(1);

        DaoLanche daoLanche = new DaoLanche();

        try {
            daoLanche.salvar(lanche);

            Lanche resultado = daoLanche.pesquisaPorNome(nomeLanche);
            assertNotNull(resultado, "Lanche deve ser encontrado após salvar");
            assertEquals(nomeLanche, resultado.getNome(), "Nome do lanche deve corresponder");
            assertEquals("Lanche de integração", resultado.getDescricao(), "Descrição deve corresponder");
            assertEquals(25.75, resultado.getValor_venda(), 0.001, "Valor de venda deve corresponder");
            assertTrue(resultado.getId_lanche() > 0, "id_lanche deve ser maior que zero");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_lanches WHERE nm_lanche = ?");
                ps.setString(1, nomeLanche);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
                // Cleanup não deve falhar o teste
            }
        }
    }
}
