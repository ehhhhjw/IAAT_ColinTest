package network.http;

import util.AddressUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by homer on 16-9-16.
 */
public class UrlUtil {

	  private  String HOST;
//	  private  String AHOST;
	//  private static final String HOST = "http://127.0.0.1:9090/server/";
	  private  String API_ROOT;
	  private  String SCREEN_SHOTS_URL;
	  private  String DEVICE_INFOS_URL;
	  private  String EXCEPTION_LOGS_URL;
	  private  String TREE_IMGS_URL;
	  private  String TEST_LOGS_URL;
	  private  String CPU_LOGS_URL;
	  private  String MEM_LOGS_URL;
	  private  String NETWORK_LOGS_URL;
	  private  String SM_LOGS_URL;
	  private  String INSTALL_LOGS_URL;
	  private  String COVERINSTALL_LOGS_URL;
	  private  String LAUNCH_LOGS_URL;
	  private  String UNINSTALL_LOGS_URL;
	  private  String EXECERROR_LOGS_URL;
	  private  String BATTERYTEMP_LOGS_URL;
	  private  String APPIUM_LOGS_URL;
	  private  String MY_LOGS_URL;
	  private  String TEST_SCRIPTS_URL;
	  private  String UPDATE_TASK_STATUS_URL;
	  private  String REGISTER_SERVICE_URL;
	  private  String REGISTER_DEVICES_URL;
	  private  String UPDATE_DEVICE_SATUS_URL;
	public UrlUtil() {
		String serverUrl = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(AddressUtil.getIpAddressConfigurationPath()));
			br.readLine();
			serverUrl = br.readLine();	//second line of ip.txt
//			AHOST = br.readLine();	//third line of ip.txt
		} catch (IOException e) {}
//		AHOST = (AHOST == null || AHOST.isEmpty()) ? "http://10.0.0.32:8080/server/" : "http://" + AHOST;
		if( serverUrl != null && !serverUrl.isEmpty() ){
			 HOST = "http://" + serverUrl;
		     API_ROOT = HOST + "api/v1/";
		     SCREEN_SHOTS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/screenShots/";
		     DEVICE_INFOS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/deviceInfos/";
		     EXCEPTION_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/exceptionLogs/";
		     TREE_IMGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/treeImgs/";
		     TEST_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/testLogs/";
		     CPU_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/cpuLog/";
		     MEM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/memLog/";
		     NETWORK_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/networkLog/";
		     SM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/smLog/";
		     INSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/installLog/";
		     COVERINSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/coverInstallLog/";
		     LAUNCH_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/launchLog/";
		     UNINSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/uninstallLog/";
		     EXECERROR_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/execErrorLog/";
		     BATTERYTEMP_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/batteryTempLog/";
		     APPIUM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/appiumLogs/";
		     MY_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/myLogs/";
		     TEST_SCRIPTS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/testScripts/";
		     UPDATE_TASK_STATUS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}";
		     REGISTER_SERVICE_URL = API_ROOT + "pcs/";
		     REGISTER_DEVICES_URL = API_ROOT + "pcs/{id}/devices/";
		     UPDATE_DEVICE_SATUS_URL = API_ROOT + "updateDeviceStatus/";
		}else{
			 HOST = "http://127.0.0.1:8080/server/";
		     API_ROOT = HOST +"api/v1/";
		     SCREEN_SHOTS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/screenShots/";
		     DEVICE_INFOS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/deviceInfos/";
		     EXCEPTION_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/exceptionLogs/";
		     TREE_IMGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/treeImgs/";
		     TEST_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/testLogs/";
		     CPU_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/cpuLog/";
		     MEM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/memLog/";
		     NETWORK_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/networkLog/";
		     SM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/smLog/";
		     INSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/installLog/";
		     COVERINSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/coverInstallLog/";
		     LAUNCH_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/launchLog/";
		     UNINSTALL_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/uninstallLog/";
		     EXECERROR_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/execErrorLog/";
		     BATTERYTEMP_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/batteryTempLog/";
		     APPIUM_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/appiumLogs/";
		     MY_LOGS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/myLogs/";
		     TEST_SCRIPTS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}/testScripts/";
		     UPDATE_TASK_STATUS_URL = API_ROOT + "tasks/{taskId}/devices/{deviceId}";
		     REGISTER_SERVICE_URL = API_ROOT + "pcs/";
		     REGISTER_DEVICES_URL = API_ROOT + "pcs/{id}/devices/";
		     UPDATE_DEVICE_SATUS_URL = API_ROOT + "updateDeviceStatus/";
		}
	}

//	public String getAhost(){
//		return AHOST;
//	}
	
    public  String getRegisterUrl() {
        return REGISTER_SERVICE_URL;
    }

    public  String getScreenShotUrl(String taskId, String deviceId) {
        return SCREEN_SHOTS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    public  String getDeviceInfosUrl(String taskId, String deviceId) {
        return DEVICE_INFOS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    public  String getExceptionLogsUrl(String taskId, String deviceId) {
        return EXCEPTION_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    public  String getTreeImgsUrl(String taskId, String deviceId) {
        return TREE_IMGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    public  String getTestLogsUrl(String taskId, String deviceId) {
        return TEST_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }

    public  String getCpuLogsUrl(String taskId, String deviceId) {
        return CPU_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }

    public  String getMemLogsUrl(String taskId, String deviceId) {
        return MEM_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }

    public  String getNetworkLogsUrl(String taskId, String deviceId) {
        return NETWORK_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }

    public  String getSMLogsUrl(String taskId, String deviceId) {
        return SM_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getInstallLogsUrl(String taskId, String deviceId){
        return INSTALL_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getCoverInstallLogsUrl(String taskId, String deviceId){
        return COVERINSTALL_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getLaunchLogsUrl(String taskId, String deviceId){
        return LAUNCH_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getUninstallLogsUrl(String taskId, String deviceId){
        return UNINSTALL_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getExecErrorLogsUrl(String taskId, String deviceId){
        return EXECERROR_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getBatteryTempLogsUrl(String taskId, String deviceId){
        return BATTERYTEMP_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getAppiumLogsUrl(String taskId, String deviceId){
        return APPIUM_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getMyLogsUrl(String taskId, String deviceId){
        return MY_LOGS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }
    
    public String getTestScriptsUrl(String taskId, String deviceId){
        return TEST_SCRIPTS_URL.replace("{taskId}", taskId).replace("{deviceId}", deviceId);
    }

    public  String getUpdateTaskStatusUrl(String taskId,String deviceId) {
        return UPDATE_TASK_STATUS_URL.replace("{taskId}", taskId).replace("{deviceId}",deviceId);
    }

    public  String getRegisterDevicesUrl(String pcId) {
        return REGISTER_DEVICES_URL.replace("{id}", pcId);
    }
    
    public  String getUpdateDeviceStatusUrl() {
        return UPDATE_DEVICE_SATUS_URL;
    }

}
