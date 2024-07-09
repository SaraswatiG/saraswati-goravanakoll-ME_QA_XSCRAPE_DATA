package demo.wrappers;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    public static File createAndAddDataToJsonFIle(ArrayList<HashMap<String,Object>> dataList, String filename){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String hockeyTeamJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataList);
            //System.out.println(hockeyTeamJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String userDir = System.getProperty("user.dir");
        File jsonFile = new File(userDir + "\\src\\test\\resources\\"+filename+".json");

        //Writing JSON on a file
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(jsonFile, dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonFile;
    }
}
