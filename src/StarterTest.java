import static org.openqa.selenium.By.xpath;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.opencsv.*;

public class StarterTest {

    public static FirefoxDriver driver;

    public static final String URL = "https://www.tvrtke.com/zagreb/web-usluge-dizajn-hosting-programiranje";
    public static final String preStartClick= "//img[contains(@id, 'imgHeader')]";
    public static final String extendSearch = "//div[contains(text(), 'Napredna tra')]";
    public static final String selectCityExpand = "//select[contains(@title, 'Grad')]";
    public static final String selectCityZagreb = "//select[contains(@title, 'Grad')]/option[contains(@value, '10000')]";
    public static final String query = "//input[contains(@id, 'Djelatnost')]";
    public static final String queryText = "Web usluge - dizajn, hosting, programiranje";
    public static final String btn = "//input[contains(@id, 'btnSearchAdvanced')]";
    public static final String loadMore = "//input[contains(@id, 'btnLoadMoreResults')]";


    public static List<String> companyURLS = new ArrayList<String>();
    public static final String querySelect = ".aNazivFirme";
    public static String baseUrlForIteratingPages = "https://www.tvrtke.com/zagreb/web-usluge-dizajn-hosting-programiranje?stranica=";
    public static String noMorePagesCSSSelector = ".divInline.divMain>div>div";
    public static String endOfSearchNotification = "Trenutno nema tvrtki koje zadovoljavaju tražene kriterije. Molimo pregledajte naše ostale djelatnosti/mjesta/proizvode.";
    public static String companyDetails = ".divCompanyInfoCenter , .divCompanyDescriptionNoLogo";
    public static String urlsFile = "company_urls.txt";

    public static void main(String[] args) throws InterruptedException, IOException {
        //System.setProperty("webdriver.gecko.driver","geckodriver");
        driver = new FirefoxDriver();
        openBrowser(URL);

        for (int i = 2; i < 5; i++) {
            if(getNumberOfCompaniesOnPage() == 0){
                break;
            }
            crawlCurrentPage();
            driver.get(baseUrlForIteratingPages + 70);
        }

        //read all existing urls from file
        readFromFile(urlsFile);

        List<String> urlsInFile = readFromFile(urlsFile);
        addOnlyNewURLSToFile(urlsInFile, companyURLS);


        crawlAllCompanyDetails();
        //open all Company details




        //driver.quit();

        /*
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
        */
        //click(querySelect); //invalid selector
        /*
        click(btn);
        while(click(loadMore));
        driver.wait(5000);
        driver.quit();
        */

    }

    public static boolean openBrowser(String URL){
        driver.get(URL);
        return true;
    }

    public static boolean click(String path){
        WebElement element = driver.findElement(xpath(path));
        while(!element.isDisplayed() || !element.isEnabled());
        driver.findElement(xpath(path)).click();
        return true;
    }

    public static boolean setText(String path, String text){
        WebElement element = driver.findElement(xpath(path));
        while(!element.isDisplayed() || !element.isEnabled());
        driver.findElement(xpath(path)).sendKeys(text);
        return true;
    }

    public static int getNumberOfCompaniesOnPage(){
        List<WebElement> elements = driver.findElementsByCssSelector(querySelect);
        return elements.size();
    }

    public static void crawlCurrentPage() throws InterruptedException {
        List<WebElement> elements = driver.findElementsByCssSelector(querySelect);
        for (WebElement element : elements) {
            companyURLS.add(element.getAttribute("href"));
        }
        Thread.sleep(1000);
    }

    public static void writeToCSV() throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("results.csv"), ';');
        List<String[]> data = new ArrayList<String[]>();
        for(String element : companyURLS){
            data.add(element.split("žnjo"));
        }

        writer.writeAll(data);
        writer.close();
    }

    public static void writeURLSToFile(List<String> companyURLS) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(urlsFile));
        String output = "";
        for(String element : companyURLS){
            output+=element+"\n";
        }
        pw.write(output);
        pw.close();
    }

    public static void addOnlyNewURLSToFile(List<String> oldList, List<String> newList)
        throws FileNotFoundException {
        for(String newURL : newList){
            int elementExists = 0;
            for(String oldURL : oldList){
                if(newURL.compareTo(oldURL) == 0) {
                    elementExists = 1;
                }
            }
            if (elementExists == 0){
                oldList.add(newURL);
            }
        }
        writeURLSToFile(oldList);
    }

    public static List<String> readFromFile(String file) throws IOException {
        List<String> URLSFromFile = new ArrayList<String>();
        while (true) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                URLSFromFile.add(line);
            }
            return URLSFromFile;
        }
    }

    public static void crawlAllCompanyDetails() throws IOException {
        for (String companyURL : companyURLS) {
            driver.get(companyURL);
            WebElement about = driver.findElementByCssSelector(companyDetails);
            System.out.println(about.getText());
        }
        writeToCSV();
    }
}
