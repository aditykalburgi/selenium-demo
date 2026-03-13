package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TC-067, TC-068: Logout Tests
 * TC-061, TC-070: Security Tests
 */
public class LogoutTest extends BaseTest {

    private static final String VALID_EMAIL    = "adityakalburghi304@gmail.com";
    private static final String VALID_PASSWORD = "Password123";         // ← change

    private void loginFirst() {
        goTo("/login");
        driver.findElement(By.id("email")).sendKeys(VALID_EMAIL);
        driver.findElement(By.id("password")).sendKeys(VALID_PASSWORD);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    // ────────────────────────────────────────────────
    // TC-067: Logout redirects to homepage
    // ────────────────────────────────────────────────
    @Test
    public void testLogout() {
        loginFirst();

        // Click profile/avatar dropdown
        driver.findElement(By.cssSelector("[data-testid='user-menu'], .avatar, .profile-icon")).click();

        // Click logout
        driver.findElement(By.xpath("//button[contains(text(),'Logout')] | //a[contains(text(),'Logout')]")).click();

        wait.until(ExpectedConditions.urlToBe(BaseTest.BASE_URL + "/"));
        assertTrue(driver.getCurrentUrl().equals(BaseTest.BASE_URL + "/") ||
                        driver.getCurrentUrl().contains("/home"),
                "Should be redirected to homepage after logout");
    }

    // ────────────────────────────────────────────────
    // TC-068: Accessing /dashboard after logout → redirect to login
    // ────────────────────────────────────────────────
    @Test
    public void testProtectedRouteRedirect() {
        loginFirst();

        // Logout first
        driver.findElement(By.cssSelector("[data-testid='user-menu'], .avatar, .profile-icon")).click();
        driver.findElement(By.xpath("//button[contains(text(),'Logout')] | //a[contains(text(),'Logout')]")).click();
        wait.until(ExpectedConditions.urlContains("/"));

        // Try to access protected route
        goTo("/dashboard");

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Protected route should redirect to login when not authenticated");
    }

    // ────────────────────────────────────────────────
    // TC-061: XSS payload in search
    // ────────────────────────────────────────────────
    @Test
    public void testXSSInSearch() {
        loginFirst();

        goTo("/search");
        driver.findElement(By.cssSelector("input[type='search'], #search-input")).sendKeys("<script>alert('xss')</script>");
        driver.findElement(By.cssSelector("button[type='submit'], .search-btn")).click();

        // If XSS succeeded, an alert would be present — assert it is NOT
        try {
            driver.switchTo().alert();
            fail("XSS vulnerability detected — alert was triggered!");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // Good — no alert means XSS was blocked
            assertTrue(true, "XSS blocked correctly");
        }
    }

    // ────────────────────────────────────────────────
    // TC-070: Auth token NOT stored in localStorage
    // ────────────────────────────────────────────────
    @Test
    public void testTokenNotInLocalStorage() {
        loginFirst();

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Check common token key names in localStorage
        String[] tokenKeys = {"token", "authToken", "jwt", "access_token", "auth"};
        for (String key : tokenKeys) {
            Object val = js.executeScript("return window.localStorage.getItem('" + key + "');");
            assertNull(val, "Auth token '" + key + "' should NOT be stored in localStorage (use HttpOnly cookies instead)");
        }
    }
}