package integration;

import DAO.DaoFuncionario;
import DAO.DaoUtil;
import Helpers.EncryptadorMD5;
import Model.Funcionario;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class DaoFuncionarioIntegrationIT {

    @Test
    public void salvarEPesquisarFuncionario() throws Exception {
        String usuario = "it_func_" + System.currentTimeMillis();

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("IT");
        funcionario.setSobrenome("Funcionario");
        funcionario.setUsuario(usuario);
        funcionario.setSenha("senha123");
        funcionario.setCargo("Atendente");
        funcionario.setSalario(1500.0);
        funcionario.setCad_por(1);  
        funcionario.setFg_ativo(1);

        DaoFuncionario dao = new DaoFuncionario();

        try {
            dao.salvar(funcionario);

            Funcionario resultado = dao.pesquisaPorUsuario(funcionario);
            assertNotNull(resultado, "Funcionario deve ser encontrado após salvar");
            assertTrue(resultado.getId() > 0, "id_funcionario deve ser maior que zero");
            assertEquals(usuario, resultado.getUsuario(), "usuario deve corresponder ao salvo");
            assertEquals("IT", resultado.getNome(), "nome deve corresponder ao salvo");
 
            EncryptadorMD5 md5 = new EncryptadorMD5();
            String hashEsperado = md5.encryptar("senha123");
            assertEquals(hashEsperado, resultado.getSenha(), "senha deve estar armazenada como MD5");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM tb_funcionarios WHERE usuario = ?");
                ps.setString(1, usuario);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) { 
            }
        }
    }

    @Test
    public void loginFuncionarioComCredenciaisCorretas() throws Exception {
        String usuario = "it_login_func_" + System.currentTimeMillis();

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Login");
        funcionario.setSobrenome("Teste");
        funcionario.setUsuario(usuario);
        funcionario.setSenha("minhasenha");
        funcionario.setCargo("Gerente");
        funcionario.setSalario(3000.0);
        funcionario.setCad_por(1);
        funcionario.setFg_ativo(1);

        DaoFuncionario dao = new DaoFuncionario();

        try {
            dao.salvar(funcionario);
 
            Funcionario credenciais = new Funcionario();
            credenciais.setUsuario(usuario);
            credenciais.setSenha("minhasenha");

            boolean loginOk = dao.login(credenciais);
            assertTrue(loginOk, "Login com credenciais corretas deve retornar true");
 
            Funcionario credenciaisErradas = new Funcionario();
            credenciaisErradas.setUsuario(usuario);
            credenciaisErradas.setSenha("senhaerrada");

            boolean loginFail = dao.login(credenciaisErradas);
            assertFalse(loginFail, "Login com senha errada deve retornar false");

        } finally {
            try (Connection conn = new DaoUtil().conecta()) {
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM tb_funcionarios WHERE usuario = ?");
                ps.setString(1, usuario);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) { 
            }
        }
    }
}
