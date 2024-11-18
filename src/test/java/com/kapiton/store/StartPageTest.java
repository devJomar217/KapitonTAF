package com.kapiton.store;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;


public class StartPageTest {

    private ExtentReports extent;
    private List<Long> pageLoadTimes = new ArrayList<>();
    private long totalTestDuration = 0;
    private int numUsers;
    private int concurrentUsers;
    private List<String> pageNames = new ArrayList<>(); // List to store page names
    private List<Long> loginPageLoadTimes = new ArrayList<>(); // Track login page load times

    @BeforeSuite
    public void setUp() {
        // Format timestamp for backup report name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Main report file name
        ExtentSparkReporter mainReporter = new ExtentSparkReporter("target/report/KapitonTAF-Test-Report.html");
        mainReporter.config().setReportName("KapitonTAF Test Report");
        mainReporter.config().setDocumentTitle("Kapiton Stress Test");
        mainReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

        // Backup report file name with timestamp
        ExtentSparkReporter backupReporter = new ExtentSparkReporter("target/report/KapitonTAF-Test-Report_" + timestamp + ".html");
        backupReporter.config().setReportName("KapitonTAF Test Report Backup");
        backupReporter.config().setDocumentTitle("Kapiton Stress Test Backup");
        backupReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

        // Attach both the main and backup reporters
        extent = new ExtentReports();
        extent.attachReporter(mainReporter, backupReporter);
    }

