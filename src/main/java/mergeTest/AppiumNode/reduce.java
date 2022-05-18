package mergeTest.AppiumNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

public class reduce {
    public static TreeSet<String> reduce(String fileName) throws IOException {
        File f = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(f));
        TreeSet<String> set = new TreeSet<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(".Hw")&&!line.equals(".Launcher")&&(line.startsWith(".")||line.startsWith("com"))) {
                set.add(line);
            }
        }
        return set;
    }
    //B里是否有A所有的元素
    public static boolean isHave(TreeSet A,TreeSet B){
        Iterator ita=A.iterator();
        while(ita.hasNext()){
            String line = (String) ita.next();
            if(!B.contains(line)){
                System.out.println("没有"+line);
                //return false;
            }
        }
        return true;
    }
    public static void main(String[] args) throws IOException {
        String fileName1 = "C:\\Users\\jon\\Desktop\\bilibefore.txt";
        String fileName2 = "C:\\Users\\jon\\Desktop\\biliafter.txt";
        String fileName3 = "C:\\Users\\jon\\Desktop\\user-bili.txt";
        TreeSet<String> set = reduce(fileName1);
        System.out.println(set.size());
        Iterator it=set.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
        set = reduce(fileName2);
        System.out.println(set.size());
        it=set.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
        set = reduce(fileName3);
        System.out.println(set.size());
        it=set.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
       System.out.println(isHave(reduce(fileName1),reduce(fileName3)));
    }
}
