package Tree;

import java.util.ArrayList;

public class ActivityTreeNodeList extends ArrayList{
	private ArrayList<ActivityTreeNode> list=new ArrayList<ActivityTreeNode>();
	
	public void add(ActivityTreeNode n){
		list.add(n);
	}
	public ActivityTreeNode get(int index){
		return (ActivityTreeNode)list.get(index);
	}
	public int size(){
		return list.size();
	}
}