    @Test
    @Parameters({"numUsers", "concurrentUsers"})
    public void testLogin(int numUsers, int concurrentUsers) throws InterruptedException {
        this.numUsers = numUsers;
        this.concurrentUsers = concurrentUsers;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        for (int i = 1; i <= numUsers; i++) {
            final int userId = i;
            executor.submit(() -> simulateLogin(userId));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    private void simulateLogin(int userId) {
        ExtentTest test = extent.createTest("Login Test for User " + userId);
        WebDriver driver = null;
        long startTime = System.currentTimeMillis();

        try {
            driver = createRemoteWebDriver();
            test.info("Starting login test for User " + userId);

            // Open the login page
            driver.get("https://kapiton.store/user/login-register");
            test.info("Opened login page: https://kapiton.store/user/login-register");

            // Wait for the login page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))); // Wait until body tag is visible, indicating page load

            // Record the login page load time
            long pageLoadTime = System.currentTimeMillis() - startTime;
            loginPageLoadTimes.add(pageLoadTime);

            // Locate the email and password fields and the login button
            WebElement emailField = driver.findElement(By.id("form-field-email"));
            WebElement passwordField = driver.findElement(By.id("form-field-password"));
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

            // Fill in the email and password
            emailField.sendKeys("REPLACE_WITH_ACTUAL_VALUE");  // Replace with actual test email
            passwordField.sendKeys("REPLACE_WITH_ACTUAL_VALUE");  // Replace with actual test password
            test.info("Entered email and password for login.");

            // Click the login button
            loginButton.click();
            test.info("Clicked the login button.");

            // Wait for the page to load after login and check if redirected to the homepage
            wait.until(ExpectedConditions.urlToBe("https://kapiton.store/"));

            pageLoadTime = System.currentTimeMillis() - startTime;
            test.info("Login completed in " + formatTime(pageLoadTime));

            if (pageLoadTime > 3000) {
                test.fail("Login page load time exceeded acceptable limit: " + formatTime(pageLoadTime));
            }

            test.pass("User " + userId + " successfully logged in and redirected to the homepage.");

        } catch (Exception e) {
            test.fail("User " + userId + " failed to login: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        long totalTestDurationForUser = System.currentTimeMillis() - startTime;
        totalTestDuration += totalTestDurationForUser;
        test.info("Total login test duration for User " + userId + ": " + formatTime(totalTestDurationForUser));
    }

    @Test
    @Parameters({"numUsers", "concurrentUsers"})
    public void testNavigateToPages(int numUsers, int concurrentUsers) throws InterruptedException {
        this.numUsers = numUsers;
        this.concurrentUsers = concurrentUsers;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        for (int i = 1; i <= numUsers; i++) {
            final int userId = i;
            executor.submit(() -> simulatePageNavigation(userId));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    private void simulatePageNavigation(int userId) {
        ExtentTest test = extent.createTest("Page Navigation Test for User " + userId);
        WebDriver driver = null;
        long startTime = System.currentTimeMillis();

        try {
            driver = createRemoteWebDriver();
            test.info("Starting page navigation test for User " + userId);

            // First, open the website
            driver.get("https://kapiton.store/");
            test.info("Opened website: https://kapiton.store/");

            // Wait for the home page to fully load (wait for the body tag to be visible)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))); // waits for the body tag to be visible, indicating page load

            // List of menu items with their link text
            String[] menuItems = {
                    "HOME",
                    "PRODUCTS",
                    "MERCHANTS",
                    "Clothing",
                    "Electronics",
                    "Home and Living",
                    "Cosmetics",
                    "Toys and Games",
                    "Pet"
            };

            // Loop through each menu item and click on it
            for (String item : menuItems) {
                long pageStartTime = System.currentTimeMillis();

                // Find and click the menu item by its link text
                WebElement link = driver.findElement(By.linkText(item));
                link.click();
                test.info("Clicked on: " + item);

                // Wait until the page has fully loaded (wait for the body tag to be visible again)
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))); // or another specific element indicating page load is complete

                long pageLoadTime = System.currentTimeMillis() - pageStartTime;
                pageLoadTimes.add(pageLoadTime);
                pageNames.add(item);  // Add the page name to the list
                test.info("Page " + item + " loaded in " + formatTime(pageLoadTime));

                if (pageLoadTime > 3000) {
                    test.fail("Page load time exceeded acceptable limit: " + formatTime(pageLoadTime));
                }
            }

            test.pass("Completed page navigation for User " + userId);

        } catch (Exception e) {
            test.fail("User " + userId + " failed: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        long totalTestDurationForUser = System.currentTimeMillis() - startTime;
        totalTestDuration += totalTestDurationForUser;
        test.info("Total test duration for User " + userId + ": " + formatTime(totalTestDurationForUser));
    }

    private WebDriver createRemoteWebDriver() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized"); // Maximizes the browser window
    
            // Set browser capabilities
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("browserName", "chrome");
            capabilities.setCapability("platformName", "Windows 11");
            capabilities.merge(options); // Merge ChromeOptions into capabilities
    
            // Initialize RemoteWebDriver
            URL hubUrl = new URL("http://192.168.1.13:4444/wd/hub");
            return new RemoteWebDriver(hubUrl, capabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage());
        }
    }

    private String formatTime(long timeInMs) {
        return String.format("%.2f seconds", timeInMs / 1000.0);
    }

    @AfterSuite
    public void tearDown() {
        long averagePageLoadTime = (long) pageLoadTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long averageLoginPageLoadTime = (long) loginPageLoadTimes.stream().mapToLong(Long::longValue).average().orElse(0);

        long minLoginPageLoadTime = loginPageLoadTimes.stream().min(Long::compareTo).orElse(0L);
        long maxLoginPageLoadTime = loginPageLoadTimes.stream().max(Long::compareTo).orElse(0L);

        ExtentTest summaryTest = extent.createTest("Stress Test Summary");

        // Adding overall summary
        summaryTest.info("Total Users Simulated: " + numUsers);
        summaryTest.info("Average Page Load Time: " + formatTime(averagePageLoadTime));
        summaryTest.info("Total Test Duration: " + formatTime(totalTestDuration));

        // Add Login page load time summary
        summaryTest.info("Login Page Load Time - Min: " + formatTime(minLoginPageLoadTime));
        summaryTest.info("Login Page Load Time - Avg: " + formatTime(averageLoginPageLoadTime));
        summaryTest.info("Login Page Load Time - Max: " + formatTime(maxLoginPageLoadTime));

        // Adding per-page summary
        summaryTest.info("Per-page load time summary:");
        for (int i = 0; i < pageLoadTimes.size(); i++) {
            String pageName = pageNames.get(i);
            long pageLoadTime = pageLoadTimes.get(i);
            summaryTest.info("Page: " + pageName + " loaded in " + formatTime(pageLoadTime));
        }

        extent.flush();
    }

}
