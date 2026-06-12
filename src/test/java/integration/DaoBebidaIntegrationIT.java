package integration;

import DAO.DaoBebida;
import Model.Bebida;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DaoBebidaIntegrationIT {

    @Test
    public void salvarListarERemoverBebida() throws Exception {
        String nomeBebida = "IT Suco " + System.currentTimeMillis();

        Bebida bebida = new Bebida();
        bebida.setNome(nomeBebida);
        bebida.setDescricao("Bebida criada pelo teste de integração");
        bebida.setQuantidade(10);
        bebida.setValor_compra(2.50);
        bebida.setValor_venda(6.00);
        bebida.setTipo("Suco");
        bebida.setFg_ativo(1);

        DaoBebida dao = new DaoBebida();

        try {
            dao.salvar(bebida);
 
            List<Bebida> lista = dao.listarTodos();
            assertNotNull(lista, "Lista de bebidas não deve ser null");
            assertFalse(lista.isEmpty(), "Lista deve conter ao menos a bebida recém-salva");

            Bebida salva = lista.stream()
                    .filter(b -> nomeBebida.equals(b.getNome()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(salva, "Bebida salva deve aparecer na listagem");
            assertEquals(nomeBebida, salva.getNome(), "Nome deve corresponder");
            assertEquals(10, salva.getQuantidade(), "Quantidade deve corresponder");
            assertEquals(6.00, salva.getValor_venda(), 0.001, "Valor de venda deve corresponder");
 
            dao.remover(salva);

            List<Bebida> listaAposRemocao = dao.listarTodos();
            boolean aindaPresente = listaAposRemocao.stream()
                    .anyMatch(b -> nomeBebida.equals(b.getNome()));
            assertFalse(aindaPresente, "Bebida removida não deve mais aparecer na listagem");

        } catch (Exception e) { 
            try {
                Bebida paraRemover = dao.pesquisaPorNome(nomeBebida);
                if (paraRemover.getId_bebida() > 0) {
                    dao.remover(paraRemover);
                }
            } catch (Exception ex) { 
            }
            throw e;
        }
    }

    @Test
    public void alterarBebida() throws Exception {
        String nomeBebida = "IT Refrigerante " + System.currentTimeMillis();

        Bebida bebida = new Bebida();
        bebida.setNome(nomeBebida);
        bebida.setDescricao("Refrigerante original");
        bebida.setQuantidade(5);
        bebida.setValor_compra(1.50);
        bebida.setValor_venda(4.00);
        bebida.setTipo("Refrigerante");
        bebida.setFg_ativo(1);

        DaoBebida dao = new DaoBebida();
        Bebida salva = null;

        try {
            dao.salvar(bebida);
            salva = dao.pesquisaPorNome(nomeBebida);
            assertNotNull(salva, "Bebida deve ser encontrada após salvar");
            assertTrue(salva.getId_bebida() > 0, "id_bebida deve ser maior que zero");
 
            salva.setQuantidade(20);
            salva.setValor_venda(5.50);
            salva.setDescricao("Refrigerante atualizado");
            dao.alterar(salva);
 
            Bebida alterada = dao.pesquisaPorNome(nomeBebida);
            assertEquals(20, alterada.getQuantidade(), "Quantidade deve ter sido atualizada");
            assertEquals(5.50, alterada.getValor_venda(), 0.001, "Valor de venda deve ter sido atualizado");

        } finally {
            try {
                if (salva != null && salva.getId_bebida() > 0) {
                    dao.remover(salva);
                }
            } catch (Exception ex) { 
            }
        }
    }
}
