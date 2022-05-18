package mergeTest.AppiumNode;

import java.util.ArrayList;

public class WidgetInfoNode {
    WidgetInfo widgetInfo;
    ArrayList<WidgetInfoNode> father = new ArrayList();
    ArrayList<WidgetInfoNode> next = new ArrayList();
    boolean isVisited = false;

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }


    public WidgetInfoNode() {
        widgetInfo=new WidgetInfo();
    }

    public WidgetInfoNode(WidgetInfo widgetInfo) {
        this.widgetInfo = widgetInfo;
    }

    public WidgetInfo getWidgetInfo() {
        return widgetInfo;
    }

    public void setWidgetInfo(WidgetInfo widgetInfo) {
        this.widgetInfo = widgetInfo;
    }

    public ArrayList<WidgetInfoNode> getFather() {
        return father;
    }

    public void setFather(ArrayList<WidgetInfoNode> father) {
        this.father = father;
    }

    public ArrayList<WidgetInfoNode> getNext() {
        return next;
    }

    public void setNext(ArrayList<WidgetInfoNode> next) {
        this.next = next;
    }

    public void setNext(WidgetInfoNode next) {
        this.next.add(next);
    }
    public void setFather(WidgetInfoNode father) {
        this.father.add(father);
    }
    //增加本Node的next内容
    public void addNext(WidgetInfoNode nextNode){
        ArrayList<WidgetInfoNode> p1 = this.next;
        if(p1.size()==0) {
            this.next.add(nextNode);
            return;
        }
        for(int i =0;i<p1.size();i++){
            WidgetInfoNode temp = p1.get(i);
            if(temp.equals(nextNode)){
                return;
            }
            if(i==p1.size()-1){
                this.next.add(nextNode);
            }
        }
    }
    public void addFather(WidgetInfoNode thisFather){
        ArrayList<WidgetInfoNode> p1 = this.father;
        if(p1.size()==0) {
            this.father.add(thisFather);
            return;
        }
        for(int i =0;i<p1.size();i++){
            WidgetInfoNode temp = p1.get(i);
            if(temp.equals(thisFather)){
                return;
            }
            if(i==p1.size()-1){
                this.father.add(thisFather);
                return;
            }
        }
    }
    //确定data是否相同
    public boolean hasNext(WidgetInfoNode next){
        for(int i = 0;i<this.next.size();i++){
            if(this.next.get(i).equals(next)){
                return true;
            }
        }
        return false;
    }

    public boolean equals(WidgetInfoNode next){
        WidgetInfo p1 = this.widgetInfo;
        WidgetInfo p2 = next.widgetInfo;
        //都是头节点
        if(p1==null&&p2==null) return true;
        if(p1.isNull()&&p2.isNull()) return true;
        try {
            return p1.equals(p2);
        }catch(Exception e){
            return false;
        }
    }

    public boolean equals(WidgetInfo next){
        WidgetInfo p1 = this.widgetInfo;
        WidgetInfo p2 = next;
        if(p1==null&&p2==null) return true;
        if(p1.isNull()&&p2.isNull()) return true;
        try {
            return p1.equals(p2);
        }catch(Exception e){
            return false;
        }
    }
}
