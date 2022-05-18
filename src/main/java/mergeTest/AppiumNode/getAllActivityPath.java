package mergeTest.AppiumNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

//已经有了AppiumNode资料，根据Node找到从初始路径走到其他activity的道路
public class getAllActivityPath {
    public ArrayList<ArrayList<String>> run(ArrayList<AppiumNode> mergeRes) throws IOException {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        ArrayList<String> activity = new ArrayList<String>();
        int nlength = 0, alength = 0;
        for (AppiumNode an : mergeRes) {
            nlength++;
            if (!activity.contains(an.getActivity())) {
                activity.add(an.getActivity());
                alength++;
            }
        }
        Boolean aisVisted[] = new Boolean[alength];
        Boolean nisVisted[] = new Boolean[nlength];
        aisVisted[0] = Boolean.TRUE;
        nisVisted[0] = Boolean.TRUE;
        //BFS
        Queue<AppiumNode> q = new LinkedList<>();
        q.add(mergeRes.get(0));
        ArrayList<AppiumNode> sonNode = new ArrayList<>();
        while (!q.isEmpty()) {
            AppiumNode now = q.poll();
            //加入队列
            if(now.getNext()==null) continue;
            for (Map.Entry e : now.getNext().entrySet()) {
                ArrayList<String> activityAndXpath = (ArrayList<String>) e.getKey();
                int index = AppiumNode.findNode(mergeRes, activityAndXpath.get(1), activityAndXpath.get(0));
                if (nisVisted[index] != Boolean.TRUE) {
                    nisVisted[index] = Boolean.TRUE;
                    AppiumNode son = mergeRes.get(index);
                    q.add(son);
                    //第一次 设置父节点
                    son.father = now;
                    //判断activity是否访问过
                    int aIndex = -1;
                    for (int i = 0; i < activity.size(); i++) {
                        if (activity.get(i).equals(son.activity)) {
                            aIndex = i;
                            break;
                        }
                    }
                    if (aisVisted[aIndex] != Boolean.TRUE) {
                        aisVisted[aIndex] = Boolean.TRUE;
                        sonNode.add(son);
                    }
                }
            }
        }
        //sonNode里包含最近的到达新的activity的节点，用father找到逆向路径。
        //son xpath -> father.Next.value[n]->father. Next.value[1]->father xpath
        for(AppiumNode son: sonNode){
            ArrayList<String> path = new ArrayList<>();
            String targetActivity = son.activity;
            if(son.father==null) {
                path.add(son.getXpath());
            }
            while(son.father!=null){
                path.add(son.getXpath());
                AppiumNode father = son.father;
                for(Map.Entry e: father.Next.entrySet()){
                    ArrayList<String> key = (ArrayList<String>) e.getKey();
                    ArrayList<String> value = (ArrayList<String>) e.getValue();
                    if(key.get(0).equals(son.getActivity())&&key.get(1).equals(son.getXpath())){
                        for(int i=value.size()-1;i>=0;i--){
                            path.add(value.get(i));
                        }
                        break;
                    }
                }
                son = son.father;
            }
            path.add(targetActivity);
            res.add(path);
        }
        ArrayList<ArrayList<String>> nres=new ArrayList<>();
        //对res中的每一个 去除第0个后逆序
        for(ArrayList<String> path : res){
            path.remove(0);
            ArrayList<String> newPath=new ArrayList<>();
            for(int i=path.size()-1;i>=0;i--){
                newPath.add(path.get(i));
            }
            nres.add(newPath);
        }
        return nres;
    }

    public static void main(String[] args) throws IOException {
        mergeAppiumNode e2j = new mergeAppiumNode();
        //ArrayList<AppiumNode> test = e2j.run("C:\\Users\\jon\\Desktop\\2月\\ApplicationForTesting");
        ArrayList<AppiumNode> test = e2j.run("/home/xyr/Kikbug_PCServer/nodeInfo/ApplicationForTesting");

        ArrayList<ArrayList<String>> path = new getAllActivityPath().run(test);
        System.out.println("1111");
    }

}
