package Tree;

import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class DrawTree {
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
	ActivityTreeNode node;
	File file;
	String father=null;

	public static void main(String args[]){
    	ActivityTreeNode n=new ActivityTreeNode();
    	n.setName(".splash.SplashActivity");
    	ActivityTreeNode n1=new ActivityTreeNode();
    	n1.setName(".ui.login.LoginActivity");
//    	ViewTreeNode n2=new ViewTreeNode();
//    	n2.setName(".MainActivity");
//    	ViewTreeNode n3=new ViewTreeNode();
//    	n3.setName(".ui.im.ConversationActivity");
//    	ViewTreeNode n4=new ViewTreeNode();
//    	n4.setName(".ui.im.ChatsetingActivity");
//    	ViewTreeNode n5=new ViewTreeNode();
//    	n5.setName(".ui.material.MaterialListActivity");
//    	ViewTreeNode n6=new ViewTreeNode();
//    	n6.setName(".ui.me.SearchActivity");
//    	ViewTreeNode n7=new ViewTreeNode();
//    	n7.setName(".ui.video.CommentListActivity");
    	
    	
    	n.add(n1);
//    	n1.add(n2);
//    	n2.add(n3);
//    	n3.add(n4);
//    	n2.add(n5);
//    	n5.add(n6);
//    	n5.add(n7);
    	
    	new DrawTree().draw(11111111,"KBDMUGP7HYF6Y595",n);
	}
	
	public void draw(int taskId,String udid,ActivityTreeNode node){
		PrintUtil.print("draw dot view ", TAG);
		String dotPath="dot" + File.separator + udid  + File.separator + "tree_" + taskId + ".dot";
//		String buff=this.getClass().getResource("/").getFile().toString();
//		String r[]=buff.split("/");
//		String result="";
//		for(int i=0;i<r.length-1;i++){
//			result=result+r[i]+"/";
//		}
//		String root=result.substring(1,result.length());
//		PrintUtil.print(root);
		String treeImgPath = AddressUtil.getTreeImgPath(taskId, udid);
		file=new File(dotPath);
		file.mkdirs();
		if(file.exists()){
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw = null;
		try {
			fw=new FileWriter(file);
			fw.write("digraph G{ \r\n");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		traverse(udid,node,fw);
		try {
			fw.write("} \r\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String c="cd ";
		//String command="cmd /k start dot -T png "+"dot\\"+udid+"\\tree.dot -o tree"+".png";
		String cmd = OSUtil.getCmd();
		String command;
		if (OSUtil.isWin()) {
//			command = cmd + " Commands\\win\\dot.bat " + dotPath + " " + treeImgPath;
			command = "dot " + dotPath + " -T png -o " + treeImgPath;
		} else {
			command = cmd + " Commands/dot.sh " + dotPath + " " + treeImgPath;
		}
		PrintUtil.print(command, TAG);
		OSUtil.runCommand(command);
		
	}
	void traverse(String udid, ActivityTreeNode node, FileWriter fw){
		if(node.getChildren().size()==0){
			PrintUtil.print(node.getName(), TAG);
			if(father==null){
				father=node.toString();
				String line=node.toString()+"[fontsize=\"70\" shapefile=\""+"NodeScreenShots"+File.separator+udid+File.separator+node.getName()+".jpg"+"\"];"+"\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				String line=node.toString()+"[fontsize=\"70\" shapefile=\""+"NodeScreenShots"+File.separator+udid+File.separator+node.getName()+".jpg"+"\"];"+"\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				line=father+"->"+node.toString()+";\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				father=node.toString();
			}
			return;
		}else{
			PrintUtil.print(node.getName(), TAG);
			if(father==null){
				father=node.toString();
				String line=node.toString()+"[fontsize=\"70\" shapefile=\""+"NodeScreenShots"+File.separator+udid+File.separator+node.getName()+".jpg"+"\"];"+"\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				String line=node.toString()+"[fontsize=\"70\" shapefile=\""+"NodeScreenShots"+File.separator+udid+File.separator+node.getName()+".jpg"+"\"];"+"\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				line=father+"->"+node.toString()+";\r\n";
				try {
					fw.write(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				father=node.toString();
			}
			for(int i=0;i<node.getChildren().size();i++){
				traverse(udid,node.getChildren().get(i),fw);
				father=node.toString();
			}
		}
	}
}
