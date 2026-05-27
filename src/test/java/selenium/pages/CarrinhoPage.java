package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CarrinhoPage extends BasePage {
    private final By botaoCadastro = By.xpath("/html/body/div/div/div/div[3]/a[1]");

    public CarrinhoPage(WebDriver driver) {
        super(driver);
    }

    public CadastroPage irParaCadastro() {
        click(botaoCadastro);
        return new CadastroPage(driver);
    }
}
