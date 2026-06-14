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

public class SalvarLanchesSeleniumTest {
    private WebDriver driver;
    private PainelPage painel;
    private String nomePao;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        painel = loginAdmin();
        // O select de pães é populado com ingredientes do tipo "pao"; garantimos um.
        nomePao = criarIngredientePao(painel);
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
        String nome = "Pao Lanche " + System.currentTimeMillis();
        painelPage.mostrarFormIngrediente()
            .preencherIngrediente(nome, "pao", "50", "1.00", "2.50", "Pao para teste de lanche")
            .salvarIngrediente();

        assertTrue(painelPage.alertaPresente(), "Criação do ingrediente pão deve exibir alerta");
        painelPage.aceitarAlertaEAguardarRecarga();
        return nome;
    }

    @Test
    public void testFormularioLancheCarregaOpcaoDePao() {
        painel.mostrarFormLanche();

        assertTrue(painel.paoDisponivel(),
            "O select de pães deve carregar ao menos a opção do pão cadastrado");
    }

    @Test
    public void testSalvarLancheComDadosValidos() {
        String nomeLanche = "Lanche " + System.currentTimeMillis();

        painel.mostrarFormLanche()
              .preencherLanche(nomeLanche, nomePao, "15.00",
                  "Lanche cadastrado por teste automatizado")
              .salvarLanche();

        assertTrue(painel.alertaPresente(),
            "Salvar lanche com dados válidos deve exibir alerta de confirmação");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("vazio"),
            "Não deve acusar campo vazio para um lanche válido, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    public void testSalvarLancheCamposObrigatoriosExibeAlerta() {
        painel.mostrarFormLanche()
              .salvarLanche();

        assertTrue(painel.alertaPresente(),
            "Salvar lanche sem preencher os campos deve exibir alerta de validação");
        String texto = painel.obterTextoAlerta();
        assertTrue(texto.contains("Vazio") || texto.contains("Campo"),
            "Alerta deve indicar campo obrigatório vazio, foi: " + texto);

        painel.aceitarAlertaEAguardarRecarga();
    }
}
