package Tree;

import util.AddressUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class TreeImage extends BufferedImage{
    private ActivityTreeNode tree;              //保存整棵树  
    private int gridWidth = 160;     //每个结点的宽度  
    private int gridHeight = 280;    //每个结点的高度  
    private int vGap = 50;          //每2个结点的垂直距离  
    private int hGap = 30;          //每2个结点的水平距离  
      
    private int startY = 10;        //根结点的Y，默认距离顶部10像素  
    private int startX = 0;         //根结点的X，默认水平居中对齐  
      
    private int childAlign;                     //孩子对齐方式  
    public static int CHILD_ALIGN_ABSOLUTE = 0; //相对Panel居中  
    public static int CHILD_ALIGN_RELATIVE = 1; //相对父结点居中  
      
    private Font font = new Font("微软雅黑",Font.BOLD,14);  //描述结点的字体  
      
    private Color gridColor = Color.BLACK;      //结点背景颜色  
    private Color linkLineColor = Color.BLACK;  //结点连线颜色  
    private Color stringColor = Color.BLACK;    //结点描述文字的颜色  
    
    private String udid;
	
	public TreeImage(int width, int height, int imageType) {
		super(width, height, imageType);
		// TODO Auto-generated constructor stub
	}
	
	 public void setTree(ActivityTreeNode n) {  
	        tree = n;  
	    }  
	 
	 public void setUdid(String udid){
		 this.udid=udid;
	 }

	
    public void paintComponent(Graphics g){  

        startX = (getWidth()-gridWidth)/2;  
        //super.paintComponent(g);  
        g.setFont(font);  
        drawAllNode(tree, startX, g); 
    }  
      
    /** 
     * 递归绘制整棵树 
     * @param n 被绘制的Node 
     * @param x 根节点的绘制X位置
     * @param g 绘图上下文环境 
     */  
    public void drawAllNode(ActivityTreeNode n, int x, Graphics g){  
        int y = (n.getLayer()-1)*(vGap+gridHeight)+startY;  
        int fontY = y + gridHeight - 15;     //5为测试得出的值，你可以通过FM计算更精确的，但会影响速度  
          
        g.setColor(gridColor);  
        ImageObserver io=new JButton();

        ImageIcon icon=new ImageIcon(AddressUtil.getNodeScreenShot(udid, n.getName()));
        Image img=icon.getImage();
        g.drawImage(img, x, y, gridWidth, gridHeight, io);
       // g.fillRect(x, y, gridWidth, gridHeight);    //画结点的格子  
          
        g.setColor(stringColor);  
        g.drawString(n.toString(), x, fontY);       //画结点的名字  
          
        if(n.hasChild()){  
            ActivityTreeNodeList c = n.getChildren();  
            int size = n.getChildren().size(); 
            int tempPosx = childAlign == CHILD_ALIGN_RELATIVE   
                         ? x+gridWidth/2 - (size*(gridWidth+hGap)-hGap)/2  
                         : (getWidth() - size*(gridWidth+hGap)+hGap)/2;   
              
            int i = 0;  
            for(int j=0;j<c.size();j++){  
                int newX = tempPosx+(gridWidth+hGap)*i; //孩子结点起始X  
                g.setColor(linkLineColor);  
                g.drawLine(x+gridWidth/2, y+gridHeight, newX+gridWidth/2, y+gridHeight+vGap);  
                drawAllNode(c.get(i), newX, g);  
                i++;  
            }  
        }  
    }  

}
