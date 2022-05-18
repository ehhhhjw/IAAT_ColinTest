package mergeTest;

import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionNotFoundException;
import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class ScreenShotThread extends Thread {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
	private AppiumDriver driver;
	private String udid;
	private int taskId;
	private String apkName;
	private boolean isEnd = false;
	public ScreenShotThread(String apkName, AppiumDriver driver, String udid, int taskId){
		this.driver = driver;
		this.udid = udid;
		this.taskId=taskId;
		this.apkName = apkName;
	}
	public void setStopFlag() {
		isEnd = true;
	}
	@Override
	public void run() {
		try {
			takeScreenShot(driver, udid);
		} catch (SessionNotFoundException e) {
			PrintUtil.printErr("ScreenShotRunner takeScreenShot session not found " + udid, TAG);
		} catch (WebDriverException e) {
			PrintUtil.printErr("ScreenShotRunner webdriver exception " + udid + " " + e.getMessage(), TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private void takeScreenShot(AppiumDriver driver,String udid) throws SessionNotFoundException {
    	while(!isEnd){
	      	File screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	      	try {
	      	    	FileUtils.copyFile(screenShotFile, new File(AddressUtil.getScreenShotFilePath(apkName,taskId,udid, getDeviceTime())));
	      	    } catch (IOException e) {
	      	    	e.printStackTrace();
	      	}
	      	FileReader fr;
	      	try{
				fr = new FileReader(screenShotFile);
				if(fr.read() == -1){
					screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		   			try {
							FileUtils.copyFile(screenShotFile, new File(AddressUtil.getScreenShotFilePath(apkName,taskId,udid, getDeviceTime())));
						} catch (IOException e) {
							e.printStackTrace();
						}
		   			if(fr.read() == -1){
		   				screenShotFile.delete();
		   			}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	       try {
			Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    private String getDeviceTime() {
    	String result = "";
    	String command = "adb -s " + udid + " shell echo ${EPOCHREALTIME:0:14}";
    	OSUtil.runCommand("adb devices");
    	result = OSUtil.runCommand(command);
    	result = result.replace(".", "");
    	result = result.trim();
    	return result;
    }
}