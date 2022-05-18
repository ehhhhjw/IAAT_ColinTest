package mergeTest.AppiumNode;

import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.ArrayList;

public class WidgetInfo {
    private static String MOOCTEST_DIR = "mooctest" + File.separator;
    int index = -1;
    String text = "";
    String className = "";
    String packageName = "";
    String desc = "";
    Boolean checkable;
    Boolean checked;
    Boolean clickable;
    Boolean enabled;
    Boolean focusable;
    Boolean focused;
    Boolean scrollable;
    Boolean longClickable;
    Boolean password;
    Boolean selected;
    Boolean displayed;
    String bounds = "";
    String id = "";
    int instance;
    String activity = "";
    String behavior = "";
    ArrayList<String> behaviorArgs = new ArrayList<>();
    String Xpath="";

    public String getXpath() {
        return Xpath;
    }

    public void setXpath(String xpath) {
        Xpath = xpath;
    }

    public boolean isNull(){
        if(text.equals("")&&className.equals("")&&packageName.equals("")&&desc.equals("")&&behavior.equals("")&&activity.equals("")){
            return true;
        }
        return false;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }


    public ArrayList<String> getBehaviorArgs() {
        return behaviorArgs;
    }

    public void setBehaviorArgs(ArrayList<String> behaviorArgs) {
        this.behaviorArgs = behaviorArgs;
    }


