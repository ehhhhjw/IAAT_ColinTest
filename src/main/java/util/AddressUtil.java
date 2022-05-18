package util;

import java.io.File;

/**
 * Created by homer on 16-9-20.
 */
public class AddressUtil {
    public static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();
    public static final String CONFIGURATION_DIR = "Configuration";
    public static final String HUMAN_SCRIPT_DIR = "Login";
    public static final String DEVICE_STATUS_FILE = "DeviceStats.json";
    public static final String TASK_STATUS_DIR = "TaskStat";
    public static final String ABORT_TASK_DIR = "AbortTask";
    public static final String AUTO_GENERATE_SCRIPT_DIR = "TestScripts";
    public static final String TEST_ACTION_DIR = "TestAction";
    public static final String MYLOG_DIR = "MyLogs";
    public static final String PREPOSITION_SCRIPT_DIR = "PrepositionScripts";
    public static final String TEST_DATA_DIR = "TestData";
    public static final String SCREENSHOT_DIR = "ScreenShots";
    public static final String TEST_LOG_DIR = "TestLogs";
    public static final String EXCEPTION_LOG_DIR = "ExceptionLogs";
    public static final String APPIUM_LOG_DIR = "AppiumLogs";
    public static final String EXTRASERVICE_DIR = "ExtraService";
    public static final String TREE_IMAGE_DIR = "TreeImage";
    public static final String NODE_SCREENSHOT_DIR = "NodeScreenShots";
    public static final String PAGE_SOURCE_DIR = "pageSource";
    public static final String PAGE_XML_DIR = "pageXml";
    public static final String PAGE_IMAGE_DIR = "pageImg";
    public static final String USER_SCRIPT_DIR = "Scripts";
    public static final String APK_DIR = "apk" + File.separator;
    public static final String DEVICE_OFFLINE_IMG = "deviceOffline.jpg";
    public static final String OUTPUT_DIR = "output";

    //新增，BB输出地点
    public static String getOutPutDir(String apkName, String taskId, String udid) {
        return OUTPUT_DIR + File.separator + apkName + File.separator + taskId + File.separator + udid + File.separator;
    }

    public static String getPrepositionScriptClassPath(String taskId, String scriptName) {
        if (scriptName.endsWith(".java")) {
            PrintUtil.print("PrepositionScript name is " + scriptName, TAG);
            return getPrepositionScriptDirById(taskId) + scriptName.split("\\.")[0] + ".class";
        } else {
            return null;
        }
    }

    public static String getInstallFlagFilePath(int taskId, String deviceId) {
        return TASK_STATUS_DIR + File.separator + taskId + "_" + deviceId + "_" + "installFlag.log";
    }

    public static String getDeviceOfflineImgPath() {
        return "lib" + File.separator + DEVICE_OFFLINE_IMG;
    }

    public static String getFatherComponentConfigurationPath() {
        return CONFIGURATION_DIR + File.separator + "fatherComponent.conf";
    }

    public static String getIgnoreConfigurationPath() {
        return CONFIGURATION_DIR + File.separator + "ignore.conf";
    }

    public static String getIpAddressConfigurationPath() {
        return CONFIGURATION_DIR + File.separator + "ipAddress.conf";
    }

    public static String getDirsConfigurationPath() {
        return CONFIGURATION_DIR + File.separator + "dirs.conf";
    }

    public static String getHumanScriptPath() {
        return HUMAN_SCRIPT_DIR + File.separator + "SecondTest.java";
    }

    public static String getDeviceStatsFile() {
        return DEVICE_STATUS_FILE;
    }

    public static String getTaskStatFilePath(int taskId, String deviceId) {
        return TASK_STATUS_DIR + File.separator + taskId + "_" + deviceId + ".log";
    }

    public static String getAbortTaskFilePath(int taskId, String deviceId) {
        return ABORT_TASK_DIR + File.separator + taskId + "_" + deviceId + ".log";
    }

    public static String getTestScript(String apkName, String udid, String taskId) {
        return getOutPutDir(apkName,taskId,udid) + "TestScript.java";
    }

    public static String getTestAction(String apkName,String udid, String taskId) {
        return getOutPutDir(apkName,taskId,udid) + "TestAction.log";
    }

    public static String getMyLogPath(String apkName,String udid, String taskId) {
        return getOutPutDir(apkName,taskId,udid) + "TestLog.log";
    }

