package com.kapiton.store;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;




public class StartPageTest {

    private ExtentReports extent;
    private List<Long> pageLoadTimes = new ArrayList<>();
    private long totalTestDuration = 0;
    private int numUsers;
    private int concurrentUsers;
    private List<String> pageNames = new ArrayList<>(); // List to store page names
    private List<Long> loginPageLoadTimes = new ArrayList<>(); // Track login page load times
    private List<PageTestResult> pageTestResultList = new ArrayList<>();
    

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
        long pageLoadTime = 0;  // Initialize page load time for the user
    
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
            pageLoadTime = System.currentTimeMillis() - startTime;
            loginPageLoadTimes.add(pageLoadTime);
    
            // Locate the email and password fields and the login button
            WebElement emailField = driver.findElement(By.id("form-field-email"));
            WebElement passwordField = driver.findElement(By.id("form-field-password"));
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
            // Fill in the email and password
            emailField.sendKeys("ninofeliciano9@gmail.com");  // Replace with actual test email
            passwordField.sendKeys("ngapasWa.1");  // Replace with actual test password
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

    public void addPageLoadTimeToPageTestResult(String pageName, int userId, long pageLoadTime) {
        // Check if pageName exists in the list
        for (PageTestResult result : pageTestResultList) {
            if (result.getPageName().equals(pageName)) {
                // If pageName exists, add the page load time and return
                result.addPageLoadData(userId, pageLoadTime);
                return;
            }
        }
        // If userId does not exist, create a new UserTestResult and add it to the list
        PageTestResult newTestResult = new PageTestResult(pageName);
        newTestResult.addPageLoadData(userId, pageLoadTime);
        pageTestResultList.add(newTestResult);
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
    
                try {
                    // Find and click the menu item by its link text
                    WebElement link = driver.findElement(By.linkText(item));
                    link.click();
                    test.info("Clicked on: " + item);
    
                    // Wait until the page has fully loaded (wait for the body tag to be visible again)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))); // or another specific element indicating page load is complete
    
                    long pageLoadTime = System.currentTimeMillis() - pageStartTime;
                    pageLoadTimes.add(pageLoadTime);
                    pageNames.add(item);  // Add the page name to the list
                    addPageLoadTimeToPageTestResult(item, userId, pageLoadTime);

                    test.info("Page " + item + " loaded in " + formatTime(pageLoadTime));
    
                    if (pageLoadTime > 3000) {
                        test.fail("Page load time exceeded acceptable limit: " + formatTime(pageLoadTime));
                    }
                } catch (Exception e) {
                    test.fail("Failed to load page: " + item + " for User " + userId + " due to: " + e.getMessage());
                    pageLoadTimes.add(-1L); // Indicate failure with a special value (optional)
                    pageNames.add(item);
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
            options.addArguments("--start-maximized");

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("browserName", "chrome");
            capabilities.setCapability("platformName", "Windows 11");
            capabilities.merge(options); // Merge ChromeOptions into capabilities

            URL hubUrl = new URL("http://192.168.1.13:4444/wd/hub");
            return new RemoteWebDriver(hubUrl, capabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage());
        }
    }


    private String formatTime(long timeInMs) {
        if (timeInMs == -1) {
            return "Data Missing"; // Handle missing data
        }
        return String.format("%.2f seconds", timeInMs / 1000.0);
    }

