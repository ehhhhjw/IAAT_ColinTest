package Tree;

import java.util.List;


public class ActivityTreeNode {
	private String name;
	private int layer = 0; 
	private ActivityTreeNodeList nodeList;
	
	public ActivityTreeNode(){
		this.name = null;
		this.nodeList = new ActivityTreeNodeList();
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setList(ActivityTreeNodeList list){
		this.nodeList = list;
	}
	public ActivityTreeNodeList getChildren(){
		return this.nodeList;
	}
//	public void addChild(ViewTreeNode n){
//		nodeList.add(n);
//	}
    public int getLayer() {  
        return layer;  
    }  
    public void setLayer(int layer) {  
        this.layer = layer;  
    }  
    public void add(ActivityTreeNode n){  
        n.setLayer(layer + 1);  
        setChildLayout(n);  
        nodeList.add(n);  
    }  
    public boolean hasChild(){  
        return nodeList == null ? false : true;  
    } 
    private void setChildLayout(ActivityTreeNode n){  
        if(n.hasChild()){  
            List<ActivityTreeNode> c = n.getChildren();  
            for(ActivityTreeNode node : c){  
                node.setLayer(node.getLayer()+1);  
                setChildLayout(node);  
            }  
        }  
    } 
    public String toString(){
        //TODO:
    	String tmp[] = name.split("\\.");
    	String result = tmp[tmp.length - 1];
        return result;  
    } 
}
