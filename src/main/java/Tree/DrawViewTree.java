package Tree;

import util.AddressUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** 
 * @author John 
 * 
 */  
public class DrawViewTree extends JFrame{  
    String udid;  
    int id;
    public DrawViewTree(int id,String udid,ActivityTreeNode n){  
    	this.udid = udid;
    	this.id = id;
        initComponents(id,n);  
    }  
      
    public static void main(String[] args){  
    	ActivityTreeNode n=new ActivityTreeNode();
    	n.setName(".splash.SplashActivity");
    	ActivityTreeNode n1=new ActivityTreeNode();
    	n1.setName(".ui.login.LoginActivity");
    	ActivityTreeNode n2=new ActivityTreeNode();
    	n2.setName(".MainActivity");
    	ActivityTreeNode n3=new ActivityTreeNode();
    	n3.setName(".ui.im.ConversationActivity");
    	ActivityTreeNode n4=new ActivityTreeNode();
    	n4.setName(".ui.im.ChatsetingActivity");
    	ActivityTreeNode n5=new ActivityTreeNode();
    	n5.setName(".ui.material.MaterialListActivity");
    	ActivityTreeNode n6=new ActivityTreeNode();
    	n6.setName(".ui.me.SearchActivity");
    	ActivityTreeNode n7=new ActivityTreeNode();
    	n7.setName(".ui.video.CommentListActivity");
    	
    	
//    	ViewTreeNode n4=new ViewTreeNode();
//    	n4.setName("Son2");
    	
    	n.add(n1);
    	n1.add(n2);
    	n2.add(n3);
    	n3.add(n4);
    	n2.add(n5);
    	n5.add(n6);
    	n5.add(n7);
    	
//    	DrawViewTree frame = new DrawViewTree(n);  
    }   
      
    public void initComponents(int id,ActivityTreeNode n){  
        
        //n.printAllNode(n);    //输出树  
          
        /* 
         * 创建一个用于绘制树的面板并将树传入,使用相对对齐方式 
         */  
        TreePanel panel1 = new TreePanel(TreePanel.CHILD_ALIGN_RELATIVE);  
        panel1.setTree(n);  
        
        TreeImage ti=new TreeImage(800,2000,BufferedImage.TYPE_INT_ARGB);
        ti.setTree(n);
        ti.setUdid(udid);
        Graphics g = ti.getGraphics();
       // g.setColor(Color.WHITE);
        
        ti.paintComponent(g);
        try {
			ImageIO.write(ti, "PNG", new File(AddressUtil.getTreeImgPath(id, udid)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
        /* 
         * 创建一个用于绘制树的面板并将树传入,使用绝对对齐方式 
         */  
//        TreePanel panel2 = new TreePanel(TreePanel.CHILD_ALIGN_ABSOLUTE);  
//        panel2.setTree(n);  
//        panel2.setBackground(Color.BLACK);  
//        panel2.setGridColor(Color.WHITE);  
//        panel2.setLinkLineColor(Color.WHITE);  
//        panel2.setStringColor(Color.BLACK);  
//          
//        JPanel contentPane = new JPanel();  
//        contentPane.setLayout(new GridLayout(2,1));  
//        contentPane.add(panel1);  
//        //contentPane.add(panel2);  
          
       // add(panel1,BorderLayout.CENTER);  
    }  
}  

