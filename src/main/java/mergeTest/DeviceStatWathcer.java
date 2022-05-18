package mergeTest;

import bean.DeviceStat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import network.http.Callback;
import network.http.HttpBusiness;
import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeviceStatWathcer extends Thread{
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
	public void run(){
		while(true){
			File file = new File(AddressUtil.getDeviceStatsFile());
			List<DeviceStat> currentDeviceStats = getDeviceList();
			String deviceStatsJson = new Gson().toJson(currentDeviceStats);
			if(file.exists()){
				try {
					BufferedReader bReader = new BufferedReader(new FileReader(AddressUtil.getDeviceStatsFile()));	
					List<DeviceStat> deviceStats = new Gson().fromJson(bReader.readLine(), new TypeToken<ArrayList<DeviceStat>>(){}.getType());
					List<DeviceStat> newDeviceStats = new ArrayList<DeviceStat>();
					for(int i = 0; i < currentDeviceStats.size() ; i++){
						boolean existFlag = false;
						for(int j = 0;j < deviceStats.size();j++){
							if(currentDeviceStats.get(i).getUdid().equals(deviceStats.get(j).getUdid()) && currentDeviceStats.get(i).getStat() == DeviceStat.IDLE){
								existFlag = true;
								DeviceStat deviceStat = new DeviceStat();
								deviceStat.setUdid(currentDeviceStats.get(i).getUdid());
								deviceStat.setStat(deviceStats.get(j).getStat());
								deviceStat.setBrand(currentDeviceStats.get(i).getBrand());
								deviceStat.setCpu(currentDeviceStats.get(i).getCpu());
								deviceStat.setMem(currentDeviceStats.get(i).getMem());
								deviceStat.setSystem(currentDeviceStats.get(i).getSystem());
								deviceStat.setModel(currentDeviceStats.get(i).getModel());
								newDeviceStats.add(deviceStat);
								break;
							}
						}
						if(!existFlag) newDeviceStats.add(currentDeviceStats.get(i));
					}
					deviceStatsJson = new Gson().toJson(newDeviceStats);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			PrintUtil.print(deviceStatsJson, TAG);
			HttpBusiness.updateDeviceStats(deviceStatsJson, new Callback(){
				@Override
				public void onFailure() {
					PrintUtil.printErr("updateDeviceStats failure", TAG);
				}

				@Override
				public void onSuccess(String content) {
//					System.err.println("updateDeviceStats " + content);
				}

			});
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public ArrayList<DeviceStat> getDeviceList()  {
//		System.out.println("getDevices");
		String[] tmp = OSUtil.runCommand("adb devices").split("\n");
		ArrayList<DeviceStat> deviceStatList = new ArrayList<DeviceStat>();
		for (int i = 1; i < tmp.length; ++i) {
//			System.out.println("find Devices " + tmp[i]);
			String[] array = tmp[i].split("\t");
			if (array.length == 2) {
				DeviceStat d=new DeviceStat();
				String udid = tmp[i].split("\t")[0];
				String status = tmp[i].split("\t")[1];
				if (!status.equals("device")) {	//offline(DELETE)
					d.setStat(DeviceStat.DELETE);
					d.setUdid(udid);
					d.setBrand("");
					d.setCpu(4);
					d.setMem(2);
					d.setModel("");
					d.setSystem("");
				}else {	//online(IDLE) ready
					d.setStat(DeviceStat.IDLE);
					d.setUdid(udid);
					d.setBrand(getBrand(udid));
					d.setCpu(getCpuKernel(udid));
					d.setMem(getTotalMem(udid));
					d.setModel(getModel(udid));
					d.setSystem(getSystem(udid));
				}
				deviceStatList.add(d);
			} else {
				System.out.println("invalid message " + tmp[i]);
			}
		}
		return deviceStatList;
	}
	public String getBrand(String udid){
		String brand = "";
		try {
			brand = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.brand").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getBrand error", TAG);
		}
		return brand;
	}
	public String getModel(String udid){
		String model = "";
		try {
			model = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.product.model").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getModel error", TAG);
		}
		return model;
	}
	public String getSystem(String udid){
		String system = "";
		try {
			system = OSUtil.runCommand("adb -s " + udid + " shell getprop ro.build.version.release").replaceAll("\n","");
		} catch (Exception e) {
			PrintUtil.printErr(udid + " getSystem error", TAG);
		}
		return system;
	}
	public int getCpuKernel(String udid) {
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
			PrintUtil.printErr(udid + " getCpuKernel error", TAG);
		}
		return kelNum;
	}
	public int getTotalMem(String udid){
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
			PrintUtil.printErr(udid + " getTotalMem error", TAG);
		}
		return totalMem;
	}
}
