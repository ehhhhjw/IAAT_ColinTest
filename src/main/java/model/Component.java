package model;


public class Component {

	private String locator;
	private String index;
	private String text;
	private String resource_id;
	private String classname;
	private String packagename;
	private String content_desc;
	private String checkable;
	private String checked;
	private String clickable;
	private String enabled;
	private String focusable;
	private String focused;
	private String scrollable;
	private String long_clickable;
	private String password;
	private String selected;
	private String bounds;
	private boolean hasBeenTested = false;
	private Component fatherComponent;
	
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Component)) return false;
        Component com = (Component)obj;
        return (com.locator.equals(this.locator) && com.classname.equals(this.classname) && com.packagename.equals(this.packagename));
    }
    
    @Override
    public int hashCode() {
        int result = bounds.hashCode();
        return result;
    }
	public Component getFatherComponent() {
		return fatherComponent;
	}
	public void setFatherComponent(Component Component) {
		this.fatherComponent = Component;
	}
	public boolean isHasBeenTested() {
		return hasBeenTested;
	}
	public void setHasBeenTested(boolean hasBeenTested) {
		this.hasBeenTested = hasBeenTested;
	}
	public String getLocator() {
		return locator;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public String getContent_desc() {
		return content_desc;
	}
	public void setContent_desc(String content_desc) {
		this.content_desc = content_desc;
	}
	public String getCheckable() {
		return checkable;
	}
	public void setCheckable(String checkable) {
		this.checkable = checkable;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getClickable() {
		return clickable;
	}
	public void setClickable(String clickable) {
		this.clickable = clickable;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getFocusable() {
		return focusable;
	}
	public void setFocusable(String focusable) {
		this.focusable = focusable;
	}
	public String getFocused() {
		return focused;
	}
	public void setFocused(String focused) {
		this.focused = focused;
	}
	public String getScrollable() {
		return scrollable;
	}
	public void setScrollable(String scrollable) {
		this.scrollable = scrollable;
	}
	public String getLong_clickable() {
		return long_clickable;
	}
	public void setLong_clickable(String long_clickable) {
		this.long_clickable = long_clickable;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getBounds() {
		return bounds;
	}
	public void setBounds(String bounds) {
		this.bounds = bounds;
	}
}