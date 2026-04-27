package Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import DAO.DaoBebida;
import DAO.DaoCliente;
import DAO.DaoLanche;
import DAO.DaoPedido;
import Helpers.ValidadorCookie;
import Model.Bebida;
import Model.Cliente;
import Model.Lanche;
import Model.Pedido;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.http.*;
import java.io.*;

public class ComprarTest {
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter sw;

    @BeforeEach
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
    }

    private void mockInputStream(String json) throws Exception {
        when(request.getInputStream())
            .thenReturn(new ServletInputStreamMock(
                new BufferedReader(new StringReader(json))));
    }

    private Cookie[] cookieValido() {
        Cookie c = new Cookie("auth", "token-valido");
        return new Cookie[]{ c };
    }

    

    @Test
    public void compraBloqueadaSemCookies() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(null);

        
        try (MockedConstruction<ValidadorCookie> mocked = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(false))) {
            
            new comprar().doPost(request, response);
        }

        assertTrue(sw.toString().contains("erro"), "Esperado 'erro' quando não há cookies.");
    }

    @Test
    public void compraBloqueadaCookieInvalido() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> mocked = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(false))) {

            new comprar().doPost(request, response);
        }

        assertTrue(sw.toString().contains("erro"), "Esperado 'erro' quando cookie é inválido.");
    }

    

    @Test
    public void compraLancheAutenticado() throws Exception {
        mockInputStream("{\"id\":1, \"X-Burguer\":[\"X-Burguer\",\"lanche\",2]}");
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente(); clienteMock.setId_cliente(1);
        Lanche lancheMock = new Lanche(); lancheMock.setValor_venda(15.00);
        Pedido pedidoMock = new Pedido(); pedidoMock.setCliente(clienteMock);

        
        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            MockedConstruction<DaoLanche> dl = mockConstruction(DaoLanche.class, (m, c) -> when(m.pesquisaPorNome(anyString())).thenReturn(lancheMock));
            MockedConstruction<DaoPedido> dp = mockConstruction(DaoPedido.class, (m, c) -> when(m.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoMock))
        ) {
            new comprar().doPost(request, response);

            DaoPedido daoPedidoMock = dp.constructed().get(0);
            verify(daoPedidoMock, times(1)).vincularLanche(eq(pedidoMock), eq(lancheMock));
        }

        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"));
    }

    @Test
    public void compraBebidaAutenticada() throws Exception {
        mockInputStream("{\"id\":1, \"Coca-Cola\":[\"Coca-Cola\",\"bebida\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente(); clienteMock.setId_cliente(1);
        Bebida bebidaMock = new Bebida(); bebidaMock.setValor_venda(7.00);
        Pedido pedidoMock = new Pedido(); pedidoMock.setCliente(clienteMock);

        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            MockedConstruction<DaoBebida> db = mockConstruction(DaoBebida.class, (m, c) -> when(m.pesquisaPorNome(anyString())).thenReturn(bebidaMock));
            MockedConstruction<DaoPedido> dp = mockConstruction(DaoPedido.class, (m, c) -> when(m.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoMock))
        ) {
            new comprar().doPost(request, response);
        }

        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"));
    }

    @Test
    public void valorTotalCalculadoCorretamenteMultiplosItens() throws Exception {
        String json = "{\"id\":1, \"X-Burguer\":[\"X-Burguer\",\"lanche\",1], \"Coca-Cola\":[\"Coca-Cola\",\"bebida\",1]}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente();
        Lanche lancheMock = new Lanche(); lancheMock.setValor_venda(20.00);
        Bebida bebidaMock = new Bebida(); bebidaMock.setValor_venda(8.00);
        Pedido pedidoMock = new Pedido();

        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            MockedConstruction<DaoLanche> dl = mockConstruction(DaoLanche.class, (m, c) -> when(m.pesquisaPorNome(anyString())).thenReturn(lancheMock));
            MockedConstruction<DaoBebida> db = mockConstruction(DaoBebida.class, (m, c) -> when(m.pesquisaPorNome(anyString())).thenReturn(bebidaMock));
            MockedConstruction<DaoPedido> dp = mockConstruction(DaoPedido.class, (m, c) -> when(m.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoMock))
        ) {
            new comprar().doPost(request, response);

            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido).salvar(argThat(p -> Double.valueOf(28.00).equals(p.getValor_total())));
        }
    }

    

    @Test
    public void pagamentoIncompletoApenasId() throws Exception {
        mockInputStream("{\"id\":1}");
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente();
        Pedido pedidoMock = new Pedido();

        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            MockedConstruction<DaoPedido> dp = mockConstruction(DaoPedido.class, (m, c) -> when(m.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoMock))
        ) {
            new comprar().doPost(request, response);

            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido, never()).vincularLanche(any(), any());
            verify(daoPedido, never()).vincularBebida(any(), any());
            verify(daoPedido).salvar(argThat(p -> Double.valueOf(0.00).equals(p.getValor_total())));
        }

        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"), "Atenção: O sistema está permitindo salvar pedidos vazios.");
    }

    @Test
    public void jsonSemCampoId() throws Exception {
        mockInputStream("{\"X-Burguer\":[\"X-Burguer\",\"lanche\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true))) {
            assertThrows(Exception.class, () -> {
                new comprar().doPost(request, response);
            });
        }
    }

    @Test
    public void bodyVazioComCookieValido() throws Exception {
        mockInputStream("");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true))) {
            assertThrows(NullPointerException.class, () -> {
                new comprar().doPost(request, response);
            });
        }
    }

    @Test
    public void lancheInexistenteGeraNPE() throws Exception {
        
        mockInputStream("{\"id\":1, \"Lanche-Fantasma\":[\"Lanche-Fantasma\",\"lanche\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente(); 
        clienteMock.setId_cliente(1);

        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            
            MockedConstruction<DaoLanche> dl = mockConstruction(DaoLanche.class, (m, c) -> when(m.pesquisaPorNome(anyString())).thenReturn(null));
        ) {
            
            assertThrows(NullPointerException.class, () -> {
                new comprar().doPost(request, response);
            });
        }
    }

    @Test
    public void itemComCategoriaDesconhecidaIgnorado() throws Exception {
        
        mockInputStream("{\"id\":1, \"Pudim\":[\"Pudim\",\"sobremesa\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());

        Cliente clienteMock = new Cliente(); 
        clienteMock.setId_cliente(1);
        Pedido pedidoMock = new Pedido();

        try (
            MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true));
            MockedConstruction<DaoCliente> dc = mockConstruction(DaoCliente.class, (m, c) -> when(m.pesquisaPorID(anyString())).thenReturn(clienteMock));
            MockedConstruction<DaoPedido> dp = mockConstruction(DaoPedido.class, (m, c) -> when(m.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoMock))
        ) {
            new comprar().doPost(request, response);

            
            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido).salvar(argThat(p -> Double.valueOf(0.00).equals(p.getValor_total())));
        }
        
        
        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"));
    }
}