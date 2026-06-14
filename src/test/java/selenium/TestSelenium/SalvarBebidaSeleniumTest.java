package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.LoginFuncionarioPage;
import selenium.pages.PainelPage;

public class SalvarBebidaSeleniumTest {
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

    @Test
    public void testSalvarBebidaComDadosValidos() {
        PainelPage painel = loginAdmin();

        String nome = "Bebida " + System.currentTimeMillis();
        painel.mostrarFormBebida()
              .preencherBebida(nome, "refrigerante", "10", "2.00", "5.00",
                  "Bebida cadastrada por teste automatizado")
              .salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Salvar bebida com dados válidos deve exibir alerta de confirmação");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("vazio"),
            "Não deve acusar campo vazio para uma bebida válida, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    public void testSalvarBebidaCamposVaziosExibeValidacao() {
        PainelPage painel = loginAdmin();

        painel.mostrarFormBebida()
              .preencherBebida("", "", "", "", "", "")
              .salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Salvar bebida com campos vazios deve exibir alerta de validação");
        String texto = painel.obterTextoAlerta();
        assertTrue(texto.contains("preencher") || texto.contains("Vazio") || texto.contains("Campo"),
            "Alerta deve indicar campo obrigatório não preenchido, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }
}
