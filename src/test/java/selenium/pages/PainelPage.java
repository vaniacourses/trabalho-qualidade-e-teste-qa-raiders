package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class PainelPage extends BasePage {

    private static final String URL = "http://localhost:8080/view/painel/painel.html";

    // Botões principais do painel
    private final By botaoAbrirLanchonete = By.xpath("//button[contains(@onclick,'abrirLanchonete')]");
    private final By botaoFecharLanchonete = By.xpath("//button[contains(@onclick,'fecharLanchonete')]");
    private final By botaoCadastrarLanches = By.xpath("//button[contains(@onclick,'showCadLanches')]");
    private final By botaoCadastrarBebidas = By.xpath("//button[contains(@onclick,'showCadBebidaDiv')]");
    private final By botaoCadastrarIngredientes = By.xpath("//button[contains(@onclick,'showCadIngredienteDiv')]");
    private final By botaoCadastrarFuncionarios = By.xpath("//button[contains(@onclick,'showCadFuncionario')]");
    private final By statusTexto = By.cssSelector(".legendStatus");

    // Formulário de ingredientes (#addIngrediente)
    private final By inputIngredienteNome = By.cssSelector("#addIngrediente input[name='nome']");
    private final By selectIngredienteTipo = By.cssSelector("#addIngrediente select[name='tipo']");
    private final By inputIngredienteQtd = By.cssSelector("#addIngrediente input[name='quantidade']");
    private final By inputIngredienteValorCompra = By.cssSelector("#addIngrediente input[name='ValorCompra']");
    private final By inputIngredienteValorVenda = By.cssSelector("#addIngrediente input[name='ValorVenda']");
    private final By inputIngredienteDescricao = By.id("textArea1");
    private final By botaoSalvarIngrediente = By.cssSelector("#addIngrediente input[value='Salvar']");

    // Formulário de bebidas (#addBebida)
    private final By inputBebidaNome = By.cssSelector("#addBebida input[name='nome']");
    private final By selectBebidaTipo = By.cssSelector("#addBebida select[name='tipo']");
    private final By inputBebidaQtd = By.cssSelector("#addBebida input[name='quantidade']");
    private final By inputBebidaValorCompra = By.cssSelector("#addBebida input[name='ValorCompra']");
    private final By inputBebidaValorVenda = By.cssSelector("#addBebida input[name='ValorVenda']");
    private final By inputBebidaDescricao = By.id("textArea2");
    private final By botaoSalvarBebida = By.cssSelector("#addBebida input[value='Salvar']");

    // Formulário de funcionários (#addFuncionario)
    private final By inputFuncNome = By.cssSelector("#addFuncionario input[name='nome']");
    private final By inputFuncSobrenome = By.cssSelector("#addFuncionario input[name='sobrenome']");
    private final By inputFuncUsuario = By.cssSelector("#addFuncionario input[name='usuarioFuncionario']");
    private final By inputFuncSenha = By.cssSelector("#addFuncionario input[name='senhaFuncionario']");
    private final By inputFuncCargo = By.cssSelector("#addFuncionario input[name='cargo']");
    private final By inputFuncSalario = By.cssSelector("#addFuncionario input[name='salario']");
    private final By botaoSalvarFuncionario = By.cssSelector("#addFuncionario input[value='Salvar']");

    // Formulário de lanches (#addItem)
    private final By inputLancheNome = By.id("nomeLanche");
    private final By inputLancheValor = By.id("ValorLanche");
    private final By selectLanchePao = By.id("selectPao");
    private final By inputLancheDescricao = By.id("textArea3");
    private final By botaoSalvarLanche = By.cssSelector("#addItem input[value='Salvar']");

    public PainelPage(WebDriver driver) {
        super(driver);
    }

    public static PainelPage abrir(WebDriver driver) {
        driver.get(URL);
        return new PainelPage(driver);
    }

    public PainelPage abrirLanchonete() {
        click(botaoAbrirLanchonete);
        return this;
    }

    public PainelPage fecharLanchonete() {
        click(botaoFecharLanchonete);
        return this;
    }

    public String getStatusTexto() {
        return getText(statusTexto);
    }

    public void aguardarStatus(String textoEsperado) {
        waitForTextPresent(statusTexto, textoEsperado);
    }

    // --- Ingredientes ---

    public PainelPage mostrarFormIngrediente() {
        click(botaoCadastrarIngredientes);
        waitForVisible(inputIngredienteNome);
        return this;
    }

    public PainelPage preencherIngrediente(String nome, String tipo, String qtd,
                                           String valorCompra, String valorVenda, String descricao) {
        type(inputIngredienteNome, nome);
        selectByValue(selectIngredienteTipo, tipo);
        type(inputIngredienteQtd, qtd);
        type(inputIngredienteValorCompra, valorCompra);
        type(inputIngredienteValorVenda, valorVenda);
        type(inputIngredienteDescricao, descricao);
        return this;
    }

    public PainelPage salvarIngrediente() {
        click(botaoSalvarIngrediente);
        return this;
    }

    // --- Bebidas ---

    public PainelPage mostrarFormBebida() {
        click(botaoCadastrarBebidas);
        waitForVisible(inputBebidaNome);
        return this;
    }

    public PainelPage preencherBebida(String nome, String tipo, String qtd,
                                      String valorCompra, String valorVenda, String descricao) {
        type(inputBebidaNome, nome);
        selectByValue(selectBebidaTipo, tipo);
        type(inputBebidaQtd, qtd);
        type(inputBebidaValorCompra, valorCompra);
        type(inputBebidaValorVenda, valorVenda);
        type(inputBebidaDescricao, descricao);
        return this;
    }

    public PainelPage salvarBebida() {
        click(botaoSalvarBebida);
        return this;
    }

    // --- Funcionários ---

    public PainelPage mostrarFormFuncionario() {
        click(botaoCadastrarFuncionarios);
        waitForVisible(inputFuncNome);
        return this;
    }

    public PainelPage preencherFuncionario(String nome, String sobrenome, String usuario,
                                           String senha, String cargo, String salario) {
        type(inputFuncNome, nome);
        type(inputFuncSobrenome, sobrenome);
        type(inputFuncUsuario, usuario);
        type(inputFuncSenha, senha);
        type(inputFuncCargo, cargo);
        type(inputFuncSalario, salario);
        return this;
    }

    public PainelPage salvarFuncionario() {
        click(botaoSalvarFuncionario);
        return this;
    }

    // --- Lanches ---

    public PainelPage mostrarFormLanche() {
        click(botaoCadastrarLanches);
        waitForVisible(inputLancheNome);
        return this;
    }

    public PainelPage preencherLanche(String nome, String nomePao, String valor, String descricao) {
        type(inputLancheNome, nome);
        type(inputLancheValor, valor);
        wait.until(d -> new Select(d.findElement(selectLanchePao)).getOptions().size() > 1);
        selectByVisibleText(selectLanchePao, nomePao);
        type(inputLancheDescricao, descricao);
        return this;
    }

    public PainelPage salvarLanche() {
        click(botaoSalvarLanche);
        return this;
    }

    /** Indica se o select de pães foi populado (ao menos uma opção além do placeholder). */
    public boolean paoDisponivel() {
        try {
            wait.until(d -> new Select(d.findElement(selectLanchePao)).getOptions().size() > 1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int aceitarTodosAlertas() {
        return acceptAllAlerts();
    }

    // --- Alertas e navegação ---

    public boolean alertaPresente() {
        return isAlertPresent();
    }

    public boolean alertaPresenteQuick() {
        return isAlertPresentQuick();
    }

    public String obterTextoAlerta() {
        return getAlertText();
    }

    public PainelPage aceitarAlertaEAguardarRecarga() {
        acceptAlert();
        waitForPageReady();
        return this;
    }

    public PainelPage clicarCancelarBebida() {
        click(By.cssSelector("#addBebida input[value='Cancelar']"));
        return this;
    }

    public PainelPage clicarCancelarIngrediente() {
        click(By.cssSelector("#addIngrediente input[value='Cancelar']"));
        return this;
    }

    public PainelPage clicarCancelarLanche() {
        click(By.cssSelector("#addItem input[value='Cancelar']"));
        return this;
    }

    public boolean painelPrincipalVisivel() {
        try {
            waitForVisible(By.id("Agrupado"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public RelatorioPage irParaRelatorio() {
        click(By.xpath("//a[contains(@href,'relatorio.html')]"));
        return new RelatorioPage(driver);
    }

    public EstoquePage irParaEstoque() {
        click(By.xpath("//a[contains(@href,'estoque.html')]"));
        return new EstoquePage(driver);
    }
}
