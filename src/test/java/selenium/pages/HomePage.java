package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By botaoCardapio = By.xpath("/html/body/div/div[2]/div/div[3]/button");
    private final By linkCarrinho = By.xpath("//*[@id=\"carrinho\"]/a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage abrirCardapioECapturarAlerta() {
        click(botaoCardapio);
        if (isAlertPresent()) {
            acceptAlert();
        }
        return this;
    }

    public CarrinhoPage irParaCarrinho() {
        click(linkCarrinho);
        return new CarrinhoPage(driver);
    }
}
