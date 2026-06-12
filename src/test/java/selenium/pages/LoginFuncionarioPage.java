package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginFuncionarioPage extends BasePage {

    private static final String URL = "http://localhost:8080/view/login/login_Funcionario.html";

    private final By inputUsuario = By.id("loginInput");
    private final By inputSenha = By.id("senhaInput");
    private final By botaoEntrar = By.cssSelector("button.buttonSubmit");

    public LoginFuncionarioPage(WebDriver driver) {
        super(driver);
    }

    public static LoginFuncionarioPage abrir(WebDriver driver) {
        driver.get(URL);
        return new LoginFuncionarioPage(driver);
    }

    public LoginFuncionarioPage preencherCredenciais(String usuario, String senha) {
        type(inputUsuario, usuario);
        type(inputSenha, senha);
        return this;
    }

    public void submeter() {
        click(botaoEntrar);
    }

    public PainelPage loginEAguardarPainel(String usuario, String senha) {
        preencherCredenciais(usuario, senha);
        submeter();
        waitForUrlContains("painel");
        return new PainelPage(driver);
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
}
