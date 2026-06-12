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

public class StatusLanchoneteSeleniumTest {

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
    public void testAbrirLanchonete() {
        PainelPage painel = loginAdmin();
        painel.abrirLanchonete();

        painel.aguardarStatus("Aberto agora!");
        String status = painel.getStatusTexto();

        assertTrue(status.contains("Aberto"),
            "Status deve ser 'Aberto agora!' após clicar em Abrir, foi: " + status);
    }

    @Test
    public void testFecharLanchonete() {
        PainelPage painel = loginAdmin();
        painel.fecharLanchonete();

        painel.aguardarStatus("Fechado agora!");
        String status = painel.getStatusTexto();

        assertTrue(status.contains("Fechado"),
            "Status deve ser 'Fechado agora!' após clicar em Fechar, foi: " + status);
    }
 
    @Test
    public void testAlternarStatusAbrirEFecha() {
        PainelPage painel = loginAdmin();

        painel.abrirLanchonete();
        painel.aguardarStatus("Aberto agora!");
        assertTrue(painel.getStatusTexto().contains("Aberto"),
            "Após abrir, status deve ser 'Aberto agora!'");

        painel.fecharLanchonete();
        painel.aguardarStatus("Fechado agora!");
        assertTrue(painel.getStatusTexto().contains("Fechado"),
            "Após fechar, status deve ser 'Fechado agora!'");
    }
 
    @Test
    public void testAlternarStatusFecharEAbre() {
        PainelPage painel = loginAdmin();

        painel.fecharLanchonete();
        painel.aguardarStatus("Fechado agora!");
        assertTrue(painel.getStatusTexto().contains("Fechado"),
            "Após fechar, status deve ser 'Fechado agora!'");

        painel.abrirLanchonete();
        painel.aguardarStatus("Aberto agora!");
        assertTrue(painel.getStatusTexto().contains("Aberto"),
            "Após abrir novamente, status deve ser 'Aberto agora!'");
    }
 
    @Test
    public void testStatusPersistidoAposNavegacao() {
        PainelPage painel = loginAdmin();
 
        painel.abrirLanchonete();
        painel.aguardarStatus("Aberto agora!");
 
        driver.get("http://localhost:8080/view/painel/painel.html");
        painel.aguardarStatus("Aberto agora!");

        assertTrue(painel.getStatusTexto().contains("Aberto"),
            "Status 'Aberto' deve persistir após recarregar o painel");
    }
}
