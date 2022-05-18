package mergeTest.monitor;

import mergeTest.ExtraService;
import org.apache.http.util.TextUtils;
import util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by homer on 17-3-10.
 */
public class LogMonitor implements Runnable{

    public static final int LOG_VERBOSE = 0; // 全部
    public static final int LOG_DEBUG = 1; // 调试
    public static final int LOG_INFO = 2; // 信息
    public static final int LOG_WARNING = 3; // 警告
    public static final int LOG_ERROR = 4; // 错误
    public static int Log_Level = LOG_VERBOSE; // 显示的log级别，起到开关的作用

    // 因为Activity的生命周期不太靠谱，所以在这里保存上次的文件名
    private static String lastSaveLog = "GTLog";

    // 日志的日期部分长度是19
    private static final int TIMESTAMP_LENGTH = 19;
    // 去掉日期，logcat的匹配模式
    private static Pattern logPattern = Pattern.compile(
            // level
            "(\\w)" +
                    "/" +
                    // tag
                    "([^(]+)" +
                    "\\(\\s*" +
                    // pid
                    "(\\d+)" +
                    // optional weird number that only occurs on ZTE blade
                    "(?:\\*\\s*\\d+)?" +
                    "\\): ");

    private Process logcatProcess;
    private BufferedReader bufferedReader;
    private String lastLine;
    protected boolean recordingMode;
    private File mTargetFile;
    private FileWriter mWriter;
    private int taskId = 1111;
    private String udid;
    private String deviceYear;
    private boolean stoped = false;
    private String apkName;
    public LogMonitor(String udid, int taskId,String apkName) {
        super();
        this.udid = udid;
        this.taskId = taskId;
        this.apkName = apkName;
        try {
            init(udid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).run();
    }
    public void stop() {
        logcatProcess.destroy();
        this.stoped = true;

    }
    private void init(String udid) throws IOException {
    	deviceYear = ExtraService.getDeviceYear(udid);
    	if(deviceYear.charAt(0) < '1' || deviceYear.charAt(0) > '9') {
    	    deviceYear = "";
        }
        logcatProcess = getLogcatProcess(udid);
        bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));

        String logPath = AddressUtil.getTestLogsPathById(apkName, taskId, udid);
        File file = new File(logPath);
        FileSystem.newFile(file);
        mTargetFile = new File(logPath);
        mWriter = new FileWriter(mTargetFile);
    }
    @Override
    public void run() {
        while (!stoped) {
            try {
                String line = readLine();
                if (line != null) {
                    logCat(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readLine() throws IOException {
        String line = bufferedReader.readLine();
        if (recordingMode && lastLine != null) {
            // still skipping past the 'last line'
            if (lastLine.equals(line) || isAfterLastTime(line)) {
                // indicates we've passed the last line
                lastLine = null;
            }
        }
        return line;
    }

    private boolean isAfterLastTime(String line) {
        // doing a string comparison is sufficient to determine whether this line is chronologically
        // after the last line, because the format they use is exactly the same and
        // lists larger time period before smaller ones
        return isDatedLogLine(lastLine) && isDatedLogLine(line) && line.compareTo(lastLine) > 0;

    }

    private boolean isDatedLogLine(String line) {
        // 18 is the size of the logcat timestamp
        return ((line != null && !line.isEmpty()) && line.length() >= 18 && Character.isDigit(line.charAt(0)));
    }

    private static List<String> getLogcatArgs(String udid) {
    	int SDK_INT = Integer.parseInt(ExtraService.getApiLevel(udid));
    	List<String> args = new ArrayList<String>();
    	if(SDK_INT >= 23){
    		//SDK > Android 6.0
           args = new ArrayList<>(Arrays.asList("adb", "-s", udid, "logcat", "-b", "main", "-b", "system", "-b", "crash", "-b", "events", "-b", "radio", "-v", "time"));
    	}else {
            args = new ArrayList<>(Arrays.asList("adb", "-s", udid, "logcat", "-b", "main", "-b", "system", "-b", "events", "-b", "radio", "-v", "time"));
		}
        return args;
    }

    public static Process getLogcatProcess(String udid) throws IOException {
        List<String> args = getLogcatArgs(udid);
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        Process process  = pb.start();
//        Process process = OSUtil.exec(args);
        return process;
    }

    public  void logCat(String originalLine)
    {
        // 先解析日志
        String sTime = null;
        long pid = -1;
        char level = 'I';
        String tag = null;
        String msg = null;

        int startIdx = 0;
        if (!TextUtils.isEmpty(originalLine)
                && Character.isDigit(originalLine.charAt(0))
                && originalLine.length() >= TIMESTAMP_LENGTH) {
            String timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1);
            sTime = timestamp;
            // cut off timestamp
            startIdx = TIMESTAMP_LENGTH;
        }

        Matcher matcher = logPattern.matcher(originalLine);
        if (matcher.find(startIdx)) {
            level = matcher.group(1).charAt(0);
            int gtLevel = LOG_VERBOSE;
            switch (level)
            {
                case 'V':
                    gtLevel = LOG_VERBOSE;
                    break;
                case 'D':
                    gtLevel = LOG_DEBUG;
                    break;
                case 'I':
                    gtLevel = LOG_INFO;
                    break;
                case 'W':
                    gtLevel = LOG_WARNING;
                    break;
                case 'E':
                    gtLevel = LOG_ERROR;
                    break;
                default: break;
            }

            tag = matcher.group(2);
            pid = Integer.parseInt(matcher.group(3));
            msg = originalLine.substring(matcher.end());

            log(pid, gtLevel, tag, msg, sTime);
        }
    }

    public  void log(long pid, int level, String tag, String msg, String sTime) {
        if (level < Log_Level) {
            return;
        }
        if (level > LOG_ERROR || null == tag || null == msg)
        {
            return;
        }
        char sLevel = 'V';
        switch (level) {
            case LOG_VERBOSE:
                sLevel = 'V';
                break;
            case LOG_DEBUG:
                sLevel = 'D';
                break;
            case LOG_INFO:
                sLevel = 'I';
                break;
            case LOG_WARNING:
                sLevel = 'W';
                break;
            case LOG_ERROR:
                sLevel = 'E';
                break;
            default: break;
        }
        try {
            mWriter = new FileWriter(mTargetFile,true);
//            mWriter.write(pid + " " + sLevel + " " + tag + " " + msg + " " + sTime + "\n");
            mWriter.write(pid + "||" + sLevel + "||" + tag + "||" + msg + "||" + deviceYear + sTime + "\n");
            mWriter.flush();
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
