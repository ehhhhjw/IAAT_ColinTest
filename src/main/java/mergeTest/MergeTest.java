package mergeTest;


import Tree.ActivityTreeNode;
import Tree.DrawTree;
import mergeTest.AppiumNode.WidgetInfoNode;
import mergeTest.AppiumNode.widgetMergeTools;
import mergeTest.monitor.LogMonitor;
import bean.Device;
import io.appium.java_client.AppiumDriver;
import model.Task;
import org.apache.commons.exec.ExecuteException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import util.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import Upoc.AppiumNode.AppiumNode;
//import Upoc.AppiumNode.getAllActivityPath;
//import Upoc.AppiumNode.mergeAppiumNode;


public class MergeTest extends Thread {
    public static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();
    private AppiumDriver driver;
    private volatile boolean isEnd = false;
    private ScreenShotThread screenShotThread;
    private DeviceObserver deviceObserver;
    private ActivityTreeNode root;
    public boolean isNormalEnd = true;
    private Task mTask;
    private String udid, port, appPackage, appActivity, appPath;
    private Device device;
    private String deviceYear;
    private Process logcatProcess;
    private boolean success = false;
    private LogMonitor mLogMonitor;
    private Thread logThread;
    public MergeTest(Task task, Device d) {
        mTask = task;
        this.device = d;
        this.udid = d.getUdid();
        this.appPackage = d.getAppPackage();
        this.appActivity = d.getAppActivity();
        this.appPath = d.getAppPath();
        root = new ActivityTreeNode();
        String port = AppiumManager.getInstance().checkDevicePort(udid);
        if (port == null) {
            startServer();
        } else {
            this.port = port;
        }
        PrintUtil.print("Device " + udid + ", the port is " + this.port, TAG, udid);
        OSUtil.runCommand("adb -s " + udid + "logcat -c");
        mLogMonitor = new LogMonitor(d.getUdid(), task.getTaskID(),task.getApp());
        logThread = new Thread(mLogMonitor);
        logThread.start();
        deviceYear = ExtraService.getDeviceYear(udid);
    }

    private void startServer() {
        int taskId = mTask.getTaskID();
        AppiumManager manager = AppiumManager.getInstance();
        String testLogsPath = AddressUtil.getTestLogsPathById(mTask.getApp(),taskId, udid);
        String appiumLogsPath = AddressUtil.getAppiumLogsPathById(mTask.getApp(),taskId,udid);
        String exceptionLogsPath = AddressUtil.getExceptionLogsPathById(mTask.getApp(),taskId, udid);
        manager.setupAppium(mTask.getApp(),taskId,udid, testLogsPath, appiumLogsPath, exceptionLogsPath);
        this.port = manager.checkDevicePort(udid);
        PrintUtil.print("Setup appium " + udid + " " + this.port, TAG, udid);
    }

