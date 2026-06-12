package unit.controllers;


import Controllers.comprar;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;

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
        when(request.getMethod()).thenReturn("POST");
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
            
            new comprar().service(request, response);
        }

        assertTrue(sw.toString().contains("erro"), "Esperado 'erro' quando nÃ£o hÃ¡ cookies.");
    }

    @Test
    public void compraBloqueadaCookieInvalido() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> mocked = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(false))) {

            new comprar().service(request, response);
        }

        assertTrue(sw.toString().contains("erro"), "Esperado 'erro' quando cookie Ã© invÃ¡lido.");
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
            new comprar().service(request, response);

            DaoPedido daoPedidoMock = dp.constructed().get(0);
            verify(daoPedidoMock, times(1)).vincularLanche(eq(pedidoMock), eq(lancheMock));

            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            assertEquals(2, lancheMock.getQuantidade(), "Quantidade do lanche deve ser populada do JSON");
            assertEquals(clienteMock, pedidoMock.getCliente(), "Cliente deve ser atribuÃ­do ao pedido apÃ³s pesquisaPorData");
            ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
            verify(daoPedidoMock).salvar(pedidoCaptor.capture());
            Pedido savedPedido = pedidoCaptor.getValue();
            assertNotNull(savedPedido.getData_pedido(), "data_pedido deve ser setada antes de salvar");
            assertEquals(clienteMock, savedPedido.getCliente(), "Cliente deve ser setado no pedido antes de salvar");
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
            new comprar().service(request, response);

            DaoPedido daoPedidoMock = dp.constructed().get(0);
 
            verify(daoPedidoMock, times(1)).vincularBebida(eq(pedidoMock), eq(bebidaMock));
            assertEquals(1, bebidaMock.getQuantidade(), "Quantidade da bebida deve ser populada do JSON");
            assertEquals(clienteMock, pedidoMock.getCliente(), "Cliente deve ser atribuÃ­do ao pedido apÃ³s pesquisaPorData");
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
            new comprar().service(request, response);

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
            new comprar().service(request, response);

            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido, never()).vincularLanche(any(), any());
            verify(daoPedido, never()).vincularBebida(any(), any());
            verify(daoPedido).salvar(argThat(p -> Double.valueOf(0.00).equals(p.getValor_total())));
        }

        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"), "AtenÃ§Ã£o: O sistema estÃ¡ permitindo salvar pedidos vazios.");
    }

    @Test
    public void jsonSemCampoId() throws Exception {
        mockInputStream("{\"X-Burguer\":[\"X-Burguer\",\"lanche\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true))) {
            assertThrows(Exception.class, () -> {
                new comprar().service(request, response);
            });
        }
    }

    @Test
    public void bodyVazioComCookieValido() throws Exception {
        mockInputStream("");
        when(request.getCookies()).thenReturn(cookieValido());

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, (m, c) -> when(m.validar(any())).thenReturn(true))) {
            assertThrows(NullPointerException.class, () -> {
                new comprar().service(request, response);
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
                new comprar().service(request, response);
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
            new comprar().service(request, response);


            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido).salvar(argThat(p -> Double.valueOf(0.00).equals(p.getValor_total())));
        }


        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"));
    }

    @Test
    public void doGet_redirecionaParaProcessRequest() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class,
                (m, c) -> when(m.validar(any())).thenReturn(false))) {
            new comprar().service(request, response);
        }

        assertTrue(sw.toString().contains("erro"), "doGet deve delegar para processRequest");
    }

    @Test
    public void getServletInfo_retornaDescricao() {
        String info = new comprar().getServletInfo();
        assertNotNull(info, "getServletInfo nÃ£o deve retornar null");
        assertFalse(info.isEmpty(), "getServletInfo deve retornar String nÃ£o-vazia");
    }
}
