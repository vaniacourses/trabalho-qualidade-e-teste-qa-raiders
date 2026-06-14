package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.EstoquePage;
import selenium.pages.LoginFuncionarioPage;
import selenium.pages.PainelPage;


public class EstoqueSeleniumTest {

    private WebDriver driver;
    private PainelPage painel;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        painel = loginAdmin();
        criarIngredientePao(painel);
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

    private String criarIngredientePao(PainelPage painelPage) {
        String nomePao = "Pao Estoque " + System.currentTimeMillis();
        painelPage.mostrarFormIngrediente()
            .preencherIngrediente(nomePao, "pao", "50", "1.00", "2.50", "Pao para teste de estoque")
            .salvarIngrediente();

        assertTrue(painelPage.alertaPresente(), "Criação do ingrediente deve exibir alerta");
        painelPage.aceitarAlertaEAguardarRecarga();
        return nomePao;
    }

    @Test
    public void testTabelasEstoquePresentes() {
        EstoquePage estoque = painel.irParaEstoque();
        estoque.aguardarCarregamento();

        assertTrue(estoque.tabelaLanchesPresente(),
            "Tabela de lanches deve estar presente na página de estoque");
        assertTrue(estoque.tabelaIngredientesPresente(),
            "Tabela de ingredientes deve estar presente na página de estoque");
        assertTrue(estoque.tabelaBebidasPresente(),
            "Tabela de bebidas deve estar presente na página de estoque");
    }

    @Test
    public void testAtualizarQtdIngredienteExibeAlerta() {
        EstoquePage estoque = painel.irParaEstoque();
        estoque.aguardarCarregamento();

        estoque.selecionarPrimeiroIngrediente()
               .atualizarQtdIngrediente("99")
               .salvarAlteracaoIngrediente();

        assertTrue(estoque.alertaPresente(),
            "Salvar alteração de ingrediente deve exibir alerta de confirmação");

        estoque.aceitarAlerta();
    }
}
