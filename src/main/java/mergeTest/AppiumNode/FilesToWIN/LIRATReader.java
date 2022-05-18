package mergeTest.AppiumNode.FilesToWIN;

import mergeTest.AppiumNode.JsonReader;
import mergeTest.AppiumNode.WidgetInfo;
import mergeTest.AppiumNode.WidgetInfoNode;
import mergeTest.AppiumNode.widgetMergeTools;

import java.io.File;
import java.util.ArrayList;

public class LIRATReader {
    public static WidgetInfo getWInfoNode(File step) {
        File info = new File(step.getAbsoluteFile()+File.separator+"info.json");
        File XML = new File(step.getAbsoluteFile()+File.separator+"ui.xml");
        JsonReader jr = new JsonReader(info);
        String activity = jr.getActivity();
        int x = jr.getX();
        int y = jr.getY();
        WidgetInfo widgetInfo = jr.getWidgetInfo();
        widgetInfo.setActivity(activity);
        return widgetInfo;
    }
    public static ArrayList<WidgetInfo> getWInfoNodeList(File rootDir){
        ArrayList<WidgetInfo> res = new ArrayList<>();
        File []steps = rootDir.listFiles();
        sortFiles(steps,"step");
        for(int i = 0;i< steps.length;i++){
            WidgetInfo wInfo = getWInfoNode(steps[i]);
            res.add(wInfo);
        }
        return res;
    }
    public static void sortFiles(File[] files,String regex){
        for (int i = 0; i < files.length - 1; i++) {
            int  min = i;
            for (int j = i + 1; j < files.length; j++) {
                if (Integer.parseInt(files[min].getName().split(regex)[1]) > Integer.parseInt(files[j].getName().split(regex)[1])) {
                    min = j;
                }
            }
            if (min != i) {
                File tmp = files[min];
                files[min] = files[i];
                files[i] = tmp;
            }
        }
    }
    public static void getWIndoNodeListTree(File file){
        ArrayList<ArrayList<WidgetInfo>> noEdgeTree = new ArrayList<>();
        File []fileList = file.listFiles();
        sortFiles(fileList,"_");
        for(int i = 0;i< fileList.length;i++){
            ArrayList<WidgetInfo> tempWInfo = getWInfoNodeList(fileList[i]);
            noEdgeTree.add(tempWInfo);
        }
        ArrayList<WidgetInfoNode> res = widgetMergeTools.getResList(noEdgeTree);
        widgetMergeTools.resList2Pic(res);
    }
    public static void main(String args[]){
        String DirName = "C:\\Users\\scarlet\\Desktop\\apks\\net.osmand";
        getWIndoNodeListTree(new File(DirName));
        System.out.println();
    }
}
