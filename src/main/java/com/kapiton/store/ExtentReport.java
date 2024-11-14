 package com.kapiton.store;

 import com.aventstack.extentreports.ExtentReports;
 import com.aventstack.extentreports.ExtentTest;
 import com.aventstack.extentreports.reporter.ExtentSparkReporter;

 public class ExtentReport {
     private static ExtentReports extent;
     private static ExtentTest test;

     public static ExtentReports getExtentReport() {
         if (extent == null) {
             ExtentSparkReporter htmlReporter = new ExtentSparkReporter("extentReport.html");
             extent = new ExtentReports();
             extent.attachReporter(htmlReporter);
         }
         return extent;
     }

     public static ExtentTest createTest(String testName) {
         test = getExtentReport().createTest(testName);
         return test;
     }
 }
