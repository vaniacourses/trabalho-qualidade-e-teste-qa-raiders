package integration;

import DAO.DaoCliente;
import DAO.DaoUtil;
import Helpers.EncryptadorMD5;
import Model.Cliente;
import Model.Endereco;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoClienteIntegrationIT {

    @Test
    public void salvarEPesquisarCliente() throws Exception {
        String usuario = "it_user_" + System.currentTimeMillis();

        Cliente cliente = new Cliente();
        cliente.setNome("IT");
        cliente.setSobrenome("User");
        cliente.setTelefone("123456789");
        cliente.setUsuario(usuario);
        cliente.setSenha("senha123");
        cliente.setFg_ativo(1);

        Endereco end = new Endereco();
        end.setRua("Rua Teste");
        end.setBairro("Bairro");
        end.setNumero(1);
        end.setComplemento("");
        end.setCidade("Cidade");
        end.setEstado("SP");
        cliente.setEndereco(end);

        DaoCliente dao = new DaoCliente();

        try {
            dao.salvar(cliente);

            Cliente resultado = dao.pesquisaPorUsuario(cliente);
            assertNotNull(resultado);
            assertEquals(usuario, resultado.getUsuario());
            assertTrue(resultado.getId_cliente() > 0, "id_cliente deve ser maior que zero");

            // Verifica hash MD5 da senha armazenada
            EncryptadorMD5 md5 = new EncryptadorMD5();
            String expectedHash = md5.encryptar(cliente.getSenha());
            assertEquals(expectedHash, resultado.getSenha(), "senha armazenada deve ser o hash MD5");

        } finally {
            // cleanup: remove o cliente criado
            try (Connection conn = new DaoUtil().conecta()){
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_clientes WHERE usuario = ?");
                ps.setString(1, usuario);
                ps.executeUpdate();
                ps.close();
            } catch(Exception ex){
                // não falhar o teste por causa do cleanup
            }
        }
    }

}
