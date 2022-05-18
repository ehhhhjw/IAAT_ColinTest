package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtil {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static void printWithColor(String msg, String TAG, String color) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(color + df.format(date) + " " + TAG + " - " + msg + ANSI_RESET);
	}
	public static void print(String msg, String TAG, String udid, String color) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(color + df.format(date) + " " + TAG + " [" + udid + "] - " + msg + ANSI_RESET);
	}
	public static void print(String msg, String TAG, String udid, BufferedWriter bufferedWriter, String color) {
		SimpleDateFormat df1 = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(color + df1.format(date) + " " + TAG + " [" + udid + "] - " + msg + ANSI_RESET);
		try {
			bufferedWriter.write(df2.format(date) + " " + TAG + " - " + msg + "\n");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void print(String msg, String TAG) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(df.format(date) + " " + TAG + " - " + msg);
	}
	public static void print(String msg, String TAG, String udid) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(df.format(date) + " " + TAG + " [" + udid + "] - " + msg);
	}
	public static void print(String msg, String TAG, String udid, BufferedWriter bufferedWriter) {
		SimpleDateFormat df1 = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(df1.format(date) + " " + TAG + " [" + udid + "] - " + msg);
		try {
			bufferedWriter.write(df2.format(date) + " " + TAG + " - " + msg + "\n");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void printErr(String msg, String TAG) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.err.println(df.format(date) + " " + TAG + " - " + msg);
	}
	public static void printErr(String msg, String TAG, String udid) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.err.println(df.format(date) + " " + TAG + " [" + udid + "] - " + msg);
	}
	public static void printException(String TAG, String udid, Exception e) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.err.println(df.format(date) + " " + TAG + " [" + udid + "] - " + getStackTrace(e));
	}
    public static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
    public static String getDeviceTime(String udid){
    	String result = "";
    	String command = "adb -s " + udid + " shell echo ${EPOCHREALTIME:0:14}";
    	OSUtil.runCommand("adb devices");
    	result = OSUtil.runCommand(command);
    	result = result.replace(".", "");
    	result = result.trim();
    	return timeStamp2Date(result);
    }
    private static String timeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
