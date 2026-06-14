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
import selenium.pages.RelatorioPage;

public class GetRelatorioLanchesSeleniumTest {
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
    public void testRelatorioCarregaTabelas() {
        PainelPage painel = loginAdmin();
        RelatorioPage relatorio = painel.irParaRelatorio();
        relatorio.aguardarCarregamento();

        assertTrue(relatorio.tituloPaginaCorreto(),
            "Título da página deve conter 'Relat'");
        assertTrue(relatorio.tabelaGastosPresente(),
            "Tabela de gastos deve estar presente no relatório");
        assertTrue(relatorio.tabelaBebidasPresente(),
            "Tabela de bebidas deve estar presente no relatório");
        assertTrue(relatorio.tabelaLanchesPresente(),
            "Tabela de lanches deve estar presente no relatório");
    }

    @Test
    public void testTabelaRelatorioLanchesPossuiCabecalho() {
        PainelPage painel = loginAdmin();
        RelatorioPage relatorio = painel.irParaRelatorio();
        relatorio.aguardarCarregamento();

        assertTrue(relatorio.linhasTabelaLanches() >= 1,
            "Tabela de lanches deve conter ao menos a linha de cabeçalho");
    }
}
