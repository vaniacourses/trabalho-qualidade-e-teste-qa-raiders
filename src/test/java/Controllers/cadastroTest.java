package Controllers;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import javax.servlet.http.*;
import java.io.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public class cadastroTest {
    @Test
    public void testProcessamentoCadastro() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Simulando JSON de entrada
        String json = "{\"endereco\":{\"bairro\":\"Centro\",\"cidade\":\"SP\",\"estado\":\"SP\",\"complemento\":\"Apt\",\"rua\":\"Rua A\",\"numero\":10},\"usuario\":{\"nome\":\"João\",\"sobrenome\":\"Silva\",\"telefone\":\"123\",\"usuario\":\"joao\",\"senha\":\"123\"}}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getInputStream()).thenReturn(new ServletInputStreamMock(reader));
        
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        new cadastro().doPost(request, response);

        assert(sw.toString().contains("Usuário Cadastrado!"));
    }

}