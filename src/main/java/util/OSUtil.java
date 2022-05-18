package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by homer on 16-9-20.
 */
public class OSUtil {
    public static boolean isWin() {
        String os = System.getProperty("os.name");
        boolean isWin = os.startsWith("win") || os.startsWith("Win");
        return isWin;
    }
    public static String getCmd() {
        if (isWin()) {
            return "cmd";
        } else {
            return "bash";
        }
    }
    public static void runCommandAsyn(String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static public void executeCommand(String[] commandArr) {
        print("Linux command: "
                + java.util.Arrays.toString(commandArr));

        try {
            ProcessBuilder pb = new ProcessBuilder(commandArr);
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            print("Process started !");

            String line;
            while ((line = in.readLine()) != null) {
                print(line);
            }

            proc.destroy();
            print("Process ended !");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static String runCommand(String[] commands) {
        if (commands == null || commands.length == 0) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader isReader = null;
        try {
//            Process proc = Runtime.getRuntime().exec(command);
//            is = proc.getInputStream();
//            isReader = new InputStreamReader(is, "utf-8");
//            br = new BufferedReader(isReader);
//            String result = "";
//            line = br.readLine();
//            while ((line = br.readLine()) != null) {
//                print(" the read is " + result);
//                result += (line + "\n");
//            }
//            return result;
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            is = proc.getInputStream();
            isReader = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isReader);
            String result = "";
            while ((line = br.readLine()) != null) {
                result += (line + "\n");
            }
            proc.destroy();
            return result;
        } catch (IOException e) {
            return line;
        } finally {
            if (isReader != null) {
                try {
                    isReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO
                }
            }
        }
    }
    public static String runCommand(String command) {
        return runCommand(command.split(" "));
    }
	private static void print(String msg) {
		String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(df.format(date) + " " + TAG + " - " + msg);
	}
    public static Process exec(List<String> args) throws IOException {
        String[] arrays = new String[args.size()];

        for (int i=0; i<args.size(); i++) {
            arrays[i] = args.get(i);
        }
        return Runtime.getRuntime().exec(arrays);
    }
}
