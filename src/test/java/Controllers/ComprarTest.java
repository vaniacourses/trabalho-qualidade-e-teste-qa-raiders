package Controllers;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import javax.servlet.http.*;
import java.io.*;

public class ComprarTest {
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
}