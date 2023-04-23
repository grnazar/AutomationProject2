import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.*;

public class WebOrderTest {

    @Test
    public void webOrders() throws  InterruptedException, IOException {

        ChromeOptions options1 = new ChromeOptions();
        options1.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options1);

        driver.get("http://secure.smartbearsoftware.com/samples/TestComplete12/WebOrders/Login.aspx");
        driver.manage().window().maximize();
        System.out.println("Testing starts");

        WebElement userName = driver.findElement(By.xpath("//input[@name = 'ctl00$MainContent$username']"));
        userName.sendKeys("Tester");

        WebElement passWord = driver.findElement(By.xpath("//input[@name = 'ctl00$MainContent$password']"));
        passWord.sendKeys("test");

        WebElement login = driver.findElement(By.xpath("//input[@name = 'ctl00$MainContent$login_button']"));
        login.click();

        WebElement order = driver.findElement(By.xpath("//a[@href = 'Process.aspx']"));
        order.click();

        WebElement product = driver.findElement(By.xpath("//select[@name='ctl00$MainContent$fmwOrder$ddlProduct']"));
        product.click();

        List<WebElement> choseOptions = driver.findElements(By.xpath("//select[@name='ctl00$MainContent$fmwOrder$ddlProduct']/option"));
        int choseIndex = (int) (Math.random() * choseOptions.size());
        choseOptions.get(choseIndex).click();
        int quantity = (int) (Math.random() * 100) + 1;
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_txtQuantity")).clear();
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_txtQuantity")).sendKeys(Integer.toString(quantity));

        driver.findElement(By.cssSelector("input[type='submit'][value='Calculate']")).click();
        int expectedTotal = (quantity < 10) ? quantity * 100 : (int) (quantity * 100 * 0.92);
        Thread.sleep(1000);
        WebElement actualTotalText = driver.findElement(By.name("ctl00$MainContent$fmwOrder$txtTotal"));
        System.out.println(actualTotalText.getText());

        Faker faker = new Faker();
        String firstAndLastName = faker.address().firstName() + " " + faker.address().lastName();
        int randomZip = 10000+(int)(Math.random()*90000);
        String zip = String.valueOf(randomZip);
        String address = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().state();
        Thread.sleep(500);
        driver.findElement(By.name("ctl00$MainContent$fmwOrder$txtName")).sendKeys(firstAndLastName);
        driver.findElement(By.name("ctl00$MainContent$fmwOrder$TextBox2")).sendKeys(address);
        driver.findElement(By.name("ctl00$MainContent$fmwOrder$TextBox3")).sendKeys(city);
        driver.findElement(By.name("ctl00$MainContent$fmwOrder$TextBox4")).sendKeys(state);
        driver.findElement(By.name("ctl00$MainContent$fmwOrder$TextBox5")).sendKeys(zip);

        List<WebElement> cardTypes = driver.findElements(By.name("ctl00$MainContent$fmwOrder$cardList"));
        int index = (int) (Math.random() * cardTypes.size());
        cardTypes.get(index).click();
        String cardNumber = "";
        int randomCardTypeIndex = 0;
        switch (randomCardTypeIndex) {
            case 0 -> // Visa
                    cardNumber = "4" + new Faker().number().digits(15);
            case 1 -> // MasterCard
                    cardNumber = "5" + new Faker().number().digits(15);
            case 2 -> // American Express
                    cardNumber = "3" + new Faker().number().digits(14);
        }

        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox6")).sendKeys(cardNumber);

        int randomDate = 1 + (int) (Math.random() * 11);
        String expDate = "" + randomDate;
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox1")).sendKeys(expDate.length() == 2 ? expDate + "/" + new Faker().number().numberBetween(23, 40) : 0 + expDate + "/" + new Faker().number().numberBetween(23, 40));

        driver.findElement(By.id("ctl00_MainContent_fmwOrder_InsertButton")).click();

        String successMessage = "New order has been successfully added.";
        Assert.assertTrue(driver.getPageSource().contains(successMessage));

        driver.findElement(By.linkText("View all orders")).click();

        WebElement ordersTable = driver.findElement(By.id("ctl00_MainContent_orderGrid"));
        WebElement firstRow = ordersTable.findElement(By.xpath("//table[@id='ctl00_MainContent_orderGrid']/tbody/tr[2]"));
        String actualName = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[2]")).getText();
        String street = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[6]")).getText();
        String city1 = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[7]")).getText();
        String state1 = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[8]")).getText();
        String zip1 = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[9]")).getText();

        Assert.assertEquals(actualName, firstAndLastName);
        Assert.assertEquals(street, address);
        Assert.assertEquals(city1, city);
        Assert.assertEquals(state1, state);
        Assert.assertEquals(zip1, zip);
        Assert.assertEquals(cardNumber, driver.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[11]")).getText());

        driver.findElement(By.id("ctl00_logout")).click();

        driver.quit();

    }}
