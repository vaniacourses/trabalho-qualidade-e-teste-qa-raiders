package Controllers;

import DAO.DaoBebida;
import Model.Bebida;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.mockConstruction;

public class SalvarBebidaTest {

    @Test
    public void deveSalvarBebidaERetornarMensagemDeSucessoNoPost() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<DaoBebida> daoMocked = mockConstruction(DaoBebida.class)) {
            new salvarBebida().doPost(request, response);

            DaoBebida dao = daoMocked.constructed().get(0);
            ArgumentCaptor<Bebida> captor = ArgumentCaptor.forClass(Bebida.class);
            verify(dao).salvar(captor.capture());

            Bebida bebidaSalva = captor.getValue();
            assertEquals("Coca Cola", bebidaSalva.getNome());
            assertEquals("Refrigerante", bebidaSalva.getDescricao());
            assertEquals(15, bebidaSalva.getQuantidade());
            assertEquals(Double.valueOf(4.25), bebidaSalva.getValor_compra());
            assertEquals(Double.valueOf(7.50), bebidaSalva.getValor_venda());
            assertEquals("refrigerante", bebidaSalva.getTipo());
            assertEquals(1, bebidaSalva.getFg_ativo());
        }

        assertTrue(saida.toString().contains("Bebida Salva!"));
    }

    @Test
    public void deveSalvarBebidaERetornarMensagemDeSucessoNoGet() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter saida = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(saida));

        try (MockedConstruction<DaoBebida> daoMocked = mockConstruction(DaoBebida.class)) {
            new salvarBebida().doGet(request, response);
            assertEquals(1, daoMocked.constructed().size());
        }

        assertTrue(saida.toString().contains("Bebida Salva!"));
    }

    @Test
    public void deveDefinirJsonEUtf8NaResposta() throws Exception {
        HttpServletRequest request = mockRequestWithBody(jsonValido());
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        try (MockedConstruction<DaoBebida> daoMocked = mockConstruction(DaoBebida.class)) {
            new salvarBebida().doPost(request, response);
            assertEquals(1, daoMocked.constructed().size());
        }

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    public void deveLancarJSONExceptionQuandoJsonInvalido() throws Exception {
        HttpServletRequest request = mockRequestWithBody("nao-e-json");
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThrows(JSONException.class, () -> new salvarBebida().doPost(request, response));
    }

    @Test
    public void deveLancarJSONExceptionQuandoCampoObrigatorioAusente() throws Exception {
        String jsonSemTipo = "{\"nome\":\"Coca Cola\",\"descricao\":\"Refrigerante\",\"quantidade\":15,\"ValorCompra\":\"4.25\",\"ValorVenda\":\"7.50\"}";
        HttpServletRequest request = mockRequestWithBody(jsonSemTipo);
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThrows(JSONException.class, () -> new salvarBebida().doPost(request, response));
    }

    @Test
    public void deveLancarNumberFormatExceptionQuandoValorCompraNaoNumerico() throws Exception {
        String jsonComValorCompraInvalido = "{\"nome\":\"Coca Cola\",\"descricao\":\"Refrigerante\",\"quantidade\":15,\"ValorCompra\":\"abc\",\"ValorVenda\":\"7.50\",\"tipo\":\"refrigerante\"}";
        HttpServletRequest request = mockRequestWithBody(jsonComValorCompraInvalido);
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThrows(NumberFormatException.class, () -> new salvarBebida().doPost(request, response));
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
                // Não utilizado nos cenários de teste síncronos.
            }
        };

        when(request.getInputStream()).thenReturn(servletInputStream);
        return request;
    }

    private String jsonValido() {
        return "{\"nome\":\"Coca Cola\",\"descricao\":\"Refrigerante\",\"quantidade\":15,\"ValorCompra\":\"4.25\",\"ValorVenda\":\"7.50\",\"tipo\":\"refrigerante\"}";
    }
}
