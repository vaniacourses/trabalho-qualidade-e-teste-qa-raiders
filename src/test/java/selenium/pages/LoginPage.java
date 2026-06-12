package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final String URL = "http://localhost:8080/view/login/login.html";

    private final By inputUsuario = By.id("loginInput");
    private final By inputSenha = By.id("senhaInput");
    private final By botaoEntrar = By.cssSelector("button.buttonSubmit");
    private final By linkCadastro = By.xpath("//a[contains(@href,'cadastro.html')]");
    private final By linkLoginFuncionario = By.xpath("//a[contains(@href,'login_Funcionario.html')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public static LoginPage abrir(WebDriver driver) {
        driver.get(URL);
        return new LoginPage(driver);
    }

    public LoginPage preencherCredenciais(String usuario, String senha) {
        type(inputUsuario, usuario);
        type(inputSenha, senha);
        return this;
    }

    public void submeter() {
        click(botaoEntrar);
    }

    public void login(String usuario, String senha) {
        preencherCredenciais(usuario, senha);
        submeter();
    }

    public boolean alertaPresente() {
        return isAlertPresent();
    }

    public String obterTextoAlerta() {
        return getAlertText();
    }

    public void aceitarAlerta() {
        acceptAlert();
    }

    public CadastroPage irParaCadastro() {
        click(linkCadastro);
        return new CadastroPage(driver);
    }

    public LoginFuncionarioPage irParaLoginFuncionario() {
        click(linkLoginFuncionario);
        return new LoginFuncionarioPage(driver);
    }
}
