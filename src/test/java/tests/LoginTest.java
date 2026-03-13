package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TC-013, TC-014, TC-015, TC-018
 * Tests: Login / Authentication flows
 *
 * ⚠️ Update VALID_EMAIL / VALID_PASSWORD with a real test account.
 */
public class LoginTest extends BaseTest {

    private static final String VALID_EMAIL    = "adityakalburgi304@gmail.com";   // ← change
    private static final String VALID_PASSWORD = "Password123";           // ← change

    // Helper: fill and submit login form
    private void login(String email, String password) {
        goTo("/login");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    // ────────────────────────────────────────────────
    // TC-013: Valid login → dashboard
    // ────────────────────────────────────────────────
    @Test
    public void testValidLogin() {
        login(VALID_EMAIL, VALID_PASSWORD);

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Valid login should reach dashboard");
    }

    // ────────────────────────────────────────────────
    // TC-014: Wrong password → error message
    // ────────────────────────────────────────────────
    @Test
    public void testInvalidPasswordError() {
        login(VALID_EMAIL, "WrongPassword!");

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".error-message, [data-testid='login-error']")));

        String msg = error.getText().toLowerCase();
        assertTrue(msg.contains("invalid") || msg.contains("incorrect") || msg.contains("credentials"),
                "Error should say credentials are wrong");
    }

    // ────────────────────────────────────────────────
    // TC-015: Unregistered email → error
    // ────────────────────────────────────────────────
    @Test
    public void testUnregisteredEmailError() {
        login("nobody_" + System.currentTimeMillis() + "@test.com", "AnyPass123!");

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".error-message, [data-testid='login-error']")));

        assertFalse(error.getText().isEmpty(), "Error message should appear for unknown user");
    }

    // ────────────────────────────────────────────────
    // TC-018: Forgot password → success message
    // ────────────────────────────────────────────────
    @Test
    public void testForgotPasswordFlow() {
        goTo("/login");

        driver.findElement(By.linkText("Forgot Password")).click();

        wait.until(ExpectedConditions.urlContains("/forgot-password"));

        driver.findElement(By.id("email")).sendKeys(VALID_EMAIL);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement successMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".success-message, [data-testid='reset-success']")));

        assertFalse(successMsg.getText().isEmpty(),
                "Success message should appear after submitting reset request");
    }
}