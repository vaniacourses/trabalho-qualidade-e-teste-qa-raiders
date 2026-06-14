package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.pages.CadastroData;
import selenium.pages.CadastroPage;
import selenium.pages.HomePage;
import selenium.pages.LoginPage;

public class LoginSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String[] registrarUsuarioTeste() {
        String username = "login" + (System.currentTimeMillis() % 1000000L);
        String senha = "senha123";

        driver.get("http://localhost:8080");
        CadastroPage cadastroPage = new HomePage(driver)
                .abrirCardapioECapturarAlerta()
                .irParaCarrinho()
                .irParaCadastro();

        cadastroPage.preencherFormulario(new CadastroData(
                "Teste", "Login", "11999999999", username,
                senha, "Rua Teste", "1", "Bairro", "apto", "Cidade", "RJ"
        )).submeter();
        cadastroPage.aceitarAlertaCadastro();

        return new String[]{username, senha};
    }

    @Test
    public void testLoginPaginaCarregaCorretamente() {
        LoginPage loginPage = LoginPage.abrir(driver);
        assertTrue(loginPage.tituloCorreto());
        assertTrue(loginPage.camposLoginPresentes());
    }

    @Test
    public void testLoginComCredenciaisValidas() {
        String[] credenciais = registrarUsuarioTeste();

        LoginPage loginPage = LoginPage.abrir(driver);
        loginPage.login(credenciais[0], credenciais[1]);

        wait.until(ExpectedConditions.urlContains("carrinho"));
        assertTrue(driver.getCurrentUrl().contains("carrinho"),
                "Login valido deve redirecionar para o carrinho");
    }

    @Test
    public void testLoginComSenhaErrada() {
        String[] credenciais = registrarUsuarioTeste();

        LoginPage loginPage = LoginPage.abrir(driver);
        loginPage.login(credenciais[0], "senhaErrada");

        assertTrue(loginPage.alertaPresente(),
                "Login com senha errada deve exibir alerta de erro");
        String textoAlerta = loginPage.obterTextoAlerta();
        assertTrue(textoAlerta.toLowerCase().contains("erro"),
                "Alerta deve indicar erro, foi: " + textoAlerta);
        loginPage.aceitarAlerta();
        assertFalse(driver.getCurrentUrl().contains("carrinho"),
                "Login com senha errada nao deve redirecionar para o carrinho");
    }

    @Test
    public void testLoginComUsuarioInexistente() {
        LoginPage loginPage = LoginPage.abrir(driver);
        loginPage.login("usuarioQueNaoExiste", "qualquerSenha");

        assertTrue(loginPage.alertaPresente(),
                "Login com usuario inexistente deve exibir alerta de erro");
        String textoAlerta = loginPage.obterTextoAlerta();
        assertTrue(textoAlerta.toLowerCase().contains("erro"),
                "Alerta deve indicar erro, foi: " + textoAlerta);
        loginPage.aceitarAlerta();
        assertFalse(driver.getCurrentUrl().contains("carrinho"),
                "Login com usuario inexistente nao deve redirecionar para o carrinho");
    }

    @Test
    public void testLoginComCamposVazios() {
        LoginPage loginPage = LoginPage.abrir(driver);

        loginPage.login("", "");

        assertTrue(loginPage.alertaPresente(),
                "Login com campos vazios deve exibir alerta");
        String textoAlerta = loginPage.obterTextoAlerta();
        assertTrue(textoAlerta.contains("Digite as Informações!"),
                "Alerta deve pedir para digitar as informacoes, foi: " + textoAlerta);
        loginPage.aceitarAlerta();
        assertFalse(driver.getCurrentUrl().contains("carrinho"),
                "Login com campos vazios nao deve redirecionar para o carrinho");
    }
}