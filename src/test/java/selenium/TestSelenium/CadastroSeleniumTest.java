package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.CadastroData;
import selenium.pages.CadastroPage;
import selenium.pages.CarrinhoPage;
import selenium.pages.HomePage;

public class CadastroSeleniumTest {
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

    private String usuario(String prefixo) {
        return prefixo + (System.currentTimeMillis() % 1000000L);
    }

    private CadastroPage navegarParaCadastro() {
        driver.get("http://localhost:8080");
        CarrinhoPage carrinhoPage = new HomePage(driver).abrirCardapioECapturarAlerta().irParaCarrinho();
        return carrinhoPage.irParaCadastro();
    }

    @Test
    public void testCadastro() {
        CadastroPage cadastroPage = navegarParaCadastro();

        cadastroPage.preencherFormulario(new CadastroData(
            "Lucas", "Bogado", "11999999999",
            usuario("lucasbogado_"),
            "123456", "Rua aaaaa", "123", "Icarai", "142", "Niteroi", "RJ"
        )).submeter();

        assertTrue(cadastroPage.alertaCadastroVisivel());
        cadastroPage.aceitarAlertaCadastro();
    }

    @Test
    public void testCadastroComDadosInvalidos() {
        CadastroPage cadastroPage = navegarParaCadastro();

        ((JavascriptExecutor) driver).executeScript(
            "document.querySelectorAll('[required]').forEach(el => el.removeAttribute('required'));"
        );

        cadastroPage.preencherFormulario(new CadastroData(
            "", "Bogado", "11999999999",
            usuario("lucasbogado_"),
            "123456", "Rua aaaaa", "123", "Icarai", "142", "Niteroi", "RJ"
        )).submeter();

        assertTrue(cadastroPage.alertaCadastroVisivel(),
            "Submeter formulario com nome vazio deve exibir alerta de validacao");
        String textoAlerta = cadastroPage.obterTextoAlerta();
        assertFalse(textoAlerta.toLowerCase().contains("sucesso"),
            "Alerta nao deve indicar cadastro bem-sucedido com nome vazio, foi: " + textoAlerta);
        cadastroPage.aceitarAlertaCadastro();
    }

    @Test
    public void testCadastroComUsernameJaExistente() {
        String usernameFixo = usuario("dup");

        CadastroPage primeira = navegarParaCadastro();
        primeira.preencherFormulario(new CadastroData(
            "Maria", "Silva", "21988887777",
            usernameFixo,
            "senha123", "Rua B", "10", "Centro", "apto1", "Rio", "RJ"
        )).submeter();
        assertTrue(primeira.alertaCadastroVisivel());
        primeira.aceitarAlertaCadastro();

        CadastroPage segunda = navegarParaCadastro();
        segunda.preencherFormulario(new CadastroData(
            "Joao", "Costa", "21977776666",
            usernameFixo,
            "outrasenha", "Rua C", "20", "Tijuca", "casa", "Rio", "RJ"
        )).submeter();

        assertTrue(segunda.alertaCadastroVisivel(),
            "Cadastro com username duplicado deve exibir alerta");
        String textoAlerta = segunda.obterTextoAlerta();
        assertFalse(textoAlerta.contains("Usuário Cadastrado!"),
            "Alerta nao deve confirmar cadastro com username duplicado, foi: " + textoAlerta);
        segunda.aceitarAlertaCadastro();
    }

    @Test
    public void testCadastroComTodosCamposVazios() {
        CadastroPage cadastroPage = navegarParaCadastro();

        ((JavascriptExecutor) driver).executeScript(
            "document.querySelectorAll('[required]').forEach(el => el.removeAttribute('required'));"
        );

        cadastroPage.preencherFormulario(new CadastroData(
            "", "", "", "", "", "", "", "", "", "", ""
        )).submeter();

        assertTrue(cadastroPage.alertaCadastroVisivel(),
            "Submeter formulario completamente vazio deve exibir alerta");
        String textoAlerta = cadastroPage.obterTextoAlerta();
        assertFalse(textoAlerta.contains("Usuário Cadastrado!"),
            "Alerta nao deve confirmar cadastro com todos os campos vazios, foi: " + textoAlerta);
        cadastroPage.aceitarAlertaCadastro();
    }
}