package nju.edu.cn;

//import io.appium.java_client.AndroidKeyCode;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.server.browserlaunchers.Sleeper;

import com.sun.prism.Texture;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.List;
public class SecondTest {
	
    private AndroidDriver driver; 
    @Before
    public void setUp() throws Exception {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "apps");
        File app = new File(appDir, "UpocStudent.apk");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("newCommandTimeout", 10);
        capabilities.setCapability("app", app.getAbsolutePath()); 
        capabilities.setCapability("appPackage", "com.xdf.ucan");
        capabilities.setCapability("appActivity", ".ui.login.StartActivity");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);       
    }
 
    @Test
    public void addContact(){
    	//UpocStudent
    	WebElement el1 = driver.findElementById("com.xdf.ucan:id/editText1");
    	el1.sendKeys("15861819028");
    	WebElement el2 = driver.findElementById("com.xdf.ucan:id/editText2");
    	el2.sendKeys("00zoosong");
    	//Jlb
    	WebElement el3 = driver.findElementById("com.yao.club:id/et_username");
    	el3.sendKeys("张三");
    	WebElement el4 = driver.findElementById("com.yao.club:id/et_pwd");
    	el4.sendKeys("123");
    	//Jchat
    	WebElement el5 = driver.findElementById("io.jchat.android:id/login_userName");
    	el5.sendKeys("1234567");
    	WebElement el6 = driver.findElementById("io.jchat.android:id/login_passWord");
    	el6.sendKeys("1234567");
    	//GuDong
    	WebElement el7 = driver.findElementById("android:id/input");
    	el7.sendKeys("test");
        //Bilibili
        WebElement el8 = driver.findElementById("com.hotbitmapgg.ohmybilibili:id/et_username");
        el8.sendKeys("123");
        WebElement el9 = driver.findElementById("com.hotbitmapgg.ohmybilibili:id/et_password");
        el9.sendKeys("123");
        //Jianshi
        WebElement el10 = driver.findElementById("com.wingjay.android.jianshi:id/email");
        el10.sendKeys("test@demo.com");
        WebElement el11 = driver.findElementById("com.wingjay.android.jianshi:id/password");
        el11.sendKeys("test123");
        WebElement el12 = driver.findElementById("com.wingjay.android.jianshi:id/edit_title");
        el12.sendKeys("demo");
        WebElement el13 = driver.findElementById("com.wingjay.android.jianshi:id/edit_content");
        el13.sendKeys("Hello World!");
  }    
    
    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}


