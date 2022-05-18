package mergeTest;

public interface ExtraServiceInterface {
	String isInstall(String appPath, String udid);
	String isUninstall(String packName, String udid);
	String getAndroidVersion(String udid);
	String getMobileModel(String udid);
}
