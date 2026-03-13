import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MyFirstTest {

    WebDriver driver; // This represents the browser

    @BeforeEach
    void setup() {
        // Automatically downloads the right ChromeDriver version
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Uncomment to run without opening browser

        driver = new ChromeDriver(options); // Opens Chrome
        driver.manage().window().maximize();
    }

    @Test
    void testGoogleSearch() {
        // 1. Open a website
        driver.get("https://www.google.com");

        // 2. Find the search box and type something
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Selenium Java tutorial");
        searchBox.sendKeys(Keys.ENTER);

        // 3. Wait a moment for results
        try { Thread.sleep(2000); } catch (Exception e) {}

        // 4. Check the page title contains our search
        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        Assertions.assertTrue(title.contains("Selenium"),
                "Title should contain 'Selenium'");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit(); // Always close the browser after test
        }
    }
}