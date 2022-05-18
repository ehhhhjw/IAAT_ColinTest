package bean;

import util.AddressUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DeviceStat {
	{
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(AddressUtil.getIpAddressConfigurationPath()));
			pcIp = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pcPort = "9090";
	}
    private String udid;
    private int stat;	//3000:ready 3001:busy 3002:offline
    private String brand;
    private String model;
    private String system;
    private int cpu;
    private int mem;
    private String pcIp;
    private String pcPort;
    public static int IDLE = 3000;
    public static int BUSY = 3001;
    public static int DELETE = 3002;
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getMem() {
		return mem;
	}
	public void setMem(int mem) {
		this.mem = mem;
	}
}
