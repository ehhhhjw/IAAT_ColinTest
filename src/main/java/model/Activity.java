package model;
import java.util.List;

public class Activity {
	private String activityName;
	private String fatherActivity;
	private int hash;
	private List<Component> comList;
	private boolean dirtyWord;	//标示该页面组件是否已遍历完
	public Activity(){
		activityName = null;
		fatherActivity = null;
		hash = 0;
		comList = null;
		dirtyWord = false;
	}
	public Activity(String activityName, String fatherActivity, int hash, List<Component> comList) {
		super();
		this.activityName = activityName;
		this.fatherActivity = fatherActivity;
		this.hash = hash;
		this.comList = comList;
		this.dirtyWord = false;
	}
	public void setActivityName(String activityName){
		this.activityName = activityName;
	}
	public void setFatherActivity(String fatherActivity){
		this.fatherActivity = fatherActivity;
	}
	public void setHash(int hash){
		this.hash = hash;
	}
	public void setComList(List<Component> comList){
		this.comList = comList;
	}
	public void setDirtyWord(boolean dirtyWord){
		this.dirtyWord = dirtyWord;
	}
	public String getActivityName(){
		return activityName;
	}
	public String getFatherActivity(){
		return fatherActivity;
	}
	public int getHash(){
		return hash;
	}
	public List<Component> getComList(){
		return comList;
	}
	public boolean getDirtyWord(){
		return dirtyWord;
	}
}