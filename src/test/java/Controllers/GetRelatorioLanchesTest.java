package Controllers;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import Model.RelatorioLanches;
import DAO.DaoRelatorio;
import Helpers.ValidadorCookie;
import java.sql.SQLException;

public class GetRelatorioLanchesTest {

    private getRelatorioLanches servlet;

    @BeforeEach
    void setUp() {
        servlet = new getRelatorioLanches();
    }

    @Test
    void testSuccessfulGetWithValidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("tokenFuncionario");
        when(cookie.getValue()).thenReturn("validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        List<RelatorioLanches> sampleData = createSampleData();

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            when(mock.validarFuncionario(any())).thenReturn(true);
        })) {
            try (MockedConstruction<DaoRelatorio> mockedDao = mockConstruction(DaoRelatorio.class, (mock, context) -> {
                when(mock.listarRelLanches()).thenReturn(sampleData);
            })) {
                servlet.doGet(request, response);

                pw.flush();
                String output = sw.toString();
                Gson gson = new Gson();
                String expectedJson = gson.toJson(sampleData);
                Assertions.assertEquals(expectedJson, output);
                verify(response).setContentType("application/json");
                verify(response).setCharacterEncoding("UTF-8");
            }
        }
    }

    @Test
    void testSuccessfulPostWithValidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("tokenFuncionario");
        when(cookie.getValue()).thenReturn("validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        List<RelatorioLanches> sampleData = createSampleData();

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            when(mock.validarFuncionario(any())).thenReturn(true);
        })) {
            try (MockedConstruction<DaoRelatorio> mockedDao = mockConstruction(DaoRelatorio.class, (mock, context) -> {
                when(mock.listarRelLanches()).thenReturn(sampleData);
            })) {
                servlet.doPost(request, response);

                pw.flush();
                String output = sw.toString();
                Gson gson = new Gson();
                String expectedJson = gson.toJson(sampleData);
                Assertions.assertEquals(expectedJson, output);
                verify(response).setContentType("application/json");
                verify(response).setCharacterEncoding("UTF-8");
            }
        }
    }

    @Test
    void testInvalidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("tokenFuncionario");
        when(cookie.getValue()).thenReturn("invalidToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            when(mock.validarFuncionario(any())).thenReturn(false);
        })) {
            servlet.doGet(request, response);

            pw.flush();
            String output = sw.toString();
            Assertions.assertTrue(output.contains("erro"));
        }
    }

    @Test
    void testNoCookies() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        when(request.getCookies()).thenReturn(null);

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            doThrow(new NullPointerException()).when(mock).validarFuncionario((Cookie[]) isNull());
        })) {
            servlet.doGet(request, response);

            pw.flush();
            String output = sw.toString();
            Assertions.assertTrue(output.contains("erro"));
        }
    }

    @Test
    void testEmptyData() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("tokenFuncionario");
        when(cookie.getValue()).thenReturn("validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        List<RelatorioLanches> emptyData = new ArrayList<>();

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            when(mock.validarFuncionario(any())).thenReturn(true);
        })) {
            try (MockedConstruction<DaoRelatorio> mockedDao = mockConstruction(DaoRelatorio.class, (mock, context) -> {
                when(mock.listarRelLanches()).thenReturn(emptyData);
            })) {
                servlet.doGet(request, response);

                pw.flush();
                String output = sw.toString();
                Assertions.assertEquals("[]", output);
                verify(response).setContentType("application/json");
                verify(response).setCharacterEncoding("UTF-8");
            }
        }
    }

    @Test
    void testResponseHeaders() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("tokenFuncionario");
        when(cookie.getValue()).thenReturn("validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        List<RelatorioLanches> sampleData = createSampleData();

        try (MockedConstruction<ValidadorCookie> mockedValidar = mockConstruction(ValidadorCookie.class, (mock, context) -> {
            when(mock.validarFuncionario(any())).thenReturn(true);
        })) {
            try (MockedConstruction<DaoRelatorio> mockedDao = mockConstruction(DaoRelatorio.class, (mock, context) -> {
                when(mock.listarRelLanches()).thenReturn(sampleData);
            })) {
                servlet.doGet(request, response);

                verify(response).setContentType("application/json");
                verify(response).setCharacterEncoding("UTF-8");
            }
        }
    }

    private List<RelatorioLanches> createSampleData() {
        List<RelatorioLanches> list = new ArrayList<>();
        RelatorioLanches rl = new RelatorioLanches();
        rl.setLanche("X-Bacon");
        rl.setIngrediente("Bacon");
        rl.setQuantidade(2);
        rl.setCustoIngrediente(5.0f);
        rl.setVendaIngrediente(8.0f);
        rl.setLucroIngrediente(3.0f);
        rl.setCustoTotalIngredientes(15.0f);
        rl.setVendaTotalIngredientes(25.0f);
        rl.setLucroTotalIngredeintes(10.0f);
        rl.setLucroOperacional(7.0f);
        rl.setValorVenda(32.0f);
        rl.setLucroTotal(17.0f);
        list.add(rl);
        return list;
    }
}
