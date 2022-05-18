package network.http;

import network.handler.TaskHandler;
import util.PrintUtil;

/**
 * Created by homer on 16-9-16.
 */
public class HttpServer {
    private static HttpServer instance;
    public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
    private int port;

    /**
     * １建立长链接 ?
     *  1.2 直接使用http,然后向server发生一个register请求，然后在进行交互
     * 2 服务器发送一个task
     * 3　假装执行
     * 4 发送http post上传数据
     */

    private HttpServer(int port) {
        PrintUtil.print("start server stub", TAG);
        this.port = port;
        initHttpServer();
    }
    public static HttpServer getInstance(int port) {
        if (instance == null) {
            instance = new HttpServer(port);
        }
        return instance;
    }

    private void initHttpServer() {
        new Thread(new ServerRunnable()).start();
    }


    class ServerRunnable implements Runnable {
        @Override
        public void run() {
            org.seleniumhq.jetty7.server.Server server = new org.seleniumhq.jetty7.server.Server(port);
            server.setHandler(new TaskHandler());
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
