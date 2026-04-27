package Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        sw       = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
    }
 
    // Monta um InputStream simulado a partir de uma String JSON
    private void mockInputStream(String json) throws Exception {
        when(request.getInputStream())
            .thenReturn(new ServletInputStreamMock(
                new BufferedReader(new StringReader(json))));
    }
 


    @Test
    public void testCompraBloqueadaSemAuth() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Simula entrada mas sem cookies válidos
        when(request.getInputStream()).thenReturn(new ServletInputStreamMock(new BufferedReader(new StringReader("{}"))));
        when(request.getCookies()).thenReturn(null);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        new comprar().doPost(request, response);
        
        assert(sw.toString().contains("erro"));
    }

     private Cookie[] cookieValido() {
        Cookie c = new Cookie("auth", "token-valido");
        return new Cookie[]{ c };
    }

    //Cookie de login nao presente
    @Test
    public void compraBloqueadaSemCookies() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(null);
 
        new comprar().doPost(request, response);
 
        assertTrue(sw.toString().contains("erro"),
            "Esperado 'erro' quando não há cookies.");
    }
 

    // Cookie presente mas ValidadorCookie.validar() retorna false.
    @Test
    public void compraBloqueadaCookieInvalido() throws Exception {
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(cookieValido());
 
        try (MockedConstruction<ValidadorCookie> mocked =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(false))) {
 
            new comprar().doPost(request, response);
        }
 
        assertTrue(sw.toString().contains("erro"),
            "Esperado 'erro' quando cookie é inválido.");
    }
 
   
    // JSON contendo um lanche; cookie válido. 
    @Test
    public void compraLancheAutenticado() throws Exception {
        String json = "{\"id\":1, \"X-Burguer\":[\"X-Burguer\",\"lanche\",2]}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());
 
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
 
        Lanche lancheMock = new Lanche();
        lancheMock.setNome("X-Burguer");
        lancheMock.setValor_venda(15.00);
 
        Pedido pedidoMock = new Pedido();
        pedidoMock.setCliente(clienteMock);
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true));
             MockedConstruction<DaoCliente> dc =
                mockConstruction(DaoCliente.class,
                    (m, c) -> when(m.pesquisaPorID("1")).thenReturn(clienteMock));
             MockedConstruction<DaoLanche> dl =
                mockConstruction(DaoLanche.class,
                    (m, c) -> when(m.pesquisaPorNome("X-Burguer")).thenReturn(lancheMock));
             MockedConstruction<DaoPedido> dp =
                mockConstruction(DaoPedido.class, (m, c) -> {
                    doNothing().when(m).salvar(any());
                    when(m.pesquisaPorData(any())).thenReturn(pedidoMock);
                    doNothing().when(m).vincularLanche(any(), any());
                })) {
 
            new comprar().doPost(request, response);
        }
 
        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"),
            "Esperado confirmação de sucesso ao comprar lanche autenticado.");
    }
 
   //JSON contendo uma bebida; cookie válido.
    @Test
    public void compraBebidaAutenticada() throws Exception {
        String json = "{\"id\":1, \"Coca-Cola\":[\"Coca-Cola\",\"bebida\",1]}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());
 
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
 
        Bebida bebidaMock = new Bebida();
        bebidaMock.setNome("Coca-Cola");
        bebidaMock.setValor_venda(7.00);
 
        Pedido pedidoMock = new Pedido();
        pedidoMock.setCliente(clienteMock);
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true));
             MockedConstruction<DaoCliente> dc =
                mockConstruction(DaoCliente.class,
                    (m, c) -> when(m.pesquisaPorID("1")).thenReturn(clienteMock));
             MockedConstruction<DaoBebida> db =
                mockConstruction(DaoBebida.class,
                    (m, c) -> when(m.pesquisaPorNome("Coca-Cola")).thenReturn(bebidaMock));
             MockedConstruction<DaoPedido> dp =
                mockConstruction(DaoPedido.class, (m, c) -> {
                    doNothing().when(m).salvar(any());
                    when(m.pesquisaPorData(any())).thenReturn(pedidoMock);
                    doNothing().when(m).vincularBebida(any(), any());
                })) {
 
            new comprar().doPost(request, response);
        }
 
        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"),
            "Esperado confirmação de sucesso ao comprar bebida autenticada.");
    }
 
   
    // JSON com lanche e bebida simultaneamente; cookie válido.
    // Valida que ambos os itens são vinculados ao pedido.
    @Test
    public void compraMultiplosItens() throws Exception {
        String json = "{\"id\":1," +
                      "\"X-Burguer\":[\"X-Burguer\",\"lanche\",1]," +
                      "\"Suco\":[\"Suco\",\"bebida\",1]}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());
 
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
        Lanche  lancheMock  = new Lanche(); lancheMock.setValor_venda(15.00);
        Bebida  bebidaMock  = new Bebida(); bebidaMock.setValor_venda(6.00);
        Pedido  pedidoMock  = new Pedido(); pedidoMock.setCliente(clienteMock);
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true));
             MockedConstruction<DaoCliente> dc =
                mockConstruction(DaoCliente.class,
                    (m, c) -> when(m.pesquisaPorID("1")).thenReturn(clienteMock));
             MockedConstruction<DaoLanche> dl =
                mockConstruction(DaoLanche.class,
                    (m, c) -> when(m.pesquisaPorNome(any(Lanche.class))).thenReturn(lancheMock));
             MockedConstruction<DaoBebida> db =
                mockConstruction(DaoBebida.class,
                    (m, c) -> when(m.pesquisaPorNome(any())).thenReturn(bebidaMock));
             MockedConstruction<DaoPedido> dp =
                mockConstruction(DaoPedido.class, (m, c) -> {
                    doNothing().when(m).salvar(any());
                    when(m.pesquisaPorData(any())).thenReturn(pedidoMock);
                    doNothing().when(m).vincularLanche(any(), any());
                    doNothing().when(m).vincularBebida(any(), any());
                })) {
 
            new comprar().doPost(request, response);
 
            DaoPedido daoPedidoInstancia = dp.constructed().get(0);
            verify(daoPedidoInstancia, atLeastOnce()).vincularLanche(any(), any());
            verify(daoPedidoInstancia, atLeastOnce()).vincularBebida(any(), any());
        }
 
        assertTrue(sw.toString().contains("Pedido Salvo com Sucesso!"));
    }
 
    
    ///InputStream retorna linha nula (body vazio); cookie válido.
    @Test
    public void bodyVazioComCookieValido() throws Exception {
        when(request.getInputStream())
            .thenReturn(new ServletInputStreamMock(
                new BufferedReader(new StringReader(""))));
        when(request.getCookies()).thenReturn(cookieValido());
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true))) {
 
            assertDoesNotThrow(() -> new comprar().doPost(request, response));
        }
    }
 
    // JSON válido mas sem o campo obrigatório "id"; cookie válido.
    @Test
    public void jsonSemCampoId() throws Exception {
        mockInputStream("{\"X-Burguer\":[\"X-Burguer\",\"lanche\",1]}");
        when(request.getCookies()).thenReturn(cookieValido());
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true))) {
 
            assertDoesNotThrow(() -> new comprar().doPost(request, response));
        }
    }
 
    
    //  Verifica que o valor_total passado ao DaoPedido.salvar()
    // corresponde à soma dos valores dos itens
    @Test
    public void valorTotalCalculadoCorretamente() throws Exception {
        String json = "{\"id\":1," +
                      "\"X-Burguer\":[\"X-Burguer\",\"lanche\",1]," +
                      "\"Coca-Cola\":[\"Coca-Cola\",\"bebida\",1]}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());
 
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
        Lanche  lancheMock  = new Lanche(); lancheMock.setValor_venda(20.00);
        Bebida  bebidaMock  = new Bebida(); bebidaMock.setValor_venda(8.00);
        Pedido  pedidoMock  = new Pedido(); pedidoMock.setCliente(clienteMock);
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true));
             MockedConstruction<DaoCliente> dc =
                mockConstruction(DaoCliente.class,
                    (m, c) -> when(m.pesquisaPorID("1")).thenReturn(clienteMock));
             MockedConstruction<DaoLanche> dl =
                mockConstruction(DaoLanche.class,
                    (m, c) -> when(m.pesquisaPorNome(any(Lanche.class))).thenReturn(lancheMock));
             MockedConstruction<DaoBebida> db =
                mockConstruction(DaoBebida.class,
                    (m, c) -> when(m.pesquisaPorNome(any())).thenReturn(bebidaMock));
             MockedConstruction<DaoPedido> dp =
                mockConstruction(DaoPedido.class, (m, c) -> {
                    doNothing().when(m).salvar(any());
                    when(m.pesquisaPorData(any())).thenReturn(pedidoMock);
                    doNothing().when(m).vincularLanche(any(), any());
                    doNothing().when(m).vincularBebida(any(), any());
                })) {
 
            new comprar().doPost(request, response);
 
            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido).salvar(argThat(p ->
                Double.valueOf(28.00).equals(p.getValor_total())
            ));
        }
    }
 

 
    //Tentativa de finalizar preenchendo apenas o ID, sem nenhum item.
    //O pedido não deve ter itens vinculados nem retornar sucesso.
    @Test
    public void pagamentoIncompleto() throws Exception {
        // Apenas o ID, sem nenhum item (lanche ou bebida)
        String json = "{\"id\":1}";
        mockInputStream(json);
        when(request.getCookies()).thenReturn(cookieValido());
 
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
 
        Pedido pedidoMock = new Pedido();
        pedidoMock.setCliente(clienteMock);
 
        try (MockedConstruction<ValidadorCookie> vc =
                mockConstruction(ValidadorCookie.class,
                    (m, c) -> when(m.validar(any())).thenReturn(true));
             MockedConstruction<DaoCliente> dc =
                mockConstruction(DaoCliente.class,
                    (m, c) -> when(m.pesquisaPorID("1")).thenReturn(clienteMock));
             MockedConstruction<DaoPedido> dp =
                mockConstruction(DaoPedido.class, (m, c) -> {
                    doNothing().when(m).salvar(any());
                    when(m.pesquisaPorData(any())).thenReturn(pedidoMock);
                })) {
 
            new comprar().doPost(request, response);
 
            // Pedido sem itens: nenhum vínculo deve ter sido feito
            DaoPedido daoPedido = dp.constructed().get(0);
            verify(daoPedido, never()).vincularLanche(any(), any());
            verify(daoPedido, never()).vincularBebida(any(), any());
        }
 
        // Resultado esperado: sistema não deve confirmar pedido sem itens
        String saida = sw.toString();
        assertFalse(saida.contains("Pedido Salvo com Sucesso!"),
            "CT17: Pedido sem itens não deve ser finalizado com sucesso.");
    }
 
   
    // Tentativa de finalizar sem preencher absolutamente nada.
    @Test
    public void finalizacaoNulaSemDadosESemAuth() throws Exception {
        // Nenhum dado e nenhuma autenticação
        mockInputStream("{}");
        when(request.getCookies()).thenReturn(null);
 
        new comprar().doPost(request, response);
 
        String saida = sw.toString();
        assertTrue(saida.contains("erro"),
            "CT18: Sem dados e sem autenticação, o sistema deve retornar alerta de erro.");
        assertFalse(saida.contains("Pedido Salvo com Sucesso!"),
            "CT18: Finalização nula não deve confirmar pedido.");
    }
 
}