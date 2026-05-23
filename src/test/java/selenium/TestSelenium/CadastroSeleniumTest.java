package selenium.TestSelenium;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.checkerframework.checker.units.qual.s;

public class CadastroSeleniumTest{
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCadastro(){
        driver.get("http://localhost:8080");
        WebElement cardapio = driver.findElement(By.xpath("/html/body/div/div[2]/div/div[3]/button"));
        cardapio.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        WebElement carrinho = driver.findElement(By.xpath("//*[@id=\"carrinho\"]/a"));
        carrinho.click();
        WebElement btnCadastro = driver.findElement(By.xpath("/html/body/div/div/div/div[3]/a[1]"));
        btnCadastro.click();
        WebElement inputNome = driver.findElement(By.xpath("//*[@id=\"Espaçamentotitle\"]"));
        inputNome.sendKeys("Lucas"); 
        WebElement inputSobrenome = driver.findElement(By.xpath("//*[@id=\"usuario\"]/input[2]"));
        inputSobrenome.sendKeys("Bogado");
        WebElement inputTelefone = driver.findElement(By.xpath("//*[@id=\"usuario\"]/input[3]"));
        inputTelefone.sendKeys("11999999999");
        WebElement inputUserName = driver.findElement(By.xpath("//*[@id=\"usuario\"]/input[4]"));
        inputUserName.sendKeys("lucasbogado_");
        WebElement inputPassword = driver.findElement(By.xpath("//*[@id=\"usuario\"]/input[5]"));
        inputPassword.sendKeys("123456");
        WebElement inputStreet = driver.findElement(By.xpath("//*[@id=\"endereco\"]/input[1]"));
        inputStreet.sendKeys("Rua aaaaa");
    }
}