    public static String getPrepositionScriptFilePath(String taskId, String scriptName) {
        return getPrepositionScriptDirById(taskId) + scriptName;
    }

    public static String getPrepositionScriptDirById(String taskId) {
        return PREPOSITION_SCRIPT_DIR + File.separator + taskId + File.separator;
    }

    public static String getTestInfoDirById(String apkName,String udid, int taskId) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid);
    }

    public static String getScreenShotFilePath(String apkName,int taskId, String udid, String deviceTime) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + SCREENSHOT_DIR + File.separator + udid + "_" + deviceTime + ".png";
    }

    public static String getScreenShotsDirById(String apkName,int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + SCREENSHOT_DIR + File.separator;
    }

    public static String getScreenShotsZipPath(String apkName,int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + SCREENSHOT_DIR + "_" + taskId + ".zip";
    }

    public static String getTestLogsPathById(String apkName, int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "Logcat.log";
    }

    public static String getANRtracesPathById(String apkName, int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "ANRtraces.log";
    }

    public static String getExceptionLogsPathById(String apkName,int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "ExceptionLog.log";
    }

    public static String getAppiumLogsPathById(String apkName,int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "AppiumLog.log";
    }

    public static String getExtraServicePath(String apkName, int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "ExtraService.log";
    }

    public static String getDeviceExecInfoPath(String apkName, int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "DeviceExecInfo.log";
    }
    public static String getDeviceInfoPath(String apkName, int taskId, String udid) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + "DeviceInfo.log";
    }
    public static String getTreeImgPath(int taskId, String deviceId) {
        return TREE_IMAGE_DIR + File.separator + deviceId + "_" + taskId + ".png";
    }

    public static String getNodeScreenShot(String deviceId, String name) {
        return NODE_SCREENSHOT_DIR + File.separator + deviceId + File.separator + name + ".jpg";
    }

    public static String getNodeScreenShotDirById(String deviceId) {
        return NODE_SCREENSHOT_DIR + File.separator + deviceId + File.separator;
    }

    public static String getPageSource(String apkName,int order, String udid, int taskId) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + PAGE_SOURCE_DIR + File.separator + order + "_" + udid + ".xml";
    }

    public static String getPageXmlPath(String apkName,String udid, int taskId, String activityName) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + PAGE_XML_DIR + File.separator + activityName + ".xml";
    }

    public static String getPageXmlDir(String apkName,String udid, int taskId) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + PAGE_XML_DIR + File.separator;
    }

    public static String getPageImgPath(String apkName,String udid, int taskId, String activityName) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + PAGE_IMAGE_DIR + File.separator + activityName + ".jpg";
    }

    public static String getPageImgDir(String apkName,String udid, int taskId) {
        return getOutPutDir(apkName,String.valueOf(taskId),udid) + PAGE_IMAGE_DIR + File.separator;
    }

    public static String getScriptClassPath(String taskId, String scriptName) {
        if (scriptName.endsWith(".java")) {
            PrintUtil.print("Script name is " + scriptName, TAG);
            return getScriptDirById(taskId) + scriptName.split("\\.")[0] + ".class";
        } else {
            return null;
        }
    }

    public static String getScriptFilePath(String taskId, String scriptName) {
        return getScriptDirById(taskId) + scriptName;
    }

    public static String getScriptDirById(String taskId) {
        return USER_SCRIPT_DIR + File.separator + taskId + File.separator;
    }

    public static String getCpuFilePath(String apkName,String deviceId, int taskId) {
        return getTestInfoDirById(apkName,deviceId, taskId) + File.separator + "Cpu.log";
    }

    public static String getMemFilePath(String apkName,String deviceId, int taskId) {
        return getTestInfoDirById(apkName,deviceId, taskId) + File.separator + "Memory.log";
    }

    public static String getNetworkFilePath(String apkName,String deviceId, int taskId) {
        return getTestInfoDirById(apkName,deviceId, taskId) + File.separator + "Network.log";
    }

    public static String getSMFilePath(String apkName,String deviceId, int taskId) {
        return getTestInfoDirById(apkName,deviceId, taskId) + File.separator + "SM.log";
    }

    public static String getBatteryFilePath(String apkName,String deviceId, int taskId) {
        return getTestInfoDirById(apkName,deviceId, taskId) + File.separator + "Battery.log";
    }
}
