package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.LoginFuncionarioPage;
import selenium.pages.PainelPage;

public class BebidaSeleniumTest {

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

    private PainelPage abrirFormBebida() {
        return loginAdmin().mostrarFormBebida();
    }

    @Test
    @DisplayName("Equivalência - classe VÁLIDA: todos os campos preenchidos")
    public void testParticaoEquivalenciaClasseValida() {
        PainelPage painel = abrirFormBebida();

        String nome = "Bebida " + System.currentTimeMillis();
        painel.preencherBebida(nome, "refrigerante", "10", "2.00", "5.00",
                "Classe valida de equivalencia").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Bebida válida deve exibir alerta de confirmação");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("vazio"),
            "Classe válida não deve acusar campo vazio, foi: " + texto);
        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    @DisplayName("Equivalência - classe INVÁLIDA: todos os campos vazios")
    public void testParticaoEquivalenciaClasseInvalida() {
        PainelPage painel = abrirFormBebida();

        painel.preencherBebida("", "", "", "", "", "").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Classe inválida deve exibir alerta de validação");
        String texto = painel.obterTextoAlerta();
        assertTrue(texto.contains("preencher") || texto.contains("Vazio") || texto.contains("Campo"),
            "Alerta deve indicar campo obrigatório, foi: " + texto);
        painel.aceitarTodosAlertas();
    }

    @Test
    @DisplayName("Valor Limite - nome com 1 caractere (borda inferior válida)")
    public void testValorLimiteNomeBordaInferior() {
        PainelPage painel = abrirFormBebida();

        painel.preencherBebida("A", "suco", "5", "1.00", "2.00",
                "Nome no limite inferior (1 caractere)").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Nome com 1 caractere (borda inferior) deve ser aceito");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("vazio"),
            "Não deve acusar campo vazio na borda inferior válida, foi: " + texto);
        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    @DisplayName("Valor Limite - nome com 30 caracteres (borda superior válida)")
    public void testValorLimiteNomeBordaSuperior() {
        PainelPage painel = abrirFormBebida();

        // monta um nome com exatamente 30 caracteres (limite superior da coluna)
        String base = "Beb" + System.currentTimeMillis();
        String nome30 = (base + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").substring(0, 30);

        painel.preencherBebida(nome30, "agua", "5", "1.00", "3.00",
                "Nome no limite superior (30 caracteres)").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Nome com 30 caracteres (borda superior) deve ser aceito");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("vazio"),
            "Não deve acusar campo vazio na borda superior válida, foi: " + texto);
        painel.aceitarAlertaEAguardarRecarga();
    }

    @Test
    @DisplayName("Causa-Efeito R2 - ¬C1 (nome vazio) ∧ C2 (tipo ok) -> E2 (validação)")
    public void testGrafoCausaEfeitoSemNomeComTipo() {
        PainelPage painel = abrirFormBebida();

        painel.preencherBebida("", "suco", "10", "2.00", "5.00",
                "Causa C1 falsa, C2 verdadeira").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Faltando o nome (¬C1), o efeito deve ser validação (E2)");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("cadastrad"),
            "Não deve confirmar cadastro sem o nome, foi: " + texto);
        painel.aceitarTodosAlertas();
    }

    @Test
    @DisplayName("Causa-Efeito R3 - C1 (nome ok) ∧ ¬C2 (tipo vazio) -> E2 (validação)")
    public void testGrafoCausaEfeitoComNomeSemTipo() {
        PainelPage painel = abrirFormBebida();

        painel.preencherBebida("Bebida " + System.currentTimeMillis(), "", "10", "2.00", "5.00",
                "Causa C1 verdadeira, C2 falsa").salvarBebida();

        assertTrue(painel.alertaPresente(),
            "Faltando o tipo (¬C2), o efeito deve ser validação (E2)");
        String texto = painel.obterTextoAlerta();
        assertFalse(texto.toLowerCase().contains("cadastrad"),
            "Não deve confirmar cadastro sem o tipo, foi: " + texto);
        painel.aceitarTodosAlertas();
    }
}
