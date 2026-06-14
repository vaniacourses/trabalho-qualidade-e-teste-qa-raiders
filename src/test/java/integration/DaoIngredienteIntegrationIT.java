package integration;

import DAO.DaoIngrediente;
import Model.Ingrediente;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DaoIngredienteIntegrationIT {

    @Test
    public void salvarListarERemoverIngrediente() throws Exception {
        String nome = "IT Ingrediente " + System.currentTimeMillis();

        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome(nome);
        ingrediente.setDescricao("Ingrediente criado pelo teste de integração");
        ingrediente.setQuantidade(15);
        ingrediente.setValor_compra(1.20);
        ingrediente.setValor_venda(3.40);
        ingrediente.setTipo("queijo");
        ingrediente.setFg_ativo(1);

        DaoIngrediente dao = new DaoIngrediente();

        try {
            dao.salvar(ingrediente);

            List<Ingrediente> lista = dao.listarTodos();
            assertNotNull(lista, "Lista de ingredientes não deve ser null");
            assertFalse(lista.isEmpty(), "Lista deve conter ao menos o ingrediente recém-salvo");

            Ingrediente salvo = lista.stream()
                    .filter(i -> nome.equals(i.getNome()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(salvo, "Ingrediente salvo deve aparecer na listagem");
            assertEquals(nome, salvo.getNome(), "Nome deve corresponder");
            assertEquals(15, salvo.getQuantidade(), "Quantidade deve corresponder");
            assertEquals(3.40, salvo.getValor_venda(), 0.001, "Valor de venda deve corresponder");

            dao.remover(salvo);

            boolean aindaPresente = dao.listarTodos().stream()
                    .anyMatch(i -> nome.equals(i.getNome()));
            assertFalse(aindaPresente, "Ingrediente removido não deve mais aparecer na listagem");

        } catch (Exception e) {
            try {
                Ingrediente busca = new Ingrediente();
                busca.setNome(nome);
                Ingrediente paraRemover = dao.pesquisaPorNome(busca);
                if (paraRemover.getId_ingrediente() > 0) {
                    dao.remover(paraRemover);
                }
            } catch (Exception ex) {
            }
            throw e;
        }
    }

    @Test
    public void alterarIngrediente() throws Exception {
        String nome = "IT Ingrediente Alt " + System.currentTimeMillis();

        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome(nome);
        ingrediente.setDescricao("Ingrediente original");
        ingrediente.setQuantidade(5);
        ingrediente.setValor_compra(1.00);
        ingrediente.setValor_venda(2.00);
        ingrediente.setTipo("molho");
        ingrediente.setFg_ativo(1);

        DaoIngrediente dao = new DaoIngrediente();
        Ingrediente salvo = null;

        try {
            dao.salvar(ingrediente);

            Ingrediente busca = new Ingrediente();
            busca.setNome(nome);
            salvo = dao.pesquisaPorNome(busca);
            assertNotNull(salvo, "Ingrediente deve ser encontrado após salvar");
            assertTrue(salvo.getId_ingrediente() > 0, "id_ingrediente deve ser maior que zero");

            salvo.setQuantidade(42);
            salvo.setValor_venda(9.90);
            salvo.setDescricao("Ingrediente atualizado");
            dao.alterar(salvo);

            Ingrediente alterado = dao.pesquisaPorNome(busca);
            assertEquals(42, alterado.getQuantidade(), "Quantidade deve ter sido atualizada");
            assertEquals(9.90, alterado.getValor_venda(), 0.001, "Valor de venda deve ter sido atualizado");

        } finally {
            try {
                if (salvo != null && salvo.getId_ingrediente() > 0) {
                    dao.remover(salvo);
                }
            } catch (Exception ex) {
            }
        }
    }
}
