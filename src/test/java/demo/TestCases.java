package demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */
    @Test
    public void testCase01(){
        System.out.println("start test case 01");
        driver.get("https://www.scrapethissite.com/pages/");
        driver.findElement(By.xpath("//a[contains(text(),'Hockey Teams')]")).click();
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']")));

        ArrayList<HashMap<String,Object>> dataList = new ArrayList<>();

        for(int pageCount=1;pageCount<=4;pageCount++){
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'"+pageCount+"')]"))).click();
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']"));
            for(WebElement row: rows){
                HashMap<String,Object> map = new HashMap<>();
                String winPercent = row.findElement(By.xpath("./td[contains(@class,'pct')]")).getText().trim();
                Double winPercentage = Double.parseDouble(winPercent);
                if(winPercentage<0.40){
                    //Getting current currentEpochTime
                    long currentTimeMillis = System.currentTimeMillis();
                    //LocalTime myObj = LocalTime.now();
                    long currentEpochTime = currentTimeMillis/1000;

                    String teamName = row.findElement(By.xpath("./td[@class='name']")).getText();

                    int year = Integer.parseInt(row.findElement(By.xpath("./td[@class='year']")).getText());

                    map.put("Epoch Time of Scrape",currentEpochTime);
                    map.put("Team Name",teamName);
                    map.put("Year",year);
                    map.put("Win%",winPercentage);
                    dataList.add(map);
                }
            }
        }

        File jsonFile = Wrappers.createAndAddDataToJsonFIle(dataList,"hockey-team-data");

        // Assert that the file exists and is not empty
        Assert.assertTrue(jsonFile.exists(), "The JSON file does not exist");
        Assert.assertTrue(jsonFile.length()>0,"The JSON file is empty");

        System.out.println("End test case 01");
    }

    @Test
    public void testCase02() throws InterruptedException {
        System.out.println("start test case 02");
        driver.get("https://www.scrapethissite.com/pages/");
        driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]")).click();

        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("oscars")));

        ArrayList<HashMap<String,Object>> dataList = new ArrayList<>();

        List<WebElement> years = driver.findElements(By.xpath("//a[@class='year-link']"));

        for(WebElement year : years){
           String selectedYear = year.getText();
           year.click();
           Thread.sleep(2000);
           List<WebElement> allFilms = driver.findElements(By.xpath("//tr[@class='film']"));
           int count = 0;
           boolean isWinner = false;
           for(int i=0;i<5;i++){
               HashMap<String,Object> map = new HashMap<>();

               count++;
               if(count==1){
                   isWinner = true;
               }else isWinner = false;

               long currentTime = System.currentTimeMillis();
               long currentEpochTime = currentTime/1000;

               String filmTitle = allFilms.get(i).findElement(By.xpath("./td[@class='film-title']")).getText();

               int nominatios = Integer.parseInt(allFilms.get(i).findElement(By.xpath("./td[@class='film-nominations']")).getText().trim());

               int awards = Integer.parseInt(allFilms.get(i).findElement(By.xpath("./td[@class='film-awards']")).getText().trim());

               map.put("Epoch Time of Scrape",currentEpochTime);
               map.put("Year",selectedYear);
               map.put("FilmTitle",filmTitle);
               map.put("Nomination",nominatios);
               map.put("Awards",awards);
               map.put("IsWinner",isWinner);
               dataList.add(map);
           }
        }

        File jsonFile = Wrappers.createAndAddDataToJsonFIle(dataList,"oscar-winner-data");

        // Assert that the file exists and is not empty
        Assert.assertTrue(jsonFile.exists(), "The JSON file does not exist");
        Assert.assertTrue(jsonFile.length()>0,"The JSON file is empty");

        System.out.println("End test case 02");

    }

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}