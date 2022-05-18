package mergeTest;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;

import java.io.IOException;

public class MyFileAppender extends FileAppender{
    //重写FilAppender
    public MyFileAppender(Layout layout,String filename) throws IOException {
        // TODO Auto-generated constructor stub
        super(layout,filename);
    }
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        // 只判断是否相等，而不判断优先级
        return this.getThreshold().equals(priority);
    }

}