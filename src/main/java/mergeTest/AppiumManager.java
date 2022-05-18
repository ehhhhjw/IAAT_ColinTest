package mergeTest;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by homer on 16-10-24.
 */
public class AppiumManager {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
    private static final int INIT_PORT = 4730;
    private static AppiumManager manager;
    private HashMap<String, String> udidAndPortMap = new HashMap<>();
    private HashMap<String, String> portAndUdidMap = new HashMap<>();
    private int nextPort;

    private AppiumManager() {
        super();
        nextPort = INIT_PORT;
    }

    public static AppiumManager getInstance() {
        if (manager == null) {
            manager = new AppiumManager();
        }
        return manager;
    }

    public String checkDevicePort(String udid) {
        return udidAndPortMap.get(udid);
    }
    //  使用一些linux命令来去掉
    public boolean stopAppium(String port) {
        return true;
    }

    public void setupAppium(String apkName,int taskId,String udid, String testLogsPath, String appiumLogsPath, String exceptionLogsPath) {

        String port = checkDevicePort(udid);
        if (port == null) {
            port = getFreePort();
            udidAndPortMap.put(udid, port);
        }

        if(checkAppiumRunning(port) ){
            PrintUtil.print("appium in " + port + " is running now", TAG);
           return;
        }
        File log = new File(testLogsPath);
        try {
            if(!log.exists()) {
                log.createNewFile();
            }else {
                log.delete();
                log.createNewFile();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        File output = new File(appiumLogsPath);
        try {
            if (!output.exists()) output.createNewFile();
            else{
                output.delete();
                output.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Runtime r=Runtime.getRuntime();
        try {
            String cmd = OSUtil.getCmd();
            if (OSUtil.isWin()) {

                String ccmd = "Commands\\win\\appiumWin.bat " + port + " " + udid + " " + exceptionLogsPath + " " + appiumLogsPath;
                r.exec(ccmd);
                try {
                    Thread.sleep(3000);
                }catch(Exception e){

                }
//            	try {
//					startAppium(port, udid, appiumLogsPath);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            } else {
                int dp = (Integer.parseInt(port) + 100);
                String comand = cmd + " Commands/newAppium.sh " + port + " " + udid + " " + exceptionLogsPath
                        + " " + appiumLogsPath + " " + dp;
                PrintUtil.print("the comand is " + comand, TAG);
                r.exec(comand);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
			FileReader fr=new FileReader(new File(AddressUtil.getAppiumLogsPathById(apkName,taskId,udid)));
			BufferedReader br=new BufferedReader(fr);
			String line=br.readLine();
			PrintUtil.print(line, TAG);
			boolean judge=false;
			if(line==null){
				judge=true;
			}else if(line.equals("")){
				judge=true;
			}else if(line.split(" ").length<=1){
				judge=true;
			}else if(line.split(" ")[1].equals("-a")){
				judge=true;
			}
			PrintUtil.print("Waiting for server "+udid+" openning...", TAG);
			while(judge){
				line=br.readLine();
                if(line==null){
					judge=true;
				}else if(line.equals("")){
					judge=true;
				}else if(line.split(" ").length<=1){
					judge=true;
				}else if(line.split(" ")[1].equals("-a")){
					judge=true;
				}else{
					judge=false;
				}
			}
			PrintUtil.print("Server " + port + " " + udid + " is open", TAG);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public  String getFreePort() {
        boolean found = false;
        String ret = null;
        while (!found) {
            if (!isLocalPortUsing(nextPort)) {
                ret = String.valueOf(nextPort);
                found = true;
            }
            nextPort ++;
        }
        return ret;
    }
    
    public static void startAppium(String port , String udid, String appiumLogsPath) throws IOException, InterruptedException { 
        // 处理外部命令执行的结果，释放当前线程，不会阻塞线程 
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler(); 
        String cmd = "appium -a 127.0.0.1 -p " + port + " -U " + udid + " --log-timestamp --local-timezone --log-no-colors > " + appiumLogsPath;
        CommandLine commandLine = CommandLine.parse(cmd); 

       // 创建监控时间60s,超过60s则中断执行 
       ExecuteWatchdog dog = new ExecuteWatchdog(60 * 1000);
       DefaultExecutor executor = new DefaultExecutor(); 

       // 设置命令执行退出值为1，如果命令成功执行并且没有错误，则返回1 
       executor.setExitValue(1);
       executor.setWatchdog(dog);
       executor.execute(commandLine, resultHandler);
       resultHandler.waitFor(5000); 
       PrintUtil.print("Appium server start", TAG); 
       }

    public static boolean checkAppiumRunning(String port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", Integer.parseInt(port));

        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isLocalPortUsing(int port){
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isPortUsing(String host,int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        int p=Integer.valueOf(port);
        try {
            Socket socket = new Socket(theAddress,p);
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }
}
