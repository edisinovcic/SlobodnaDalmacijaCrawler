import static java.lang.Thread.sleep;
import static org.openqa.selenium.By.xpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class StarterTest {

  public static FirefoxDriver driver;

  public static final String URLSlobodna = "http://www.slobodnadalmacija.hr/";
  public static final String linksCSSSelectorSlobodna = ".story__link";

  public static final String URLIndex = "http://www.index.hr/";
  public static final String clanakIndexXpath = "//a[contains(@href, \"clanak\")]";

  public static final String urlsFileSlobodna = "urlsSlobodna.txt";
  public static final String urlsFileIndex = "urlsIndex.txt";

  public static List<String> slobodnaLinksFromPage = new ArrayList<String>();
  public static List<String> slobodnaLinksInFile = new ArrayList<String>();
  public static List<String> indexLinksFromPage = new ArrayList<String>();
  public static List<String> indexLinksInFile = new ArrayList<String>();
  public static List<WebElement> iframes = new ArrayList<WebElement>();


  public static void main(String[] args) throws InterruptedException, IOException {
    driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

    logInToFacebook();
    sleep(2000);

    openBrowser(URLSlobodna);
    slobodnaLinksFromPage = getAllURLsFromPageWithCSSSelector(linksCSSSelectorSlobodna);
    slobodnaLinksInFile = readFromFile(urlsFileSlobodna);
    addOnlyNewURLSToFile(urlsFileSlobodna, slobodnaLinksInFile, slobodnaLinksFromPage);
    sleep(5000);

    //iterate through links
    iterateLinks(readFromFile(urlsFileSlobodna));


    /*
    openBrowser(URLIndex);
    indexLinksFromPage = getAllURLsFromPageWithXPATHSelector(clanakIndexXpath);
    indexLinksInFile = readFromFile(urlsFileSlobodna);
    addOnlyNewURLSToFile(urlsFileIndex, indexLinksInFile, indexLinksFromPage);
*/


        /*
        //driver.quit();


        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);

        //click(querySelect); //invalid selector

        click(btn);
        while(click(loadMore));
        driver.wait(5000);
        driver.quit();
        */

  }

  public static void logInToFacebook() {
    openBrowser("https://www.facebook.com/");
    setText("//*[contains(@id, 'email')]", "mayanna.apt@gmail.com");
    setText("//*[contains(@id, 'pass')]", "mandrilo");
    click("//*[contains(@id, 'loginbutton')]");
  }


  public static List<String> getAllURLsFromPageWithCSSSelector(String elementSelector) {
    List<WebElement> elements = driver.findElementsByCssSelector(elementSelector);
    List<String> links = new ArrayList<String>();
    for (WebElement element : elements) {
      links.add(element.getAttribute("href"));
    }
    return links;
  }

  public static List<String> getAllURLsFromPageWithXPATHSelector(String elementSelector) {
    List<WebElement> elements = driver.findElementsByXPath(elementSelector);
    List<String> links = new ArrayList<String>();
    for (WebElement element : elements) {
      links.add(element.getAttribute("href"));
    }
    return links;
  }


  public static List<String> readFromFile(String file) throws IOException {
    createFileIfFileDoesNotExists(file);
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

  public static void createFileIfFileDoesNotExists(String url) throws IOException {
    File file = new File(url);
    if (!file.exists()) {
      file.createNewFile();
    }
  }

  public static void addOnlyNewURLSToFile(String urlsFile, List<String> oldList,
      List<String> newList)
      throws FileNotFoundException {
    for (String newURL : newList) {
      int elementExists = 0;
      for (String oldURL : oldList) {
        if (newURL.compareTo(oldURL) == 0) {
          elementExists = 1;
        }
      }
      if (elementExists == 0) {
        oldList.add(newURL);
      }
    }
    writeURLSToFile(urlsFile, oldList);
  }

  public static void writeURLSToFile(String urlsFile, List<String> pageURLS)
      throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(urlsFile));
    String output = "";
    for (String element : pageURLS) {
      output += element + "\n";
    }
    System.out.println(output);
    pw.write(output);
    pw.close();
  }

  public static void iterateLinks(List<String> urlLinks) throws InterruptedException {
    for (int i = 0; i < 1; i++) {
      openBrowser(urlLinks.get(i));
      //click("//*[contains(text(),'Komentiraj')]/../../..");
      WebElement facebookFrame = returnFacebookFrame();
      updateFrameList();
      printFrameListByClassName();
      System.out.println(facebookFrame.getAttribute("class"));
      driver.switchTo().frame(facebookFrame);
      click("//div[contains(@class, '_1mf _1mj')]");
      click("//*[contains(@class, '_1cb _5yk1')]");
      setText("//*[contains(@class, '_1cb _5yk1')]",
          "Zelite jeftino i udobno ljetovati u Splitu? \n Mayanna Apartment: https://hr.airbnb.com/rooms/19257413");

      click("//button[contains(@type, 'submit')]");
      driver.switchTo().frame(0);

    }
  }

  //-----------------------------------------------------------------------------------------------------------------------------------------

  public static String getCurrentFrame(){
    JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
    String currentFrame = (String) jsExecutor.executeScript("return self.name");
    return currentFrame;
  }


  public static WebElement returnFacebookFrame() {
    final List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

    int i = 0;
    for (WebElement iframe : iframes) {
      System.out.println(iframe.getAttribute("class"));
      if(iframe.getAttribute("class").equals("fb_ltr")){
        return iframe;
      }
      /*
      if (iframe.getAttribute("id").equals("") || iframe.getAttribute("id")
          .equals("fb_xdm_frame_http") || iframe.getAttribute("id").equals("fb_xdm_frame_https")
          || iframe.getAttribute("id").equals("instagram-embed-0") || iframe.getAttribute("id")
          .equals("instagram-embed-1") || iframe.getAttribute("id").equals("aswift_0") || iframe
          .getAttribute("id").equals("aswift_1")) {
        continue;
      }
      return iframe;
      */
    }
    return null;
  }

  public static void updateFrameList(){
    iframes = driver.findElements(By.tagName("iframe"));
  }

  public static void printFrameListByClassName(){
    System.out.println("Iframes id list:");
    for (WebElement iframe : iframes){
      System.out.println(iframe.getAttribute("class"));
    }
  }


  public static boolean openBrowser(String URL) {
    driver.get(URL);
    return true;
  }

  public static boolean click(String path) {
    WebElement element = driver.findElement(xpath(path));
    while (!element.isDisplayed() || !element.isEnabled()) {
      ;
    }
    driver.findElement(xpath(path)).click();
    return true;
  }

  public static boolean setText(String path, String text) {
    WebElement element = driver.findElement(xpath(path));
    while (!element.isDisplayed() || !element.isEnabled()) {
    }
    driver.findElement(xpath(path)).sendKeys(text);
    return true;
  }

  public static int getNumberOfLinksOnPageCSSSelector(String CSSSelector) {
    List<WebElement> elements = driver.findElementsByCssSelector(CSSSelector);
    return elements.size();
  }

    /*
    public static void crawlCurrentPage(String CSSSelector) throws InterruptedException {
        List<WebElement> elements = driver.findElementsByCssSelector(CSSSelector);
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

    public static void crawlAllCompanyDetails() throws IOException {
        for (String companyURL : companyURLS) {
            driver.get(companyURL);
            WebElement about = driver.findElementByCssSelector(companyDetails);
            System.out.println(about.getText());
        }
        writeToCSV();
    }

    */


}
