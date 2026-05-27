package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    public void testCadastro() {
        driver.get("http://localhost:8080");

        HomePage homePage = new HomePage(driver);
        CarrinhoPage carrinhoPage = homePage.abrirCardapioECapturarAlerta().irParaCarrinho();
        CadastroPage cadastroPage = carrinhoPage.irParaCadastro();

        CadastroData dadosCadastro = new CadastroData(
            "Lucas",
            "Bogado",
            "11999999999",
            "lucasbogado_",
            "123456",
            "Rua aaaaa",
            "123",
            "Icarai",
            "142",
            "Niteroi",
            "RJ"
        );

        cadastroPage.preencherFormulario(dadosCadastro)
            .submeter();

        assertTrue(cadastroPage.alertaCadastroVisivel());
        cadastroPage.aceitarAlertaCadastro();
    }

    @Test public void testCadastroComDadosInvalidos(){
        driver.get("http://localhost:8080");
        HomePage homePage = new HomePage(driver);
        CarrinhoPage carrinhoPage = homePage.abrirCardapioECapturarAlerta().irParaCarrinho();
        CadastroPage cadastroPage = carrinhoPage.irParaCadastro();

        CadastroData dadosCadastro = new CadastroData(
            "", // Nome vazio
            "Bogado",
            "11999999999",
            "lucasbogado_",
            "123456",
            "Rua aaaaa",
            "123",
            "Icarai",
            "142",
            "Niteroi",
            "RJ"
        );

    }
}