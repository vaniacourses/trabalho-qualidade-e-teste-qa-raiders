package integration;

import DAO.DaoCliente;
import DAO.DaoFuncionario;
import DAO.DaoToken;
import Model.Cliente;
import Model.Funcionario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SegurancaIntegrationIT {

    @Test
    public void loginClienteComSqlInjectionDeveRetornarFalse() throws Exception {
        DaoCliente dao = new DaoCliente();

        Cliente credenciais = new Cliente();
        credenciais.setUsuario("' OR '1'='1");
        credenciais.setSenha("' OR '1'='1");

        assertFalse(dao.login(credenciais),
                "SQL injection no login de cliente não deve autenticar");
    }

    @Test
    public void loginClienteComComentarioSqlDeveRetornarFalse() throws Exception {
        DaoCliente dao = new DaoCliente();

        Cliente credenciais = new Cliente();
        credenciais.setUsuario("admin'--");
        credenciais.setSenha("qualquer");

        assertFalse(dao.login(credenciais),
                "Comentário SQL no campo usuario do cliente não deve autenticar");
    }

    @Test
    public void loginFuncionarioComSqlInjectionDeveRetornarFalse() throws Exception {
        DaoFuncionario dao = new DaoFuncionario();

        Funcionario credenciais = new Funcionario();
        credenciais.setUsuario("' OR '1'='1");
        credenciais.setSenha("' OR '1'='1");

        assertFalse(dao.login(credenciais),
                "SQL injection no login de funcionario não deve autenticar");
    }

    @Test
    public void validarTokenComSqlInjectionDeveRetornarFalse() throws Exception {
        DaoToken dao = new DaoToken();
        String tokenMalformado = "' OR '1'='1; DROP TABLE tb_tokens;--";

        assertFalse(dao.validar(tokenMalformado),
                "Token com SQL injection não deve ser considerado válido");
    }
}
