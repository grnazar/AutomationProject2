import com.github.javafaker.Faker;
//import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class WebOrder {

    @Test
    public void webOrders() throws  InterruptedException, IOException {

        WebDriver driver = new EdgeDriver();

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


        Path reader = Path.of("src/test/java/Selenium_2/data..csv");
        List<String[]> dataRows = Files.readAllLines(reader)
                .stream()
                .skip(1)
                .map(line -> line.split(",")).toList();

        Random random = new Random();
        String[] randomDataRow = dataRows.get(random.nextInt(dataRows.size()));

        WebElement name1 = driver.findElement(By.id("ctl00_MainContent_fmwOrder_txtName"));
        name1.sendKeys(randomDataRow[0]);
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox2")).sendKeys(randomDataRow[1]);
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox3")).sendKeys(randomDataRow[2]);
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox4")).sendKeys(randomDataRow[3]);
        driver.findElement(By.id("ctl00_MainContent_fmwOrder_TextBox5")).sendKeys(randomDataRow[4]);


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
        String city = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[7]")).getText();
        String state = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[8]")).getText();
        String zip = firstRow.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[9]")).getText();

        Assert.assertEquals(actualName, randomDataRow[0]);
        Assert.assertEquals(street, randomDataRow[1]);
        Assert.assertEquals(city, randomDataRow[2]);
        Assert.assertEquals(state, randomDataRow[3]);
        Assert.assertEquals(zip, randomDataRow[4]);
        Assert.assertEquals(cardNumber, driver.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]/td[11]")).getText());

        System.out.println("Actualy :" + driver.findElement(By.xpath("//*[@id=\"ctl00_MainContent_orderGrid\"]/tbody/tr[2]")).getText());
        Thread.sleep(5000);
        System.out.println("Excepted :" + Arrays.deepToString(randomDataRow));

        driver.findElement(By.id("ctl00_logout")).click();

        driver.quit();

    }
}
