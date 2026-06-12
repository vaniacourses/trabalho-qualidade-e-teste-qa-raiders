package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CadastroPage extends BasePage {
    private final By inputNome = By.id("Espaçamentotitle");
    private final By inputSobrenome = By.xpath("//*[@id=\"usuario\"]/input[2]");
    private final By inputTelefone = By.xpath("//*[@id=\"usuario\"]/input[3]");
    private final By inputUsername = By.xpath("//*[@id=\"usuario\"]/input[4]");
    private final By inputSenha = By.xpath("//*[@id=\"usuario\"]/input[5]");
    private final By inputRua = By.xpath("//*[@id=\"endereco\"]/input[1]");
    private final By inputNumero = By.xpath("//*[@id=\"endereco\"]/input[2]");
    private final By inputBairro = By.xpath("//*[@id=\"endereco\"]/input[3]");
    private final By inputComplemento = By.xpath("//*[@id=\"endereco\"]/input[4]");
    private final By inputCidade = By.xpath("//*[@id=\"endereco\"]/input[5]");
    private final By comboUf = By.id("UF");
    private final By botaoCadastrar = By.xpath("/html/body/div/div/div/div[2]/div[2]/button");

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

        click(comboUf);
        By opcaoUf = By.xpath("//*[@id=\"UF\"]/option[normalize-space(.)='" + dados.getUf() + "']");
        wait.until(ExpectedConditions.elementToBeClickable(opcaoUf));
        click(opcaoUf);

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
