package integration;

import DAO.DaoCliente;
import DAO.DaoPedido;
import DAO.DaoUtil;
import Model.Cliente;
import Model.Endereco;
import Model.Pedido;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoPedidoIntegrationIT {

    @Test
    public void salvarPedidoUsandoClienteExistente() throws Exception {
        String usuario = "it_pedido_user_" + System.currentTimeMillis();
        String dataPedido = "2026-05-23 00:00:00-" + System.currentTimeMillis();

        Cliente cliente = new Cliente();
        cliente.setNome("IT Cliente");
        cliente.setSobrenome("Pedido");
        cliente.setTelefone("11999999999");
        cliente.setUsuario(usuario);
        cliente.setSenha("senhaTeste");
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua IT Pedido");
        endereco.setBairro("Bairro Teste");
        endereco.setNumero(1234);
        endereco.setComplemento("Apto 101");
        endereco.setCidade("Cidade Teste");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        DaoCliente daoCliente = new DaoCliente();
        DaoPedido daoPedido = new DaoPedido();

        try {
            daoCliente.salvar(cliente);

            Cliente clienteSalvo = daoCliente.pesquisaPorUsuario(cliente);
            assertNotNull(clienteSalvo, "Cliente deve ser encontrado após salvar");
            assertTrue(clienteSalvo.getId_cliente() > 0, "id_cliente deve ser maior que zero");

            Pedido pedido = new Pedido();
            pedido.setCliente(clienteSalvo);
            pedido.setData_pedido(dataPedido);
            pedido.setValor_total(59.90);

            daoPedido.salvar(pedido);

            Pedido pedidoBuscado = daoPedido.pesquisaPorData(pedido);
            assertNotNull(pedidoBuscado, "Pedido deve ser encontrado após salvar");
            assertEquals(dataPedido, pedidoBuscado.getData_pedido(), "data_pedido deve corresponder");
            assertEquals(59.90, pedidoBuscado.getValor_total(), 0.01, "valor_total deve corresponder");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement psPedido = conn.prepareStatement("DELETE FROM tb_pedidos WHERE data_pedido = ?");
                psPedido.setString(1, dataPedido);
                psPedido.executeUpdate();
                psPedido.close();

                PreparedStatement psCliente = conn.prepareStatement("DELETE FROM tb_clientes WHERE usuario = ?");
                psCliente.setString(1, usuario);
                psCliente.executeUpdate();
                psCliente.close();

                PreparedStatement psEndereco = conn.prepareStatement(
                        "DELETE FROM tb_enderecos WHERE rua = ? AND numero = ? AND bairro = ? AND cidade = ? AND estado = ?");
                psEndereco.setString(1, endereco.getRua());
                psEndereco.setInt(2, endereco.getNumero());
                psEndereco.setString(3, endereco.getBairro());
                psEndereco.setString(4, endereco.getCidade());
                psEndereco.setString(5, endereco.getEstado());
                psEndereco.executeUpdate();
                psEndereco.close();
            } catch (Exception ex) {
            }
        }
    }
}
