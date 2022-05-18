package model;

public class Action {
    private String timeBeforeAction;
    private String timeAfterAction;
    private String type;
    private String message;
    private String activityBeforeAction;
    private String activityAfterAction;
    
    public Action(String timeBeforeAction, String timeAfterAction, String type,
			String message, String activityBeforeAction,
			String activityAfterAction) {
		super();
		this.timeBeforeAction = timeBeforeAction;
		this.timeAfterAction = timeAfterAction;
		this.type = type;
		this.message = message;
		this.activityBeforeAction = activityBeforeAction;
		this.activityAfterAction = activityAfterAction;
	}

	public String getTimeBeforeAction() {
		return timeBeforeAction;
	}

	public void setTimeBeforeAction(String timeBeforeAction) {
		this.timeBeforeAction = timeBeforeAction;
	}

	public String getTimeAfterAction() {
		return timeAfterAction;
	}

	public void setTimeAfterAction(String timeAfterAction) {
		this.timeAfterAction = timeAfterAction;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActivityBeforeAction() {
        return activityBeforeAction;
    }

    public void setActivityBeforeAction(String activityBeforeAction) {
        this.activityBeforeAction = activityBeforeAction;
    }

    public String getActivityAfterAction() {
        return activityAfterAction;
    }

    public void setActivityAfterAction(String activityAfterAction) {
        this.activityAfterAction = activityAfterAction;
    }
}
