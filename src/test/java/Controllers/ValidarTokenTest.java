package Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import Helpers.ValidadorCookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.http.*;
import java.io.*;

public class ValidarTokenTest {

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

    @Test
    public void testTokenValido() throws Exception {
        
        Cookie[] cookiesSimulados = new Cookie[]{ new Cookie("auth", "token_valido_123") };
        when(request.getCookies()).thenReturn(cookiesSimulados);

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(true))) {
            
            new validarToken().doPost(request, response);
        }

        
        assertFalse(sw.toString().contains("Invalido"), "Um token válido não deve retornar mensagem de 'Invalido'");
    }

    @Test
    public void testTokenInexistente() throws Exception {
        
        when(request.getCookies()).thenReturn(null); 

        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(false))) {
            
            new validarToken().doPost(request, response);
        }

        
        verify(request, atLeastOnce()).getCookies();
        
        
        
        assertTrue(sw.toString().contains("Invalido") || sw.toString().contains("erro"), "Sem cookies, a resposta deve indicar erro");
    }

    @Test
    public void testTokenExpirado() throws Exception {
        
        Cookie[] cookiesSimulados = new Cookie[]{ new Cookie("auth", "expirado") };
        when(request.getCookies()).thenReturn(cookiesSimulados);

        
        try (MockedConstruction<ValidadorCookie> vc = mockConstruction(ValidadorCookie.class, 
            (mock, context) -> when(mock.validar(any())).thenReturn(false))) {
            
            new validarToken().doPost(request, response);
        }

        
        assertTrue(sw.toString().contains("Invalido") || sw.toString().contains("erro"), "Token expirado deve retornar erro");
    }
}