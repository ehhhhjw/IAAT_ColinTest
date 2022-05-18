package mergeTest.AppiumNode;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class JsonReader {
    JSONObject jsonObject;
    public JsonReader(File f){
        try {
            jsonObject = JSONObject.parseObject(FileUtils.readFileToString(f));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("读取json文件失败:"+f.getAbsolutePath());
        }
    }
    public JsonReader(String fileName){
        File f = new File(fileName);
        new JsonReader(f);
    }

    //只读一层
    public String getActivity(){
        String res = "";
        String att = jsonObject.getString("activity");
        String []atts = att.split(" ");
        for(int i =atts.length-1;i>=0;i--){
            if(atts[i].contains("/")){
                res = atts[i].split("\\/")[1];
                break;
            }
        }
        return res;
    }
    public int getX(){
        int att = jsonObject.getInteger("x");
        return att;
    }
    public int getY(){
        int att = jsonObject.getInteger("y");
        return att;
    }
    public WidgetInfo getWidgetInfo(){
        jsonObject = jsonObject.getJSONObject("node_attrib");
        String id = jsonObject.getString("resource-id");
        String text = jsonObject.getString("text");
        String className = jsonObject.getString("class");
        String desc = jsonObject.getString("content-desc");
        String packageName = jsonObject.getString("package");
        int index = jsonObject.getString("index").equals("0")?-1:Integer.parseInt(jsonObject.getString("index"));
        boolean enabled = jsonObject.getString("enabled").equals("true") ? true : false;
        boolean checkable = jsonObject.getString("checkable").equals("true") ? true : false;
        boolean checked = jsonObject.getString("checked").equals("true") ? true : false;
        boolean clickable = jsonObject.getString("clickable").equals("true") ? true : false;
        boolean focusable = jsonObject.getString("focusable").equals("true") ? true : false;
        boolean focused = jsonObject.getString("focused").equals("true") ? true : false;
        boolean longClickable = jsonObject.getString("long-clickable").equals("true") ? true : false;
        boolean scrollable = jsonObject.getString("scrollable").equals("true") ? true : false;
        boolean selected = jsonObject.getString("selected").equals("true") ? true : false;
        String Xpath = jsonObject.getString("xpath");
        String bounds = jsonObject.getString("bounds");
        boolean password = jsonObject.getString("password").equals("true") ? true : false;

        WidgetInfo widgetInfo = new WidgetInfo(text,className,id,desc,packageName);
        widgetInfo.setIndex(index);widgetInfo.setEnabled(enabled);widgetInfo.setCheckable(checkable);
        widgetInfo.setChecked(checked);widgetInfo.setClickable(clickable);widgetInfo.setFocusable(focusable);
        widgetInfo.setFocused(focused);widgetInfo.setLongClickable(longClickable);widgetInfo.setScrollable(scrollable);
        widgetInfo.setSelected(selected);widgetInfo.setXpath(Xpath);widgetInfo.setPassword(password);

        widgetInfo.setBehavior("click");

        return widgetInfo;
    }
    public static void main(String args[]){
        File f = new File("C:\\Users\\scarlet\\Desktop\\P7C0218510004542_113\\step4\\info.json");
        JsonReader jr = new JsonReader(f);
        System.out.println(jr.getActivity());
        System.out.println(jr.getX());
        System.out.println(jr.getY());
    }
}
