package mergeTest;


import bean.Device;
import bean.DeviceStat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Task;
import org.apache.commons.io.FileUtils;
import service.PCService;
import util.AddressUtil;
import util.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Driver {
    public static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();
    private List<MergeTest> taskList;
    private List<Device> deviceList;
    private Task mTask;
    private int timeout = 180;
    public boolean localTestFlag = false;

    public Driver(Task task, List<Device> deviceList) {
        mTask = task;
        if (mTask.getExecuteTime() > 0) {
            timeout = mTask.getExecuteTime();
            PrintUtil.print("Use custom timeout " + timeout + " minutes", TAG, deviceList.get(0).getUdid());
        } else {
            PrintUtil.print("Use default timeout " + timeout + " minutes", TAG, deviceList.get(0).getUdid());
        }
        this.taskList = new ArrayList<>();
        this.deviceList = new ArrayList<>();
        this.deviceList.addAll(deviceList);
    }

    public Driver(Task task, List<Device> deviceList, boolean localTestFlag) {
        mTask = task;
        if (mTask.getExecuteTime() > 0) {
            timeout = mTask.getExecuteTime();
            PrintUtil.print("Use custom timeout " + timeout + " minutes", TAG, deviceList.get(0).getUdid());
        } else {
            PrintUtil.print("Use default timeout " + timeout + " minutes", TAG, deviceList.get(0).getUdid());
        }
        this.taskList = new ArrayList<>();
        this.deviceList = new ArrayList<>();
        this.deviceList.addAll(deviceList);
        this.localTestFlag = localTestFlag;
        if (localTestFlag) {
            FileSystem.createNecessaryDirs();
        }
    }

    public static void main(String args[]) {
        runInIDE();
//		runInJar(args);
        System.exit(0);
    }

    private static void runInIDE() {
        Task task = new Task();
        task.setTaskID((int) (new Date().getTime()-new Date(2020,1,1).getTime()));
        task.setHasScript(false);
        task.setHasPrepositionScript(false);
        //实验用 false为dfs true为有脚本组
        task.setHasPrepositionScriptGroup(true);
        task.setHasFullGroup(false);
        //String mID = "WCH7N15B03002266";
        String mID = "emulator-5554";
        String apkName = "Wikipedia_latest.apk";
        String learningDataRes = "D:\\myWorkSpace\\test";
        task.setApp(apkName.substring(0, apkName.lastIndexOf(".")));
        List<Device> deviceList = PCService.getInstance().prepareDevices(new ArrayList<>(Arrays.asList(mID)), apkName);
        new Driver(task, deviceList, true).start();
        PrintUtil.print("FINISHED", TAG);
    }

    private static void runInJar(String args[]) {
        List<String> devices = new ArrayList<>();
        List<String> appList = new ArrayList<>();
        int taskId = 1;
        if (args.length >= 1 && args[0].equals("batch")) {
            //Mode 2
            try {
                taskId = Integer.parseInt(args[1]);
                for (int i = 2; i < args.length; i++) {
                    devices.add(args[i]);
                }
            } catch (Exception e) {
                System.out.println("Mode 1 (1 selected App, N devices): Appium_localTest.jar <appName.apk> <taskId> <deviceId...>");
                System.out.println("Mode 2 (All Apps in apk/taskList.txt file, N devices): Appium_localTest.jar batch <startTaskId> <deviceId...>");
                System.exit(0);
            }
            String strline;
            try {
                BufferedReader taskListReader = new BufferedReader(new FileReader(AddressUtil.APK_DIR + "taskList.txt"));
                while ((strline = taskListReader.readLine()) != null) {
                    appList.add(strline);
                }
                taskListReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Mode 1
            try {
                appList = Arrays.asList(args[0]);
                taskId = Integer.parseInt(args[1]);
                for (int i = 2; i < args.length; i++) {
                    devices.add(args[i]);
                }
            } catch (Exception e) {
                System.out.println("Mode 1 (1 selected App, N devices): Appium_localTest.jar <appName.apk> <taskId> <deviceId...>");
                System.out.println("Mode 2 (All Apps in apk/taskList.txt file, N devices): Appium_localTest.jar batch <startTaskId> <deviceId...>");
                System.exit(0);
            }
        }
        BufferedWriter taskListInfoWriter = null;
        try {
            taskListInfoWriter = new BufferedWriter(new FileWriter(new File("taskListInfo.csv"), true));
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNowStr = sdf3.format(new Date());
            taskListInfoWriter.write("\n" + dateNowStr + "\n");
            taskListInfoWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String appName : appList) {
            if (appName.endsWith(".apk")) {
                int currentTaskId = taskId;
                Task task = new Task();
                task.setTaskID(taskId++);
                task.setHasScript(false);
                task.setHasPrepositionScript(false);
                List<Device> deviceList;
                try {
                    deviceList = PCService.getInstance().prepareDevices(devices, appName);
                } catch (Exception e) {
                    try {
                        taskListInfoWriter.write(currentTaskId + "," + appName + "," + "skip\n");
                        taskListInfoWriter.flush();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                new Driver(task, deviceList, true).start();
                PrintUtil.print("FINISHED", TAG);
                try {
                    taskListInfoWriter.write(currentTaskId + "," + appName + "," + " \n");
                    taskListInfoWriter.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /*
    public static void testMultipleAppOneDevice(String appDirPath, String deviceId, String port, int startTaskId){
        File file = new File(appDirPath);
        String[] appList = file.list();
        for(String appName : appList){
            if(appName.endsWith("apk")){
                   ApkInfo apkInfo = null;
                String path = appDirPath + appName;
                PrintUtil.print(path, TAG);
                try {
                    apkInfo = new ApkUtil().getApkInfo(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String packageName = apkInfo.getPackageName();
                String activityName = apkInfo.getLaunchableActivity();
                List<Device> deviceList = new ArrayList<>();
                Device d = new Device(deviceId, port, packageName, activityName, path, true);
                deviceList.add(d);
                Task task = new Task();
                task.setTaskID(startTaskId++);
                task.setHasScript(false);
                task.setHasPrepositionScript(false);
                new Driver(task, deviceList, true).start();
                PrintUtil.print("FINISHED", TAG);
            }
        }
        PrintUtil.print("All app have finished", TAG);
    }
    */
    public void start() {
        PrintUtil.print("Driver start", TAG, deviceList.get(0).getUdid());
        dispatchTest();
        observeTest();
    }

    private void createOutputDir(String apkName, int taskId, String Udid) {
        String rootDir = AddressUtil.getOutPutDir(apkName,String.valueOf(taskId),Udid);
        File[] fs = {new File(rootDir), new File(rootDir + "pageImg" + File.separator),
                new File(rootDir + "pageSource" + File.separator), new File(rootDir + "pageXml" + File.separator),
                new File(rootDir + "ScreenShots" + File.separator)};
        System.out.println("wait 3s for create output dir");
        for (File f : fs) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("sleep 3s");
        }
    }

    private void dispatchTest() {
        for (Device device : deviceList) {
            String udid = device.getUdid();
            createOutputDir(mTask.getApp(), mTask.getTaskID(), udid);
            //File fileToDelete = new File(AddressUtil.getScreenShotsDirById(mTask.getTaskID(),udid));
            //deleteDir(fileToDelete);
            boolean isOnline = device.getIsOnline();
            if (isOnline) {
                MergeTest task = startTestTask(device);
                taskList.add(task);
            } else {
                handleOfflineDevice(device);
            }
        }
    }

    private MergeTest startTestTask(Device device) {
        PrintUtil.print("startTestTask " + mTask.getTaskID() + " " + device.getUdid(), TAG, deviceList.get(0).getUdid());
        createAbortTaskFile(device.getUdid());
        MergeTest upocTest = new MergeTest(mTask, device);
        upocTest.start();
        return upocTest;
    }

    private void observeTest() {
        int time = 1000 * 60 * timeout;
        //5秒检查一次
        int checkBreak = 1000 * 5;
        for (int n = 0; n < time / checkBreak; n++) {
            PrintUtil.print("observeTest " + n, TAG, deviceList.get(0).getUdid());
            for (int i = 0; i < taskList.size(); i++) {
                int taskId = taskList.get(i).getMtask().getTaskID();
                String udid = taskList.get(i).getUdid();
                //状态文件中写入当前最大剩余时间，单位s
                writeTaskStat(udid, taskId, (time / checkBreak - n) * 5);
                int abortTaskFlag = readAbortTaskFile(taskList.get(i).getUdid(), taskList.get(i).getMtask().getTaskID());
                //每次都要检查一下
                PrintUtil.print("find uptest " + n, TAG, deviceList.get(0).getUdid());
                // 执行提前结束或已置终止标志
                if (!taskList.get(i).isAlive() || abortTaskFlag == 1) {
                    boolean isNormalEnd = taskList.get(i).isNormalEnd;
                    if (abortTaskFlag == 1) {
                        PrintUtil.printErr("Abort task " + taskList.get(i).getMtask().getTaskID() + "_" + taskList.get(i).getUdid(), TAG, deviceList.get(0).getUdid());
                    } else {
                        PrintUtil.printErr("find thread is not alive ", TAG, deviceList.get(0).getUdid());
                    }
                    MergeTest task = taskList.get(i);
                    task.setEnd();
                    task.setObserverStop();
                    try {
                        MergeTest.stopAppiumServer(task.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintUtil.print("handle screenShot", TAG, deviceList.get(0).getUdid());
                    handleScreenShot(task.getUdid());
                    Device device = taskList.get(i).getDevice();
                    ExtraService.isUninstall(task.getAppPackage(), task.getUdid());
                    if (task.getSuccess()) {
                        PrintUtil.printErr("output file is " + device.getUdid() + " success", TAG, deviceList.get(0).getUdid());
                        ExtraService.writeInfoOnlineSuccess(taskId, device);
                    } else {
                        PrintUtil.printErr("output file is " + device.getUdid() + " fail", TAG, deviceList.get(0).getUdid());
                        ExtraService.writeInfoOnlineFailure(taskId, device);
                    }
                    if (!localTestFlag) {
                        PCService.getInstance().uploadTestResult(String.valueOf(taskId), device);
                    }
                    taskList.remove(i);
                    PrintUtil.print("find thread is not alive " + device.getUdid(), TAG, deviceList.get(0).getUdid());
                    if (isNormalEnd) {
                        //在状态文件中写入正常结束标志，此时是相当于这个设备提前结束了该任务
                        writeTaskStat(udid, taskId, -4);
                    } else {
                        //在状态文件中写入非正常退出标志，说明自动化脚本抛出了异常
                        writeTaskStat(udid, taskId, -3);
                    }
                    updateDeviceStat(device.getUdid());
                }
            }
            if (taskList.isEmpty()) {
                break;
            }
            try {
                //每5秒检查一次
                Thread.sleep(checkBreak);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < taskList.size(); i++) {
            MergeTest task = taskList.get(i);
            task.setEnd();
            task.saveDeviceData();
            task.setObserverStop();
            try {
                MergeTest.stopAppiumServer(task.getPort());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //卸载安装的应用
            ExtraService.isUninstall(task.getAppPackage(), task.getUdid());
            handleScreenShot(task.getUdid());
            Device device = task.getDevice();
            int taskId = mTask.getTaskID();
            String udid = taskList.get(i).getUdid();
            if (task.getSuccess()) {
                PrintUtil.printErr("final output file is " + device.getUdid() + " success", TAG, deviceList.get(0).getUdid());
                ExtraService.writeInfoOnlineSuccess(taskId, device);
            } else {
                PrintUtil.printErr("final output file is " + device.getUdid() + " fail", TAG, deviceList.get(0).getUdid());
                ExtraService.writeInfoOnlineFailure(taskId, device);
            }
            if (!localTestFlag) {
                PCService.getInstance().uploadTestResult(String.valueOf(taskId), device);
            }
            //在状态文件中写入正常结束标志，此时是所有设备都结束了该任务
            writeTaskStat(udid, taskId, -4);
            updateDeviceStat(device.getUdid());
        }
        if (localTestFlag) {
            FileSystem.deleteDeviceStatsFile();
        }
        PrintUtil.print("All devices are finished", TAG, deviceList.get(0).getUdid());
        PCService.getInstance().deleteTask(mTask);
        return;
    }

    private void createAbortTaskFile(String udid) {
        File abortTaskFile = new File(AddressUtil.getAbortTaskFilePath(mTask.getTaskID(), udid));
        FileSystem.newFile(abortTaskFile);
        BufferedWriter abortFlagWriter;
        try {
            //写入AbortTask文件的默认值
            abortFlagWriter = new BufferedWriter(new FileWriter(abortTaskFile, false));
            abortFlagWriter.write(Integer.toString(0));
            abortFlagWriter.flush();
            abortFlagWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTaskStat(String udid, int taskId, int maxRemainTime) {
        File taskStat = new File(AddressUtil.getTaskStatFilePath(taskId, udid));
        BufferedWriter statWriter;
        try {
            if (!taskStat.exists()) {
                taskStat.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            statWriter = new BufferedWriter(new FileWriter(taskStat, false));
            statWriter.write(Integer.toString(maxRemainTime));
            statWriter.flush();    //刷新写缓存
            statWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readAbortTaskFile(String udid, int taskId) {
        int abortTaskFlag = -128;
        BufferedReader abortFlagReader = null;
        File abortTaskFile = new File(AddressUtil.getAbortTaskFilePath(taskId, udid));
        try {
            abortFlagReader = new BufferedReader(new FileReader(abortTaskFile));
        } catch (FileNotFoundException e) {
            PrintUtil.printErr("AbortFile " + taskId + "_" + udid + ".txt" + " is not exist", TAG, deviceList.get(0).getUdid());
        }
        if (abortFlagReader != null) {
            try {
                //读取AbortTask文件数据，1则根据udid和taskid终止任务
                abortTaskFlag = Integer.parseInt(abortFlagReader.readLine());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return abortTaskFlag;
    }

    private void handleOfflineDevice(Device device) {
        String udid = device.getUdid();
        PrintUtil.print("Handle device " + udid + " offline", TAG, deviceList.get(0).getUdid());
        File file = new File(AddressUtil.getTestLogsPathById(mTask.getApp(),mTask.getTaskID(), udid));
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(AddressUtil.getTestLogsPathById(mTask.getApp(),mTask.getTaskID(), udid)), true));
            writer.write("Device is offline!");
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String offlinePath = AddressUtil.getDeviceOfflineImgPath();
        File screenShotFile = new File(offlinePath);
        try {
            FileUtils.copyFile(screenShotFile, new File(AddressUtil.getScreenShotsDirById(mTask.getApp(), mTask.getTaskID(), udid) + File.separator + AddressUtil.DEVICE_OFFLINE_IMG));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int taskId = mTask.getTaskID();
        ZipCompressorByAnt zcba = new ZipCompressorByAnt(AddressUtil.getScreenShotsZipPath(mTask.getApp(), taskId, udid));
        zcba.compressExe(AddressUtil.getScreenShotsDirById(mTask.getApp(), mTask.getTaskID(), udid));
        ExtraService.writeInfoOffline(taskId, device);
        if (!localTestFlag) {
            PCService.getInstance().uploadTestResult(String.valueOf(taskId), device);
        }
    }

    private void handleScreenShot(String udid) {
        String path = AddressUtil.getScreenShotsDirById(mTask.getApp(), mTask.getTaskID(), udid);
        File file = new File(path);

        if (file.exists()) {
            String[] fileList = file.list();
            if (fileList != null && fileList.length >= 1) {
                File firstFile = new File(path + fileList[0]);
                PrintUtil.print(path + fileList[0], TAG, deviceList.get(0).getUdid());
                firstFile.delete();
            }
        }
        String dest = AddressUtil.getScreenShotsZipPath(mTask.getApp(), mTask.getTaskID(), udid);
        String source = AddressUtil.getScreenShotsDirById(mTask.getApp(), mTask.getTaskID(), udid);
        if (FileSystem.isDirectory(source)) {
            ZipCompressorByAnt zcba = new ZipCompressorByAnt(dest);
            zcba.compressExe(source);
        } else {
            PrintUtil.printErr("screenshot zip path not found " + udid, TAG, deviceList.get(0).getUdid());
        }
    }

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public void updateDeviceStat(String udid) {
        File file = new File(AddressUtil.getDeviceStatsFile());
        if (file.exists()) {
            try {
                BufferedReader bReader = new BufferedReader(new FileReader(AddressUtil.getDeviceStatsFile()));
                List<DeviceStat> deviceStats = new Gson().fromJson(bReader.readLine(), new TypeToken<ArrayList<DeviceStat>>() {
                }.getType());
                bReader.close();
                List<DeviceStat> newDeviceStats = new ArrayList<DeviceStat>();
                for (int i = 0; i < deviceStats.size(); i++) {
                    if (deviceStats.get(i).getUdid().equals(udid)) {
                        DeviceStat deviceStat = new DeviceStat();
                        deviceStat.setStat(DeviceStat.IDLE);
                        deviceStat.setUdid(udid);
                        deviceStat.setBrand(deviceStats.get(i).getBrand());
                        deviceStat.setCpu(deviceStats.get(i).getCpu());
                        deviceStat.setMem(deviceStats.get(i).getMem());
                        deviceStat.setSystem(deviceStats.get(i).getSystem());
                        deviceStat.setModel(deviceStats.get(i).getModel());
                        newDeviceStats.add(deviceStat);
                    } else {
                        newDeviceStats.add(deviceStats.get(i));
                    }
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getDeviceStatsFile()), false));
                writer.write(new Gson().toJson(newDeviceStats));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

