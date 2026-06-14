package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.LoginFuncionarioPage;
import selenium.pages.PainelPage;

public class CadastroFuncionarioSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private PainelPage loginAdmin() {
        return LoginFuncionarioPage.abrir(driver).loginEAguardarPainel("admin", "admin");
    }

    private String usuario(String prefixo) {
        return prefixo + (System.currentTimeMillis() % 1000000L);
    }

    @Test
    public void testCadastrarFuncionarioComDadosValidos() {
        PainelPage painel = loginAdmin();

        painel.mostrarFormFuncionario()
              .preencherFuncionario(
                  "Carlos",
                  "Pereira",
                  usuario("carlos"),   
                  "senha123",
                  "Atendente",
                  "1500.00"
              )
              .salvarFuncionario();

        assertTrue(painel.alertaPresente(),
            "Cadastro de funcionário válido deve exibir alerta");

        String texto = painel.obterTextoAlerta();
        assertTrue(texto.contains("Funcionario Cadastrado") || texto.contains("Cadastrado"),
            "Alerta deve confirmar o cadastro do funcionário, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    public void testCadastrarDoisFuncionariosDistintosAmbosAceitos() {
        PainelPage painel = loginAdmin();

        painel.mostrarFormFuncionario()
              .preencherFuncionario(
                  "Ana",
                  "Rodrigues",
                  usuario("ana"),      
                  "senhaAna",
                  "Cozinheira",
                  "2000.00"
              )
              .salvarFuncionario();

        assertTrue(painel.alertaPresente(),
            "Primeiro cadastro de funcionário deve exibir alerta de sucesso");
        String textoAlerta1 = painel.obterTextoAlerta();
        assertTrue(textoAlerta1.contains("Funcionario Cadastrado") || textoAlerta1.contains("Cadastrado"),
            "Alerta do primeiro cadastro deve confirmar sucesso, foi: " + textoAlerta1);
        painel.aceitarAlertaEAguardarRecarga();

        painel.mostrarFormFuncionario()
              .preencherFuncionario(
                  "Bruno",
                  "Alves",
                  usuario("bruno"),    
                  "senhaBruno",
                  "Gerente",
                  "3500.00"
              )
              .salvarFuncionario();

        assertTrue(painel.alertaPresente(),
            "Segundo cadastro de funcionário deve exibir alerta de sucesso");
        String textoAlerta2 = painel.obterTextoAlerta();
        assertTrue(textoAlerta2.contains("Funcionario Cadastrado") || textoAlerta2.contains("Cadastrado"),
            "Alerta do segundo cadastro deve confirmar sucesso, foi: " + textoAlerta2);
        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    public void testCadastrarFuncionarioCamposVaziosExibeValidacao() {
        PainelPage painel = loginAdmin();

        painel.mostrarFormFuncionario()
              .preencherFuncionario("", "", "", "", "", "")
              .salvarFuncionario();

        assertTrue(painel.alertaPresente(),
            "Submeter formulário vazio deve exibir alerta de validação");

        String texto = painel.obterTextoAlerta();
        assertTrue(!texto.contains("Funcionario Cadastrado"),
            "Alerta de campos vazios não deve indicar cadastro bem-sucedido, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }
}