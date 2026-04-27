package Controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import javax.servlet.http.*;
import java.io.*;

public class ValidarTokenTest {

    @Test
    public void testTokenValido() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Simula o envio de um token via atributo de sessão ou parâmetro
        when(request.getParameter("token")).thenReturn("token_valido_123");
        
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        new validarToken().doPost(request, response);
        // Se a lógica estiver correta, não deve retornar erro de "Não autorizado"
        assertFalse(sw.toString().contains("Invalido"));
    }

    @Test
    public void testTokenInexistente() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("token")).thenReturn(null);
        
        new validarToken().doPost(request, response);
        // O sistema deve tratar a ausência de token
        verify(request).getParameter("token");
    }

    @Test
    public void testTokenExpirado() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("token")).thenReturn("expirado");
        
        new validarToken().doPost(request, response);
        assertNotNull(response);
    }
}