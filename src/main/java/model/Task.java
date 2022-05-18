package model;

/**
 * Created by homer on 16-9-18.
 */
public class Task {
    /*
 * status:0-Waiting,1-Running,2-Finished
 */
    int status;
    int executeTime = 0;
    int taskID;
    long appid;
    String app,uploadTime;
    private String scriptName;
    private boolean hasScript = false;
    private boolean hasPrepositionScript = false;
    private boolean hasPrepositionScriptGroup = false;
    private boolean hasFullGroup = false;
    private String prepositionScriptName;

    public boolean isHasFullGroup(){
        return hasFullGroup;
    }
    public boolean isHasPrepositionScript() {
        return hasPrepositionScript;
    }
    public boolean isHasPrepositionScriptGroup(){
        return hasPrepositionScriptGroup;
    }
    public void setHasPrepositionScript(boolean hasPrepositionScript) {
        this.hasPrepositionScript = hasPrepositionScript;
    }
    public void setHasPrepositionScriptGroup(boolean hasPrepositionScriptGroup) {
        this.hasPrepositionScriptGroup = hasPrepositionScriptGroup;
    }

    public void setHasFullGroup(boolean hasFullGroup) {
        this.hasFullGroup = hasFullGroup;
    }

    public String getPrepositionScriptName() {
        return prepositionScriptName;
    }

    public void setPrepositionScriptName(String prepositionScriptName) {
        this.prepositionScriptName = prepositionScriptName;
    }
    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public boolean isHasScript() {
        return hasScript;
    }

    public void setHasScript(boolean hasScript) {
        this.hasScript = hasScript;
    }

    public long getAppid() {
        return appid;
    }
    public void setAppid(long appid) {
        this.appid = appid;
    }
    public int getTaskID() {
        return taskID;
    }
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getApp() {
        return app;
    }
    public void setApp(String app) {
        this.app = app;
    }
    public String getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
	public int getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(int executeTime) {
		this.executeTime = executeTime;
	}
}
