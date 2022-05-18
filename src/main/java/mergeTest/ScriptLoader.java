package mergeTest;

import Tree.ActivityTreeNode;
import io.appium.java_client.AppiumDriver;
import util.AddressUtil;
import util.OSUtil;
import util.PrintUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by homer on 16-11-8.
 */
public class ScriptLoader extends ClassLoader{
	public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
    private byte[] results;
    private boolean isPreposition = false;

    public ScriptLoader(String taskId, String scriptName, boolean isPreposition) {
        this.isPreposition = isPreposition;
        compileJava(taskId, scriptName);
        results = loadClassFile(taskId, scriptName);
    }
    public ScriptLoader(String taskId, String scriptName) {
        this(taskId, scriptName, false);
    }


    private byte[] loadClassFile(String taskId, String scriptName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String classPath;
        if (isPreposition) {
            classPath = AddressUtil.getPrepositionScriptClassPath(taskId, scriptName);
        } else {
            classPath = AddressUtil.getScriptClassPath(taskId, scriptName);
        }
        byte[] data = new byte[1024 * 256];
        try {
            FileInputStream fi = new FileInputStream(classPath);
            BufferedInputStream bis = new BufferedInputStream(fi);
            int ch = 0;
            while ((ch = bis.read(data, 0, data.length)) != -1) {
                    bos.write(data, 0, ch);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bos.toByteArray();
    }



    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            if (getParent() != null) {
                try {
                    //父加载器加载，如果不能加载就抛出异常
                    clazz = getParent().loadClass(name);
                } catch (Exception e) {

                }
            }
            if (clazz == null) {
                clazz = defineClass(name, results, 0, results.length);
            }
        }
        return clazz;
    }

    public boolean executeJava(AppiumDriver driver, ActivityTreeNode root, String packageName, String udid) {
        try {
            Class<?> clazz = loadClass("com.mooctest.Main");
            Object o = clazz.newInstance();
            //通过反射机制

            Method method = clazz.getDeclaredMethod("test", AppiumDriver.class);
            method.setAccessible(true);
            method.invoke(o, driver);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void compileJava(String taskId, String scriptName) {
        String path;
        if (isPreposition) {
            path = AddressUtil.getPrepositionScriptFilePath(taskId, scriptName);
        } else {
            path = AddressUtil.getScriptFilePath(taskId, scriptName);
        }
        String command;
        if(OSUtil.isWin()) {
            command = "Commands\\win\\compile.bat " + path;
        }else {
            command = OSUtil.getCmd() + " Commands/compile.sh " + path;
        }
        PrintUtil.print("command is " + command, TAG);
        OSUtil.runCommand(command);
    }
}
