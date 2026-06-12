package selenium.TestSelenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.pages.MenuPage;

public class MenuSeleniumTest {

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
    public void testMenuCarregaComTituloCorreto() {
        MenuPage menuPage = MenuPage.abrir(driver);
        assertTrue(menuPage.tituloCorreto(),
            "Título da página do cardápio deve conter 'Card'");
    }
 
    @Test
    public void testSecaoLanchesEstaPresente() {
        MenuPage menuPage = MenuPage.abrir(driver);
        assertTrue(menuPage.screenLanchesVisivel(),
            "O elemento #screen (seção de lanches) deve estar presente no DOM da página do menu");
    }
 
    @Test
    public void testSecaoBebidasEstaPresente() {
        MenuPage menuPage = MenuPage.abrir(driver);
        assertTrue(menuPage.screenBebidasVisivel(),
            "O elemento #screenBebidas (seção de bebidas) deve estar presente no DOM da página do menu");
    }
 
    @Test
    public void testLinkCarrinhoPresente() {
        MenuPage.abrir(driver);
        boolean linkPresente = driver.findElements(By.cssSelector("#carrinho a")).size() > 0;
        assertTrue(linkPresente,
            "Link para o carrinho deve estar presente na navegação do menu");
    }
 
    @Test
    public void testClicarVerLanchesExibeSecaoLanches() {
        MenuPage menuPage = MenuPage.abrir(driver);
        menuPage.mostrarLanches();
        assertTrue(menuPage.screenLanchesVisivel(),
            "Após clicar em 'Ver Lanches', a seção de lanches deve estar visível");
    }

    @Test
    public void testClicarVerBebidasExibeSecaoBebidas() {
        MenuPage menuPage = MenuPage.abrir(driver);
        menuPage.mostrarBebidas();
        assertTrue(menuPage.screenBebidasVisivel(),
            "Após clicar em 'Ver Bebidas', a seção de bebidas deve estar visível");
    }
}