    public String getActivity() {
        return activity;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getCheckable() {
        return checkable;
    }

    public void setCheckable(Boolean checkable) {
        this.checkable = checkable;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getClickable() {
        return clickable;
    }

    public void setClickable(Boolean clickable) {
        this.clickable = clickable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getFocusable() {
        return focusable;
    }

    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    public Boolean getFocused() {
        return focused;
    }

    public void setFocused(Boolean focused) {
        this.focused = focused;
    }

    public Boolean getScrollable() {
        return scrollable;
    }

    public void setScrollable(Boolean scrollable) {
        this.scrollable = scrollable;
    }

    public Boolean getLongClickable() {
        return longClickable;
    }

    public void setLongClickable(Boolean longClickable) {
        this.longClickable = longClickable;
    }

    public Boolean getPassword() {
        return password;
    }

    public void setPassword(Boolean password) {
        this.password = password;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public Boolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(Boolean displayed) {
        this.displayed = displayed;
    }

    public WidgetInfo() {

    }
    public WidgetInfo(String text, String className, String id,
                      String desc, String packageName){
        super();
        this.text = text;
        this.className = className;
        this.desc = desc;
        this.id = id;
        this.packageName = packageName;

    }
    public WidgetInfo(String text, String className, String id,
                      String desc, Boolean enabled, Boolean checkable,
                      Boolean checked, Boolean clickable,
                      Boolean focusable, Boolean focused, Boolean longClickable, Boolean scrollable,
                      Boolean selected, Boolean displayed) {
        super();
        this.text = text;
        this.className = className;
        this.desc = desc;
        this.checkable = checkable;
        this.checked = checked;
        this.clickable = clickable;
        this.enabled = enabled;
        this.focusable = focusable;
        this.focused = focused;
        this.scrollable = scrollable;
        this.longClickable = longClickable;
        this.selected = selected;
        this.id = id;
        this.displayed = displayed;
    }

    public WidgetInfo(String text, String className,
                      String id, String desc) {
        super();
        this.text = text;
        this.className = className;
        this.desc = desc;
        this.id = id;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setBehavior(String behavior, ArrayList<String> behaviorArgs) {
        this.behavior = behavior;
        this.behaviorArgs = behaviorArgs;
    }

    public static WidgetInfo getElementInfo(WebElement e) {
        WidgetInfo tempWInfo;
        try {
            String id = e.getAttribute("resourceId") == null ? "" : e.getAttribute("resourceId");
            String text = e.getAttribute("text") == null ? "" : e.getAttribute("text");
            String className = e.getAttribute("className") == null ? "" : e.getAttribute("className");
            String desc = e.getAttribute("contentDescription") == null ? "" : e.getAttribute("contentDescription");
            boolean enabled = e.getAttribute("enabled").equals("true") ? true : false;
            boolean checkable = e.getAttribute("checkable").equals("true") ? true : false;
            boolean checked = e.getAttribute("checked").equals("true") ? true : false;
            boolean clickable = e.getAttribute("clickable").equals("true") ? true : false;
            boolean focusable = e.getAttribute("focusable").equals("true") ? true : false;
            boolean focused = e.getAttribute("focused").equals("true") ? true : false;
            boolean longClickable = e.getAttribute("longClickable").equals("true") ? true : false;
            boolean scrollable = e.getAttribute("scrollable").equals("true") ? true : false;
            boolean selected = e.getAttribute("selected").equals("true") ? true : false;
            boolean displayed = e.getAttribute("displayed").equals("true") ? true : false;

            tempWInfo = new WidgetInfo(text, className, id, desc, enabled,
                    checkable, checked, clickable, focusable, focused, longClickable,
                    scrollable, selected, displayed);

            return tempWInfo;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public void recordElement(String FileName) {
        File f = new File(FileName);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            bw.write("behavior:" + behavior + "\n");
            bw.write("arg:" + behaviorArgs + "\n");
            bw.write("id:" + id + "\n");
            bw.write("text:" + text + "\n");
            bw.write("className:" + className + "\n");
            bw.write("desc:" + desc + "\n");
            bw.write("enabled:" + enabled + "\n");
            bw.write("checkable:" + checkable + "\n");
            bw.write("checked:" + checked + "\n");
            bw.write("clickable:" + clickable + "\n");
            bw.write("focusable:" + focusable + "\n");
            bw.write("focused:" + focused + "\n");
            bw.write("longClickable:" + longClickable + "\n");
            bw.write("scrollable:" + scrollable + "\n");
            bw.write("selected:" + selected + "\n");
            bw.write("displayed:" + displayed + "\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getNowXML() {
        String timeName = getThisTimeData();
        File[] fs = new File(getThisTimeData() + File.separator + "XML").listFiles();
        int maxNum = 0;
        for (File f : fs) {
            if (f.getName().endsWith("xml")) {
                maxNum = Math.max(maxNum, Integer.parseInt(f.getName().split("\\.")[0]));
            }
        }
        return timeName + File.separator + "XML" + File.separator + maxNum + ".xml";
    }

    private static String getThisTimeData() {
        File fs[] = new File(MOOCTEST_DIR).listFiles();
        long max = 0;
        for (File f : fs) {
            if (f.isDirectory()) {
                max = Math.max(max, Long.parseLong(f.getName()));
            }
        }
        String timeName = MOOCTEST_DIR + "" + String.valueOf(max);
        return timeName;
    }

    public void simpleOutput() {
        System.out.println(text + " " + className + " " + id + " " + desc);
    }

    public static void getWholePath(String dirName) {
        File XMLDir = new File(dirName + File.separator + "XML");
        ArrayList<WidgetInfo> res = new ArrayList();
        String XMLDirPath = XMLDir.getAbsolutePath() + File.separator;
        int index = 0;
        while (true) {
            File f = new File(XMLDirPath + index + ".txt");
            if (!f.exists()) {
                break;
            }
            String activity = File2Activity(XMLDirPath, index);
            WidgetInfo wi = File2WidgetInfo(XMLDirPath, index);
            wi.setActivity(activity);
            res.add(wi);
            index++;
        }
        outPutWholePath(res, dirName);
    }

    public static void getWholePath() {
        File XMLDir = new File(getThisTimeData() + File.separator + "XML");
        ArrayList<WidgetInfo> res = new ArrayList();
        String XMLDirPath = XMLDir.getAbsolutePath() + File.separator;
        int index = 0;
        while (true) {
            File f = new File(XMLDirPath + index + ".txt");
            if (!f.exists()) {
                break;
            }
            String activity = File2Activity(XMLDirPath, index);
            WidgetInfo wi = File2WidgetInfo(XMLDirPath, index);
            wi.setActivity(activity);
            res.add(wi);
            index++;
        }
        outPutWholePath(res);
    }

    public static String File2Activity(String XMLDriPath, int index) {
        File xml = new File(XMLDriPath + index + "-activity.txt");
        String activity = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(xml));
            activity = br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return activity;
    }

    public static WidgetInfo File2WidgetInfo(String XMLDriPath, int index) {
        File widgetFile = new File(XMLDriPath + index + ".txt");
        File XPathFile = new File(XMLDriPath + index + "-XPath.txt");
        WidgetInfo res = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(widgetFile));
            String behavior = br.readLine().split(":")[1];
            String args = br.readLine().split(":")[1];
            ArrayList<String> list = new ArrayList();
            if (args == null || args.equals("null")) {
                list = null;
            } else {
                args = args.substring(1, args.length() - 1);
                String[] splits = args.split(",");
                for (int i = 0; i < splits.length; i++) {
                    list.add(splits[i].trim());
                }
            }
            String line;
            String[] lines;
            line = br.readLine();
            lines = line.split(":");
            if (lines.length <= 1) {
                line = null;
            } else {
                line = line.substring(line.indexOf(":") + 1, line.length());
            }
            String id = line == null ? "" : line;
            line = br.readLine();
            lines = line.split(":");
            if (lines.length <= 1) {
                line = null;
            } else {
                line = line.substring(line.indexOf(":") + 1, line.length());
            }
            String text = line == null ? "" : line;
            line = br.readLine();
            lines = line.split(":");
            if (lines.length <= 1) {
                line = null;
            } else {
                line = line.substring(line.indexOf(":") + 1, line.length());
            }
            String className = line == null ? "" : line;
            line = br.readLine();
            lines = line.split(":");
            if (lines.length <= 1) {
                line = null;
            } else {
                line = line.substring(line.indexOf(":") + 1, line.length());
            }
            String desc = line == null ? "" : line;
            boolean enabled = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean checkable = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean checked = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean clickable = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean focusable = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean focused = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean longClickable = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean scrollable = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean selected = br.readLine().split(":")[1].equals("true") ? true : false;
            boolean displayed = br.readLine().split(":")[1].equals("true") ? true : false;
            res = new WidgetInfo(text, className, id, desc, enabled,
                    checkable, checked, clickable, focusable, focused, longClickable,
                    scrollable, selected, displayed);
            res.setBehavior(behavior, list);
            if (id == null || id.equals("") || id.equals("null")) {
                if (text == null || text.equals("") || text.equals("null")) {
                    if (desc == null || desc.equals("") || desc.equals("null")) {
                        if (XPathFile.exists()) {
                            BufferedReader br2 = new BufferedReader(new FileReader(XPathFile));
                            String originCommand = br2.readLine();
                            if (originCommand.contains("@package")) {
                                String temp = originCommand.split("@package=")[1];
                                temp = temp.split("'")[1];
                                res.setPackageName(temp);
                            }
                            if (originCommand.contains("@index")) {
                                String temp = originCommand.split("@index=")[1];
                                temp = temp.split("'")[1];
                                res.setIndex(Integer.parseInt(temp));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    public void recordWholeElement(String FileName) {
        File f = new File(FileName);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(f, true));
            bw.write("activity:" + activity + "\n");
            bw.write("behavior:" + behavior + "\n");
            bw.write("arg:" + (behaviorArgs == null ? "" : behaviorArgs) + "\n");
            bw.write("index:" + index + "\n");
            bw.write("id:" + id + "\n");
            bw.write("text:" + text + "\n");
            bw.write("className:" + className + "\n");
            bw.write("desc:" + desc + "\n");
            bw.write("packageName:" + packageName + "\n");
            bw.write("enabled:" + enabled + "\n");
            bw.write("checkable:" + checkable + "\n");
            bw.write("checked:" + checked + "\n");
            bw.write("clickable:" + clickable + "\n");
            bw.write("focusable:" + focusable + "\n");
            bw.write("focused:" + focused + "\n");
            bw.write("longClickable:" + longClickable + "\n");
            bw.write("scrollable:" + scrollable + "\n");
            bw.write("selected:" + selected + "\n");
            bw.write("displayed:" + displayed + "\n");
            bw.write("\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void outPutWholePath(ArrayList<WidgetInfo> res) {
        for (int i = 0; i < res.size(); i++) {
            WidgetInfo wi = res.get(i);
            String txtName = getThisTimeData() + File.separator + "res.txt";
            wi.recordWholeElement(txtName);
        }
    }

    public static void outPutWholePath(ArrayList<WidgetInfo> res, String path) {
        for (int i = 0; i < res.size(); i++) {
            WidgetInfo wi = res.get(i);
            String txtName = path + File.separator + "res.txt";
            wi.recordWholeElement(txtName);
        }
    }

    public boolean equals(WidgetInfo next) {
        WidgetInfo p1 = this;
        WidgetInfo p2 = next;
        if(p1==null&&p2==null) return true;
        if(p1.isNull()&&p2.isNull()) return true;
        if (p1.getActivity().equals(p2.getActivity()) && p1.getId().equals(p2.getId()) && p1.getClassName().equals(p2.getClassName())
                && p1.getDesc().equals(p2.getDesc()) && p1.getText().equals(p2.getText()) && p1.getPackageName().equals(p2.getPackageName())
                && p1.getIndex() == p2.getIndex()) {
            if(p1.getBehavior().equals(p2.getBehavior())){
                ArrayList<String> p1Args = p1.getBehaviorArgs();
                ArrayList<String> p2Args = p2.getBehaviorArgs();
                if(p1Args==null&&p2Args==null){
                    return true;
                }
                if(p1Args.size() == p2Args.size()){
                    for(int i =0;i<p1Args.size();i++){
                        if(!p1Args.get(i).equals(p2Args.get(i))) return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String args[]) {
        getWholePath("C:\\Users\\jon\\Desktop\\1574660320774");
        getWholePath("C:\\Users\\jon\\Desktop\\1574658681331");
        ArrayList<WidgetInfoNode> resList = widgetMergeTools.getResList("C:\\Users\\jon\\Desktop\\test");
        widgetMergeTools.resList2Pic(resList);
        ArrayList<WidgetInfoNode> nodeList = widgetMergeTools.getAllPath(resList);
    }
}