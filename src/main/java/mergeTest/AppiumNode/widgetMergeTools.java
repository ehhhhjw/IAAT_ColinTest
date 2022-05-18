package mergeTest.AppiumNode;

import mergeTest.DFSRunner;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.*;

public class widgetMergeTools {
    //将执行脚本生成的行为文件生成控件信息列表（）
    public static ArrayList<WidgetInfo> resFile2Widget(File f){
        ArrayList<WidgetInfo> res = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            WidgetInfo widgetInfo;
            String firstLine;
            String line;
            String[] lines;
            while((firstLine = br.readLine())!=null) {
                String activity = firstLine.split(":")[1];
                String behavior = br.readLine().split(":")[1];

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String args = line;
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

                int index = Integer.parseInt(br.readLine().split(":")[1]);

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String id = line == null ? "" : line;

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String text = line == null ? "" : line;

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String className = line == null ? "" : line;

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String desc = line == null ? "" : line;

                line = br.readLine();
                lines = line.split(":");
                if (lines.length <= 1) {
                    line = null;
                } else {
                    line = line.substring(line.indexOf(":")+1,line.length());
                }
                String packageName = line == null ? "" : line;

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

                widgetInfo = new WidgetInfo(text, className, id, desc, enabled,
                        checkable, checked, clickable, focusable, focused, longClickable,
                        scrollable, selected, displayed);
                widgetInfo.setBehavior(behavior, list);
                widgetInfo.setActivity(activity);
                widgetInfo.setId(id);
                widgetInfo.setPackageName(packageName);
                widgetInfo.setIndex(index);
                res.add(widgetInfo);
                br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    //返回一个不带边的List
    public static ArrayList<WidgetInfoNode> getNormalList(ArrayList<WidgetInfo> widgetInfoList){
        ArrayList<WidgetInfoNode> res = new ArrayList();
        for(int i =0;i<widgetInfoList.size();i++){
            res.add(new WidgetInfoNode(widgetInfoList.get(i)));
        }
        return res;
    }
    //为控件信息列表加上指向，生成图，存放在列表内
    public static ArrayList<WidgetInfoNode> getList(ArrayList<WidgetInfo> widgetInfoList){
        ArrayList<WidgetInfoNode> res = new ArrayList();
        WidgetInfoNode head = new WidgetInfoNode();
        WidgetInfoNode nowNode;
        for(int i = 0;i<widgetInfoList.size();i++) {
            nowNode = new WidgetInfoNode(widgetInfoList.get(i));
            head.setNext(nowNode);
            nowNode.setFather(head);
            res.add(head);
            head = nowNode;
        }
        res.add(head);
        return res;
    }
    //合并两张带边的图，并生成一张新图
    //有问题 TODO
    public static ArrayList<WidgetInfoNode> mergeList(ArrayList<WidgetInfoNode> winList1, ArrayList<WidgetInfoNode> winList2){
        ArrayList<WidgetInfoNode> res = winList1;
        if(winList1.size()==0){
            WidgetInfoNode head = new WidgetInfoNode();
            res.add(head);
            for(int i = 0;i<winList2.size();i++){
                WidgetInfoNode wInfoNode = winList2.get(i);
                head.addNext(wInfoNode);
                wInfoNode.addFather(head);
                head = wInfoNode;
                res.add(head);
            }

            return res;
        }
        WidgetInfoNode head = winList1.get(0);
        //加入所有路径中的所有节点，同时去重
        for(int i =0;i<winList2.size();i++){
            WidgetInfoNode wInfoNode = winList2.get(i);
            //在原本list里查重
            if(!hasInfo(res,wInfoNode)) {
                if (i == 0) {
                    head.next.add(wInfoNode);
                }
                res.add(wInfoNode);
            }
        }
        //加入所有路径关系
        //扫描所有路线，w1和w2指向前后用来添加关系
        try {
            for (int i = 0; i < winList2.size() - 1; i++) {
                WidgetInfoNode w1 = winList2.get(i);
                WidgetInfoNode w2 = winList2.get(i + 1);
                WidgetInfoNode wn1 = findNode(res, w1);
                WidgetInfoNode wn2 = findNode(res, w2);
                wn1.addNext(wn2);
            }
        }catch(Exception e){
            System.out.println(e.getStackTrace());
            System.out.println("有东西为null?");
        }

        //逆向加入父关系
        for(int i =0;i<res.size();i++){
            WidgetInfoNode nowNode = res.get(i);
            ArrayList<WidgetInfoNode> next = nowNode.next;
            for(int j =0;j<next.size();j++){
                WidgetInfoNode nowNext = next.get(j);
                nowNext.addFather(nowNode);
            }
        }
        return res;
    }
    public static void getPathList(ArrayList<ArrayList<WidgetInfo>> pathList,File f){
        if(f.isDirectory()){
            File []fs = f.listFiles();
            for(File file:fs){
                getPathList(pathList,file);
            }
        }else{
            if(f.getName().contains("res")&&f.getName().endsWith(".txt")){
                pathList.add(resFile2Widget(f));
            }
        }
    }
    public static boolean hasInfo(ArrayList<WidgetInfoNode> list , WidgetInfo wInfo){
        for(int i = 0;i<list.size();i++){
            if(list.get(i).equals(wInfo)){
                return true;
            }
        }
        return false;
    }
    public static boolean hasInfo(ArrayList<WidgetInfoNode> list , WidgetInfoNode wInfo){
        if(list.size()==0) return false;
        for(int i = 0;i<list.size();i++){
            if(list.get(i).equals(wInfo)){
                return true;
            }
        }
        return false;
    }
    public static WidgetInfoNode findNode(ArrayList<WidgetInfoNode> list , WidgetInfo wInfo){
        for(int i =0;i<list.size();i++){
            WidgetInfo temp = list.get(i).widgetInfo;
            if(temp.equals(wInfo)){
                return list.get(i);
            }
        }
        return null;
    }
    public static WidgetInfoNode findNode(ArrayList<WidgetInfoNode> list , WidgetInfoNode wInfoNode){
        for(int i =0;i<list.size();i++){
            WidgetInfo temp = list.get(i).widgetInfo;
            if(temp.equals(wInfoNode.widgetInfo)){
                return list.get(i);
            }
        }
        return null;
    }
    public static ArrayList<WidgetInfoNode> getResList(String dataFile) {
        //读所有的文件获取所有列表
        File f = new File(dataFile);
        ArrayList<ArrayList<WidgetInfo>> pathList = new ArrayList<>();
        getPathList(pathList,f);
        return getResList(pathList);
    }
    //读实验数据中的所有res file，获取唯一的 winList
    public static ArrayList<WidgetInfoNode> getResList(ArrayList<ArrayList<WidgetInfo>> pathList){
        ArrayList<WidgetInfoNode> res = new ArrayList<>();
        //头节点
        WidgetInfoNode head = new WidgetInfoNode();
        //加入所有路径中的所有节点，同时去重
        for(int i =0;i<pathList.size();i++){
            ArrayList<WidgetInfo> path = pathList.get(i);
            for(int j =0;j<path.size();j++){
                WidgetInfo wInfo = path.get(j);
                //在原本list里查重
                if(!hasInfo(res,wInfo)) {
                    WidgetInfoNode wInfoNode = new WidgetInfoNode(wInfo);
                    if (j == 0) {
                        head.next.add(wInfoNode);
                    }
                    res.add(wInfoNode);
                }
            }
        }
        res.add(head);
        //加入所有路径关系
        //扫描所有路线，w1和w2指向前后用来添加关系
        for(int i =0;i<pathList.size();i++){
            ArrayList<WidgetInfo> path = pathList.get(i);
            for(int j =0;j<path.size()-1;j++){
                WidgetInfo w1 = path.get(j);
                WidgetInfo w2 = path.get(j+1);
                WidgetInfoNode wn1 = findNode(res,w1);
                WidgetInfoNode wn2 = findNode(res,w2);
                wn1.addNext(wn2);
            }
        }
        //逆向加入父关系
        for(int i =0;i<res.size();i++){
            WidgetInfoNode nowNode = res.get(i);
            ArrayList<WidgetInfoNode> next = nowNode.next;
            for(int j =0;j<next.size();j++){
                WidgetInfoNode nowNext = next.get(j);
                nowNext.addFather(nowNode);
            }
        }
        return res;
    }

    //找到图中的从初始状态到达所有activity的路径
    public static ArrayList<WidgetInfoNode> getAllPath(ArrayList<WidgetInfoNode> widgetInfoNodesList){
        ArrayList<WidgetInfoNode> res = new ArrayList<>();
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<WidgetInfoNode> isVisited = new ArrayList<>();
        WidgetInfoNode head = widgetInfoNodesList.get(widgetInfoNodesList.size()-1);
        //开始DFS搜
        Queue<WidgetInfoNode> queue = new LinkedList<>();
        queue.add(head);
        isVisited.add(head);
        while(!queue.isEmpty()){
            WidgetInfoNode nowNode = queue.poll();
            ArrayList<WidgetInfoNode> next = nowNode.next;
            for(int i =0;i<next.size();i++){
                WidgetInfoNode nowNext = next.get(i);
                if(!hasInfo(isVisited,nowNext)) {
                    //加入队列
                    queue.add(nowNext);
                    isVisited.add(nowNext);
                    //判断是否保存
                    if (nowNode.widgetInfo!= null) {
                        //前一个操作的Activity为A1，后一个操作为A2，操作1进行后到达A2，保留操作1，和A2
                        if(!nowNode.widgetInfo.getActivity().equals(nowNext.widgetInfo.getActivity())){
                            if(!activities.contains(nowNext.widgetInfo.getActivity())){
                                activities.add(nowNext.widgetInfo.getActivity());
                                res.add(nowNode);
                            }
                        }
                    }
                }
            }
        }
        return res;
    }
    //获取最短路径
    public static Stack<WidgetInfoNode> getPath(int depth, int maxDepth, Stack<WidgetInfoNode> path, WidgetInfoNode node) throws Exception{
        path.add(node);
        if(node.widgetInfo.isNull()) {
            return path;
        }
        if(depth==maxDepth) {
            return null;
        }
        else{
            int size = node.father.size();
            Stack<WidgetInfoNode> res = getPath(depth+1,maxDepth,(Stack<WidgetInfoNode>)path.clone(),node.father.get(0));
            if(res!=null){
                maxDepth = Math.min(res.size()+1,maxDepth);
            }
            for(int i =1;i<size;i++){

                Stack<WidgetInfoNode> temp = getPath(depth+1,maxDepth,(Stack<WidgetInfoNode>)path.clone(),node.father.get(i));
                if(res!=null&&temp!=null&&res.size()>temp.size()){
                    res = temp;
                    maxDepth = res.size()+1;
                }
                if(res==null&&temp!=null){
                    res = temp;
                }
            }
            return res;
        }
    }
    //运行其中一条路径
    public static void runOnePath(int maxSize, WidgetInfoNode node, AppiumDriver driver, DFSRunner script){
        WebElement we;
        String using;
        try {
            Stack<WidgetInfoNode> path = new Stack<>();
            path = getPath(0, maxSize, new Stack<WidgetInfoNode>(), node);
            path.pop();
            while (!path.empty()) {
                Thread.sleep(3000);
                WidgetInfoNode wInfoNode= path.pop();
                wInfoNode.setVisited(true);
                WidgetInfo wInfo = wInfoNode.widgetInfo;
                //先定位
                String id = wInfo.getId();
                String text = wInfo.getText();
                String className = wInfo.getClassName();
                String desc = wInfo.getDesc();
                String packageName = wInfo.getPackageName();
                int index = wInfo.getIndex();
                using = "//" + className + "[";
                if (!text.equals("") && text != null) {
                    using += "@text='" + text + "'";
                }
                if (!id.equals("") && id != null) {
                    if (using.contains("@")) {
                        using += " and ";
                    }
                    using += "@resource-id='" + id + "'";
                }
                if (!desc.equals("") && desc != null) {
                    if (using.contains("@")) {
                        using += " and ";
                    }
                    using += "@content-desc='" + desc + "'";
                }
                if (!packageName.equals("") && packageName != null) {
                    if (using.contains("@")) {
                        using += " and ";
                    }
                    using += "@package='" + packageName + "'";
                }
                if (index != -1) {
                    if (using.contains("@")) {
                        using += " and ";
                    }
                    using += "@index='" + index + "'";
                }

                using += "]";
                System.out.println(using);
                try {
                    if (wInfo.behavior.equals("click")) {
                        we = driver.findElementByXPath(using);
                        script.openExecuteClick(we);
                    } else if (wInfo.behavior.equals("sendKeys")) {
                        we = driver.findElementByXPath(using);
                        script.openExectureInput(wInfo.behaviorArgs.get(0),we);
                    } else if (wInfo.behavior.equals("sendKeyEvent")) {
                        script.openExecuteKeyEvent(Integer.parseInt(wInfo.behaviorArgs.get(0)));
                    } else if (wInfo.behavior.equals("swipe")) {
                        ArrayList<String> args = wInfo.behaviorArgs;
                        script.opeExecuteSwipe(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)), Integer.parseInt(args.get(3)), Integer.parseInt(args.get(4)));
                    }
                }catch(NoSuchElementException ex){
                    System.out.println("未找到元素："+using);
                    System.out.println("继续复现，但本条路径可能已经失败");
                }
            }
            return;
        }catch(Exception e){
            System.out.println("复现失败");
        }
    }


