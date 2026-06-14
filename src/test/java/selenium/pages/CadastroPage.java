package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CadastroPage extends BasePage {

    private final By inputNome        = By.name("nome");
    private final By inputSobrenome   = By.name("sobrenome");
    private final By inputTelefone    = By.name("telefone");
    private final By inputUsername    = By.name("usuario");
    private final By inputSenha       = By.name("senha");
    private final By inputRua         = By.name("rua");
    private final By inputNumero      = By.name("numero");
    private final By inputBairro      = By.name("bairro");
    private final By inputComplemento = By.name("complemento");
    private final By inputCidade      = By.name("cidade");
    private final By comboUf          = By.id("UF");

    private final By botaoCadastrar   = By.cssSelector("button.buttonSubmit");

    public CadastroPage(WebDriver driver) {
        super(driver);
    }

    public CadastroPage preencherFormulario(CadastroData dados) {
        type(inputNome, dados.getNome());
        type(inputSobrenome, dados.getSobrenome());
        type(inputTelefone, dados.getTelefone());
        type(inputUsername, dados.getUsername());
        type(inputSenha, dados.getSenha());
        type(inputRua, dados.getRua());
        type(inputNumero, dados.getNumero());
        type(inputBairro, dados.getBairro());
        type(inputComplemento, dados.getComplemento());
        type(inputCidade, dados.getCidade());

        selectByValue(comboUf, dados.getUf());

        return this;
    }

    public CadastroPage submeter() {
        click(botaoCadastrar);
        return this;
    }

    public boolean alertaCadastroVisivel() {
        return isAlertPresent();
    }

    public void aceitarAlertaCadastro() {
        acceptAlert();
    }

    public String obterTextoAlerta() {
        return getAlertText();
    }
}