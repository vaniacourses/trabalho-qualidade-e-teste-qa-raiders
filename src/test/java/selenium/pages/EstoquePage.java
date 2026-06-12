package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EstoquePage extends BasePage {

    private static final String URL = "http://localhost:8080/view/estoque/estoque.html";

    private final By tabelaLanches = By.id("tabelaLanches");
    private final By tabelaIngredientes = By.id("tabelaIngredientes");
    private final By tabelaBebidas = By.id("tabelaBebidas"); 
    private final By inputIngredienteQtd = By.id("ingredientesQuantidade"); 
    private final By botaoAlterarIngrediente = By.cssSelector("#editarIngredientes input[value='Alterar']"); 

    public EstoquePage(WebDriver driver) {
        super(driver);
    }

    public static EstoquePage abrir(WebDriver driver) {
        driver.get(URL);
        return new EstoquePage(driver);
    }

    public boolean tabelaLanchesPresente() {
        return isElementPresent(tabelaLanches);
    }

    public boolean tabelaIngredientesPresente() {
        return isElementPresent(tabelaIngredientes);
    }

    public boolean tabelaBebidasPresente() {
        return isElementPresent(tabelaBebidas);
    }

    public EstoquePage aguardarCarregamento() {
        waitForVisible(tabelaIngredientes);
        return this;
    }

    public EstoquePage atualizarQtdIngrediente(String novaQtd) {
        type(inputIngredienteQtd, novaQtd);
        return this;
    }

    public EstoquePage salvarAlteracaoIngrediente() {
        click(botaoAlterarIngrediente);
        return this;
    }

    public boolean alertaPresente() {
        return isAlertPresent();
    }

    public void aceitarAlerta() {
        acceptAlert();
    }
}
