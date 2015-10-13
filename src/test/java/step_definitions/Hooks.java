package step_definitions;

import java.net.MalformedURLException;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import java.net.URL;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.saucerest.SauceREST;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Hooks{

    public static WebDriver driver;
    private SauceREST client;

    /**
     * Instance variable which contains the Sauce Job Id.
     */
    private String sessionId;
    private String runType = System.getProperty("RUN_TYPE");

    @Before
    /**
     * Delete all cookies at the start of each scenario to avoid
     * shared state between tests
     */
    public void openBrowser() throws MalformedURLException {
    	System.out.println("Called openBrowser");

        String browser = System.getProperty("BROWSER");

        if(runType.equals("SAUCELABS")) {
            SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(System.getProperty("SAUCE_LABS_USERNAME"), System.getProperty("SAUCE_LABS_ACCESS_KEY"));
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.BROWSER_NAME, "internet explorer");
            capabilities.setCapability(CapabilityType.VERSION, "11");
            capabilities.setCapability(CapabilityType.PLATFORM, "Windows 7");
            capabilities.setCapability("name", "Sauce Sample Test");
            driver = new RemoteWebDriver(
                    new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
                    capabilities);
            this.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();

            client = new SauceREST(System.getProperty("SAUCE_LABS_USERNAME"), System.getProperty("SAUCE_LABS_ACCESS_KEY"));
        }
        else {
            switch (browser) {
                case "chrome":
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    driver = new FirefoxDriver();
                    break;
                case "ie":
                    driver = new InternetExplorerDriver();
                    break;
                case "safari":
                    driver = new SafariDriver();
                    break;
                default:
                    driver = new ChromeDriver();
                    break;
            }
        }

        System.out.println("Opening Browser...."+browser);
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
    }

     
    @After
    /**
     * Embed a screenshot in test report if test is marked as failed
     */
    public void embedScreenshot(Scenario scenario) {
       
        if(scenario.isFailed() && !runType.equals("SAUCELABS")) {
            try {
                 scenario.write("Current Page URL is " + driver.getCurrentUrl());
    //            byte[] screenshot = getScreenshotAs(OutputType.BYTES);
                byte[] screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
                scenario.embed(screenshot, "image/png");
            } catch (WebDriverException somePlatformsDontSupportScreenshots) {
                System.err.println(somePlatformsDontSupportScreenshots.getMessage());
            }
        }

        driver.quit();

        if(runType.equals("SAUCELABS")) {
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("passed", !scenario.isFailed());
            client.updateJobInfo(sessionId, updates);
            System.out.println(client.getJobInfo(sessionId));
        }
    }
    
}