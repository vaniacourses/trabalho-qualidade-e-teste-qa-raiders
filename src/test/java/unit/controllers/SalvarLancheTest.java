package unit.controllers;


import Controllers.salvarLanche;
import DAO.DaoIngrediente;
import DAO.DaoLanche;
import Helpers.ValidadorCookie;
import Model.Ingrediente;
import Model.Lanche;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.mockConstruction;

public class SalvarLancheTest {

    @Test
    public void deveSalvarLancheEVincularIngredientesQuandoFuncionarioAutorizado() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true));
                MockedConstruction<DaoLanche> lancheDaoMocked = mockConstruction(DaoLanche.class,
                        (mock, context) -> when(mock.pesquisaPorNome(any(Lanche.class))).thenReturn(lancheComId()));
                MockedConstruction<DaoIngrediente> ingredienteDaoMocked = mockConstruction(DaoIngrediente.class,
                        (mock, context) -> when(mock.pesquisaPorNome(any(Ingrediente.class))).thenAnswer(invocation -> {
                            Ingrediente recebido = invocation.getArgument(0);
                            Ingrediente retorno = new Ingrediente();
                            retorno.setNome(recebido.getNome());
                            retorno.setId_ingrediente(recebido.getNome().equals("queijo") ? 1 : 2);
                            return retorno;
                        }))) {

            new salvarLanche().service(request, response);

            DaoLanche daoLanche = lancheDaoMocked.constructed().get(0);
            ArgumentCaptor<Lanche> lancheCaptor = ArgumentCaptor.forClass(Lanche.class);
            verify(daoLanche).salvar(lancheCaptor.capture());

            Lanche lancheSalvo = lancheCaptor.getValue();
            assertEquals("X-Burger", lancheSalvo.getNome());
            assertEquals("Lanche com queijo", lancheSalvo.getDescricao());
            assertEquals(Double.valueOf(22.5), lancheSalvo.getValor_venda());

            verify(daoLanche, times(2)).vincularIngrediente(any(Lanche.class), any(Ingrediente.class));
            assertEquals(1, validadorMocked.constructed().size());
            assertEquals(1, ingredienteDaoMocked.constructed().size());
        }

        assertTrue(saida.toString().contains("Lanche Salvo com Sucesso!"));
    }

    @Test
    public void deveRetornarErroQuandoFuncionarioNaoAutorizado() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(false));
                MockedConstruction<DaoLanche> lancheDaoMocked = mockConstruction(DaoLanche.class);
                MockedConstruction<DaoIngrediente> ingredienteDaoMocked = mockConstruction(DaoIngrediente.class)) {

            new salvarLanche().service(request, response);

            assertEquals(1, validadorMocked.constructed().size());
            assertEquals(0, lancheDaoMocked.constructed().size());
            assertEquals(0, ingredienteDaoMocked.constructed().size());
        }

        assertTrue(saida.toString().contains("erro"));
    }

    @Test
    public void deveRetornarErroQuandoCookiesForemNulos() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(null);
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any())).thenThrow(new NullPointerException()));
                MockedConstruction<DaoLanche> lancheDaoMocked = mockConstruction(DaoLanche.class)) {
            new salvarLanche().service(request, response);
            assertEquals(1, validadorMocked.constructed().size());
            assertEquals(0, lancheDaoMocked.constructed().size());
        }

        assertTrue(saida.toString().contains("erro"));
    }

    @Test
    public void deveConfigurarContentTypeEEncodingDaResposta() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(false))) {
            new salvarLanche().service(request, response);
            assertEquals(1, validadorMocked.constructed().size());
        }

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    public void deveLancarJSONExceptionQuandoJsonInvalidoMesmoAutorizado() throws Exception {
        HttpServletRequest request = mockRequestWithBody("nao-e-json");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true))) {
            assertThrows(JSONException.class, () -> new salvarLanche().service(request, response));
            assertEquals(1, validadorMocked.constructed().size());
        }
    }

    @Test
    public void deveProcessarDoGetDaMesmaFormaQueDoPost() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true));
                MockedConstruction<DaoLanche> lancheDaoMocked = mockConstruction(DaoLanche.class,
                        (mock, context) -> when(mock.pesquisaPorNome(any(Lanche.class))).thenReturn(lancheComId()));
                MockedConstruction<DaoIngrediente> ingredienteDaoMocked = mockConstruction(DaoIngrediente.class,
                        (mock, context) -> when(mock.pesquisaPorNome(any(Ingrediente.class)))
                                .thenReturn(ingredienteComId(1)))) {

            new salvarLanche().service(request, response);

            assertEquals(1, validadorMocked.constructed().size());
            assertEquals(1, lancheDaoMocked.constructed().size());
            assertEquals(1, ingredienteDaoMocked.constructed().size());
        }

        assertTrue(saida.toString().contains("Lanche Salvo com Sucesso!"));
    }

     @Test
    public void deveSalvarLancheSemVincularIngredientesQuandoListaVazia() throws Exception { 
        String jsonSemIngredientes = "{\"nome\":\"X-Burger\",\"descricao\":\"Lanche com queijo\",\"ValorVenda\":22.5,\"ingredientes\":{}}";
        HttpServletRequest request = mockRequestWithBody(jsonSemIngredientes);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));
 
        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true));
                MockedConstruction<DaoLanche> lancheDaoMocked = mockConstruction(DaoLanche.class,
                        (mock, context) -> when(mock.pesquisaPorNome(any(Lanche.class))).thenReturn(lancheComId()));
                MockedConstruction<DaoIngrediente> ingredienteDaoMocked = mockConstruction(DaoIngrediente.class)) {
 
            new salvarLanche().service(request, response);
 
            DaoLanche daoLanche = lancheDaoMocked.constructed().get(0);
  
            verify(daoLanche).salvar(any(Lanche.class));
  
            verify(daoLanche, times(0)).vincularIngrediente(any(Lanche.class), any(Ingrediente.class));
        }
 
        assertTrue(saida.toString().contains("Lanche Salvo com Sucesso!"),
            "Lanche sem ingredientes ainda deve ser salvo com sucesso.");
    }
 
    @Test
    public void deveLancarExcecaoQuandoJsonNaoContemCampoNome() throws Exception { 
        String jsonSemNome = "{\"descricao\":\"Lanche com queijo\",\"ValorVenda\":22.5,\"ingredientes\":{\"queijo\":2}}";
        HttpServletRequest request = mockRequestWithBody(jsonSemNome);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("tokenFuncionario", "abc") });
 
        try (MockedConstruction<ValidadorCookie> validadorMocked = mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true))) {
 
            assertThrows(JSONException.class, () -> new salvarLanche().service(request, response),
                "CT18: JSON sem campo 'nome' deve lanÃ§ar JSONException.");
        }
    }
 

    private HttpServletRequest mockRequestWithBody(String body) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() {
                return input.read();
            }

            @Override
            public boolean isFinished() {
                return input.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) { 
            }
        };

        when(request.getInputStream()).thenReturn(servletInputStream);
        when(request.getMethod()).thenReturn("POST");
        return request;
    }

    private String jsonValido() {
        return "{\"nome\":\"X-Burger\",\"descricao\":\"Lanche com queijo\",\"ValorVenda\":22.5,\"ingredientes\":{\"queijo\":2,\"pao\":1}}";
    }

    private Lanche lancheComId() {
        Lanche lanche = new Lanche();
        lanche.setId_lanche(99);
        lanche.setNome("X-Burger");
        return lanche;
    }

    private Ingrediente ingredienteComId(int id) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setId_ingrediente(id);
        return ingrediente;
    }
}