    public static void resList2Pic(ArrayList<WidgetInfoNode> resList) {
        String dirName = "ActivityList";
        String dotName = "mergeWidgetInfo";
        File dotFile = new File(dirName+File.separator+dotName+".dot");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(dotFile));
            Set<String> activityName = new HashSet<>();
            for (int i = 0; i < resList.size();i++){
                WidgetInfoNode wInfoNode = resList.get(i);
                String activity = wInfoNode.getWidgetInfo().getActivity();
                activityName.add(activity);
            }
            String []activityNames = activityName.toArray(new String[activityName.size()]);

            //write start
            bw.write("digraph g {\n");
            bw.flush();
            bw.write("    node [shape=box, style=filled, fillcolor=lightblue, fontname=\"simsun.ttc\", fontsize=14];\n" +
                    "    edge [fontname=\"simsun.ttc\", fontsize=12];\n");
            bw.flush();
            //write subgraph
            for(int i = 0;i<activityNames.length;i++){
                String aName = activityNames[i];
                String outputAName = aName.replaceAll("\\.","_");
                bw.write("    subgraph cluster_"+outputAName+"{\n");
                bw.write("    label = \""+outputAName+"\"\n");
                bw.flush();
                for (int j = 0; j < resList.size();j++){
                    WidgetInfoNode wInfoNode = resList.get(j);
                    WidgetInfo wInfo = wInfoNode.getWidgetInfo();
                    if(aName.equals(wInfo.getActivity())){
                        if(aName.equals("")){
                            bw.write("        " + j + "[label = \"" + "Start" + "        \"];\n");
                        }else {
                            String labelContains = "";
                            labelContains += "behavior = " + wInfo.getBehavior() + "\n";
                            labelContains += "        arg = " + wInfo.getBehaviorArgs() + "\n";
                            labelContains += "        index = " + wInfo.getIndex() + "\n";
                            labelContains += "        id = " + wInfo.getId() + "\n";
                            labelContains += "        text = " + wInfo.getText() + "\n";
                            labelContains += "        className = " + wInfo.getClassName() + "\n";
                            labelContains += "        desc = " + wInfo.getDesc() + "\n";
                            labelContains += "        packageName = " + wInfo.getPackageName() + "\n";
                            bw.write("        " + j + "[label = \"" + labelContains + "        \"];\n");
                        }
                    }
                }
                bw.write("    }\n");
                bw.flush();
            }

