package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class SauceDemoTests {

    WebDriver driver;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- End to End Checkout ---
    @Test(priority = 1)
    public void testEndToEndCheckout() {
        driver.get("https://www.saucedemo.com/");

        // Login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Add to Cart
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // Navigate to Cart & Checkout
        driver.findElement(By.className("shopping_cart_link")).click();
        driver.findElement(By.id("checkout")).click();

        // Fill Checkout Information (Using a random dummy name)
        driver.findElement(By.id("first-name")).sendKeys("Taylor");
        driver.findElement(By.id("last-name")).sendKeys("Smith");
        driver.findElement(By.id("postal-code")).sendKeys("90210");
        driver.findElement(By.id("continue")).click();

        // Finish Checkout
        driver.findElement(By.id("finish")).click();

        // Verify Completion
        WebElement completeHeader = driver.findElement(By.className("complete-header"));
        Assert.assertEquals(completeHeader.getText(), "Thank you for your order!");
    }

    // --- Negative Login Test 1: Negative Login Test (Locked Out User) ---
    @Test(priority = 2)
    public void testLockedOutUserLogin() {
        driver.get("https://www.saucedemo.com/");

        // Login using the provided locked out credentials
        driver.findElement(By.id("user-name")).sendKeys("locked_out_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Verify the exact error message appears
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue(errorMessage.isDisplayed());
        Assert.assertTrue(errorMessage.getText().contains("Sorry, this user has been locked out."));
    }

    // --- Negative Login Test 2: Add and Remove Item from Cart ---
    @Test(priority = 3)
    public void testAddAndRemoveItem() {
        driver.get("https://www.saucedemo.com/");

        // Login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Add item to cart
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // Verify cart badge shows 1 item
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "1");

        // The "Add to cart" button dynamically changes its ID to "remove..."
        // Click the remove button from the inventory page
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        // Verify the cart badge disappears completely from the DOM
        // Using findElements (plural) prevents throwing a NoSuchElementException if it doesn't exist
        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        Assert.assertTrue(badges.isEmpty(), "Cart badge should be completely gone after removing the item.");
    }
}