package mergeTest.monitor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by homer on 17-2-10.
 */
public class SMMonitor {
    boolean killed = false;
    private int mPid;
    private AtomicInteger count = new AtomicInteger(0);
    private String udid;
    public void startSMMonitor(String udid, int pid) {
//        if (!isEnableForMonitor()) {
//            //进行设置，重启
//        }
//        //使用日志来收集frame的time
//        //计算
        this.udid = udid;
        this.mPid = pid;
        new Thread(new SMMonitorRunnable()).start();
    }

    public void stop() {
        killed = true;
    }

    public int getSkippedFrameCount() {
        return count.getAndSet(0);
    }
    private boolean isEnableForMonitor() {
        String cmd = "adb shell getprop debug.choreographer.skipwarning";
        ProcessBuilder execBuilder = new ProcessBuilder(cmd);
        execBuilder.redirectErrorStream(true);
        boolean flag = false;
        try {
            Process p = execBuilder.start();
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line = br.readLine()) != null) {
                if (line.compareTo("1") == 0) {
                    flag = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    class SMMonitorRunnable implements Runnable {
        Process dumpLogcatProcess = null;
        BufferedReader reader = null;

        @Override
        public void run() {
            List<String> args = new ArrayList<>(Arrays.asList("adb", "-s", udid, "shell", "logcat", "-v", "time", "Choreographer:I", "*:S"));
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                processBuilder.redirectErrorStream(true);
                dumpLogcatProcess = processBuilder.start();
                reader = new BufferedReader(new InputStreamReader(dumpLogcatProcess.getInputStream()), 8192);

                String line;

                while((line = reader.readLine()) != null && !killed) {
                    if (!line.contains("uch work on its main t")) {
                        continue;
                    }
                    int pID = LogLine.newLogLine(line, false).getProcessId();
                    if (pID != mPid) {
                        continue;
                    }
                    line = line.substring(50, line.length() - 71);
                    Integer value = Integer.parseInt(line.trim());
                    //全部加到count中去。
                    count.addAndGet(value);
                    //SM = (60* totalSeconds - totalSkippedFrames) / totalSeconds;

                }
                if (dumpLogcatProcess != null) {
                    dumpLogcatProcess.destroy();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                killProcess();
            }
        }

        public void killProcess() {
            if (!killed) {
                if (dumpLogcatProcess != null) {
                    dumpLogcatProcess.destroy();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                killed = true;
            }
        }
    }
}