            //可添加的其他配置项

            //write edge
            for (int i = 0; i < resList.size();i++) {
                WidgetInfoNode wInfoNode = resList.get(i);
                ArrayList<WidgetInfoNode> nextList = wInfoNode.getNext();
                for(int j = 0 ;j<nextList.size();j++){
                    WidgetInfoNode nextNode = nextList.get(j);
                    int nextIndex = resList.indexOf(nextNode);
                    bw.write("    "+i+"->"+nextIndex+";\n");
                    bw.flush();
                }
            }

            bw.write("}\n");
            bw.flush();
            bw.close();

            createDot(dirName,dotName);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("画图失败");
        }
    }
    public static void createDot(String dirName,String dotName){
        Process p;
        File f =new File("");
        String dirAbsoluteName = f.getAbsolutePath()+File.separator+dirName+File.separator;
        String cmd = "dot -Tpng -o "+dirAbsoluteName+dotName+".png "+dirAbsoluteName+dotName+".dot";
        try {
            p = Runtime.getRuntime().exec(cmd);
            //String inStr = consumeInputStream(p.getInputStream());
            //String errStr = consumeInputStream(p.getErrorStream());
            p.waitFor();
            p.destroy();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    //读输出流
    public static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s ;
        StringBuilder sb = new StringBuilder();
        while((s=br.readLine())!=null){
            System.out.println(s);
            sb.append(s);
        }
        return sb.toString();
    }
    public static void main(String []args){
        ArrayList<WidgetInfoNode> resList = widgetMergeTools.getResList("D:\\工作\\test");
        widgetMergeTools.resList2Pic(resList);
//        ArrayList<WidgetInfoNode> nodeList = widgetMergeTools.getAllPath(resList);
//        //nodeList.size()
//        for(int i =0;i<1;i++){
//            //runOnePath(getResList("C:\\Users\\jon\\Desktop\\test").size(),nodeList.get(i),null);
//            Stack<WidgetInfoNode> path = new Stack<>();
//            path = getPath(0, resList.size(), new Stack<WidgetInfoNode>(), nodeList.get(i));
//            System.out.println(123);
//        }
    }
}