    @AfterSuite
    public void tearDown() {
        System.out.println(pageTestResultList.toString());
        long averagePageLoadTime = (long) pageLoadTimes.stream().filter(time -> time != -1).mapToLong(Long::longValue).average().orElse(0);
        long minPageLoadTime = pageLoadTimes.stream().filter(time -> time != -1).min(Long::compareTo).orElse(0L);
        long maxPageLoadTime = pageLoadTimes.stream().filter(time -> time != -1).max(Long::compareTo).orElse(0L);
        long minLoginTime = Long.MAX_VALUE;
        long maxLoginTime = Long.MIN_VALUE;
        long avgLoginTime = 0;
        ExtentTest summaryTest = extent.createTest("Stress Test Summary");
    
        // Adding overall summary to the report
        summaryTest.info("Total Users Simulated: " + numUsers);
        summaryTest.info("Average Page Load Time: " + formatTime(averagePageLoadTime));
        summaryTest.info("Total Test Duration: " + formatTime(totalTestDuration));
    
        // Generate a custom HTML summary file
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filePath = "target/report/SummaryReport-" + timestamp + ".html";
    
        // Ensure the report directory exists
        File reportDir = new File("target/report");
        if (!reportDir.exists()) {
            boolean dirCreated = reportDir.mkdirs(); // Create the directory if it doesn't exist
            if (dirCreated) {
                System.out.println("Report directory created.");
            } else {
                System.out.println("Failed to create report directory.");
            }
        }
    
        // Start building the HTML content
StringBuilder htmlContent = new StringBuilder();
htmlContent.append("<!DOCTYPE html>\n")
    .append("<html lang=\"en\">\n")
    .append("<head>\n")
    .append("<meta charset=\"UTF-8\">\n")
    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
    .append("<title>Kapiton Stress Test Summary</title>\n")
    // Add Bootstrap CDN link for styling and responsive design
    .append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n")
    .append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n") // Include Chart.js for visualization
    .append("</head>\n")
    .append("<body>\n")
    .append("<div class=\"container mt-5\">\n") // Add container for better padding and centering
    .append("<h1 class=\"text-center text-primary mb-4\">Kapiton Stress Test Summary</h1>\n") // Main heading

    // Test Summary section
    .append("<div class=\"card mb-4\">\n")
    .append("<div class=\"card-header bg-primary text-white\">\n")
    .append("<h2 class=\"h4 mb-0\">Test Overview</h2>\n")
    .append("</div>\n")
    .append("<div class=\"card-body\">\n")
    .append("<p><strong>Total Users Simulated:</strong> ").append(numUsers).append("</p>\n")
    .append("<p><strong>Total Test Duration:</strong> ").append(formatTime(totalTestDuration)).append("</p>\n")
    .append("</div>\n")
    .append("</div>\n");

// Login Test Summary Section (with min, avg, and max load time)
htmlContent.append("<div class=\"card mb-4\">\n")
    .append("<div class=\"card-header bg-success text-white\">\n")
    .append("<h2 class=\"h4 mb-0\">Login Test Summary</h2>\n")
    .append("</div>\n")
    .append("<div class=\"card-body\">\n")
    .append("<table class=\"table table-striped table-bordered table-hover\">\n")
    .append("<thead><tr><th>User</th><th>Login Time (s)</th></tr></thead>\n")
    .append("<tbody>\n");

if (loginPageLoadTimes.size() > 0) {
    
    long totalLoginTime = 0;
    int loginCount = 0;

    // Loop through the login page load times and generate rows
    for (int i = 1; i <= numUsers; i++) {
        if (i <= loginPageLoadTimes.size()) {
            long loginTime = loginPageLoadTimes.get(i - 1); // Assuming you store these times in a list
            String status = loginTime > 3000 ? "Failed (Timeout)" : "Passed";

            // Calculate min, max, and total for avg calculation
            if (loginTime < minLoginTime) minLoginTime = loginTime;
            if (loginTime > maxLoginTime) maxLoginTime = loginTime;
            totalLoginTime += loginTime;
            loginCount++;

            htmlContent.append("<tr><td>").append("User ").append(i).append("</td>")
                    //.append("<td>").append(status).append("</td>")
                    .append("<td>").append(formatSeconds(loginTime)).append("</td>") // Time in seconds
                    //.append("<td>").append(formatSeconds(totalTestDurationForUser)).append("</td>")
                    .append("</td></tr>\n");
        } else {
            // If no data for a user, display 'No Data' in the table
            htmlContent.append("<tr><td>").append("User ").append(i).append("</td>")
                    .append("<td>").append("No Data").append("</td>")
                    .append("<td>").append("N/A").append("</td>")
                    .append("<td>").append("N/A").append("</td></tr>\n");
        }
    }

    // Calculate avg login time
    avgLoginTime = (loginCount > 0) ? totalLoginTime / loginCount : 0;

    // Add min, avg, and max login times
    htmlContent.append("<tr class=\"table-success\"><td><strong>Min Login Time</strong></td><td>")
            .append(formatSeconds(minLoginTime)).append("</td></tr>\n")
            .append("<tr class=\"table-info\"><td><strong>Avg Login Time</strong></td><td>")
            .append(formatSeconds(avgLoginTime)).append("</td></tr>\n")
            .append("<tr class=\"table-danger\"><td><strong>Max Login Time</strong></td><td>")
            .append(formatSeconds(maxLoginTime)).append("</td></tr>\n");
} else {
    // Display a message if login data is empty
    htmlContent.append("<tr><td colspan=\"4\" class=\"text-center\">No login data available.</td></tr>\n");
}

htmlContent.append("</tbody>\n")
    .append("</table>\n")
    .append("</div>\n")
    .append("</div>\n");

// Overall Load Time Summary Section
htmlContent.append("<div class=\"card mb-4\">\n")
    .append("<div class=\"card-header bg-info text-white\">\n")
    .append("<h2 class=\"h4 mb-0\">Overall Load Times</h2>\n")
    .append("</div>\n")
    .append("<div class=\"card-body\">\n")
    .append("<table class=\"table table-striped table-bordered table-hover\">\n")
    .append("<thead><tr><th>Metric</th><th>Value (s)</th></tr></thead>\n")
    .append("<tbody>\n")
    .append("<tr><td><strong>Min Load Time</strong></td><td>").append(formatSeconds(minPageLoadTime)).append("</td></tr>\n")
    .append("<tr><td><strong>Avg Load Time</strong></td><td>").append(formatSeconds(averagePageLoadTime)).append("</td></tr>\n")
    .append("<tr><td><strong>Max Load Time</strong></td><td>").append(formatSeconds(maxPageLoadTime)).append("</td></tr>\n")
    .append("</tbody>\n")
    .append("</table>\n")
    .append("</div>\n")
    .append("</div>\n");

// Page Load Times per Test Section
for (PageTestResult pageTestResult : pageTestResultList) {
    String pageName = pageTestResult.getPageName();

    // Initialize variables for min, max, and total load time to calculate avg
    long minLoadTime = Long.MAX_VALUE;
    long maxLoadTime = Long.MIN_VALUE;
    long totalLoadTime = 0;
    int loadTimeCount = 0;

    htmlContent.append("<div class=\"card mb-4\">\n")
            .append("<div class=\"card-header bg-warning text-white\">\n")
            .append("<h2 class=\"h4 mb-0\">").append(pageName).append("</h2>\n")
            .append("</div>\n")
            .append("<div class=\"card-body\">\n")
            .append("<table class=\"table table-striped table-bordered table-hover\">\n") // Table with Bootstrap styling
            .append("<thead><tr><th>User</th><th>Page Load Time (s)</th></tr></thead>\n")
            .append("<tbody>\n");

    // Adding rows for each user and calculating min, max, and total load time for avg
    for (PageLoadData pageLoadData : pageTestResult.getPageLoadDataList()) {
        long loadTime = pageLoadData.getLoadTime();

        // Update min and max load time
        if (loadTime < minLoadTime) {
            minLoadTime = loadTime;
        }
        if (loadTime > maxLoadTime) {
            maxLoadTime = loadTime;
        }

        // Add load time for average calculation
        totalLoadTime += loadTime;
        loadTimeCount++;

        String user = "User " + pageLoadData.getUserID();
        htmlContent.append("<tr><td>").append(user).append("</td><td>")
                .append(formatSeconds(loadTime)).append("</td></tr>\n");
    }

    // Calculate average load time
    long avgLoadTime = (loadTimeCount > 0) ? totalLoadTime / loadTimeCount : 0;

    // Add the min, avg, and max load times to the HTML content with custom row colors using Bootstrap classes
    htmlContent.append("<tr class=\"table-success\"><td><strong>Min Load Time</strong></td><td>")
            .append(formatSeconds(minLoadTime)).append("</td></tr>\n")
            .append("<tr class=\"table-info\"><td><strong>Avg Load Time</strong></td><td>")
            .append(formatSeconds(avgLoadTime)).append("</td></tr>\n")
            .append("<tr class=\"table-danger\"><td><strong>Max Load Time</strong></td><td>")
            .append(formatSeconds(maxLoadTime)).append("</td></tr>\n");

    htmlContent.append("</tbody>\n")
            .append("</table>\n")
            .append("</div>\n")
            .append("</div>\n");
}

// Adding Chart.js for load time visualization
// htmlContent.append("<canvas id=\"loadTimeChart\" class=\"mt-4\"></canvas>\n")
//         .append("<script>\n")
//         .append("    var ctx = document.getElementById('loadTimeChart').getContext('2d');\n")
//         .append("    var loadTimeChart = new Chart(ctx, {\n")
//         .append("        type: 'bar',\n")
//         .append("        data: {\n")
//         .append("            labels: ['Login', 'Min Load Time', 'Avg Load Time', 'Max Load Time'],\n")
//         .append("            datasets: [{\n")
//         .append("                label: 'Page Load Times (s)',\n")
//         .append("                data: [").append(formatSeconds(minLoginTime)).append(", ")
//         .append(formatSeconds(avgLoginTime)).append(", ").append(formatSeconds(maxLoginTime)).append(", ")
//         .append(formatSeconds(minPageLoadTime)).append("],\n")
//         .append("                backgroundColor: ['rgba(76, 175, 80, 0.2)', 'rgba(33, 150, 243, 0.2)', 'rgba(244, 67, 54, 0.2)', 'rgba(33, 150, 243, 0.2)'],\n")
//         .append("                borderColor: ['rgba(76, 175, 80, 1)', 'rgba(33, 150, 243, 1)', 'rgba(244, 67, 54, 1)', 'rgba(33, 150, 243, 1)'],\n")
//         .append("                borderWidth: 1\n")
//         .append("            }]\n")
//         .append("        },\n")
//         .append("        options: {\n")
//         .append("            responsive: true,\n")
//         .append("            scales: {\n")
//         .append("                y: {\n")
//         .append("                    beginAtZero: true\n")
//         .append("                }\n")
//         .append("            }\n")
//         .append("        }\n")
//         .append("    });\n")
//         .append("</script>\n")
//         .append("</body>\n")
//         .append("</html>");

    
        // Write the HTML report to a file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(htmlContent.toString());
            summaryTest.pass("Custom HTML summary report generated at: " + filePath);
        } catch (IOException e) {
            summaryTest.fail("Error generating HTML summary report: " + e.getMessage());
        }

        extent.flush();
    }

    // Helper function to format time in seconds
private String formatSeconds(long milliseconds) {
    return String.format("%.3f", milliseconds / 1000.0);
}


}
