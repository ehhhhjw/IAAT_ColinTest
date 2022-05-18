package service;

import mergeTest.Driver;
import bean.Device;
import bean.DeviceStat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ApkUtils.ApkInfo;
import ApkUtils.ApkUtil;
import model.FrontDevice;
import model.Task;
import network.http.Callback;
import network.http.HttpBusiness;
import util.AddressUtil;
import util.FileSystem;
import util.OSUtil;
import util.PrintUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PCService {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
	private static PCService instance;
	private String serverIP;
	private Task task;
	private int id;
	public static PCService getInstance() {
		if (instance == null) {
			instance = new PCService();
		}
		return instance;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private PCService()  {
		super();
	}
	public void setServerIP(String serverIP){
		this.serverIP=serverIP;
	}
	public void finish(){
		//TODO:这边要有安全性保证
		String taskId = String.valueOf(task.getTaskID());
		PrintUtil.print("the taskId is" + taskId, TAG);
//		updateTaskStatus(taskId);
	}
	public int queryTaskStat(String deviceId, String taskId){
		File taskStat;
		BufferedReader statReader = null;
		int remainSeconds = -1;
		taskStat = new File(AddressUtil.getTaskStatFilePath(Integer.parseInt(taskId), deviceId));
		File installFlagFile = new File(AddressUtil.getInstallFlagFilePath(Integer.parseInt(taskId), deviceId));
		if(taskStat.exists()){
			if(installFlagFile.exists()){
				//taskStat存在说明下载完成，installFlagFile存在说明安装完成正在进行测试
				try {
					statReader = new BufferedReader(new FileReader(taskStat));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					remainSeconds = Integer.parseInt(statReader.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//taskStat存在installFlagFile不存在说明下载完了正在安装
				remainSeconds = -2;
			}
		}
		return remainSeconds;
	}
	public String abortTask(String deviceId, String taskId){
		String result = "failure";
		File abortTaskFile = new File("AbortTask" + File.separator + taskId + "_" + deviceId + ".txt");
		if(abortTaskFile.exists()){
			BufferedWriter abortFlagWriter = null;
			try {
				abortFlagWriter = new BufferedWriter(new FileWriter(abortTaskFile,false));	//写入AbortTask文件的终止值
				abortFlagWriter.write(Integer.toString(1));
				abortFlagWriter.flush();
				result = "success";
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public void uploadTestResult(String taskId, Device device) {
		sendExceptionLogs(taskId, device.getUdid(),device.getApkName());
		sendTestLogs(taskId, device.getUdid(),device.getApkName());
		sendDeviceInfos(taskId, device.getUdid(),device.getApkName());
		sendScreenShots(taskId, device.getUdid(),device.getApkName());
		sendTreeImgs(taskId, device.getUdid());
		sendCpuLogs(taskId, device.getUdid(),device.getApkName());
		sendMemLogs(taskId, device.getUdid(),device.getApkName());
		sendNetworkLogs(taskId, device.getUdid(),device.getApkName());
		sendSMLogs(taskId, device.getUdid(),device.getApkName());
		sendInstallLogs(taskId, device.getUdid(),device.getApkName());
		sendCoverInstallLogs(taskId, device.getUdid(),device.getApkName());
		sendLaunchLogs(taskId, device.getUdid(),device.getApkName());
		sendUninstallLogs(taskId, device.getUdid(),device.getApkName());
		sendExecErrorLogs(taskId, device.getUdid(),device.getApkName());
		sendBatteryTempLogs(taskId, device.getUdid(),device.getApkName());
		sendAppiumLogs(taskId, device.getUdid(),device.getApkName());
		sendMyLogs(taskId, device.getUdid(),device.getApkName());
		sendTestScripts(taskId, device.getUdid(),device.getApkName());
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateTaskStatus(taskId, device.getUdid());
	}
	private boolean updateTaskStatus(String taskId,final String deviceId) {
		HttpBusiness.updateTaskStatus(taskId,deviceId, new Callback() {
			@Override
			public void onFailure() {
				PrintUtil.printErr("updateTaskStatus failure", TAG, deviceId);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("updateTaskStatus is all " + content, TAG, deviceId);
			}
		});
		return true;
	}
	/**
	 * Task执行完毕，删除上传的apk,script和生成的文件
	 * @param task
	 */
	public void deleteTask(Task task) {
		int taskId = task.getTaskID();
	}
	public void registerDevices() {
		PrintUtil.print("registry devices", TAG);
		List<FrontDevice> devices = getDeviceList();
		int id = getId();
		PrintUtil.print("send request", TAG);
//		HttpBusiness.registerDevices(String.valueOf(id), devices, new Callback() {
//			@Override
//			public void onFailure() {
//				PrintUtil.printErr("register devices failure", TAG);
//			}
//
//			@Override
//			public void onSuccess(String content) {
//				PrintUtil.print("register devices " + content + getId(), TAG);
//			}
//		});
	}
	public ArrayList<FrontDevice> getDeviceList()  {
		PrintUtil.print("getDevices", TAG);
		String[] tmp = OSUtil.runCommand("adb devices").split("\n");
		ArrayList<FrontDevice> deviceList = new ArrayList<FrontDevice>();
		for (int i = 1; i < tmp.length; ++i) {
			PrintUtil.print("find Devices " + tmp[i], TAG);
			String[] array = tmp[i].split("\t");
			if (array.length == 2) {
				FrontDevice d=new FrontDevice();
				String udid = tmp[i].split("\t")[0];
				String status = tmp[i].split("\t")[1];
				if (!status.equals("device")) {
					continue;
				}
				d.setDevStatus(0);
				d.setUdid(udid);
				String[] tmp1 = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.build.version.release").split("\n");
				String os = tmp1[tmp1.length - 1];
				d.setOs(os);
				String[] tmp2 = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.model").split("\n");
				String deviceName = tmp2[tmp2.length - 1];
				d.setDeviceName(deviceName);
				deviceList.add(d);
			} else {
				PrintUtil.print("invalid message " + tmp[i], TAG);
			}
		}
		return deviceList;
	}
	public List<Device> prepareDevices(List<String> devicesIds, String filename) {
		List<Device> devices = new ArrayList<>();
		ApkInfo apkInfo = null;
		String path = AddressUtil.APK_DIR + filename;
		try {
			apkInfo = new ApkUtil().getApkInfo(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String udid : devicesIds) {
			String tmp;
			if (OSUtil.isWin()) {
				tmp = OSUtil.runCommand("Commands\\win\\checkdevice.bat " + udid);
			} else {
				tmp = OSUtil.runCommand(OSUtil.getCmd()  + " Commands/checkdevice.sh " + udid);
			}
			boolean isOnline = tmp != null && !tmp.isEmpty() && tmp.contains("device");
			PrintUtil.print(udid + " stat is " + isOnline, TAG);
			PrintUtil.print(udid + ";" + apkInfo.getPackageName() + ";" + apkInfo.getLaunchableActivity(), TAG);
			Device device = new Device();
			device.setUdid(udid);
			device.setAppPackage(apkInfo.getPackageName());
			device.setAppActivity(apkInfo.getLaunchableActivity());
			//TODO
			if(device.getAppActivity().equals("com.squareup.leakcanary.internal.DisplayLeakActivity")){
				device.setAppActivity("com.hotbitmapgg.bilibili.module.common.SplashActivity");
			}
			//maybewrong TODO
			try {
				device.setApkName(filename.substring(0, filename.lastIndexOf(".")));
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("wrong apk name");
			}
			device.setAppLabel(apkInfo.getApplicationLable());
			device.setAppPath(path);
			device.setIsOnline(isOnline);
			devices.add(device);
			DeviceStat deviceStat = new DeviceStat();
			deviceStat.setUdid(udid);
			deviceStat.setStat(isOnline ? DeviceStat.BUSY : DeviceStat.DELETE);
			deviceStat.setBrand(getBrand(udid));
			deviceStat.setCpu(getCpuKernel(udid));
			deviceStat.setMem(getTotalMem(udid));
			deviceStat.setSystem(getSystem(udid));
			deviceStat.setModel(getModel(udid));
			List<DeviceStat> newdeviceStats = new ArrayList<DeviceStat>();
			newdeviceStats.add(deviceStat);
			File file = new File(AddressUtil.getDeviceStatsFile());
			if(file.exists()){
				try {
					BufferedReader bReader = new BufferedReader(new FileReader(AddressUtil.getDeviceStatsFile()));
					List<DeviceStat> deviceStats = new Gson().fromJson(bReader.readLine(), new TypeToken<ArrayList<DeviceStat>>(){}.getType());
					for(int i = 0;i < deviceStats.size();i++){
						if(deviceStats.get(i).getUdid().equals(udid)) {
							continue;
						} else {
							newdeviceStats.add(deviceStats.get(i));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(AddressUtil.getDeviceStatsFile()), false));
				writer.write(new Gson().toJson(newdeviceStats));
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		}
		return devices;
	}
	public void coverageTest(Task task, String filename, List<String> deviceIds)  {
		PrintUtil.print("coverageTest " + task.getTaskID() + " " + filename, TAG);
		this.task = task;
		List<Device> prepareDevices = prepareDevices(deviceIds, filename);
		new Driver(task, prepareDevices).start();
		PrintUtil.print("Traverse is end", TAG);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public boolean saveApk(String filename, File file)  {
		String path = AddressUtil.APK_DIR + filename;
		try {
			InputStream stream = new FileInputStream(file);
			FileSystem.saveTo(stream, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean savePrepositionScript(String taskId, String fileName, File file) {
		String path = AddressUtil.getPrepositionScriptFilePath(taskId, fileName);
		String dirPath = AddressUtil.getPrepositionScriptDirById(taskId);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			InputStream stream = new FileInputStream(file);
			FileSystem.saveTo(stream, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean saveScript(String taskId, String fileName, File file) {
		String path = AddressUtil.getScriptFilePath(taskId, fileName);
		String dirPath = AddressUtil.getScriptDirById(taskId);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			InputStream stream = new FileInputStream(file);
			FileSystem.saveTo(stream, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void sendScreenShots(String taskId, final String uid,String apkName) {
		HttpBusiness.sendScreenShots(taskId, uid, getScreenShot(apkName,taskId, uid), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send screenShots failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send screenShots " + content, TAG, uid);
			}
		});
	}
	private void sendTestLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendTestLogs(taskId, uid, getTestLog(apkName,taskId, uid), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send testLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send testLogs " + content, TAG, uid);
			}
		});
	}
	private void sendTreeImgs(String taskId, final String uid) {
		HttpBusiness.sendTreeImgs(taskId, uid, getTreeImage(taskId, uid), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send treeImgs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send treeImgs " + content, TAG, uid);
			}
		});
	}
	private void sendCpuLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendCpuLogs(taskId, uid, new File(AddressUtil.getCpuFilePath(apkName,uid, Integer.parseInt(taskId))), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send cpuLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send cpuLogs " + content, TAG, uid);
			}
		});
	}
	private void sendMemLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendMemLogs(taskId, uid, new File(AddressUtil.getMemFilePath(apkName,uid, Integer.parseInt(taskId))), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send memoryLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send memoryLogs " + content, TAG, uid);
			}
		});
	}
	private void sendNetworkLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendNetworkLogs(taskId, uid, new File(AddressUtil.getNetworkFilePath(apkName,uid, Integer.parseInt(taskId))), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send networkLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send networkLogs " + content, TAG, uid);
			}
		});
	}
	private void sendSMLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendSMLogs(taskId, uid, new File(AddressUtil.getSMFilePath(apkName,uid, Integer.parseInt(taskId))), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send smLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send smLogs " + content, TAG, uid);
			}
		});
	}
	private void sendExceptionLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendExceptionLogs(taskId, uid, getExceptionLog(apkName,taskId, uid), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send exceptionLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send exceptionLogs " + content, TAG, uid);
			}
		});
	}
	private void sendDeviceInfos(String taskId, final String uid,String apkName) {
		HttpBusiness.sendDeviceInfos(taskId, uid, getDeviceInfo(apkName,taskId, uid), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send deviceInfos failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send deviceInfos " + content, TAG, uid);
			}
		});
	}
	private void sendInstallLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendInstallLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "Install.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send installLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send installLogs " + content, TAG, uid);
			}
		});
	}
	
	private void sendCoverInstallLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendCoverInstallLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "CoverInstall.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send coverInstallLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send coverInstallLogs " + content, TAG, uid);
			}
		});
	}
	private void sendLaunchLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendLaunchLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "Launch.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send launchLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send launchLogs " + content, TAG, uid);
			}
		});
	}
	private void sendUninstallLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendUninstallLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "Uninstall.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send uninstallLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send uninstallLogs " + content, TAG, uid);
			}
		});
	}
	private void sendExecErrorLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendExecErrorLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "ExecError.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send execErrorLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send execErrorLogs " + content, TAG, uid);
			}
		});
	}
	private void sendBatteryTempLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendBatteryTempLogs(taskId, uid, new File(AddressUtil.getTestInfoDirById(apkName,uid, Integer.parseInt(taskId)) + File.separator + "batteryTemp.log"), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send batteryTempLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send batteryTempLogs " + content, TAG, uid);
			}
		});
	}
	private void sendAppiumLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendAppiumLogs(taskId, uid, new File(AddressUtil.getAppiumLogsPathById(apkName,Integer.parseInt(taskId),uid)), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send appiumLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send appiumLogs " + content, TAG, uid);
			}
		});
	}
	private void sendMyLogs(String taskId, final String uid,String apkName) {
		HttpBusiness.sendMyLogs(taskId, uid, new File(AddressUtil.getMyLogPath(apkName,uid, taskId)), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send myLogs failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send myLogs " + content, TAG, uid);
			}
		});
	}
	private void sendTestScripts(String taskId, final String uid,String apkName) {
		HttpBusiness.sendTestScripts(taskId, uid, new File(AddressUtil.getTestScript(apkName,uid, taskId)), new Callback(){
			@Override
			public void onFailure() {
				PrintUtil.printErr("send testScripts failure", TAG, uid);
			}
			@Override
			public void onSuccess(String content) {
				PrintUtil.print("send testScripts " + content, TAG, uid);
			}
		});
	}
	private File getTestLog(String taskId, String udid,String apkName){
		return new File(AddressUtil.getTestLogsPathById(apkName, Integer.parseInt(taskId), udid));
	}
	private File getDeviceInfo(String taskId, String udid,String apkName){
		return new File(AddressUtil.getExtraServicePath(apkName,Integer.parseInt(taskId), udid));
	}
	private File getScreenShot(String taskId, String udid,String apkName){
		return new File(AddressUtil.getScreenShotsZipPath(apkName,Integer.parseInt(taskId), udid));
	}
	private File getExceptionLog(String taskId, String udid,String apkName) {
		return new File(AddressUtil.getExceptionLogsPathById(apkName,Integer.parseInt(taskId), udid));
	}
	private File getTreeImage(String taskId, String udid) {
		return new File(AddressUtil.getTreeImgPath(Integer.parseInt(taskId), udid));
	}
	private String getBrand(String udid){
		String brand = "";
		try {
			brand = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.brand").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getBrand error", TAG, udid);
		}
		return brand;
	}
	private String getModel(String udid){
		String model = "";
		try {
			model = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.model").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getModel error", TAG, udid);
		}
		return model;
	}
	private String getSystem(String udid){
		String system = "";
		try {
			system = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.build.version.release").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getSystem error", TAG, udid);
		}
		return system;
	}
	private int getCpuKernel(String udid) {
		int kelNum = 4;
		try {
			int count = 0;
			String cmd = "adb -s " + udid +" shell ls /sys/devices/system/cpu/ ";
			String msg = OSUtil.runCommand(cmd);
			String[] filename = msg.split("\n");
			for(String file : filename){
				if(file.startsWith("cpu")){
					boolean flag = true;
					for(int i = 3 ; i < file.length() ; i++){
						if(file.charAt(i) < '0' || file.charAt(i) > '9'){
							flag = false;
							break;
						}
					}
					if(flag)	count++;
				}
			}
			kelNum = count;
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getCpuKernel error", TAG, udid);
		}
		return kelNum;
	}
	private int getTotalMem(String udid){
		int totalMem = 2;
		try {
			String tmp = OSUtil.runCommand("adb -s " + udid + " shell cat /proc/meminfo | grep MemTotal");
			tmp = tmp.replaceAll("\\s*","");
			tmp = tmp.substring(tmp.indexOf(":") + 1, tmp.indexOf("kB"));
			double double_num = Double.parseDouble(tmp);
			double_num = double_num / (1024.0 * 1024.0);
			int int_num = (int)(double_num + 0.5);
			totalMem = int_num;
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getTotalMem error", TAG, udid);
		}
		return totalMem;
	}
}
