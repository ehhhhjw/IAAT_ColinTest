package mergeTest;

import mergeTest.monitor.SMMonitor;
import com.google.gson.Gson;
import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by homer on 16-10-15.
 */
public class DeviceObserver extends Thread {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
    private String cpuInfo = "0";	//%
    private String memInfo = "0";	//kB
    private String networkInfo = "0,0";	//snd,rcv byte
    private String powerInfo = "0";
    private String batteryTempInfo = "0";	//0.1℃
    private String maxCpuInfo = "0";
    private String maxMemInfo = "0";
    private String maxNetWorkInfo = "0";
    private String maxBatteryTempInfo = "0";
    private String udid;
    private int taskId;
    private String packageName;
    private int pid;
    private String apkName;
    private volatile boolean isEnd = false;
    private SMMonitor mSmMonitor;

    private FileWriter cpuWriter;
    private FileWriter memWriter;
    private FileWriter networkWriter;
    private FileWriter smWriter;
    private FileWriter batteryTempWriter;

    private List<Double> cpuList = new LinkedList<>();
    private List<Integer> memList = new LinkedList<>();
    private List<String> networkList = new LinkedList<>();
    private List<Integer> smList = new LinkedList<>();
    private List<Integer> batteryTempList = new LinkedList<>();

//    private LogMonitor mLogMonitor;
//    private Thread logThread;
    public DeviceObserver(String udid, String packageName,int taskId,String apkName) {
        super();
        setDeviceUdid(udid);
        setApkPackage(packageName);
        this.taskId = taskId;
        this.apkName = apkName;
//        mLogMonitor = new LogMonitor(udid, taskId);
//        logThread = new Thread(mLogMonitor);
//        logThread.start();
    }

    private void saveData() {
        try {
            File cpuLog = new File(AddressUtil.getCpuFilePath(apkName,udid, taskId));
            File memLog = new File(AddressUtil.getMemFilePath(apkName,udid, taskId));
            File networkLog = new File(AddressUtil.getNetworkFilePath(apkName,udid, taskId));
            File smLog = new File(AddressUtil.getSMFilePath(apkName,udid, taskId));
            File batteryTempLog = new File(AddressUtil.getBatteryFilePath(apkName,udid, taskId));
            File dir = new File(AddressUtil.getTestInfoDirById(apkName,udid, taskId));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (cpuLog.exists()) {
                cpuLog.delete();
            }
            cpuLog.createNewFile();
            if (memLog.exists()) {
                memLog.delete();
            }
            memLog.createNewFile();
            if (networkLog.exists()) {
                networkLog.delete();
            }
            if (smLog.exists()) {
                smLog.delete();
            }
            if (batteryTempLog.exists()) {
            	batteryTempLog.delete();
            }
            networkLog.createNewFile();
            cpuLog.createNewFile();
            memLog.createNewFile();
            smLog.createNewFile();
            batteryTempLog.createNewFile();

            Gson gson = new Gson();
            String cpuData = gson.toJson(cpuList);
            String memData = gson.toJson(memList);
            String networkData = gson.toJson(networkList);
            String smData = gson.toJson(smList);
            String batteryData = gson.toJson(batteryTempList);

            cpuWriter = new FileWriter(cpuLog);
            memWriter = new FileWriter(memLog);
            networkWriter = new FileWriter(networkLog);
            smWriter = new FileWriter(smLog);
            batteryTempWriter = new FileWriter(batteryTempLog);

            cpuWriter.write(cpuData);
            memWriter.write(memData);
            networkWriter.write(networkData);
            smWriter.write(smData);
            batteryTempWriter.write(batteryData);

            cpuWriter.flush();
            memWriter.flush();
            networkWriter.flush();
            smWriter.flush();
            batteryTempWriter.flush();

            cpuWriter.close();
            memWriter.close();
            networkWriter.close();
            smWriter.close();
            batteryTempWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭
        }
    }

    public void setDeviceUdid(String udid) {
        this.udid = udid;
    }

