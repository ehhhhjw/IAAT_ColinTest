package mergeTest;

import bean.Device;
import util.AddressUtil;
import util.OSUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtraService {
    public static void writeInfoOnlineSuccess(int id, Device d) {
        String udid = d.getUdid();
        String appPackage = d.getAppPackage();
        String DeviceExecInfoPath = AddressUtil.getDeviceExecInfoPath(d.getApkName(), id, udid);
        String DeviceInfoPath = AddressUtil.getDeviceInfoPath(d.getApkName(), id, udid);
        String install = d.isInstall() ? "Success" : "Failure";
        String uninstall = d.isUninstall() ? "Success" : "Failure";
        String coverInstall = d.isCoverInstall() ? "Success" : "Failure";
        //test Success -> launch Success
        String launch = "Success";
        Date endTime = new Date();
        File file = new File(DeviceExecInfoPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
		file = new File(DeviceInfoPath);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			// TODO:linux下换行符可能也要处理
            //write DeviceExecInfo.txt
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DeviceExecInfoPath), true));
			writer.write("MaxCpu=" + d.getMaxCpuRate() + "\r\n");
			writer.write("MaxMem=" + d.getMaxMem() + "\r\n");
			writer.write("MaxNetwork=" + d.getMaxNetwork() + "\r\n");
			writer.write("MaxBatteryTemperature=" + d.getMaxBatteryTemp() + "\r\n");
			writer.write("ColdStartTime=" + d.getColdStartTime() + "\r\n");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			writer.write("startTime="+ sdf.format(d.getStartTime()) + "\r\n");
			writer.write("endTime="+ sdf.format(endTime) + "\r\n" );
            writer.write("AppPid="+ d.getAppPid() + "\r\n" );
			writer.close();
			//write DeviceInfo.txt
			writer = new BufferedWriter(new FileWriter(new File(DeviceInfoPath), true));
			writer.write("udid=" + udid + "\r\n");
			writer.write("os=" + getAndroidVersion(udid) + "\r\n");
			writer.write("deviceModel=" + getMobileModel(udid) + "\r\n");
			writer.write("brand=" + getMobileBrand(udid) + "\r\n");
			writer.write("deviceName=" + "test" + "\r\n");
			writer.write("resolution=" + getResolution(udid) + "\r\n");
			writer.write("Power=" + getBatteryLevel(udid) + "\r\n");
            writer.close();
			/*
            writer.write("TaskId=" + id + "\r\n");
            writer.write("Package=" + appPackage + "\r\n");
            writer.write("Install=" + install + "\r\n");
            writer.write("Uninstall=" + uninstall + "\r\n");
            writer.write("Coverinstall=" + coverInstall + "\r\n");
            writer.write("Launch=" + launch + "\r\n");
            writer.write("Pass=Success" + "\r\n");
            writer.write("AndroidVersion=" + getAndroidVersion(udid) + "\r\n");
            writer.write("Model=" + getMobileModel(udid) + "\r\n");
            writer.write("Brand=" + getMobileBrand(udid) + "\r\n");
            writer.write("MaxCpu=" + d.getMaxCpuRate() + "\r\n");
            writer.write("MaxMem=" + d.getMaxMem() + "\r\n");
            writer.write("MaxNetwork=" + d.getMaxNetwork() + "\r\n");
            writer.write("MaxBatteryTemperature=" + d.getMaxBatteryTemp() + "\r\n");
            writer.write("ColdStartTime=" + d.getColdStartTime() + "\r\n");
            writer.write("InstallTime=" + d.getInstallTime() + "\r\n");
            writer.write("InstallStartTime=" + d.getInstallStartTime() + "\r\n");
            writer.write("LaunchStartTime=" + d.getLaunchStartTime() + "\r\n");
            writer.write("UninstallStartTime=" + d.getUninstallStartTime() + "\r\n");
            writer.write("ExecStartTime=" + d.getExecStartTime() + "\r\n");
            writer.write("ExecStopTime=" + d.getExecStopTime() + "\r\n");
            writer.write("Power=" + getBatteryLevel(udid) + "\r\n");
            writer.write("ExecTime=" + d.getExecTime() + "\r\n");
            writer.write("AppLabel=" + d.getAppLabel() + "\r\n");
            writer.write("AppPid=" + d.getAppPid());
            writer.close();
			 */
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void writeInfoOnlineFailure(int id, Device d) {
        String udid = d.getUdid();
        String appPackage = d.getAppPackage();
        String infoPath = AddressUtil.getExtraServicePath(d.getApkName(), id, udid);
        String install = d.isInstall() ? "Success" : "Failure";
        String uninstall = d.isUninstall() ? "Success" : "Failure";
        String coverInstall = d.isCoverInstall() ? "Success" : "Failure";
        File nodeScreenShotDir = new File(AddressUtil.getNodeScreenShotDirById(d.getUdid()));
        if (nodeScreenShotDir.exists() && nodeScreenShotDir.list().length != 0)
            d.setLaunch(true);
        String launch = d.isLaunch() ? "Success" : "Failure";
        File file = new File(infoPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(infoPath), true));
            writer.write("TaskId=" + id + "\r\n");
            writer.write("Package=" + appPackage + "\r\n");
            writer.write("Install=" + install + "\r\n");
            writer.write("Uninstall=" + uninstall + "\r\n");
            writer.write("Coverinstall=" + coverInstall + "\r\n");
            writer.write("Launch=" + launch + "\r\n");
            writer.write("Pass=Failure" + "\r\n");
            writer.write("AndroidVersion=" + getAndroidVersion(udid) + "\r\n");
            writer.write("Model=" + getMobileModel(udid) + "\r\n");
            writer.write("Brand=" + getMobileBrand(udid) + "\r\n");
            writer.write("MaxCpu=" + d.getMaxCpuRate() + "\r\n");
            writer.write("MaxMem=" + d.getMaxMem() + "\r\n");
            writer.write("MaxNetwork=" + d.getMaxNetwork() + "\r\n");
            writer.write("MaxBatteryTemperature=" + d.getMaxBatteryTemp() + "\r\n");
            writer.write("ColdStartTime=" + d.getColdStartTime() + "\r\n");
            writer.write("InstallTime=" + d.getInstallTime() + "\r\n");
            writer.write("InstallStartTime=" + d.getInstallStartTime() + "\r\n");
            writer.write("LaunchStartTime=" + d.getLaunchStartTime() + "\r\n");
            writer.write("UninstallStartTime=" + d.getUninstallStartTime() + "\r\n");
            writer.write("ExecStartTime=" + d.getExecStartTime() + "\r\n");
            writer.write("ExecStopTime=" + d.getExecStopTime() + "\r\n");
            writer.write("Power=" + getBatteryLevel(udid) + "\r\n");
            writer.write("ExecTime=" + d.getExecTime() + "\r\n");
            writer.write("AppLabel=" + d.getAppLabel() + "\r\n");
            writer.write("AppPid=" + d.getAppPid());
            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void writeInfoOffline(int id, Device d) {
        String udid = d.getUdid();
        String infoPath = AddressUtil.getExtraServicePath(d.getApkName(), id, udid);
        File file = new File(infoPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(infoPath), true));
            writer.write("Device is offline!");
            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static String isInstall(String appPath, String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " install " + appPath).split("\n");
        String result = tmp[tmp.length - 1];
        if (result.startsWith("Failure") && !result.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
            result = "Failure";
        } else {
            result = "Success";
        }
        return result;
    }

    public static String isUninstall(String packName, String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " uninstall " + packName).split("\n");
        String result = tmp[tmp.length - 1];
        return result;
    }

    public static String getAndroidVersion(String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.build.version.release").split("\n");
        String result = tmp[tmp.length - 1];
        return result;
    }

    public static String getApiLevel(String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.build.version.sdk").split("\n");
        String result = tmp[tmp.length - 1];
        return result;
    }

    public static String getMobileModel(String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.model").split("\n");
        String result = tmp[tmp.length - 1];
        return result;
    }

    public static String getMobileBrand(String udid) {
        String[] tmp = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.brand").split("\n");
        String result = tmp[tmp.length - 1];
        return result;
    }

    public static int getBatteryTempInfo(String udid) {
        String batteryTempInfo = "0";
        String command;
        if (OSUtil.isWin()) {
            command = "Commands\\win\\batteryTemp.bat " + udid;
        } else {
            command = OSUtil.getCmd() + "Commands/batteryTemp.sh " + udid;
        }
        String result = OSUtil.runCommand(command);
        if (result != null) {
            result = result.replaceAll("\\s*", "");
        }
        result = result.substring(result.indexOf(":") + 1, result.length());
        batteryTempInfo = result;
        return Integer.parseInt(batteryTempInfo);
    }

    public static String getBatteryLevel(String udid) {
        String command = "adb -s " + udid + " shell dumpsys battery";
        String result = OSUtil.runCommand(command);
        if (result != null) {
            result = result.replaceAll("\\s*", "");
            int firstLevel = result.indexOf("level:") + 6;
            if (result.indexOf("level") != result.lastIndexOf("level")) {
                result = result.substring(firstLevel, result.indexOf("level", firstLevel));
            } else {
                result = result.substring(firstLevel, result.indexOf("scale"));
            }
            return result;
        } else {
            return "0";
        }
    }

	public static String getResolution(String udid) {
		String command = "adb -s " + udid + " shell  wm size";
		String []tmp = OSUtil.runCommand(command).split("\n");
		String result = tmp[tmp.length-1].split(":")[1];
		if (result != null) {
			result = result.replaceAll("\\s*", "");
			return result;
		} else {
			return "0x0";
		}
	}

    public static String getDeviceYear(String udid) {
        String result = "";
        String command = "adb -s " + udid + " shell date +%Y-";
        OSUtil.runCommand("adb devices");
        result = OSUtil.runCommand(command);
        result = result.trim();
        return result;
    }
}
