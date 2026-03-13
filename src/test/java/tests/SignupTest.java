package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TC-001, TC-002, TC-003, TC-004, TC-005, TC-006
 * Tests: Signup / Registration flows
 *
 * ⚠️ BEFORE RUNNING:
 *   Update selectors (By.id, By.name, etc.) to match your actual site HTML.
 *   Use Chrome DevTools (F12 → right-click element → Inspect) to find them.
 */
public class SignupTest extends BaseTest {

    // ────────────────────────────────────────────────
    // TC-001: Ideator signs up successfully
    // ────────────────────────────────────────────────
    @Test
    public void testIdeatorSignupSuccess() {
        goTo("/signup");

        // Select Ideator role
        driver.findElement(By.xpath("//button[contains(text(),'Ideator')]")).click();

        // Fill form
        driver.findElement(By.id("name")).sendKeys("Test Ideator");
        driver.findElement(By.id("email")).sendKeys("ideator_" + System.currentTimeMillis() + "@test.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234!");

        // Accept T&C
        WebElement checkbox = driver.findElement(By.id("terms"));
        if (!checkbox.isSelected()) checkbox.click();

        // Submit
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Assert: redirected to dashboard or onboarding
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should redirect to dashboard after signup");
    }

    // ────────────────────────────────────────────────
    // TC-002: Consultant signs up successfully
    // ────────────────────────────────────────────────
    @Test
    public void testConsultantSignupSuccess() {
        goTo("/signup");

        driver.findElement(By.xpath("//button[contains(text(),'Consultant')]")).click();

        driver.findElement(By.id("name")).sendKeys("Test Consultant");
        driver.findElement(By.id("email")).sendKeys("consultant_" + System.currentTimeMillis() + "@test.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234!");

        // Consultant-specific field
        driver.findElement(By.id("expertise")).sendKeys("Marketing");

        WebElement checkbox = driver.findElement(By.id("terms"));
        if (!checkbox.isSelected()) checkbox.click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Consultant should be redirected to dashboard");
    }

    // ────────────────────────────────────────────────
    // TC-003: Signup with already-used email
    // ────────────────────────────────────────────────
    @Test
    public void testDuplicateEmailError() {
        goTo("/signup");

        driver.findElement(By.id("name")).sendKeys("Duplicate User");
        // Use an email you know already exists in the system
        driver.findElement(By.id("email")).sendKeys("existing@test.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234!");

        WebElement checkbox = driver.findElement(By.id("terms"));
        if (!checkbox.isSelected()) checkbox.click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Assert error message appears
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".error-message, [data-testid='email-error']")));

        assertTrue(errorMsg.getText().toLowerCase().contains("email") ||
                        errorMsg.getText().toLowerCase().contains("already"),
                "Error should mention duplicate email");
    }

    // ────────────────────────────────────────────────
    // TC-004: Invalid email format
    // ────────────────────────────────────────────────
    @Test
    public void testInvalidEmailFormat() {
        goTo("/signup");

        driver.findElement(By.id("email")).sendKeys("notanemail");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email-error")));

        assertFalse(errorMsg.getText().isEmpty(), "Email validation error should appear");
    }

    // ────────────────────────────────────────────────
    // TC-006: Submit without accepting T&C
    // ────────────────────────────────────────────────
    @Test
    public void testSignupWithoutTerms() {
        goTo("/signup");

        driver.findElement(By.id("name")).sendKeys("No Terms User");
        driver.findElement(By.id("email")).sendKeys("noterms@test.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@1234!");
        // Intentionally do NOT check T&C
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Assert still on signup page (not redirected)
        assertTrue(driver.getCurrentUrl().contains("/signup"),
                "Should stay on signup page if T&C not accepted");
    }
}