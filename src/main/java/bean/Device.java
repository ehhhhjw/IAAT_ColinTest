package bean;

import java.io.Serializable;
import java.util.Date;

public class Device implements Serializable{
	private String id;
	private String pcId;
	private String udid;
	private String port;
	private String appPackage;
	private String appActivity;
	private String appPath;
	private boolean isOnline;
	private String maxCpuRate;
	private String maxMem;
	private String maxNetwork;
	private String maxBatteryTemp;
	private String coldStartTime ;
	private String installTime;
	private boolean coverInstall = false;
	private boolean install = false;
	private boolean uninstall = false;
	private boolean launch = false;
	private String installStartTime;
	private String launchStartTime;
	private String uninstallStartTime;
	private String execStartTime;
	private String execStopTime;
	private String execTime = "0";
	private String appLabel;
	private Integer appPid;

	public Date getStartTime() {
		return startTime;
	}

	private Date startTime;
	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	private String apkName;
	
	public Device(){
		super();
		this.startTime = new Date();
	}
	
	public Device(String udid, String port, String appPackage, String appActivity, String appPath, boolean isOnline,String apkName) {
		super();
		this.udid = udid;
		this.port = port;
		this.appPackage = appPackage;
		this.appActivity = appActivity;
		this.appPath = appPath;
		this.isOnline = isOnline;
		this.apkName = apkName;
		this.startTime = new Date();
	}
	
	public Integer getAppPid() {
		return appPid;
	}

	public void setAppPid(Integer appPid) {
		this.appPid = appPid;
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public String getInstallStartTime() {
		return installStartTime;
	}

	public void setInstallStartTime(String installStartTime) {
		this.installStartTime = installStartTime;
	}

	public String getLaunchStartTime() {
		return launchStartTime;
	}

	public void setLaunchStartTime(String launchStartTime) {
		this.launchStartTime = launchStartTime;
	}

	public String getUninstallStartTime() {
		return uninstallStartTime;
	}

	public void setUninstallStartTime(String uninstallStartTime) {
		this.uninstallStartTime = uninstallStartTime;
	}

	public String getExecStartTime() {
		return execStartTime;
	}

	public void setExecStartTime(String execStartTime) {
		this.execStartTime = execStartTime;
	}

	public String getExecStopTime() {
		return execStopTime;
	}

	public void setExecStopTime(String execStopTime) {
		this.execStopTime = execStopTime;
	}

	public String getInstallTime() {
		return installTime;
	}

	public void setInstallTime(String installTime) {
		this.installTime = installTime;
	}

	public String getColdStartTime() {
		return coldStartTime;
	}

	public void setColdStartTime(String coldStartTime) {
		this.coldStartTime = coldStartTime;
	}

	public boolean isCoverInstall() {
		return coverInstall;
	}

	public void setCoverInstall(boolean coverInstall) {
		this.coverInstall = coverInstall;
	}

	public boolean isInstall() {
		return install;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public boolean isUninstall() {
		return uninstall;
	}

	public void setUninstall(boolean uninstall) {
		this.uninstall = uninstall;
	}
	
	public boolean isLaunch(){
		return launch;
	}
	
	public void setLaunch(boolean launch){
		this.launch = launch;
	}
	
	public void setUdid(String udid){
		this.udid=udid;
	}
	public void setPort(String port){
		this.port=port;
	}
	public void setAppPackage(String appPackage){
		this.appPackage=appPackage;
	}
	public void setAppActivity(String appActivity){
		this.appActivity=appActivity;
	}
	public void setAppPath(String appPath){
		this.appPath=appPath;
	}
	public void setIsOnline(boolean isOnline){
		this.isOnline=isOnline;
	}
	
	public String getUdid(){
		return udid;
	}
	public String getPort(){
		return port;
	}
	public String getAppPackage(){
		return appPackage;
	}
	public String getAppActivity(){
		return appActivity;
	}
	public String getAppPath(){
		return appPath;
	}
	public boolean getIsOnline(){
		return isOnline;
	}

	public String getMaxCpuRate() {
		return maxCpuRate;
	}

	public void setMaxCpuRate(String maxCpuRate) {
		this.maxCpuRate = maxCpuRate;
	}

	public String getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(String maxMem) {
		this.maxMem = maxMem;
	}

	public String getMaxNetwork() {
		return maxNetwork;
	}

	public void setMaxNetwork(String network) {
		this.maxNetwork = network;
	}

	public String getMaxBatteryTemp() {
		return maxBatteryTemp;
	}

	public void setMaxBatteryTemp(String power) {
		this.maxBatteryTemp = power;
	}
}
