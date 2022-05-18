package mergeTest.AppiumNode.FilesToWIN;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class AutoCategory {
    public static void category(File f){
        File[] files = f.listFiles();
        for(File step:files){
            File info = new File(step.getAbsolutePath()+File.separator+"step1"+File.separator+"info.json");
            try {
                JSONObject jsonObject = JSONObject.parseObject(FileUtils.readFileToString(info));
                String att = jsonObject.getString("activity");
                String []atts = att.split(" ");
                String packName="";
                for(int i =atts.length-1;i>=0;i--){
                    if(atts[i].contains("/")){
                        packName = atts[i].split("\\/")[0];
                        break;
                    }
                }
                File pacFile = new File(f.getAbsolutePath()+File.separator+packName);
                if(!pacFile.exists()){
                    pacFile.mkdir();
                }
                String cmdStr = "xcopy "+step.getAbsolutePath()+" "+pacFile.getAbsolutePath()+File.separator+step.getName()+" /y /e /i /q";
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmdStr);
                    p.waitFor();
                    p.destroy();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String args[]){
        File f = new File("C:\\Users\\scarlet\\Desktop\\scripts");
        category(f);
    }
}