    @Override
    public void run() {
        //创建测试文件
        createTestDataFiles();
        //安装
        install();
        //覆盖安装
        coverInstall();
        //评估冷启动时间
        measureColdStartTime();
        //卸载
        uninstall();
        device.setExecStartTime(deviceYear + getDeviceTime());
        //初始化驱动
        initDriver();
        //启动截屏与设备观察线程
        startScreenShotAndDeviceObserverThread();
        //创建安装标识文件
        createInstallFlagFile();
        PrintUtil.print("Device " + udid + ", stop 10 seconds", TAG, udid);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        if (isEnd) {
            return;
        }
        PrintUtil.print("Device " + udid + ", start test", TAG, udid);
        if (isEnd) {
            return;
        }
        long scriptStartTime = System.currentTimeMillis();
        try {
            //这句是什么鬼
            //new NewDefaultScript().init(udid, String.valueOf(mTask.getTaskID()));
            //new execute
            executeScriptWithHumanKnowledge(driver, root, appPackage, udid);
            //执行前置脚本
            //executePrepositionScript(driver, root, appPackage, udid);
            //执行脚本
            //executeScript(driver, root, appPackage, udid);
        } catch (SessionNotFoundException e) {
            writeExecErrorLog(e);
            PrintUtil.printErr("Device " + udid + ". SessionNotFound " + udid, TAG, udid);
        } catch (WebDriverException e) {
            writeExecErrorLog(e);
            PrintUtil.printErr("Device " + udid + ". WebDriverException " + udid + " " + e.getMessage(), TAG, udid);
        } catch (Exception e) {
            writeExecErrorLog(e);
            PrintUtil.printErr("Device " + udid + ". Run script err " + e.getMessage(), TAG, udid);
            isNormalEnd = false;
        }
        //测试完成后
        device.setExecStopTime(deviceYear + getDeviceTime());
        long scriptEndTime = System.currentTimeMillis();
        device.setExecTime(String.valueOf(scriptEndTime - scriptStartTime));
        PrintUtil.print("Device " + udid + ", finish test", TAG, udid);
        setDefaultIME(udid);
        drawTreeAndStopShoot();
        saveDeviceData();
        pullANRtraces(mTask.getApp(),mTask.getTaskID(), udid);
        killAppAndGoHome(appPackage, udid);
        stopScreenShotAndDeviceObserverAndLogcatThread();
        try {
            stopAppiumServer(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintUtil.print("Device " + udid + ", test has finished", TAG, udid);
    }

    private void stopScreenShotAndDeviceObserverAndLogcatThread() {
        try {
            screenShotThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deviceObserver.interrupt();
        deviceObserver.setStopFlag();
        deviceObserver.stop();
        if (mLogMonitor != null) {
            mLogMonitor.stop();
        }
        if (logThread != null) {
            logThread.stop();
        }
        try {
            screenShotThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startScreenShotAndDeviceObserverThread() {
        PrintUtil.print("Device " + udid + ", start screenShot and device observer thread", TAG, udid);
        screenShotThread = new ScreenShotThread(mTask.getApp(),driver, udid,mTask.getTaskID());
        screenShotThread.start();
        deviceObserver = new DeviceObserver(udid, appPackage, mTask.getTaskID(),mTask.getApp());
        deviceObserver.start();
    }

    private void measureColdStartTime() {
        device.setLaunchStartTime(deviceYear + getDeviceTime());
        String command = "adb -s " + udid + " shell am start -W " + this.appPackage + "/" + this.appActivity;
        PrintUtil.print(command, TAG, udid);
        String msg = OSUtil.runCommand(command);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Launch.log"), false));
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msg != null) {
            Pattern pattern = Pattern.compile("ThisTime:\\s+(\\d+)");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                this.device.setColdStartTime(String.valueOf(Double.parseDouble(matcher.group(1)) * 0.001));
            } else {
                this.device.setColdStartTime("unknown");
            }
        } else {
            this.device.setColdStartTime("unknown");
        }
        if (msg.contains("Complete")) {
            this.device.setLaunch(true);
        }
    }

    private void install() {
        device.setInstallStartTime(deviceYear + getDeviceTime());
        String command = "adb -s " + udid + " install " + this.appPath;
        String msg = OSUtil.runCommand(command);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Install.log"), false));
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msg != null && msg.contains("Success")) {
            this.device.setInstall(true);
        } else {
            this.device.setInstall(false);
        }
        msg = msg.replaceAll("\\s*", "");
        String installTime = null;
        try {
            installTime = msg.substring(msg.indexOf("bytesin") + 7, msg.indexOf("s)"));
        } catch (Exception e) {
        }
        if (installTime != null) {
            this.device.setInstallTime(installTime);
        } else {
            this.device.setInstallTime("unknown");
        }
    }

    private void coverInstall() {
        String command = "adb -s " + udid + " install -r " + this.appPath;
        String msg = OSUtil.runCommand(command);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "CoverInstall.log"), false));
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msg != null && msg.contains("Success")) {
            this.device.setCoverInstall(true);
        } else {
            this.device.setCoverInstall(false);
        }
    }

    private void uninstall() {
        device.setUninstallStartTime(deviceYear + getDeviceTime());
        String command = "adb -s " + udid + " uninstall " + this.appPackage;
        String msg = OSUtil.runCommand(command);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Uninstall.log"), false));
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (msg != null && msg.contains("Success")) {
            this.device.setUninstall(true);
        } else {
            this.device.setUninstall(false);
        }
    }

    private void createInstallFlagFile() {
        File installFlagFile = new File(AddressUtil.getInstallFlagFilePath(mTask.getTaskID(), udid));
        try {
            if (!installFlagFile.exists()) {
                installFlagFile.createNewFile();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void drawTreeAndStopShoot() {
        try {
            new DrawTree().draw(mTask.getTaskID(), udid, root.getChildren().get(0));
        } catch (Exception e) {
            PrintUtil.printErr("drawTree error " + udid + " " + e.getMessage() + "get index 0 ", TAG, udid);
        }
        PrintUtil.print("Device " + udid + ", Done draw treeImage", TAG, udid);
        if (screenShotThread != null) {
            screenShotThread.interrupt();
            screenShotThread.setStopFlag();
            screenShotThread.stop();
        }
    }

    public void saveDeviceData() {
        if (deviceObserver != null && device != null) {
            String maxCpuRate = deviceObserver.getMaxCpuInfo();
            String maxMem = deviceObserver.getMaxMemInfo();
            String maxNetwork = deviceObserver.getMaxNetworkInfo();
            String maxBatteryTemp = deviceObserver.getMaxBatteryTempInfo();
            int pid = deviceObserver.getPid();
            device.setMaxCpuRate(maxCpuRate);
            device.setMaxMem(maxMem);
            device.setMaxNetwork(maxNetwork);
            device.setMaxBatteryTemp(maxBatteryTemp);
            device.setAppPid(pid);
        }
    }

    public void setEnd() {
        this.isEnd = true;
    }

    public void setObserverStop() {
        if (deviceObserver != null && deviceObserver.isAlive()) {
            deviceObserver.setStopFlag();
            saveDeviceData();
        }
        drawTreeAndStopShoot();
        endCaptureLog();
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getUdid() {
        return udid;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }

    public String getAppPath() {
        return appPath;
    }

    public Device getDevice() {
        return device;
    }

    public Task getMtask() {
        return mTask;
    }

    public String getPort() {
        return port;
    }

    private void initDriver() {
        PrintUtil.print("init driver " + udid + " " + port, TAG, udid);
        File app = new File(appPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("platformVersion", "4.1.2");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("unicodeKeyboard", "true");
        capabilities.setCapability("resetKeyboard", "true");
        //10分钟等待，超过无反应认为死机
        capabilities.setCapability("newCommandTimeout", 600);
//		capabilities.setCapability("noReset", true);
        boolean success = false;
        int index = 0;
        while (!success && index <= 3 && !isEnd) {
            try {
                driver = new AppiumDriver(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
                success = true;
            } catch (MalformedURLException e1) {
                writeExecErrorLog(e1);
                e1.printStackTrace();
                index++;
            } catch (UnreachableBrowserException e) {
                writeExecErrorLog(e);
                PrintUtil.printErr("Device " + udid + ", restart appium", TAG, udid);
                startServer();
                index++;
            } catch (WebDriverException e) {
                writeExecErrorLog(e);
                PrintUtil.printErr("init android driver  web driverException " + udid + e.getMessage(), TAG, udid);
                index++;
            }
        }
        if (driver != null) {
            PrintUtil.print("the driver init successfully " + udid, TAG, udid);
        } else {
            PrintUtil.print("the driver init wrongly " + udid, TAG, udid);
        }
    }

    public void startCaptureLog(String udid, String appPackage) {
        //应该是只获得当前app的和系统级别为error的log
        String logPath = AddressUtil.getTestLogsPathById(mTask.getApp(),mTask.getTaskID(), udid);
        String command;
        if (OSUtil.isWin()) {
            command = "Commands\\win\\getPid.bat " + udid + " " + appPackage + "\\>";
        } else {
            command = OSUtil.getCmd() + " Commands/getPid.sh " + udid + " " + appPackage + "\\>";
        }
        //获取应用的pid
        String output = OSUtil.runCommand(command);
        PrintUtil.print("the getPid is " + output + command, TAG, udid);
        String[] tmp = output.split("\n");
        String pid;
        if (tmp != null && tmp.length >= 1) {
            //默认只看第一个
            String oneLineMsg = tmp[0];
            PrintUtil.print("the one line is " + oneLineMsg, TAG, udid);
            String[] items = oneLineMsg.split("\\s+");
            PrintUtil.print("the one line is " + items, TAG, udid);
            pid = items[1];
        } else {
            //需要重新获取
            pid = "1";
        }
        if (OSUtil.isWin()) {
            command = "Commands\\win\\logcat.bat " + udid + " " + pid + " " + logPath;
        } else {
            command = OSUtil.getCmd() + " Commands/logcat.sh " + udid + " " + pid + " " + logPath;
        }
        PrintUtil.print("command is " + command, TAG, udid);
        try {
            logcatProcess = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endCaptureLog() {
        if (logcatProcess != null) {
            logcatProcess.destroyForcibly();
            logcatProcess = null;
        }
        PrintUtil.print("Device " + udid + ", end capture log", TAG, udid);
    }

    private void executeScriptWithHumanKnowledge(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid) throws InterruptedException {
        //获取人工数据
        DFSRunner script = new DFSRunner(driver, root, packageName, udid, mTask.getTaskID(),mTask.getApp());
        ArrayList<WidgetInfoNode> resList = null;

        try {
        //第一次，工具自己跑一遍
            script.test();
        }catch(Exception e){
            System.out.println(e.getStackTrace());
            System.out.println("自动化测试失败");
        }



        try {
            //正式根据脚本跑
            //TODO
            resList = widgetMergeTools.getResList("D:\\myWorkSpace\\test\\test");
            widgetMergeTools.resList2Pic(resList);
            ArrayList<WidgetInfoNode> nodeList = widgetMergeTools.getAllPath(resList);
            //第一遍自己跑的数据解析
            ArrayList<WidgetInfoNode> wholeMap = script.wholeMap;
            for(int i = 0;i<resList.size();i++){
                for(int j =0 ;j<wholeMap.size();j++){
                    if(resList.get(i).equals(wholeMap.get(j))){
                        resList.get(i).setVisited(true);
                        break;
                    }
                }
            }
            int testNum=0;
            if (resList != null) {
                for (int i = 0; i < nodeList.size(); i++) {
                    try {
                        if (nodeList.get(i).isVisited()) {
                            System.out.println("第" + (i + 1) + "条路径 已被覆盖，继续下一条");
                            testNum++;
                            continue;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getStackTrace());
                        System.out.println("查询路线覆盖情况失败？");
                    }
                    driver.resetApp();
                    System.out.println("第" + (i + 1) + "条路径");
                    System.out.println("sleep 10s to reset APP");
                    Thread.sleep(10000);
                    try {
                        widgetMergeTools.runOnePath(resList.size(), nodeList.get(i), driver, script);
                    } catch (Exception e) {
                        System.out.println(e.getStackTrace());
                        System.out.println("第" + (i + 1) + "条路径 复现失败");
                    }
                    System.out.println("路径复现完毕，3秒后开始自动化测试");
                    Thread.sleep(3000);
                    try {
                        script.setStopTestFlag(false);
                        script.test();
                    } catch (Exception e) {
                        System.out.println(e.getStackTrace());
                        System.out.println("第" + (i + 1) + "条路径 自动化测试失败");
                    }
                    try {
                        wholeMap = script.wholeMap;
                        for (int m = 0; m < resList.size(); m++) {
                            for (int n = 0; n < wholeMap.size(); n++) {
                                if (resList.get(m).equals(wholeMap.get(n))) {
                                    resList.get(m).setVisited(true);
                                    break;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getStackTrace());
                        System.out.println("用户节点删减失败");
                    }
                    System.out.println("本次遍历完成，进入下一条路径");
                }
                System.out.println("跑完啦");
                System.out.println("经过的activity存储在/ActivityList中");
            }
            //实验用数据
            int num=0;
            for(int m = 0;m<resList.size();m++){
                if(!resList.get(m).isVisited()){
                    num++;
                }
            }
            System.out.println(testNum+"个新Activity点在测试中被重复覆盖");
            System.out.println("还有"+num+"个点未被覆盖");

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("脚本执行失败");
        }
        widgetMergeTools.resList2Pic(script.wholeMap);
        script.closeWriter();
        success=true;
    }

    private void executePrepositionScript(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid) {
        if (mTask.isHasPrepositionScript()) {
            PrintUtil.print("use the custom script " + udid, TAG, udid);
            String scriptName = "Main.java";
            ScriptLoader loader = new ScriptLoader(mTask.getTaskID() + "", scriptName, true);
            loader.executeJava(driver, root, packageName, udid);
        }
    }

    private void executeScript(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid) {
        if (mTask.isHasScript()) {
            PrintUtil.print("use the custom script " + udid, TAG, udid);
//			String scriptName = mTask.getScriptName();
            String scriptName = "Main.java";
            ScriptLoader loader = new ScriptLoader(mTask.getTaskID() + "", scriptName);
            success = loader.executeJava(driver, root, packageName, udid);
        } else if (!mTask.isHasPrepositionScriptGroup()) {
            PrintUtil.print("use the default script " + udid, TAG, udid);
            //driver.resetApp();
            DFSRunner script = new DFSRunner();
            script.test(driver, root, packageName, udid, mTask.getTaskID());
            success = true;
        }
    }

    public static void stopAppiumServer(String Port) throws ExecuteException, IOException {
        if (OSUtil.isWin()) {
            Runtime.getRuntime().exec("echo off & FOR /F \"usebackq tokens=5\" %a in"
                    + " (`netstat -nao ^| findstr /R /C:\"" + Port + "\"`) do (FOR /F \"usebackq\" %b in"
                    + " (`TASKLIST /FI \"PID eq %a\" ^| findstr /I node.exe`) do taskkill /F /PID %a)");
        } else {
//				Runtime.getRuntime().exec("pkill -9 node");
            Runtime.getRuntime().exec(OSUtil.getCmd() + " Commands/stopAppium.sh " + Port);
        }
    }

    private void writeExecErrorLog(Throwable e) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "ExecError.log"), false));
            writer.write(getStackTrace(e));
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    private String getDeviceTime() {
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss,SSS");
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setDefaultIME(String udid) {
        String cmd = "adb -s " + udid + " shell ime list -s";
        String msg = OSUtil.runCommand(cmd);
        String[] tmp = msg.split("\n");
        for (String i : tmp) {
            if (!i.equals("io.appium.android.ime/.UnicodeIME")) {
                OSUtil.runCommand("adb -s " + udid + " shell ime set " + i);
            }
        }
    }

    private void pullANRtraces(String apkName, int taskId, String deviceId) {
        String cmd = "adb -s " + deviceId + " pull /data/anr/traces.txt " + AddressUtil.getANRtracesPathById(apkName, taskId, deviceId);
        OSUtil.runCommand(cmd);
    }

    private void killAppAndGoHome(String packageName, String deviceId) {
        String pressHomeButton = "adb -s " + deviceId + " shell input keyevent 3";
        OSUtil.runCommand(pressHomeButton);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String killApp = "adb -s " + deviceId + " shell am kill " + packageName;
        OSUtil.runCommand(killApp);
    }

    private void createTestDataFiles() {
        File extraServiceDir = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()));
        File installLog = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Install.log");
        File launchLog = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Launch.log");
        File uninstallLog = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "Uninstall.log");
        File coverInstallLog = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "CoverInstall.log");
        File execErrorLog = new File(AddressUtil.getTestInfoDirById(mTask.getApp(),udid, mTask.getTaskID()) + File.separator + "ExecError.log");
        FileSystem.newDir(extraServiceDir);
        FileSystem.newFile(installLog);
        FileSystem.newFile(launchLog);
        FileSystem.newFile(uninstallLog);
        FileSystem.newFile(coverInstallLog);
        FileSystem.newFile(execErrorLog);
    }
}

