
# Kapiton TAF (Test Automation Framework)

Kapiton TAF is a Test Automation Framework built using Selenium and TestNG for stress testing web applications. This project requires Java 11 and Apache Maven for building and running tests.

## Prerequisites

1. **Java 11**: Ensure Java 11 is installed and `JAVA_HOME` is set up.
    - [Download Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) if not already installed.
    - Verify Java installation by running:
      ```bash
      java -version
      ```

2. **Apache Maven**: Maven is required for building the project and managing dependencies.
    - [Download Maven](https://maven.apache.org/download.cgi) and extract it to a desired location.
    - Add the Maven `bin` directory to your system PATH.
    - Verify Maven installation by running:
      ```bash
      mvn -version
      ```

3. **Selenium Server 4.25.0**: Download the Selenium Server `.jar` file to run the server locally.
    - [Download Selenium Server](https://www.selenium.dev/downloads/).

## Installation and Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/devJomar217/KapitonTAF.git
   ```

2. **Extract the Project** (if downloaded as a ZIP):
    - Extract the project to a local directory.

3. **Build the Project**:
    - In the project root directory, run:
      ```bash
      mvn clean install
      ```
    - This command will compile the code and install dependencies.

4. **Set Up Selenium Server**:
    - In one terminal window, start the Selenium hub:
      ```bash
      java -jar selenium-server-4.25.0.jar hub
      ```
    - In another terminal window, start a Selenium node with configuration:
      ```bash
      java -jar selenium-server-4.25.0.jar node --config nodeConfig.json
      ```

5. **Run Tests**:
    - To execute the test suite, use:
      ```bash
      mvn test
      ```
6. **Modifying the Number of Users and Concurrent Users**: Here, users are instructed on how to modify the `testng.xml` file to set their preferred values for the number of users (`numUsers`) and concurrent users (`concurrentUsers`).

7. **Test Reports**: Users are informed about where to find the test reports (in the `target/` folder), as well as the naming conventions used for the reports.

8. **Important Notes**: Any critical information regarding login credentials and Java/Maven compatibility is listed here.

## Report

The custom stress test report will be generated in the following location after running the tests: target/custom-stress-test-report


## Configuration Details

- **Java Version**: Ensure Java 11 is installed and configured.
- **Maven**: Maven is required for dependency management and test execution.
- **Selenium Server**: Running the hub and node configurations allows for remote test execution.

## Project Structure

- `src/test`: Contains test scripts written in Java.
- `pom.xml`: The Maven configuration file managing dependencies and plugins.
- `nodeConfig.json`: Configuration file for the Selenium node setup.

## Troubleshooting

- If `mvn` commands are not recognized, ensure Maven's `bin` directory is added to your PATH.
- For Java issues, confirm that `JAVA_HOME` points to Java 11.

## License

This project is licensed under the MIT License.
