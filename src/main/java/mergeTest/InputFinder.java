package mergeTest;

/**
 * Created by homer on 17-9-5.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class InputFinder {

    public static void main(String[] args) throws IOException
    {

        String filepath = "C:\\Users\\atian\\eclipse_workspace\\My_Appium\\src\\nju\\edu\\cn\\SecondTest.java";
        InputFinder inputFinder = new InputFinder();
        System.out.println(inputFinder.getInputValue(filepath,"com.xdf.ucan:id/editText1"));

    }

    public String getInputValue(String filepath,String resource_id) throws IOException
    {
        BufferedReader bre = null;
        bre = new BufferedReader(new FileReader(filepath));//此时获取到的bre就是整个文件的缓存流
        StringBuilder variablebuilder = new StringBuilder();
        StringBuilder inputBuilder = new StringBuilder();
        String variableName = null;
        String inputValue = null;
        boolean flag1 = false;	//是否找到给定控件所对应的变量名

        String strLine = "";
        while ((strLine = bre.readLine())!= null) // 判断最后一行不存在，为空结束循环
        {
            int index1 = -1;
            if(strLine.indexOf(resource_id) != -1)	//找到出现过给定控件的resource-id的那一行
            {
                index1 = strLine.indexOf("WebElement");
                //过滤掉WebElement类名
                while(strLine.charAt(index1)!=' ') index1++;
                //过滤掉类名和变量名之间空格、回车、水平制表符
                while(strLine.charAt(index1)==' '||strLine.charAt(index1)=='\r'||strLine.charAt(index1)=='\t') index1++;
                //将变量名拼成字符串
                while(strLine.charAt(index1)!=' '&&strLine.charAt(index1)!='='&&strLine.charAt(index1)!='\t'&&strLine.charAt(index1)!='\r')
                {
                    variablebuilder.append(strLine.charAt(index1));
                    index1++;
                }
                variableName = variablebuilder.toString();
                flag1 = true; 	//给定控件的变量名已找到
                continue;//直接跳到读取下一行
            }

            int index2 = -1;
            if(flag1)
            {
                if((index2 = strLine.indexOf(variableName+".sendKeys"))!=-1)	//找到像控件发送输入的那一行
                {
                    while(strLine.charAt(index2)!='\"') index2++;
                    index2++;
                    while(strLine.charAt(index2)!='\"')
                    {
                        inputBuilder.append(strLine.charAt(index2));
                        index2++;
                    }
                    inputValue = inputBuilder.toString();

                }
            }
        }

        return inputValue;
    }

}
