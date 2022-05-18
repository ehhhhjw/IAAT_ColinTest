package network.handler;

import com.google.gson.reflect.TypeToken;
import model.Task;
import network.http.MultipartFilter;
import org.seleniumhq.jetty7.server.Request;
import org.seleniumhq.jetty7.servlet.ServletContextHandler;
import service.PCService;
import util.JsonUtl;
import util.PrintUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by homer on 16-9-18.
 */
public class TaskHandler extends ServletContextHandler{
    private final String targetPath = "/api/v1/tasks";
    public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();;
    private MultipartFilter filter;
    public TaskHandler() {
        super();
        setContextPath(targetPath);
        filter = new MultipartFilter();
    }
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintUtil.print("do handle " + baseRequest.getRequestURI(), TAG);
        String path = baseRequest.getRequestURI();
        if ((targetPath + "/").equals(path)) {
            handleInitTaskRequest(baseRequest, request, response);
        } else if(path.startsWith(targetPath + "/q/")){
        	handleQueryTaskRequest(baseRequest, request, response);
        	//     handleFinishTaskRequest(baseRequest, request, response);
        }else if(path.startsWith(targetPath + "/abort/")){
        	handleAbortTaskRequest(baseRequest, request, response);
        }else if(path.startsWith(targetPath + "/result/")){
        	handleResultTaskRequest(baseRequest, request, response);
        }else{
        	handleFinishTaskRequest(baseRequest, request, response);	//不确定这个作用
        }

    }
    private void handleInitTaskRequest(Request baseRequest,
                                       HttpServletRequest request, final HttpServletResponse response)
                                                                                    throws IOException, ServletException{
        filter.doFilter(request, response, new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest,
                                 ServletResponse servletResponse) throws IOException, ServletException {
                String task = servletRequest.getParameter("task");
                String devices = servletRequest.getParameter("deviceIds");
                String apkName = servletRequest.getParameter("apk");
                File apk = (File)servletRequest.getAttribute("apk");
//                String scriptName = servletRequest.getParameter("script");
                String scriptName = "Main.java";
                String prepositionScriptName = servletRequest.getParameter("prepositionscript");
                File prepositionScript = (File) servletRequest.getAttribute("prepositionscript");
                File script = (File)servletRequest.getAttribute("script");
                Task taskObject = JsonUtl.fromJson(task, Task.class);
                Type type = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> deviceIds = JsonUtl.fromJson(devices, type);
                for (String udid : deviceIds) {
                    PrintUtil.print(" the devices id are " + udid, TAG);
                }
                PCService pcService = PCService.getInstance();
                boolean result = pcService.saveApk(apkName, apk);
                if (taskObject.isHasScript()) {
                    result |= pcService.saveScript(taskObject.getTaskID() + "", scriptName, script);
                }
                PrintUtil.print("the result is " + result + " " + apk.length(), TAG);
                if (taskObject.isHasPrepositionScript()) {
                    result |= pcService.savePrepositionScript(taskObject.getTaskID() + "", prepositionScriptName, prepositionScript);
                }
                pcService.coverageTest(taskObject, apkName, deviceIds);
                response.getWriter().append("success");
                response.getWriter().flush();
            }
        });
    }
    private void handleQueryTaskRequest(Request baseRequest,
            							HttpServletRequest request, HttpServletResponse response)
            															throws IOException, ServletException {
		String path = baseRequest.getRequestURI();
		String[] params = path.split("/");
		int end = params.length;
		String deviceId = params[end - 1];
		String taskId = params[end - 2];
		PrintUtil.print("Query task request " + " taskId " + taskId + " deviceId " + deviceId, TAG);
		int remainSeconds = PCService.getInstance().queryTaskStat(deviceId, taskId);	//下载-1、安装-2、运行 >0、运行失败（非正常结束）-3、结束-4
		response.getWriter().append(Integer.toString(remainSeconds));	//不确定这里具体应该怎么写
		response.getWriter().flush();  
    }
    
    private void handleAbortTaskRequest(Request baseRequest,
										HttpServletRequest request, HttpServletResponse response)
																		throws IOException, ServletException {
		String path = baseRequest.getRequestURI();
		String[] params = path.split("/");
		int end = params.length;
		String deviceId = params[end - 1];
		String taskId = params[end - 2];
		PrintUtil.print("Abort task request " + " taskId " + taskId + " deviceId " + deviceId, TAG);
		String result = PCService.getInstance().abortTask(deviceId, taskId);
		response.getWriter().append(result);
		response.getWriter().flush();  
    }
    private void handleResultTaskRequest(Request baseRequest,
										HttpServletRequest request, HttpServletResponse response)
																		throws IOException, ServletException {
		String path = baseRequest.getRequestURI();
		String[] params = path.split("/");
		int end = params.length;
		String deviceId = params[end - 1];
		String taskId = params[end - 2];
		PrintUtil.print("Result task request " + " taskId " + taskId + " deviceId " + deviceId, TAG);
		PCService.getInstance().finish();
		response.getWriter().append("success");
		response.getWriter().flush();  
    }
    private void handleFinishTaskRequest(Request baseRequest,
                                         HttpServletRequest request, HttpServletResponse response)
                                                                        throws IOException, ServletException {
        String path = baseRequest.getRequestURI();
        String[] params = path.split("/");
        int end = params.length;
        String taskId = params[end - 1];
        PrintUtil.print("finish task request " + taskId, TAG);
        PCService.getInstance().finish();
        response.getWriter().append("success");
        response.getWriter().flush();
    }
}
