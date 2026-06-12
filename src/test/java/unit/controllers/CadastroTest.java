package unit.controllers;


import Controllers.cadastro;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import Model.Cliente;
import Model.Endereco;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import DAO.DaoCliente;

@MockitoSettings(strictness = Strictness.LENIENT)
public class CadastroTest {
 
    private static final String JSON_VALIDO =
        "{\"endereco\":{\"bairro\":\"Centro\",\"cidade\":\"SP\",\"estado\":\"SP\"," +
        "\"complemento\":\"Apt\",\"rua\":\"Rua A\",\"numero\":10}," +
        "\"usuario\":{\"nome\":\"Joao\",\"sobrenome\":\"Silva\",\"telefone\":\"123\"," +
        "\"usuario\":\"joao\",\"senha\":\"123\"}}";

    private HttpServletRequest buildRequest(String json) throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(req.getInputStream()).thenReturn(new ServletInputStreamMock(reader));
        when(req.getMethod()).thenReturn("POST");
        return req;
    }
 
    @Test
    public void testProcessamentoCadastro() throws Exception {
        HttpServletRequest request = buildRequest(JSON_VALIDO);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<DaoCliente> mockedDao = mockConstruction(DaoCliente.class)) {
            cadastro servlet = new cadastro();
            servlet.service(request, response);

            String resultado = sw.toString();
            assertTrue(resultado.contains("Cadastrado!"), "A mensagem de retorno deveria ser de sucesso.");
 
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");

            DaoCliente daoInjetado = mockedDao.constructed().get(0);
            verify(daoInjetado, times(1)).salvar(any());
 
            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(daoInjetado).salvar(captor.capture());
            Cliente clienteSalvo = captor.getValue();

            assertEquals("Joao", clienteSalvo.getNome());
            assertEquals("Silva", clienteSalvo.getSobrenome());
            assertEquals("123", clienteSalvo.getTelefone());
            assertEquals("joao", clienteSalvo.getUsuario());
            assertEquals("123", clienteSalvo.getSenha());
            assertEquals(1, clienteSalvo.getFg_ativo());
            assertNotNull(clienteSalvo.getEndereco(), "EndereÃ§o deve ser setado no cliente");

            Endereco end = clienteSalvo.getEndereco();
            assertEquals("Centro", end.getBairro());
            assertEquals("SP", end.getCidade());
            assertEquals("SP", end.getEstado());
            assertEquals("Apt", end.getComplemento());
            assertEquals("Rua A", end.getRua());
            assertEquals(10, end.getNumero());
        }
    }
 
    @Test
    public void testDoGetChamaProcessRequest() throws Exception {
        HttpServletRequest request = buildRequest(JSON_VALIDO);
        when(request.getMethod()).thenReturn("GET");
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<DaoCliente> mockedDao = mockConstruction(DaoCliente.class)) {
            new cadastro().service(request, response);

            assertTrue(sw.toString().contains("Cadastrado!"),
                    "doGet deve delegar para processRequest e retornar sucesso");
        }
    }
 
    @Test
    public void testGetServletInfo_retornaDescricao() {
        String info = new cadastro().getServletInfo();
        assertNotNull(info, "getServletInfo nÃ£o deve retornar null");
        assertFalse(info.isEmpty(), "getServletInfo deve retornar uma String nÃ£o-vazia");
    }
}

