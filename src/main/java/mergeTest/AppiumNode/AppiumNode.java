package mergeTest.AppiumNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppiumNode {
    int id;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Next == null) ? 0 : Next.hashCode());
        result = prime * result + ((Xpath == null) ? 0 : Xpath.hashCode());
        result = prime * result + ((activity == null) ? 0 : activity.hashCode());
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppiumNode other = (AppiumNode) obj;
        if (Xpath == null) {
            if (other.Xpath != null)
                return false;
        } else if (!Xpath.equals(other.Xpath))
            return false;
        if (activity == null) {
            if (other.activity != null)
                return false;
        } else if (!activity.equals(other.activity))
            return false;
        return true;
    }

    String activity;
    String Xpath;
    LinkedHashMap<ArrayList<String>, ArrayList<String>> Next;
    AppiumNode father;

    /*
    Next:{
        key: name:string, activity:string
        value: behavior1:string,...behaviorN:string
    }
    */
    AppiumNode(String a, String X) {
        activity = a;
        Xpath = X;
        father = null;
    }

    AppiumNode(int id, String a, String X) {
        this.id = id;
        activity = a;
        Xpath = X;
        father = null;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getXpath() {
        return Xpath;
    }

    public void setXpath(String xpath) {
        Xpath = xpath;
    }

    public LinkedHashMap<ArrayList<String>, ArrayList<String>> getNext() {
        return Next;
    }

    public void setNext(LinkedHashMap<ArrayList<String>, ArrayList<String>> next) {
        Next = next;
    }

    public AppiumNode getFather() {
        return father;
    }

    public void setFather(AppiumNode f) {
        father = f;
    }

    public static int findNode(List<AppiumNode> mergeRes, String xpath, String activity) {
        for (int i = 0; i < mergeRes.size(); i++) {
            if (mergeRes.get(i).getXpath().equals(xpath) && mergeRes.get(i).getActivity().equals(activity)) {
                return i;
            }
        }
        return -1;
    }
}
