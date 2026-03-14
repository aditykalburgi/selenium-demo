package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class SignupTest extends BaseTest {

    private static final String LOGIN_URL  = "https://qa.pitchsap.com/login";
    private static final String SIGNUP_URL = "https://qa.pitchsap.com/signup";

    private static final By LOGIN_EMAIL    = By.id("email");
    private static final By LOGIN_PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON   = By.cssSelector("button[type='submit']");

    private static final By SIGNUP_FIRSTNAME = By.cssSelector("input[placeholder='First Name']");
    private static final By SIGNUP_LASTNAME  = By.cssSelector("input[placeholder='Last Name']");
    private static final By SIGNUP_EMAIL     = By.cssSelector("input[placeholder='Email']");
    private static final By SIGNUP_PHONE     = By.cssSelector("input[type='tel']");
    private static final By SIGNUP_PASSWORD  = By.cssSelector("input[type='password']");
    private static final By SIGNUP_BUTTON    = By.cssSelector("button[type='submit']");
    private static final By ROLE_DROPDOWN    = By.cssSelector("select.input-field");

    private static final String MY_EMAIL    = "adityakalburgi304@gmail.com";
    private static final String MY_PASSWORD = "Password";

    private void jsClick(By locator) {
        WebElement el = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void fillSignupForm(String roleValue, String email) {
        driver.get(SIGNUP_URL);
        try { Thread.sleep(3000); } catch (Exception e) {}

        wait.until(ExpectedConditions.presenceOfElementLocated(ROLE_DROPDOWN));
        Select roleSelect = new Select(driver.findElement(ROLE_DROPDOWN));
        roleSelect.selectByVisibleText(roleValue);
        try { Thread.sleep(1000); } catch (Exception e) {}

        System.out.println("Selected role: " + roleSelect.getFirstSelectedOption().getText());

        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_FIRSTNAME));
        driver.findElement(SIGNUP_FIRSTNAME).sendKeys("Test");
        driver.findElement(SIGNUP_LASTNAME).sendKeys("User");
        driver.findElement(SIGNUP_EMAIL).sendKeys(email);
        driver.findElement(SIGNUP_PHONE).sendKeys("7463526367");
        driver.findElement(SIGNUP_PASSWORD).sendKeys("Test@1234!");
        try { Thread.sleep(500); } catch (Exception e) {}

        jsClick(SIGNUP_BUTTON);
        try { Thread.sleep(5000); } catch (Exception e) {}

        System.out.println("URL after submit: " + driver.getCurrentUrl());
    }

    @Test
    public void testIdeatorSignupSuccess() {
        String email = "ideator_" + System.currentTimeMillis() + "@gmail.com";
        fillSignupForm("Ideator Role", email);                              // ✅ fixed
        assertFalse(driver.getCurrentUrl().contains("/signup"),
                "TC-001 FAILED — Still on signup after Ideator registration");
        System.out.println("TC-001 PASSED — Ideator signup successful");
    }

    @Test
    public void testConsultantSignupSuccess() {
        String email = "consultant_" + System.currentTimeMillis() + "@gmail.com";
        fillSignupForm("Consultant", email);                                // ✅ already correct
        assertFalse(driver.getCurrentUrl().contains("/signup"),
                "TC-002 FAILED — Still on signup after Consultant registration");
        System.out.println("TC-002 PASSED — Consultant signup successful");
    }

    @Test
    public void testSignupDuplicateEmail() {
        fillSignupForm("Ideator Role", MY_EMAIL);                           // ✅ fixed
        String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean hasError = pageText.contains("already") ||
                pageText.contains("exists")  ||
                pageText.contains("taken")   ||
                driver.getCurrentUrl().contains("/signup");
        System.out.println("TC-003 | URL: " + driver.getCurrentUrl());
        assertTrue(hasError, "TC-003 FAILED — No duplicate email error shown");
        System.out.println("TC-003 PASSED — Duplicate email correctly rejected");
    }

    @Test
    public void testSignupEmptyFields() {
        driver.get(SIGNUP_URL);
        try { Thread.sleep(3000); } catch (Exception e) {}

        wait.until(ExpectedConditions.presenceOfElementLocated(ROLE_DROPDOWN));
        new Select(driver.findElement(ROLE_DROPDOWN)).selectByVisibleText("Ideator Role"); // ✅ fixed
        try { Thread.sleep(1000); } catch (Exception e) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_BUTTON));
        jsClick(SIGNUP_BUTTON);
        try { Thread.sleep(2000); } catch (Exception e) {}

        System.out.println("TC-004 | URL: " + driver.getCurrentUrl());
        assertTrue(driver.getCurrentUrl().contains("/signup"),
                "TC-004 FAILED — Should stay on signup with empty fields");
        System.out.println("TC-004 PASSED — Empty form blocked correctly");
    }

    @Test
    public void testLoginSuccess() {
        driver.get(LOGIN_URL);
        try { Thread.sleep(2000); } catch (Exception e) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_EMAIL));
        driver.findElement(LOGIN_EMAIL).sendKeys(MY_EMAIL);
        driver.findElement(LOGIN_PASSWORD).sendKeys(MY_PASSWORD);
        jsClick(LOGIN_BUTTON);
        try { Thread.sleep(3000); } catch (Exception e) {}

        String urlAfter = driver.getCurrentUrl();
        System.out.println("TC-013 | URL after login: " + urlAfter);
        assertFalse(urlAfter.contains("/login"),
                "TC-013 FAILED — Still on login. Check email/password.");
        System.out.println("TC-013 PASSED — Redirected to: " + urlAfter);
    }

    @Test
    public void testLoginWrongPassword() {
        driver.get(LOGIN_URL);
        try { Thread.sleep(2000); } catch (Exception e) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_EMAIL));
        driver.findElement(LOGIN_EMAIL).sendKeys(MY_EMAIL);
        driver.findElement(LOGIN_PASSWORD).sendKeys("WrongPassword000!");
        jsClick(LOGIN_BUTTON);
        try { Thread.sleep(2000); } catch (Exception e) {}

        String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean hasError = pageText.contains("invalid")   ||
                pageText.contains("incorrect") ||
                pageText.contains("wrong")     ||
                pageText.contains("error")     ||
                driver.getCurrentUrl().contains("/login");
        System.out.println("TC-014 | URL: " + driver.getCurrentUrl());
        assertTrue(hasError, "TC-014 FAILED — No error for wrong password");
        System.out.println("TC-014 PASSED — Wrong password correctly rejected");
    }

    @Test
    public void testLoginUnregisteredEmail() {
        driver.get(LOGIN_URL);
        try { Thread.sleep(2000); } catch (Exception e) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_EMAIL));
        driver.findElement(LOGIN_EMAIL).sendKeys("nobody_xyz_999@fake.com");
        driver.findElement(LOGIN_PASSWORD).sendKeys("SomePass123!");
        jsClick(LOGIN_BUTTON);
        try { Thread.sleep(2000); } catch (Exception e) {}

        System.out.println("TC-015 | URL: " + driver.getCurrentUrl());
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "TC-015 FAILED — Should stay on login for unknown email");
        System.out.println("TC-015 PASSED — Unregistered email correctly rejected");
    }
}