    public void setApkPackage(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void run() {
        PrintUtil.print("run", TAG, udid);
        mSmMonitor = new SMMonitor();
        pid = getPid(udid, packageName);
        if (pid != -1) {
            mSmMonitor.startSMMonitor(udid, pid);
        }
        while (!isEnd) {
            PrintUtil.print("run while", TAG, udid);
            getCpuInfo(udid, packageName);
            getMemInfo(udid, packageName);
            getNetworkInfo(udid, packageName);
            getSmInfo();
            getBatteryTempInfo(udid);
            saveData();
            try {
                this.sleep(5000);
            } catch (InterruptedException e) {
            	PrintUtil.printException(TAG, udid, e);
//                e.printStackTrace();
            }
        }
    }

    public void setStopFlag() {
        isEnd = true;
        if (mSmMonitor != null) {
            mSmMonitor.stop();
        }
        saveData();
//        if (mLogMonitor != null) {
//            mLogMonitor.stop();
//        }
//        if (logThread != null) {
//            logThread.stop();
//        }
    }


    private int getPid(String udid, String apkPackage) {
        String command;
        if (OSUtil.isWin()) {
            command =  "Commands\\win\\getPid.bat " + udid + " " + apkPackage;
        } else {
            command = OSUtil.getCmd() + " Commands/getPid.sh " + udid + " " + apkPackage +"\\>";
        }
        //获取应用的pid
        String output = OSUtil.runCommand(command);
        PrintUtil.print("the getPid is " + output  + command + " in deviceObserver", TAG, udid);
        String[] tmp = output.split("\n");
        String pid = "-1";
        if (tmp != null && tmp.length >=1) {
            for(int i=0;i<tmp.length;i++) {
                String firstLine = tmp[i]; //默认只看第一个
                if(firstLine.contains(apkPackage)&&!firstLine.contains("adb")) {
                    String[] items = firstLine.split("\\s+");
                    if (items != null && items.length >= 2) {
                        pid = items[1];
                        break;
                    } else {
                        pid = "-1";
                    }
                }
            }
        } else {
            //需要重新获取
            pid = "-1";
        }
        return Integer.parseInt(pid);
    }
    private void getSmInfo() {
        int skippedFrameCount = mSmMonitor.getSkippedFrameCount();
        if (skippedFrameCount == 0) {
            smList.add(60);
        } else {
            int sm = 60 - skippedFrameCount / 5;
            smList.add(sm);
        }
    }


//    private void getCpuInfo(String udid, String apkPackage) {
//    	PrintUtil.print("getCpuInfo");
//        String command;
//        if (OSUtil.isWin()) {
//        	command = "bash Commands\\cpuInfo.sh " + udid + " " + apkPackage;
//        } else {
//            command = OSUtil.getCmd() + " Commands/cpuinfo.sh " + udid + " " + apkPackage;
//        }
//        String result = OSUtil.runCommand(command);
//        result = result.split("\n")[0];	
//        if (result != null && result.contains("%")) {
//        	result = result.replaceAll("\\s*","");	//去除字符串中的空格、回车、换行符、制表符
//        	result = result.substring(0, result.indexOf("%", 0)); //截取从开始到第一个%之间的串
//        	cpuInfo = result;
//            PrintUtil.print((int) Double.parseDouble(cpuInfo) + "%");	//数据出现小数时先转成double再用int截断
//        }else	PrintUtil.print("CpuInfo is invalid , use previous data " + cpuInfo + "%");
//        int tempCpuInfo = (int) Double.parseDouble(cpuInfo);
//        if(tempCpuInfo > Integer.parseInt(maxCpuInfo)) maxCpuInfo = String.valueOf(tempCpuInfo);
//        cpuList.add((int) Double.parseDouble(cpuInfo));
//    }
    private void getCpuInfo(String udid, String apkPackage) {
    	PrintUtil.print("getCpuInfo", TAG, udid);

        double tempCpuInfo = getCpuRate(String.valueOf(pid), udid);	//数据出现小数时先用int截断
        if(tempCpuInfo==-100.0){
            getPid(udid, apkPackage);
            tempCpuInfo = getCpuRate(String.valueOf(pid), udid);	//数据出现小数时先用int截断
        }
    	if(tempCpuInfo < 0)	PrintUtil.print("CpuInfo is invalid , use previous data " + cpuInfo + "%", TAG, udid);
    	else {
    		cpuInfo = String.format("%.2f",tempCpuInfo);	//四舍五入两位小数
    		PrintUtil.print(cpuInfo + "%", TAG, udid);
            if(tempCpuInfo > Double.parseDouble(maxCpuInfo)) maxCpuInfo = String.format("%.2f",tempCpuInfo);
    	}
        cpuList.add(Double.parseDouble(cpuInfo));
    }

    private void getMemInfo(String udid, String apkPackage) {
    	PrintUtil.print("getMemInfo", TAG, udid);
		String command;
        if (OSUtil.isWin()) {
            command = "Commands\\win\\memInfo.bat " + udid + " " + apkPackage;
        } else {
            command = OSUtil.getCmd() + " Commands/meminfo.sh " + udid + " " + apkPackage;
        }
        String result = OSUtil.runCommand(command);
        
        if (result != null&&result.contains("kB")) {
        	result = result.split("\n")[0];	//只看第一行PSS值
        	result = result.replaceAll("\\s*","");	//去除字符串中的空格、回车、换行符、制表符
//        	PrintUtil.print(result);
        	if(result.indexOf("kB", 0) < result.indexOf("(", 0)) result = result.substring(0,result.indexOf("kB", 0));
        	else result = result.substring(0,result.indexOf("(", 0));	//处理不同的结果格式
        	memInfo = result;
            PrintUtil.print(Integer.parseInt(memInfo) + "kB", TAG, udid);
        }else	PrintUtil.print("MemInfo is invalid , use previous data " + memInfo + "kB", TAG, udid);
        if(Integer.parseInt(memInfo)>Integer.parseInt(maxMemInfo)) maxMemInfo = memInfo;
        memList.add(Integer.parseInt(memInfo));
    }

    private void getNetworkInfo(String udid, String apkPackage) {
    	PrintUtil.print("getNetWorkInfo", TAG, udid);
        String command;
        if (OSUtil.isWin()) {
            command = "Commands\\win\\getUid.bat " + udid + " " + apkPackage;
        } else {
            command = OSUtil.getCmd() + " Commands/getUid.sh " + udid + " " + apkPackage;
        }
        String testUid[] = OSUtil.runCommand(command).split("\\n");
        String uid;
        if(OSUtil.isWin()) uid = testUid[testUid.length-1].substring(0,testUid[testUid.length-1].indexOf(' '));
        else uid = testUid[testUid.length-1].replaceAll("\\s*","");	//获得应用Uid
        PrintUtil.print("Uid is: " + uid, TAG, udid);
        int index = 0;
        for(;index < uid.length();index++)
        {
        	if(uid.charAt(index) >= '0' && uid.charAt(index) <= '9') continue;	//过滤uid=10198 gids=xxxx这一类结果
        	else break;
        }
        uid = uid.substring(0,index);
        String snd = OSUtil.runCommand("adb -s " + udid + " shell cat /proc/uid_stat/" + uid + "/tcp_snd").replaceAll("\\s*","");
        String rcv = OSUtil.runCommand("adb -s " + udid + " shell cat /proc/uid_stat/" + uid + "/tcp_rcv").replaceAll("\\s*","");
        boolean dataErrorFlag = false;
        try {
			Integer.parseInt(snd);
			Integer.parseInt(rcv);
		} catch (Exception e) {
			dataErrorFlag = true;
		}
        if(!dataErrorFlag) {
        	networkInfo = snd + "," + rcv;
        	PrintUtil.print("snd: " + snd + ",rcv: " + rcv + " bytes", TAG, udid);
        }else	PrintUtil.print("NetWorkInfo is invalid , use previous data " + networkInfo + " bytes", TAG, udid);
        networkList.add(networkInfo);	//if get data error, will write last time data(networkInfo)
//        PrintUtil.print("uid: " + uid);
//        if (OSUtil.isWin()) {
//            command = "bash Commands\\network.sh " + udid + " " + uid;
//        } else {
//            command = OSUtil.getCmd() + " Commands/network.sh " + udid + " " + uid;
//        }
//        
//        String result = OSUtil.runCommand(command);
//     //   PrintUtil.print(result);
//        
//        if (result != null && result.contains("0")) {	//排除非法结果
//            // 每一行的第6、8列两个数据，全部加起来
//            String[] array = result.split("\n");
//            if (array != null) {
//                int total = 0;
//                for(int i = 0;i < array.length;i++){
//                	if(array[i].contains(uid))	//排除空行
//                	{
//	                	total=total+Integer.parseInt(array[i].split(" ")[5])+Integer.parseInt(array[i].split(" ")[7]);
//                	}
//                }
//                total = total / 1000; //转换成kB
//                result = String.valueOf(total);
//            } else {
//                result = "0";
//            }
//            networkInfo = result;
//            PrintUtil.print(Integer.parseInt(networkInfo) + "kB");
//        }else {
//        	PrintUtil.print("NetWorkInfo is invalid , return");
//        	return;
//        }
//        if(Integer.parseInt(networkInfo)>Integer.parseInt(maxNetWorkInfo)) maxNetWorkInfo = networkInfo;
//        networkList.add(Integer.parseInt(networkInfo));
    }
    
    private void getBatteryTempInfo(String udid){
    	String command;
        if (OSUtil.isWin()) {
            command = "Commands\\win\\batteryTemp.bat " + udid;
        } else {
            command = OSUtil.getCmd() + " Commands/batteryTemp.sh " + udid;
        }
        String result = OSUtil.runCommand(command);
        if(result != null) {
            result = result.split("\\n")[result.split("\\n").length-1];
            result = result.replaceAll("\\s*","");
        }	////去除字符串中的空格、回车、换行符、制表符
        result = result.substring(result.indexOf(":")+1,result.length());
    //    if(result.compareTo(batteryTempInfo) > 0){	//如果加了这个条件，那么温度降低就无法输出到日志
        	batteryTempInfo = result;
     //   }
        if(Integer.parseInt(batteryTempInfo)>Integer.parseInt(maxBatteryTempInfo)) maxBatteryTempInfo = batteryTempInfo;
        batteryTempList.add(Integer.parseInt(batteryTempInfo));
    }
	public static double getCpuRate(String pid, String udid){
		double result = -1;
		try {
			int processCpuTime1 = processCpuTime(pid, udid);
			if(processCpuTime1==-1) return -100.0;
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int processCpuTime2 = processCpuTime(pid, udid);
			int processCpuTime3 = processCpuTime2 - processCpuTime1;
			int totalCpuTime1 = totalCpuTime(udid);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int totalCpuTime2 = totalCpuTime(udid);
			int totalCpuTime3 = (totalCpuTime2 - totalCpuTime1) * getCpuKernel(udid);
			result = 100 * ((double)processCpuTime3) / (double)totalCpuTime3;
		} catch (Exception e) {
			PrintUtil.printErr("getCpuRateError: " + getStackTrace(e), TAG, udid);
		}
		return result;
	}
	public static int getCpuKernel(String udid) {
		int kelNum = 0;
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
				if(flag)	kelNum++;
			}
		}
		return kelNum == 0 ? 2 : kelNum;
	}
	public static int totalCpuTime(String udid) {
		int result = 0;
		int user, nice, system, idle, iowait, irq, softirq = 0;
		String cmd = "adb -s " + udid +" shell cat /proc/stat";
		String msg = OSUtil.runCommand(cmd); 
		String[] tmp = msg.split(" +");
		for(String i : tmp){
			if(i.equals("cpu")){
				user = Integer.parseInt(tmp[1]);
				nice = Integer.parseInt(tmp[2]);
				system = Integer.parseInt(tmp[3]);
				idle = Integer.parseInt(tmp[4]);
				iowait = Integer.parseInt(tmp[5]);
				irq = Integer.parseInt(tmp[6]);
				softirq = Integer.parseInt(tmp[7]);
				result = user + nice + system + idle + iowait + irq + softirq;
				return result;
			}
		}
		return result;
	}
	public static int processCpuTime(String pid, String udid) {
		int result = 0;
		int utime, stime, cutime, cstime = 0;
		String cmd = "adb -s "+ udid + " shell cat /proc/" + pid +"/stat";
		String msg = OSUtil.runCommand(cmd);
		String[] res = msg.split(" +");
		//TODO reget pid
        if(res.length<13){
            return -1;
        }
	    utime = Integer.parseInt(res[13]);
	    stime = Integer.parseInt(res[14]);
	    cutime = Integer.parseInt(res[15]);
	    cstime = Integer.parseInt(res[16]);
	    result = utime + stime + cutime + cstime;
		return result;
	}
    private static String getStackTrace(Throwable t){
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    try{
		    t.printStackTrace(pw);
		    return sw.toString();
	    }
	    finally{
	    	pw.close();
	    }
    }
    public int getPid(){
    	return pid;
    }
    public String getMaxCpuInfo() {
        return maxCpuInfo;
    }

    public String getMaxMemInfo() {
        return maxMemInfo;
    }

    public String getMaxNetworkInfo() {
        return maxNetWorkInfo;
    }

    public String getMaxBatteryTempInfo() {
        return maxBatteryTempInfo;
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
