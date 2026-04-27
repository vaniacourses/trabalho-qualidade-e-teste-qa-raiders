package Controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import DAO.DaoCliente;

@MockitoSettings(strictness = Strictness.LENIENT)
public class CadastroTest {

    @Test
    public void testProcessamentoCadastro() throws Exception { 
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class); 
        String json = "{\"endereco\":{\"bairro\":\"Centro\",\"cidade\":\"SP\",\"estado\":\"SP\",\"complemento\":\"Apt\",\"rua\":\"Rua A\",\"numero\":10},\"usuario\":{\"nome\":\"João\",\"sobrenome\":\"Silva\",\"telefone\":\"123\",\"usuario\":\"joao\",\"senha\":\"123\"}}";
        
        BufferedReader reader = new BufferedReader(new StringReader(json));
         
        when(request.getInputStream()).thenReturn(new ServletInputStreamMock(reader));
         
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw)); 
        try (MockedConstruction<DaoCliente> mockedDao = mockConstruction(DaoCliente.class, (mock, context) -> { 
        })) { 
            cadastro servlet = new cadastro();
            servlet.doPost(request, response);

            String resultado = sw.toString();
            
            assertTrue(resultado.contains("Usuário Cadastrado!"), "A mensagem de retorno deveria ser de sucesso.");
            
            DaoCliente daoInjetado = mockedDao.constructed().get(0);
            verify(daoInjetado, times(1)).salvar(any());
        }
    }
}