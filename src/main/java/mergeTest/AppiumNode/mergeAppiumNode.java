package mergeTest.AppiumNode;

import util.OSUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/*
 * 将学生用户运行代码后生成的流程记录整合成Node的形式并返回
 */
class tempNode {
    AppiumNode an;
    ArrayList<String> list;

    tempNode(AppiumNode an, ArrayList<String> list) {
        this.an = an;
        this.list = list;
    }
}

public class mergeAppiumNode {
    public ArrayList<AppiumNode> run(String projectPath) throws IOException {
        //initial
        String pNames[];
        String pName;
        String ePath;
        File fin;
        File fout;
        if(OSUtil.isWin()) {
            pNames = projectPath.split("\\\\");
            pName = pNames[pNames.length - 1];
            ePath = projectPath + "\\lib\\.metadata\\.data";
            fin = new File(ePath + "\\.experiment-" + pName);
            fout = new File(ePath + "\\.json-" + pName);
        }else{
            pNames = projectPath.split("/");
            pName = pNames[pNames.length - 1];
            ePath = projectPath + "/lib/.metadata/.data";
            fin = new File(ePath + "/.experiment-" + pName);
            fout = new File(ePath + "/.json-" + pName);
        }
        BufferedReader br = new BufferedReader(new FileReader(fin));
        FileWriter fw = new FileWriter(fout);
        //read
        String line;
        ArrayList<tempNode> res = new ArrayList();
        AppiumNode tempNode = new AppiumNode("", "");
        ArrayList<String> tempBehavior = new ArrayList();
        //到达新界面后，直接使用swipe等非控件操作的情况归类到上一个界面的控件后续操作中
        int count = 0;
        while ((line = br.readLine()) != null) {
            String activity = line;
            line = br.readLine();
            if (line == null) break;
            if (line.startsWith("//")) {
                //对新控件进行操作，将上个控件的相关内容加入map
                res.add(new tempNode(tempNode, tempBehavior));
                tempNode = new AppiumNode(activity, line);
                tempBehavior = new ArrayList();
                line = br.readLine();
                tempBehavior.add(line);
            } else {
                //非控件操作
                //TODO 暂时无视了activity不同的连续非控件操作
                tempBehavior.add(line);
            }
        }
        //最后一个node

        res.add(new tempNode(tempNode, tempBehavior));

        //读完文件，生成了操作顺序表
        //merge成为所需结构
        ArrayList<AppiumNode> mergeRes = new ArrayList<AppiumNode>();
        for (int i = 0; i < res.size(); i++) {
            //from to
            mergeTest.AppiumNode.tempNode from = res.get(i);
            mergeTest.AppiumNode.tempNode to;
            if (i == res.size() - 1) to = null;
            else to = res.get(i + 1);
            //get root node
            String act = from.an.activity;//activity
            String Xpath = from.an.Xpath;//Xpath
            ArrayList<String> bList = from.list;//Next value
            //Next key
            String keyActivity;
            String keyXpath;
            ArrayList key = null;
            if (to != null) {
                keyActivity = to.an.activity;
                keyXpath = to.an.Xpath;
                key = new ArrayList();
                key.add(keyActivity);
                key.add(keyXpath);
            }
            AppiumNode root = new AppiumNode(act, Xpath);
            //find
            int mIndex = mergeRes.indexOf(root);
            AppiumNode now;
            if (mIndex != -1) {
                //获取该节点
                root = mergeRes.get(mIndex);
                //存疑
                mergeRes.remove(mIndex);
            }//else 就直接root了
            if (to != null) {
                LinkedHashMap<ArrayList<String>, ArrayList<String>> map = root.Next;
                if (map == null) map = new LinkedHashMap<ArrayList<String>, ArrayList<String>>();
                map.put(key, bList);
                root.setNext(map);
            }//空的就不加 直接null了
            //放回去
            mergeRes.add(root);
            //System.out.println(i);
        }
        System.out.println("mergeRes success");
        for (AppiumNode an : mergeRes) {
            an.setId(count++);
        }
        //setFather
        /*failed
        for (AppiumNode an : mergeRes) {
            for(Entry e:an.Next.entrySet()){
                ArrayList<String> toInfo = (ArrayList<String>) e.getKey();
                String toXpath = toInfo.get(1);
                for (AppiumNode temp : mergeRes) {
                    if (temp.Xpath.equals(toXpath)){
                        if(temp.father == null){
                            temp.father = an;
                        }else if(!temp.father.equals(an)){
                            System.out.println("wrong father set:f:"+an.getXpath()+" s:"+temp.getXpath());
                        }
                    }
                }
            }
        }
*/
        return mergeRes;
    }

    public static void main(String[] args) {
        mergeAppiumNode e2j = new mergeAppiumNode();
        try {
            //ArrayList<AppiumNode> test = e2j.run("C:\\Users\\jon\\Desktop\\2月\\ApplicationForTesting");
            ArrayList<AppiumNode> test = e2j.run("/home/xyr/Kikbug_PCServer/nodeInfo/ApplicationForTesting");
            System.out.println("1111");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
