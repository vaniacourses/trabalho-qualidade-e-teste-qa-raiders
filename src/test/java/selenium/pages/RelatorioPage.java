package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RelatorioPage extends BasePage {

    private static final String URL = "http://localhost:8080/view/relatorio/relatorio.html";

    private final By tabelaGastos = By.id("tbRelatorioGastos");
    private final By tabelaBebidas = By.id("tbRelatorioBebidas");
    private final By tabelaLanches = By.id("tbRelatorioLanches");

    public RelatorioPage(WebDriver driver) {
        super(driver);
    }

    public static RelatorioPage abrir(WebDriver driver) {
        driver.get(URL);
        return new RelatorioPage(driver);
    }

    public boolean tabelaGastosPresente() {
        return isElementPresent(tabelaGastos);
    }

    public boolean tabelaBebidasPresente() {
        return isElementPresent(tabelaBebidas);
    }

    public boolean tabelaLanchesPresente() {
        return isElementPresent(tabelaLanches);
    }

    public boolean tituloPaginaCorreto() {
        return driver.getTitle().contains("Relat");
    }

    public int linhasTabelaGastos() {
        return driver.findElements(By.cssSelector("#tbRelatorioGastos tr")).size();
    }

    public int linhasTabelaBebidas() {
        return driver.findElements(By.cssSelector("#tbRelatorioBebidas tr")).size();
    }

    public int linhasTabelaLanches() {
        return driver.findElements(By.cssSelector("#tbRelatorioLanches tr")).size();
    }

    public RelatorioPage aguardarCarregamento() {
        waitForVisible(tabelaGastos);
        return this;
    }
}
