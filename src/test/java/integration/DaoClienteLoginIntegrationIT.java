package integration;

import DAO.DaoCliente;
import DAO.DaoUtil;
import Model.Cliente;
import Model.Endereco;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoClienteLoginIntegrationIT {

    @Test
    public void loginClienteComCredenciaisCorretas() throws Exception {
        String usuario = "cli" + (System.currentTimeMillis() % 1000000L);
        String senha = "senha123";

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente");
        cliente.setSobrenome("Teste");
        cliente.setTelefone("11999999999");
        cliente.setUsuario(usuario);
        cliente.setSenha(senha);
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setBairro("Bairro");
        endereco.setNumero(1);
        endereco.setComplemento("");
        endereco.setCidade("Cidade");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        DaoCliente dao = new DaoCliente();

        try {
            dao.salvar(cliente);

            Cliente credenciais = new Cliente();
            credenciais.setUsuario(usuario);
            credenciais.setSenha(senha);
            assertTrue(dao.login(credenciais),
                    "Login com credenciais corretas deve retornar true");

            Cliente credenciaisErradas = new Cliente();
            credenciaisErradas.setUsuario(usuario);
            credenciaisErradas.setSenha("senhaErrada");
            assertFalse(dao.login(credenciaisErradas),
                    "Login com senha errada deve retornar false");

            Cliente usuarioInexistente = new Cliente();
            usuarioInexistente.setUsuario("naoexiste");
            usuarioInexistente.setSenha(senha);
            assertFalse(dao.login(usuarioInexistente),
                    "Login com usuario inexistente deve retornar false");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM tb_clientes WHERE usuario = ?");
                ps.setString(1, usuario);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
            }
        }
    }
}
