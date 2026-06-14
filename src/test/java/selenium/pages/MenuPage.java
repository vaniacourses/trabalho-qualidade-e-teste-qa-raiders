package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MenuPage extends BasePage {

    private static final String URL = "http://localhost:8080/view/menu/menu.html";

    private final By linkCarrinho = By.cssSelector("#carrinho a");
    private final By linkLanches = By.xpath("//a[contains(@href,'showLanches')]");
    private final By linkBebidas = By.xpath("//a[contains(@href,'showBebidas')]");
    private final By screenLanches = By.id("screen");
    private final By screenBebidas = By.id("screenBebidas");

    public MenuPage(WebDriver driver) {
        super(driver);
    }

    public static MenuPage abrir(WebDriver driver) {
        driver.get(URL);
        MenuPage page = new MenuPage(driver);
        page.dismissAlertIfPresent();
        return page;
    }

    public LoginPage irParaLogin() {
        click(linkCarrinho);
        return new LoginPage(driver);
    }

    public MenuPage mostrarLanches() {
        click(linkLanches);
        return this;
    }

    public MenuPage mostrarBebidas() {
        click(linkBebidas);
        return this;
    }

    public boolean tituloCorreto() {
        return driver.getTitle().contains("Card");
    }

    public boolean screenLanchesPresente() {
        return isElementPresent(screenLanches);
    }

    public boolean screenBebidasPresente() {
        return isElementPresent(screenBebidas);
    }

    public boolean screenLanchesVisivel() {
        return isVisible(screenLanches);
    }

    public boolean screenBebidasVisivel() {
        return isVisible(screenBebidas);
    }
}
