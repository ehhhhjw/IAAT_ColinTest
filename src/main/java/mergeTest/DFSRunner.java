package mergeTest;

import Tree.ActivityTreeNode;
import Tree.ActivityTreeNodeList;
import mergeTest.AppiumNode.WidgetInfo;
import mergeTest.AppiumNode.WidgetInfoNode;
import mergeTest.AppiumNode.widgetMergeTools;
import com.google.gson.Gson;
import io.appium.java_client.AndroidKeyCode;
import io.appium.java_client.AppiumDriver;
import model.Action;
import model.Activity;
import model.Component;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import util.*;

import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DFSRunner {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
    private AppiumDriver driver;
    //存放的是Activity对象，用于记录已遍历过的Activity的相关信息 ，辅助深搜
    List<Activity> activityList = new ArrayList<>();
    //本次测试过程中自己点击的顺序
    ArrayList<WidgetInfoNode> wholeMap = new ArrayList<>();
    ArrayList<WidgetInfo> oneMap = new ArrayList<>();
    ArrayList<ArrayList<WidgetInfo>> pathList = new ArrayList<>();
    private String fatherActivity;
    private File mylog;
    private File testScript;
    private File testAction;
    private boolean closeWebPageFlag = false;
    private boolean stopTestFlag = false;
    private boolean hasTakenScreenshot = false;
    private boolean firstLaunchFlag = true;
    private int outAppSemapher = 0;
    private ActivityTreeNode root;
    private int taskId;
    private String udid, appPackage;
    private String apkName;
    BufferedWriter scriptWriter;
    BufferedWriter mylogWriter;
    BufferedWriter actionWriter;
    List<String> fatherComponentList = new ArrayList<String>();
    List<String> ignoreIfIdEquals = new ArrayList<String>();
    List<String> ignoreIfIdContains = new ArrayList<String>();
    List<String> ignoreIfIdStartsWith = new ArrayList<String>();
    List<String> ignoreIfTextEquals = new ArrayList<String>();
    List<String> ignoreIfTextContains = new ArrayList<String>();
    List<String> ignoreIfTextStartsWith = new ArrayList<String>();
    List<String> ignoreIfContent_descEquals = new ArrayList<String>();
    List<String> ignoreIfContent_descContains = new ArrayList<String>();
    List<String> ignoreIfContent_descStartsWith = new ArrayList<String>();
    public DFSRunner(){
        //初始化
        init(udid, String.valueOf(taskId));
        //继承初始化
        //continueInit(udid, String.valueOf(taskId));
    }
    public DFSRunner(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid, int taskId, String apkName){
        //初始化
        this.driver = driver;
        this.appPackage = packageName;
        this.udid = udid;
        this.root = root;
        this.taskId = taskId;
        this.apkName = apkName;
        init(udid, String.valueOf(taskId));
        //继承初始化
        //continueInit(udid, String.valueOf(taskId));
    }
    //测试主逻辑
    public void test(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid, int taskId) {
        this.driver = driver;
        this.appPackage = packageName;
        this.udid = udid;
        this.root = root;
        this.taskId = taskId;
        //初始化
        //init(udid, String.valueOf(taskId));
        //继承初始化
        //continueInit(udid, Strzing.valueOf(taskId));
        //深搜
        DFS(0, root);
        //closeWriter();
    }
    public void test(){
        DFS(0, root);
        //执行完DFS后，将本次的路径加入总path中,并更新新的path
        pathList.add(oneMap);
        wholeMap = widgetMergeTools.getResList(pathList);
    }
    public void continueInit(String udid, String taskId){
        mylog = new File(AddressUtil.getMyLogPath(apkName,udid, taskId));
        testScript = new File(AddressUtil.getTestScript(apkName,udid, taskId));
        testAction = new File(AddressUtil.getTestAction(apkName,udid, taskId));
        initWriter();
        loadConfiguration();
    }
    public void init(String udid, String taskId) {
        mylog = new File(AddressUtil.getMyLogPath(apkName,udid, taskId));
        testScript = new File(AddressUtil.getTestScript(apkName,udid, taskId));
        testAction = new File(AddressUtil.getTestAction(apkName,udid, taskId));
        deleteFolder(AddressUtil.NODE_SCREENSHOT_DIR + File.separator);
        createResultFile(new ArrayList<>(Arrays.asList(mylog, testScript, testAction)));
        createResultDir(new ArrayList<>(Arrays.asList(new File(AddressUtil.getPageXmlDir(apkName,udid, this.taskId)), new File(AddressUtil.getPageImgDir(apkName,udid, this.taskId)))));
        initWriter();
        loadConfiguration();
    }
    private void loadConfiguration(){
        readIgnoreComponent();
        readFatherComponent();
    }
    public void closeWriter(){
        try {
			scriptWriter.close();
			mylogWriter.close();
			actionWriter.close();
		} catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
		}
    }
    private void initWriter(){
        try {
            scriptWriter = new BufferedWriter(new FileWriter(testScript,true));
            mylogWriter = new BufferedWriter(new FileWriter(mylog,true));
            actionWriter = new BufferedWriter(new FileWriter(testAction, true));
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    private void createResultFile(List<File> fileList){
    	for(File file : fileList) {
    	    FileSystem.newFile(file);
        }
    }
    private void createResultDir(List<File> fileList){
    	for(File file : fileList) {
    	    FileSystem.newDir(file);
        }
    }
    private void deleteFolder(String dir){
        File deletefolder = new File(dir);
        File[] oldFile = deletefolder.listFiles();
        try{
            for(int i = 0;i < oldFile.length;i++){
                if(oldFile[i].isDirectory()){
                    deleteFolder(dir + oldFile[i].getName() + File.separator);
                }
                oldFile[i].delete();
            }
        }catch(Exception e){
            PrintUtil.printErr("Device " + udid + ", An error occurs when delete folder", TAG, udid);
        }
    }
    private String getDeviceTime(){
    	String result = "";
    	String command = "adb -s " + udid + " shell echo ${EPOCHREALTIME:0:14}";
    	OSUtil.runCommand("adb devices");
    	result = OSUtil.runCommand(command);
    	result = result.replace(".", "");
    	result = result.trim();
    	return timeStamp2Date(result);
    }
    private String timeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
            return null;
        }
    }
    private void recordAction(Action action){
    	try {
			actionWriter.write(new Gson().toJson(action) + "\n");
			actionWriter.flush();
    	} catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
			e.printStackTrace();
		}
    }
    private boolean checkStopFlag(){
        if(stopTestFlag){
            return true;
        }
        return false;
    }
    private void clickAndroidAlert(){
    	boolean locateSuccessfulFlag = false;
    	int i = 0;
        //没有可以按的允许按钮才按取消
    	String[] labels = new String[]{"android:id/button1", "确定", "确认", "允许", "始终允许", "同意", "同意并继续", "始终", "下一步", "取消"};
    	while(!locateSuccessfulFlag){
    		if(i >= labels.length) {
    		    break;
            }
    		locateSuccessfulFlag = true;
    		if(labels[i].contains("android")){
    	        try{
    	            PrintUtil.print("Click button1 for AndroidAlert", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
    	            String activityBeforeAction = driver.currentActivity();
                    //记录到达的activity
                    recordActivity(activityBeforeAction);
    	            String timeBeforeAction = getDeviceTime();
    	            driver.findElementById(labels[i]).click();
    	            String timeAfterAction = getDeviceTime();
    	            String activityAfterAction = driver.currentActivity();
                    //记录到达的activity
                    recordActivity(activityAfterAction);
    	            try {
    	            	String type = "CLICK";
    	            	String msg = "Click Confirm button for AndroidAlert";
    	            	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
    	                scriptWriter.write("driver.findElementById(\"" + labels[i] + "\").click();\n");
    	                scriptWriter.flush();
    	            } catch (IOException e1) {
    	                PrintUtil.printException(TAG, udid, e1);
    	            }
    	        } catch (Exception e) {
    	        	locateSuccessfulFlag = false;
    	            PrintUtil.print("Can't locate AndroidAlert compotent", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
    	        }
    		}else{
	       		 try{
	                 PrintUtil.print("Click allow button for AndroidAlert", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
	                 String activityBeforeAction = driver.currentActivity();
                     //记录到达的activity
                     recordActivity(activityBeforeAction);
	    	         String timeBeforeAction = getDeviceTime();
	                 driver.findElementByXPath("//android.widget.Button[contains(@text,'" + labels[i] + "')]").click();
	    	         String timeAfterAction = getDeviceTime();
	                 String activityAfterAction = driver.currentActivity();
                     //记录到达的activity
                     recordActivity(activityAfterAction);
	                 try {
	                	 String type = "CLICK";
	                	 String msg = "Click Confirm button for AndroidAlert";
	                	 recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
	                     scriptWriter.write("driver.findElementByXPath(\"//android.widget.Button[contains(@text,'" + labels[i] + "')]\").click();\n");
	                     scriptWriter.flush();
	                 } catch (IOException e1) {
	                     PrintUtil.printException(TAG, udid, e1);
	                 }
	             } catch (Exception e) {
	             	locateSuccessfulFlag = false;
	                 PrintUtil.print("Can't locate AndroidAlert compotent", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
	             }
    		}
    		 i++;
    	}
    }
    private void clickAndroidMsg(){
        try{
            PrintUtil.print("Click cancel button for AndroidMsg", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
            String activityBeforeAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityBeforeAction);
            String timeBeforeAction = getDeviceTime();
            driver.findElementById("android:id/button2").click();
            String timeAfterAction = getDeviceTime();
            String activityAfterAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityAfterAction);
            try {
            	String type = "CLICK";
            	String msg = "Click Cancel button for AndroidMsg";
            	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
                scriptWriter.write("driver.findElementById(\"android:id/button2\").click();\n");
                scriptWriter.flush();
            } catch (IOException e1) {
                PrintUtil.printException(TAG, udid, e1);
            }
        } catch (Exception e) {
            PrintUtil.print("Can't locate AndroidMsg component", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
        }
        threadSleep(2);
    }
    public void handleAndroidAlertOnLaunch(){
        //判断页面是否弹出了android警告弹窗
    	boolean isAndroidAlert = false;
        String testPage = driver.getPageSource();
        savePageSource4Parse(testPage, -1);
        DoXml testXml = new DoXml();
        String testPath = AddressUtil.getPageSource(apkName,-1, udid,this.taskId);
        List<Component> coms = testXml.run(testPath);
        for(int k = 0;k < coms.size();k++){
            if(coms.get(k).getResource_id().contains("alertTitle")||coms.get(k).getResource_id().contains("event_title")||coms.get(k).getPackagename().contains("packageinstaller")||coms.get(k).getPackagename().contains("com.huawei.systemmanager:id/btn_allow")) {
                //有android弹窗
                isAndroidAlert = true;
            }
        }
        int i = 0;
        while(isAndroidAlert){
            //若页面上弹出了android警告弹窗则循环
        	if(i >= 5) {
        		PrintUtil.print("Can't handle AndroidAlert On Launch", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
        		break;
        	}
            isAndroidAlert = false;
            clickAndroidAlert();
            threadSleep(2);
            testPage = driver.getPageSource();
            savePageSource4Parse(testPage,-1);
            testXml = new DoXml();
            testPath = AddressUtil.getPageSource(apkName,-1, udid,this.taskId);
            coms=testXml.run(testPath);
            for(int k = 0;k < coms.size();k++){
                if(coms.get(k).getResource_id().contains("alertTitle")||coms.get(k).getResource_id().contains("event_title")||coms.get(k).getPackagename().contains("packageinstaller")||coms.get(k).getPackagename().contains("com.huawei.systemmanager:id/btn_allow")) {
                    //有android弹窗
                    isAndroidAlert = true;
                }
            }
            i++;
        }
    }
    private void threadSleep(int second){
        try {
            PrintUtil.print("Sleep " + String.valueOf(second) + " seconds", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            Thread.sleep(second * 1000);
        } catch (InterruptedException e1) {
            PrintUtil.printException(TAG, udid, e1);
        }
        try {
            scriptWriter.write(" try {\nThread.sleep(" + String.valueOf(second*1000) + ");\n} catch (InterruptedException e) {\ne.printStackTrace();\n}\n");
            scriptWriter.flush();
        } catch (IOException e1) {
            PrintUtil.printException(TAG, udid, e1);
        }
    }
    private void handleAndroidAlertandMsg(){
        boolean isAndroidAlert = false;
        boolean isAndroidmsg = false;
        String testPage = driver.getPageSource();
        savePageSource4Parse(testPage, -1);
        DoXml testXml = new DoXml();
        String testPath = AddressUtil.getPageSource(apkName,-1, udid,this.taskId);
        List<Component> coms = testXml.run(testPath);
        for(int k = 0;k < coms.size();k++){
            if(coms.get(k).getResource_id().equals("android:id/message")) {
                //有androidMsg弹窗
                isAndroidmsg = true;
            }
            if(coms.get(k).getResource_id().contains("alertTitle")||coms.get(k).getResource_id().contains("event_title")||coms.get(k).getPackagename().contains("packageinstaller")) {
                //有android警告窗
                isAndroidAlert = true;
            }
        }
        if(isAndroidmsg) {
        	clickAndroidMsg();
        }
        if(isAndroidAlert) {
        	clickAndroidAlert();
        }
    }
    private boolean handleAndroidMsgAndCheckPkg(){
      boolean exitJudge = true;
      boolean isAndroidmsg = false;
      String testPage = driver.getPageSource();
      savePageSource4Parse(testPage, -1);
      DoXml testXml = new DoXml();
      String testPath = AddressUtil.getPageSource(apkName,-1, udid,this.taskId);
      List<Component> coms = testXml.run(testPath);
      for(int k = 0;k < coms.size();k++){
          if(coms.get(k).getResource_id().equals("android:id/message")) {
              isAndroidmsg = true;
          }
          if(coms.get(k).getPackagename().equals(appPackage)) {
              //如果在AppPackage中，置exitJudge为false
              exitJudge = false;
          }
      }
      if(isAndroidmsg) {
    	  clickAndroidMsg();
      }
      return exitJudge;
    }
    public List<Component> getCurrentClickableComList(int i, boolean printFlag){
        String pageSource = driver.getPageSource();
        //保存pagesource到XML文件
        savePageSource4Parse(pageSource,i);
        String path = AddressUtil.getPageSource(apkName,i, udid,this.taskId);
        ParseXml xmlParser = new ParseXml();
        List<Component> comList = xmlParser.run(path);
        if(printFlag){
        	printComponentList(comList);
        }
        return comList;
    }
    private List<Component> getCurrentComList(int i){
        String pageSource = driver.getPageSource();
        savePageSource4Parse(pageSource,i);
        String path = AddressUtil.getPageSource(apkName,i, udid,this.taskId);
        DoXml doXmlParser = new DoXml();
        List<Component> comList = doXmlParser.run(path);
    	printComponentList(comList);
        return comList;
    }
    //此处开始，复现时所需的execute,外部使用，皆为public
    public void opeExecuteSwipe(int startx,int starty,int endx,int endy,int duration){
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();

        //记录Node
        WidgetInfo wi = new WidgetInfo();
        wi.simpleOutput();
        ArrayList<String> list = new ArrayList();
        list.add(String.valueOf(startx));
        list.add(String.valueOf(starty));
        list.add(String.valueOf(endx));
        list.add(String.valueOf(endy));
        list.add(String.valueOf(500));
        wi.setBehavior("swipe", list);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);

        driver.swipe(startx, starty, endx, endy, duration);
        String timeAfterAction = getDeviceTime();
        threadSleep(3);
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
            String type = "SWIPE";
            String msg = "startX: " + startx + ",startY: " + starty + ",endX: " + endx + ",endY: " + endy + ",duration: 500 ms";
            recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            scriptWriter.write("driver.swipe(" + startx + ", " + starty + ", " + endx + ", " + endy + ", 1000);\n");
            scriptWriter.flush();
        } catch (IOException e1) {
            PrintUtil.printException(TAG, udid, e1);
            e1.printStackTrace();
        }
    }
    private void executeSwipe(){
        int screenWidth = driver.manage().window().getSize().width;
        int screenHeight = driver.manage().window().getSize().height;
        int startx = screenWidth * 3 / 4;
        int starty = screenHeight / 2;
        int endx = screenWidth / 4;
        int endy = starty;
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();

        //记录Node
        WidgetInfo wi = new WidgetInfo();
        wi.simpleOutput();
        ArrayList<String> list = new ArrayList();
        list.add(String.valueOf(startx));
        list.add(String.valueOf(starty));
        list.add(String.valueOf(endx));
        list.add(String.valueOf(endy));
        list.add(String.valueOf(500));
        wi.setBehavior("swipe", list);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);

        driver.swipe(startx, starty, endx, endy, 500);
        String timeAfterAction = getDeviceTime();
        threadSleep(3);
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
        	String type = "SWIPE";
        	String msg = "startX: " + startx + ",startY: " + starty + ",endX: " + endx + ",endY: " + endy + ",duration: 500 ms";
        	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            scriptWriter.write("driver.swipe(" + startx + ", " + starty + ", " + endx + ", " + endy + ", 1000);\n");
            scriptWriter.flush();
        } catch (IOException e1) {
            PrintUtil.printException(TAG, udid, e1);
            e1.printStackTrace();
        }
    }
    public void openExecuteKeyEvent(int androidKeyCode){
        executeKeyEvent("openExecuteKeyEvent",androidKeyCode);
    }
    private void executeKeyEvent(String msg, int androidKeyCode){
        String activityBeforeAction;
        String activityAfterAction;
        String timeBeforeAction;
        String timeAfterAction;
        try{
            PrintUtil.print(msg, TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
            activityBeforeAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityBeforeAction);
            timeBeforeAction = getDeviceTime();

            //记录Node
            WidgetInfo wi = new WidgetInfo();
            wi.simpleOutput();
            ArrayList<String> list = new ArrayList();
            list.add(String.valueOf(androidKeyCode));
            wi.setBehavior("sendKeyEvent", list);
            wi.setActivity(activityBeforeAction);
            oneMap.add(wi);


            //按返回Home键
            driver.sendKeyEvent(androidKeyCode);
            timeAfterAction = getDeviceTime();
            threadSleep(2);
            activityAfterAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityAfterAction);
        }catch(Exception e){
            PrintUtil.printException(TAG, udid, e);
            return;
        }
        try {
        	String type = "CLICK";
        	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            scriptWriter.write("driver.sendKeyEvent(" + androidKeyCode + ");\n");
            scriptWriter.flush();
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    public void openExectureInput(String inputValue,WebElement element){
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();

        //记录node
        WidgetInfo wi = WidgetInfo.getElementInfo(element);
        ArrayList<String> list = new ArrayList();
        list.add(inputValue);
        wi.setBehavior("sendKeys", list);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);

        //找到了该控件的输入值，进行输入
        element.sendKeys(inputValue);
        String timeAfterAction = getDeviceTime();
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
            String type = "INPUT";
            //TODO 删除了此处的控件locator，可能需要补充
            String msg = "Input use vaule " + inputValue;
            recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            /*
            if(locator.startsWith("//")) {
                scriptWriter.write("driver.findElementByXPath(\"" + locator + "\").sendKeys(\"" + inputValue + "\");\n");
            } else {
                scriptWriter.write("driver.findElementById(\"" + locator + "\").sendKeys(\"" + inputValue + "\");\n");
            }
            */
            scriptWriter.flush();
        } catch (IOException e1) {
            PrintUtil.printException(TAG, udid, e1);
            e1.printStackTrace();
        }
    }
    private void executeInput(Component component, WebElement element){
        String inputValue = null;
        PrintUtil.print("This is an editText , trying to find input value", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
        InputFinder finder = new InputFinder();
        try {
            inputValue = finder.getInputValue(AddressUtil.getHumanScriptPath(),component.getResource_id());
        } catch (IOException e) {
            PrintUtil.print("Find input value error!", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            e.printStackTrace();
        }
        if(inputValue != null){
            PrintUtil.print("Input " + component.getLocator() + " use vaule " + inputValue, TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
            String activityBeforeAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityBeforeAction);
            String timeBeforeAction = getDeviceTime();

            //记录node
            WidgetInfo wi = WidgetInfo.getElementInfo(element);
            ArrayList<String> list = new ArrayList();
            list.add(inputValue);
            wi.setBehavior("sendKeys", list);
            wi.setActivity(activityBeforeAction);
            oneMap.add(wi);



            //找到了该控件的输入值，进行输入
            element.sendKeys(inputValue);
            String timeAfterAction = getDeviceTime();
            String activityAfterAction = driver.currentActivity();
            //记录到达的activity
            recordActivity(activityAfterAction);
            try {
            	String type = "INPUT";
            	String msg = "Input " + component.getLocator() + " use vaule " + inputValue;
            	if(!component.getLocator().contains("bounds")) {
            	    msg = msg + ", bounds: \'" + component.getBounds() + "\'";
                }
            	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
                String locator = component.getLocator();
                if(locator.startsWith("//")) {
                    scriptWriter.write("driver.findElementByXPath(\"" + locator + "\").sendKeys(\"" + inputValue + "\");\n");
                } else {
                    scriptWriter.write("driver.findElementById(\"" + locator + "\").sendKeys(\"" + inputValue + "\");\n");
                }
                scriptWriter.flush();
            } catch (IOException e1) {
                PrintUtil.printException(TAG, udid, e1);
                e1.printStackTrace();
            }
        }else {PrintUtil.print("Can't find input value for this edittext", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);}
    }
    public void openExecuteClick(WebElement element){
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();

        //记录node
        WidgetInfo wi = WidgetInfo.getElementInfo(element);
        wi.simpleOutput();
        wi.setBehavior("click", null);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);

        element.click();
        String timeAfterAction = getDeviceTime();
        threadSleep(6);
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
            String type = "CLICK";
            String msg = "Click widget";
            recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            /*
            if(!locator.contains("bounds"))
                msg = msg + ", bounds: \'" + component.getBounds() + "\'";
            if(locator.startsWith("//"))
                scriptWriter.write("driver.findElementByXPath(\"" + locator + "\").click();\n");
            else
                scriptWriter.write("driver.findElementById(\"" + locator + "\").click();\n");
             */
            scriptWriter.flush();
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    private void executeClick(Component component, WebElement element){
    	String locator = component.getLocator();
        PrintUtil.print("Click component " + locator, TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();

        //记录node
        WidgetInfo wi = WidgetInfo.getElementInfo(element);
        wi.simpleOutput();
        wi.setBehavior("click", null);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);

        element.click();
        String timeAfterAction = getDeviceTime();
        threadSleep(6);
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
        	String type = "CLICK";
        	String msg = "Click widget " + locator;
        	if(!locator.contains("bounds"))
        		msg = msg + ", bounds: \'" + component.getBounds() + "\'";
        	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            if(locator.startsWith("//"))
                scriptWriter.write("driver.findElementByXPath(\"" + locator + "\").click();\n");
            else 
                scriptWriter.write("driver.findElementById(\"" + locator + "\").click();\n");
            scriptWriter.flush();
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    private void executeClickFatherComponent(Component component){
    	String locator = component.getLocator();
        PrintUtil.print("Click father component " + locator, TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
        WebElement element;
        try {
			if(locator.startsWith("//")) {
			    element = driver.findElementByXPath(locator);
            } else {
			    element = driver.findElementById(locator);
            }
		} catch (Exception e) {
            PrintUtil.print("Can't locate father component " + locator, TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            return;
		}
        String activityBeforeAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityBeforeAction);
        String timeBeforeAction = getDeviceTime();


        //记录node
        WidgetInfo wi = WidgetInfo.getElementInfo(element);
        wi.simpleOutput();
        wi.setBehavior("click", null);
        wi.setActivity(activityBeforeAction);
        oneMap.add(wi);



        element.click();
        String timeAfterAction = getDeviceTime();
        threadSleep(6);
        String activityAfterAction = driver.currentActivity();
        //记录到达的activity
        recordActivity(activityAfterAction);
        try {
        	String type = "CLICK";
        	String msg = "Click widget " + locator;
        	if(!locator.contains("bounds"))
        		msg = msg + ", bounds: \'" + component.getBounds() + "\'";
        	recordAction(new Action(timeBeforeAction, timeAfterAction, type, msg, activityBeforeAction, activityAfterAction));
            if(locator.startsWith("//"))
                scriptWriter.write("driver.findElementByXPath(\"" + locator + "\").click();\n");
            else 
                scriptWriter.write("driver.findElementById(\"" + locator + "\").click();\n");
            scriptWriter.flush();
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    public List<Component> handleNoClickableComActivityWhenFirstLaunch(List<Component> comList, int i){
    	List<Component> newComList = comList;
        if(firstLaunchFlag)	{
            //若是应用首次启动（为了处理没有跳过按钮的滑动引导页面）
            int swipecount = 0;
            while(newComList.size() == 0) {
                //若页面上没有clickable为true的控件，则滑动之后再获取,直到有clickable为true控件的页面
                swipecount++;
                if(swipecount >= 10) {
                    //如果滑了十次还没有出现clickable为true控件的页面，就跳出循环，因为可能引导页面之后出现的第一个页面就没有任何按钮（虽然此情况极少）
                    break;
                }
                PrintUtil.print("Ready to swipe because there is no clickable component", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                executeSwipe();
                newComList = getCurrentClickableComList(i, true);
            }
            firstLaunchFlag = false;
        }
        return newComList;
    }
    private boolean judgeOutOfAppPackage(List<Component> comList){
    	for(Component component : comList){
    		if(component.getPackagename().equals(appPackage)) {
    		    return false;
            }
    	}
    	return true;
    }
    private boolean judgeIsInApp(List<Component> comList){
        for(Component component : comList){
            if(component.getPackagename().equals(appPackage)) {
                return false;
            }
        }
        return true;
    }
    private List<Component> handleOutOfAppPackage(List<Component> comList, boolean outOfAppPackageFlag, int threshold,int i){
    	int numOfAttempts = 0;
    	List<Component> newComList = comList;
        while(outOfAppPackageFlag) {
            //当前页面不属于待测appPackage
        	if(numOfAttempts >= threshold) {
        	    break;
            } else {
        	    numOfAttempts++;
            }
        	executeKeyEvent("Click Return button because not in appPackage and ready to check again", AndroidKeyCode.BACK);
            //置为true，为了再获取pagesource，来判定在按完返回键之后，页面有没有回到待测appPackage
        	outOfAppPackageFlag = true;
            newComList = getCurrentClickableComList(i, false);
            outOfAppPackageFlag = judgeOutOfAppPackage(newComList);
        }
        return newComList;
    }
    private void returnPrevActivity(){
    	String returnStartActivity = driver.currentActivity();
        //记录到达的activity
        recordActivity(returnStartActivity);
        String currentActivity = returnStartActivity;
        int returnCount = 0;
        while(currentActivity.equals(returnStartActivity)) {
            //按返回键直到activity发生改变
        	executeKeyEvent("Click Return button because this page has done", AndroidKeyCode.BACK);
        	returnCount++;
             if(returnCount >= 3 ) {
                 //按3次返回页面都没变化，就连按两次退出应用
            	 executeKeyEvent("Click Home button because has tried more than 3 times", AndroidKeyCode.HOME);
            }
            currentActivity = driver.currentActivity();
            //记录到达的activity
            recordActivity(currentActivity);
            PrintUtil.print("Current activity is " + currentActivity, TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            PrintUtil.print("Return start activity is" + returnStartActivity, TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            if(returnCount >= 3) {
                break;
            }
        }
    }
    /**
     * @description 
     * 	作用：更新当前页面控件集
     *	被调用时刻：1、当前测试页面UI变化 2、从其他页面返回后发现当前页面变化 3、进入已测试过页面发现页面变化
     *	更新机制：先把comList4Update中的所有控件放插入到newComList的最前面（但这些控件若在comList中出现过，那么hasBeenTested属性设置为comList中对应的，即该控件可能已被测试过），放在newComList的最前面是因为comList4Update中控件代表了实时的页面控件集，优先进行这些控件的测试；
     *	接下来再把comList中其他的和comList4Update不相交的控件插入newComList
     *	--采用这种机制的原因是为了避免一个Activity上会出现多个页面覆盖的问题（为了保留住该页面上所有出现过的控件的hasBeenTested属性）--
     *	比如某Acticity上初始页面是一组控件 <a,b,c,d>，点击控件a后，页面上弹出了一个新的组件覆盖了此页面（Activity没变，<a,b,c,d>被完全覆盖住），这时候页面上可以获取到的控件为<e,f,g,h>，那么如果直接将此Activity控件集更新为<e,f,g,h>，那在测试完<e,f,g,h>，如果又回到之前的初始页面也就是<a,b,c,d>，理应继续测试控件b，但由于此时控件集里只有<e,f,g,h>的测试状态，所以会认为，控件a并没有被测试过，所以会继续测试控件a，导致程序陷入一个死循环
     * @param comList 变化前页面控件集
     * @param comList4Update 当前实时解析的页面控件集
     * @return 
     */
    private List<Component> setTestedComInList(List<Component> comList, List<Component> comList4Update){
    	List<Component> newComList = new ArrayList<Component>();
    	for(Component component : comList4Update){
    		if(comList.contains(component)){
    			int index = comList.indexOf(component);
    			component.setHasBeenTested(comList.get(index).isHasBeenTested());
    			if(comList.get(index).getFatherComponent() != null) {
    			    component.setFatherComponent(comList.get(index).getFatherComponent());
                }
    		}
    		newComList.add(component);
    	}
    	for(Component component : comList){
    		if(!comList4Update.contains(component)) {
    		    newComList.add(component);
            }
    	}
    	return newComList;
    }
    private void mergeImage(String activityAfterClick){
        //为此页面截图，命名为new_driver.currentActivity().jpg
        takeScreenShotAgain();
        String[] files = new String[2];
        String photoName = activityAfterClick;
        files[0] = AddressUtil.getNodeScreenShot(udid, photoName);
        files[1] = AddressUtil.getNodeScreenShot(udid, "new_" + photoName);
        String targetFile = AddressUtil.getNodeScreenShot(udid, photoName);
        int type = 1;
        boolean mergeImageSucessFlag = true;
        try {
            //拼接同一activity的不同页面截图
            System.out.println("拼接同一activity的不同页面截图");
			ImageHandleHelper.mergeImage(files, type, targetFile);
		} catch (Exception e) {
			mergeImageSucessFlag = false;
			PrintUtil.printException(TAG, udid, e);
		}
        if(mergeImageSucessFlag) {
            PrintUtil.print("Merge images successfully", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
        }
    }
    private void readFatherComponent(){
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader(AddressUtil.getFatherComponentConfigurationPath()));
		} catch (FileNotFoundException e) {
			PrintUtil.printException(TAG, udid, e);
		}
    	String strLine = "";
    	try {
			while((strLine = br.readLine()) != null){
				if(strLine.contains("#")) {
				    fatherComponentList.add(strLine);
                }
			}
		} catch (IOException e) {
			PrintUtil.printException(TAG, udid, e);
		}
    }
    private boolean judgeFatherComponent(Component component, String currentActivity){
    	for(String fatherComponent : fatherComponentList){
    		String locator = fatherComponent.split("#")[0];
    		String activity = fatherComponent.split("#")[1];
    		if(locator.equals(component.getLocator()) && currentActivity.contains(activity)) {
    		    return true;
            }
    	}
    	return false;
    }
    private List<Component> handleFatherComponent(List<Component> comList, Component fatherComponent, int i){
    	List<Component> comListAfterClickFatherCom = getCurrentClickableComList(i, true);
    	for(int index = 0;index < comListAfterClickFatherCom.size();index++){
    		if(!comList.contains(comListAfterClickFatherCom.get(index))){
    			comListAfterClickFatherCom.get(index).setFatherComponent(fatherComponent);
    		}
    	}
    	return setTestedComInList(comList, comListAfterClickFatherCom);
    }
    private boolean judgeChildComponent(Component component){
    	return component.getFatherComponent() != null;
    }
    private boolean judgeTestedComponent(Component component){
    	if(component.isHasBeenTested()){
            PrintUtil.print(component.getLocator() + " has been tested, skip to next one", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
    		return true;
    	}
    	return false;
    }
    private void readIgnoreComponent(){
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader(AddressUtil.getIgnoreConfigurationPath()));
			/**
			 * component in ignore.conf file will be ignored. 
			 * component identifier can only be id/content-desc/text, and use method equals/startsWith/contains
			 * must write in order equals, startsWith, contains
			 */
		} catch (FileNotFoundException e) {
			PrintUtil.printException(TAG, udid, e);
		}
        boolean readEquals = false;
        boolean readContains = false;
        boolean readStartsWith = false;
    	String strLine = "";
    	try {
			while((strLine = br.readLine()) != null){
				if(strLine.isEmpty() || strLine.startsWith("//")) {
				    continue;
                }
				if(strLine.equals("*equals*")){
					readEquals = true;
					continue;
				}
				if(strLine.equals("*startsWith*")){
					readEquals = false;
					readStartsWith = true;
					continue;
				}
				if(strLine.equals("*contains*")){
					readEquals = false;
					readStartsWith = false;
					readContains = true;
					continue;
				}
				String[] temp = strLine.split("#"); 
				if(readEquals){
					if(temp[0].equals("id")){
						ignoreIfIdEquals.add(temp[1]);
					}else if(temp[0].equals("text")){
						ignoreIfTextEquals.add(temp[1]);
					}else if(temp[0].equals("content_desc")){
						ignoreIfContent_descEquals.add(temp[1]);
					}
				}
				else if(readStartsWith){
					if(temp[0].equals("id")){
						ignoreIfIdStartsWith.add(temp[1]);
					}else if(temp[0].equals("text")){
						ignoreIfTextStartsWith.add(temp[1]);
					}else if(temp[0].equals("content_desc")){
						ignoreIfContent_descStartsWith.add(temp[1]);
					}
				}
				else if(readContains){
					if(temp[0].equals("id")){
						ignoreIfIdContains.add(temp[1]);
					}else if(temp[0].equals("text")){
						ignoreIfTextContains.add(temp[1]);
					}else if(temp[0].equals("content_desc")){
						ignoreIfContent_descContains.add(temp[1]);
					}
				}
			}
		} catch (IOException e) {
			PrintUtil.printException(TAG, udid, e);
		}
    }
    private boolean judgeIgnoreComponent(Component component){
    	boolean ignoreFlag = false;
		try {
			for(int i = 0;i < ignoreIfContent_descContains.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getContent_desc().contains(ignoreIfContent_descContains.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfContent_descEquals.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getContent_desc().equals(ignoreIfContent_descEquals.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfContent_descStartsWith.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getContent_desc().startsWith(ignoreIfContent_descStartsWith.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfIdContains.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getResource_id().contains(ignoreIfIdContains.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfIdEquals.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getResource_id().equals(ignoreIfIdEquals.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfIdStartsWith.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getResource_id().startsWith(ignoreIfIdStartsWith.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfTextContains.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getText().contains(ignoreIfTextContains.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfTextEquals.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getText().equals(ignoreIfTextEquals.get(i))){
					ignoreFlag = true;
					break;
				}
			}
			for(int i = 0;i < ignoreIfTextStartsWith.size();i++){
				if(ignoreFlag) {
				    break;
                }
				if(component.getText().startsWith(ignoreIfTextStartsWith.get(i))){
					ignoreFlag = true;
					break;
				}
			}
		} catch (Exception e) {
			PrintUtil.printException(TAG, udid, e);
		}
		if(ignoreFlag) {
		    PrintUtil.print(component.getLocator() + " is a component in ignore file, skip to next one", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
        }
    	return ignoreFlag;
    }
    private void takeScreenShot(){
        File screenShotFile = null;
        File newfile = null;
        try{
            screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        }catch(Exception e){
            PrintUtil.print("Screenshot error ", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        try {
            newfile = new File(AddressUtil.getNodeScreenShot(udid, driver.currentActivity()));
            //记录到达的activity
            recordActivity(driver.currentActivity());
            FileUtils.copyFile(screenShotFile, newfile);
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        FileReader fr;
        try{
            fr = new FileReader(newfile);
            int screenShotCount = 0;
            //截图失败就重复尝试20次
            while(fr.read() == -1){
            	screenShotCount++;
            	if(screenShotCount >= 20) {
            		PrintUtil.print("Screenshot can't be done , give up", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            		break;
            	}
                PrintUtil.print("Taking screenshot failed , try again", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
                try{
                    screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                }catch(Exception e){
                    PrintUtil.print("Screenshot error ", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
                    PrintUtil.printException(TAG, udid, e);
                    e.printStackTrace();
                }
                try {
                    newfile = new File(AddressUtil.getNodeScreenShot(udid, driver.currentActivity()));
                    //记录到达的activity
                    recordActivity(driver.currentActivity());
                    FileUtils.copyFile(screenShotFile, newfile);
                } catch (IOException e) {
                    PrintUtil.printException(TAG, udid, e);
                    e.printStackTrace();
                }
                fr = new FileReader(newfile);
            }
            savePageImg(newfile);
        }catch(Exception e){
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        PrintUtil.print("Screenshot has been saved successfully", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
    }
    private void takeScreenShotAgain(){
        File screenShotFile = null;
        File newfile = null;
        try{
            screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        }catch(Exception e){
            PrintUtil.print("New screenshot error ", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        try {
            newfile = new File(AddressUtil.getNodeScreenShot(udid, "new_" + driver.currentActivity()));
            //记录到达的activity
            recordActivity(driver.currentActivity());
            FileUtils.copyFile(screenShotFile, newfile);
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        FileReader fr;
        try{
            fr = new FileReader(newfile);
            int screenShotCount = 0;
            while(fr.read() == -1){
            	screenShotCount++;
            	if(screenShotCount >= 20) {
            		PrintUtil.print("Screenshot can't be done , give up", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
            		break;
            	}
                PrintUtil.print("Taking new screenshot failed , try again", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
                try{
                    screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                }catch(Exception e){
                    PrintUtil.print("New screenshot error ", TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
                    PrintUtil.printException(TAG, udid, e);
                    e.printStackTrace();
                }
                try {
                    String tmp = AddressUtil.getNodeScreenShot(udid, "new_" + driver.currentActivity());
                    //记录到达的activity
                    recordActivity(driver.currentActivity());
                    newfile = new File(tmp);
                    FileUtils.copyFile(screenShotFile, newfile);
                } catch (IOException e) {
                    PrintUtil.printException(TAG, udid, e);
                    e.printStackTrace();
                }
                fr = new FileReader(newfile);
            }
        }catch(Exception e){
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
        PrintUtil.print("New screenshot has been saved successfully", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
    }
    private void savePageXml(){
    	String activityName = driver.currentActivity();
        //记录到达的activity
        recordActivity(driver.currentActivity());
    	String xmlPath = AddressUtil.getPageXmlPath(apkName,udid, taskId, activityName);
    	String pageXml = driver.getPageSource();
    	FileOutputStream writerStream;
    	int tempIndex = xmlPath.lastIndexOf(File.separator);
        String fn = xmlPath.substring(0,tempIndex);
    	File f = new File(fn);
    	if(!f.exists()){
    	    f.mkdirs();
        }
		 try {
		     writerStream = new FileOutputStream(xmlPath);
		     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
		     writer.write(pageXml);
		     writer.close();
		 } catch (IOException e) {
		     PrintUtil.printException(TAG, udid, e);
		     e.printStackTrace();
		 }
    }
    private void savePageImg(File file){
    	String activityName = driver.currentActivity();
        //记录到达的activity
        recordActivity(driver.currentActivity());
    	File pageImg = new File(AddressUtil.getPageImgPath(apkName,udid, taskId, activityName));
		try {
			 FileUtils.copyFile(file, pageImg);
		 } catch (IOException e) {
		     PrintUtil.printException(TAG, udid, e);
		     e.printStackTrace();
		 }
    }
    private void savePageSource4Parse(String page,int i){
        String path = AddressUtil.getPageSource(apkName,i, udid,this.taskId);
        FileOutputStream writerStream;
        try {
            writerStream = new FileOutputStream(path);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(page);
            writer.close();
        } catch (IOException e) {
            PrintUtil.printException(TAG, udid, e);
            e.printStackTrace();
        }
    }
    private int indexOfCurrentActivityInActivityList(String currentActivity){
        for(int i = 0;i < activityList.size();i++){
            Activity activity = activityList.get(i);
            if(currentActivity.equals(activity.getActivityName())){
                return i;
            }
        }
        return -1;
    }
    private void printComponentList(List<Component> comList){
    	for(Component component: comList){
    		String father = "Null";
    		if(component.getFatherComponent() != null ){
    			father = component.getFatherComponent().getLocator();
    		}
    		PrintUtil.print(component.getLocator() + ", Tested: " + component.isHasBeenTested() + " , Father: " + father, TAG, udid, mylogWriter, PrintUtil.ANSI_YELLOW);
    	}
    }
    private boolean countSameComponent(List<Component> comList, List<Component> comListAfterClick){
        //保存原页面的控件列表的size和当前页面控件列表的size的较小者
        int minLength = Math.min(comList.size(), comListAfterClick.size());
        int sameComNum = 0;
        //统计原控件列表和现控件列表中,相同的元素个数,比较locator
        for(int m = 0;m < minLength;m++) {
            for(int n = 0;n < minLength;n++) {
                if((comListAfterClick.get(n).getLocator()).equals(comList.get(m).getLocator())) {
                    sameComNum++;
                }
            }
        }
        return (2 * sameComNum) >= minLength;
    }
    private boolean isNativeApp(){
        Set<String> contextNames = driver.getContextHandles();
        for (String contextName : contextNames) {
            if (contextName.contains("WEBVIEW")) {
                System.out.println("is webview, return");
                return false;
            }
        }
        return true;
    }
    public void DFS(int i,ActivityTreeNode fatherNode){
        if(i==0){
            //reset时会重新进行测试，此时父Activity还是上一次的
            fatherActivity=null;
        }
        //当前页面是否已跳出AppPackage
        boolean outOfAppPackageFlag = true;
        //如果跳转到的当前页面是一个新的子页面，则置该变量false
        boolean pageHaveBeenFlag = true;
        //若当前访问页面的activity等于activityList中某一ViewInfo对象的activity,则置该变量false（即跳转到的子页面曾经出现过）
        boolean pageNeverBeenFlag = true;
        //代表新进入的当前页面的Node
        ActivityTreeNode currentNode = new ActivityTreeNode();
        if(checkStopFlag()){
            return;
        }
        //处理刚安装启动时的多个系统警告窗，这里用得解析类是DoXml
        handleAndroidAlertOnLaunch();
        threadSleep(2);
        //获取当前页面clickable为true的组件，解析类用的是ParseXml
        List<Component> comList = getCurrentClickableComList(i, true);
        comList = handleNoClickableComActivityWhenFirstLaunch(comList, i);
        //对于没有clickable为true的组件的页面（虽然这种页面也是少数），改用DoXml解析
        if(comList.size() == 0) {
            comList = getCurrentComList(i);
        }
        outOfAppPackageFlag = judgeOutOfAppPackage(comList);
        comList = handleOutOfAppPackage(comList, outOfAppPackageFlag, 2, i);
        outOfAppPackageFlag = judgeOutOfAppPackage(comList);
        if(outOfAppPackageFlag) {
            if (outAppSemapher == 0) {
                //此时可能是跳出应用，reset一次尝试
                PrintUtil.print("Perhaps not in app, reset one time", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                driver.resetApp();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outAppSemapher++;
            }
            outOfAppPackageFlag=false;
        }
        if(outOfAppPackageFlag){
            PrintUtil.print("Have tried 2 times, still not in appPackage", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            PrintUtil.print("Change isEnd to true", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            stopTestFlag = true;
        }else if(i!=0&&driver.currentActivity().equals(fatherActivity)){
            //记录到达的activity
            recordActivity(driver.currentActivity());
            //如果和上一个页面是同一页面，返回，因为上一个页面在开始新一轮深搜时会设置变量fatherActivity = currentActivity
            return;
        }
        if(checkStopFlag()) {
            return;
        }
        String currentActivity = driver.currentActivity();

        //记录到达的activity
        recordActivity(currentActivity);
        //flag开启的情况下，到了web界面则自动返回，防止无限前进
        if(closeWebPageFlag){
            if(!isNativeApp()){
                System.out.println("not native App");
                driver.sendKeyEvent(AndroidKeyCode.BACK);
                currentActivity = driver.currentActivity();
            }
        }
        //TODO 太慢
        System.out.println("计算页面hash值");
        int currentPageHash = driver.getPageSource().hashCode();
        currentNode.setName(currentActivity);
        //若当前子页面的activity在activityList中，currentActivityIndex为指向该对象的index，不在则为-1
        int currentActivityIndex = indexOfCurrentActivityInActivityList(currentActivity);
        if(currentActivityIndex >= 0){
            //若当前访问页面的activity等于activityList中某一activity对象的activityName，说明此子页面之前出现过
            if(fatherActivity!=null&&!activityList.get(currentActivityIndex).getFatherActivity().equals(fatherActivity)){
                PrintUtil.print("Previous Activity is " + fatherActivity + ", but " + currentActivity + "'s father is " + activityList.get(currentActivityIndex).getFatherActivity() + ", change father", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                //变更父子关系，设置该子页面的fatherActivity为最近一次跳转过来的那个Activity
                activityList.get(currentActivityIndex).setFatherActivity(fatherActivity);
            	fatherNode.add(currentNode);
            }
            int oldPageHash = activityList.get(currentActivityIndex).getHash();
            if(oldPageHash != currentPageHash) {
                //hash值不相等，说明此子页面发生了改变，则更新原子页面
                activityList.get(currentActivityIndex).setHash(currentPageHash);
                comList = setTestedComInList(activityList.get(currentActivityIndex).getComList(), comList);
                activityList.get(currentActivityIndex).setComList(comList);
            }
            pageNeverBeenFlag = false;
        }else{	//若不在activityList中，新页面
            fatherNode.add(currentNode);
            pageHaveBeenFlag = false;
            threadSleep(3);
            takeScreenShot();
            savePageXml();
            //由于该页面不在列表activityList中，新建一个将其加入
            activityList.add(new Activity(currentActivity, fatherActivity, currentPageHash, comList));
            //currentActivityIndex是列表activityList的指针，加入了新元素时指向列表activityList末尾的新元素
            currentActivityIndex = activityList.size() - 1;
        }
        //设置变量fatherActivity为当前页面的Activity
        fatherActivity = currentActivity;
        Activity activity4Test = activityList.get(currentActivityIndex);
        if(activity4Test.getDirtyWord()){
            PrintUtil.print("Going to return because this page is over", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            returnPrevActivity();
            return;
        }else{
            //遍历该页面所有控件，尝试定位控件并进行操作
            for(int comIndex = 0;comIndex < comList.size();comIndex++){
                PrintUtil.print(currentActivity + "'s index is " + comIndex + "/" + comList.size(), TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                Component component = comList.get(comIndex);
                WebElement element4Test = null;
                if(judgeTestedComponent(component) || judgeIgnoreComponent(component)) {
                    continue;
                }
                if(judgeChildComponent(component)){
                	executeClickFatherComponent(component.getFatherComponent());
                }
                if(component.getLocator() != null){
                    try{//定位控件
                    	String locator = component.getLocator();
                        PrintUtil.print("Try to locate " + locator, TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                        if(locator.startsWith("//")) {
                            element4Test = driver.findElementByXPath(locator);
                        }
                        else {
                            element4Test = driver.findElementById(locator);
                        }
                    }catch(Exception exception){	//定位失败，尝试定位该页面下一个控件
                        PrintUtil.print("Can't locate component " + component.getLocator(), TAG, udid, mylogWriter, PrintUtil.ANSI_RED);
                        continue;
                    }
                    comList.get(comIndex).setHasBeenTested(true);
                    //handle father component, add child component into comList
                    if(judgeFatherComponent(component, currentActivity)){
                    	executeClick(component, element4Test);
                    	comList = handleFatherComponent(comList, component, i);
                        PrintUtil.print("After set father component", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                    	printComponentList(comList);
                    	executeKeyEvent("Click Return button because handled father components", AndroidKeyCode.BACK);
                    	comIndex = -1;
                        activityList.get(currentActivityIndex).setComList(comList);
                        //因为处理完父控件的子控件后又返回了，所以相当于处于未点击父控件之前的页面，所以不更新pageHash
                        continue;
                    }
                    //获取本页面的父页面activity
                    String fatherOfCurrentActivity = activity4Test.getFatherActivity();
                    if(component.getClassname().contains("EditText")){
                        //若是登录控件则输入账号密码
                    	executeInput(component, element4Test);
                    }else{
                        //若是其他控件，点击
                        executeClick(component, element4Test);
                        if(checkStopFlag()) {
                            return;
                        }
                        //只在进入视频播放页面第一次时截图并新建节点加入遍历生成树，之后都是直接按返回键(因为获取视频播放页面的PageSource会崩溃)
                        if(driver.currentActivity().equals(".ui.video.VideoPlayerActivity")){
                            if(!hasTakenScreenshot){
                                takeScreenShot();
                                savePageXml();
                                ActivityTreeNode videoActivityNode = new ActivityTreeNode();
                                videoActivityNode.setName(driver.currentActivity());
                                //记录到达的activity
                                recordActivity(driver.currentActivity());
                                currentNode.add(videoActivityNode);
                                hasTakenScreenshot = true;
                            }
                            executeKeyEvent("Click Return button cause only enter VideoPlayerActivity", AndroidKeyCode.BACK);
                        }
                        //点击控件后马上检查是否有android消息弹窗或警告窗出现（至多只会出现一种弹窗），若有（这种情况一般是点了页面上的返回按钮，返回了主页面）就点击取消按钮
                        handleAndroidAlertandMsg();
                        String activityAfterClick = driver.currentActivity();
                        //记录到达的activity
                        recordActivity(activityAfterClick);
                        //web开启则返回
                        if(closeWebPageFlag){
                            if(!isNativeApp()){
                                System.out.println("not native App");
                                driver.sendKeyEvent(AndroidKeyCode.BACK);
                                activityAfterClick = driver.currentActivity();
                            }
                        }
                        //TODO 太慢
                        System.out.println("计算页面hash值");
                        int pageHashAfterClick = driver.getPageSource().hashCode();
                        if(pageHashAfterClick == currentPageHash){
                            //点击控件后的页面的pageSource的hash值没变，页面没有任何改变
                            PrintUtil.print("The page has not changed", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                        }else if(activityAfterClick.equals(currentActivity)){
                            //页面的pageSource的hash值变了，但是activity没变
                            PrintUtil.print("The UI components have changed", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                            List<Component> comListAfterClick = getCurrentClickableComList(i, true);
                            if(comList.hashCode() != comListAfterClick.hashCode()) {
                                //若控件列表的hash值不相同
                            	boolean sameComponentFlag = countSameComponent(comList, comListAfterClick);
                                comList = setTestedComInList(comList, comListAfterClick);
                                //将此Activity的comList(去除先前页面已有且测试过的控件)和hash更新，index重置，继续遍历此Activity控件，而不是开始新一轮深搜
                                comIndex = -1;
                                activityList.get(currentActivityIndex).setComList(comList);
                                activityList.get(currentActivityIndex).setHash(pageHashAfterClick);
                                if(sameComponentFlag){
                                    PrintUtil.print("Update the activity because the different components are less than half", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                }else{
                                    //不相同控件个数过半，认为是进入了此Activity的另一个页面（更新页面继续进行遍历）
                                    PrintUtil.print("Reset this activity's index, beacause the different components are more than half", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                    mergeImage(activityAfterClick);
                                }
                                if(checkStopFlag()) {
                                    return;
                                }
                            }else {
                                PrintUtil.print("The component list hash code has not changed, continue to test current comList", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
							}
                            printComponentList(comList);
                        }else{
                            //页面的PageSource的hash值和Activity都变了
                            PrintUtil.print("The page has changed", TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                            PrintUtil.print("Current activity is " + activityAfterClick, TAG, udid, mylogWriter, PrintUtil.ANSI_GREEN);
                            //页面的pageSource的hash值、activity都变了
                            if(activityAfterClick.equals(fatherOfCurrentActivity)){
                                //如果改变后的当前页面为之前页面的父页面，返回（即不认为是开始了一次新的一轮深搜）
                                PrintUtil.print("I'm going to return because of my dad" + "\r\n", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                return;
                            }else{
                                //页面发生改变，进入的是子页面
                            	if(checkStopFlag()) {
                            	    return;
                                }
                                //此if-else语句目的是找到对应当前页面（点击了控件跳转到的新页面）的父亲节点对应的node，来开始进行对当前页面的新一轮遍历
                                if(pageHaveBeenFlag){
                                    if(!pageNeverBeenFlag){
                                        //点击之前的页面在activityList中能找到（pageHaveBeenFlag=true，pageNeverBeenFlag=false）
                                        //这里的fatherNode是跳转前页面的父亲节点
                                        ActivityTreeNodeList childNodeList = fatherNode.getChildren();
                                        int childNodeIndex = 0;
                                        for(childNodeIndex = 0;childNodeIndex < childNodeList.size();childNodeIndex++){
                                            //找到发生跳转（点击控件）前的页面对应的node节点，即为当前页面的父亲节点
                                        	if((childNodeList.get(childNodeIndex).getName()).equals(currentActivity)){
                                                break;
                                            }
                                        }
                                        DFS(i + 1,childNodeList.get(childNodeIndex));
                                    }
                                }else{
                                    //pageHaveBeenFlag=false，点击前页面不在activityList中，为新的子页面，则跳转前页面对应的节点就为newNode（也就是当前页面的父亲节点）
                                    DFS(i + 1,currentNode);
                                }
                                //点击此控件所跳转到的子页面开展的新一轮深搜已完成并返回（也按了返回键见line892），理论上此时页面应该是点击该控件前的页面
                                if(checkStopFlag()) {
                            	    return;
                                }
                                PrintUtil.print("Has returned", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                //返回之后马上检查是否有android消息弹窗出现，若有（这种情况一般是返回了MainActivity）就点击取消按钮
                                //并检查是否在AppPackage中,不在就结束遍历
                                outOfAppPackageFlag = handleAndroidMsgAndCheckPkg();
                                if(outOfAppPackageFlag) {
                                    //如果返回后的页面不在AppPackage中
                                    PrintUtil.print("Change isEnd to true because not in AppPackage", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                    stopTestFlag = true;
                                    return;
                                }
                                //准备继续遍历这个页面接下来的控件
                                try{
                                    //把变量fatherActivity置为此页面，因为这个变量的值在刚返回的深搜中被改变过
                                    fatherActivity = driver.currentActivity();
                                    //记录到达的activity
                                    recordActivity(driver.currentActivity());
                                    PrintUtil.print("Current activity is " + fatherActivity, TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                }catch(Exception e){
                                    PrintUtil.printException(TAG, udid, e);
                                    return;
                                }
                                List<Component> comListAfterReturn = getCurrentClickableComList(i, true);
                                if(comList.hashCode() != comListAfterReturn.hashCode()){
                                    //此时的页面和之前的页面有区别，就用现在的页面更新了之前页面comList，且去除先前页面已有且测试过的控件，继续遍历这个页面剩下的控件
                                    PrintUtil.print("Update the activity's component , because current comList is different with before", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                                    comList = setTestedComInList(comList, comListAfterReturn);
                                    comIndex = -1;
                                    activityList.get(currentActivityIndex).setComList(comListAfterReturn);
                                    activityList.get(currentActivityIndex).setHash(driver.currentActivity().hashCode());
                                    //记录到达的activity
                                    recordActivity(driver.currentActivity());
                                    printComponentList(comList);
                                }
                                PrintUtil.print("After return, current index is " + comIndex + "/" + comList.size(), TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
                            }
                        }
                    }
                }
            }
            //本页面所有控件已遍历完
            PrintUtil.print("Going to return beacuse this page has done" + "\r\n", TAG, udid, mylogWriter, PrintUtil.ANSI_BLUE);
            activityList.get(currentActivityIndex).setDirtyWord(true);
            returnPrevActivity();
            return;
        }
    }

    private void recordActivity(String act) {
        //实验用，记录每一个到达过的activity
        File f = new File("ActivityList");
        if(!f.exists()){
            f.mkdirs();
        }
        f=new File("ActivityList"+File.separator+this.taskId+".txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f,true));
            bw.write(act+"\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setStopTestFlag(boolean b){
        this.stopTestFlag=b;
    }
}