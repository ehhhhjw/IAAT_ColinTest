package network.http;

import okhttp3.*;
import org.seleniumhq.jetty7.http.MimeTypes;
import util.PrintUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by homer on 16-9-16.
 */
public class HttpBusiness {
    private static OkHttpClient client = new OkHttpClient();
    public static final String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();

    public static void sendScreenShots(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getScreenShotUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendTreeImgs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getTreeImgsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendExceptionLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getExceptionLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendDeviceInfos(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getDeviceInfosUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendTestLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getTestLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendCpuLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getCpuLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendMemLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getMemLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendNetworkLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getNetworkLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void sendSMLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getSMLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendInstallLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getInstallLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendCoverInstallLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getCoverInstallLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendLaunchLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getLaunchLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendUninstallLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getUninstallLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendExecErrorLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getExecErrorLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendBatteryTempLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getBatteryTempLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendAppiumLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getAppiumLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendMyLogs(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getMyLogsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void sendTestScripts(String taskId, String deviceId, File uploadFile, final Callback callback) {
        String url = new UrlUtil().getTestScriptsUrl(taskId, deviceId);
        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.FORM_ENCODED), uploadFile);
        RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", uploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
       .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
    public static void updateDeviceStats(String deviceStats, final Callback callback) {
        String url = new UrlUtil().getUpdateDeviceStatusUrl();
//        PrintUtil.print("DeviceStats: " + deviceStats, TAG);
//        System.err.println("url: " + url);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.TEXT_JSON), deviceStats);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), deviceStats);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void updateTaskStatus(String taskId,String deviceId, final Callback callback) {
        String url = new UrlUtil().getUpdateTaskStatusUrl(taskId,deviceId);
        PrintUtil.print("url: " + url, TAG);
        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.TEXT_PLAIN),"finish");
//        Request request = new Request.Builder().url(url).patch(body).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
    
//    public static void updateTaskStatus2(String taskId,String deviceId, final Callback callback) {
//        String url = new UrlUtil().getAhost() + "api/v1/tasks/" + taskId + "/devices/" + deviceId;
//        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.TEXT_PLAIN),"finish");
//        Request request = new Request.Builder().url(url).patch(body).build();
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                callback.onSuccess(response.body().string());
//            }
//        });
//    }

//    public static void registerService(String ip, String port, final Callback callback) {
//        String url = new UrlUtil().getRegisterUrl();
////        String url = new UrlUtil().getAhost() + "api/v1/pcs/";
//        PrintUtil.print("url: " + url, TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.TEXT_PLAIN),ip + ":" + port);
//        Request request = new Request.Builder().url(url).post(body).build();
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                callback.onSuccess(response.body().string());
//            }
//        });
//    }

//    public static void registerDevices(String pcId, List<FrontDevice> devices, final Callback callback) {
//        String url = new UrlUtil().getRegisterDevicesUrl(pcId);
////    	String url = new UrlUtil().getAhost() + "api/v1/pcs/" + pcId + "/devices/";
//        Gson gson = new Gson();
//        PrintUtil.print("url: " + url + gson.toJson(devices), TAG);
//        RequestBody body = RequestBody.create(MediaType.parse(MimeTypes.TEXT_JSON), gson.toJson(devices));
//        Request request = new Request.Builder().url(url).post(body).build();
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure();
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                callback.onSuccess(response.body().string());
//            }
//        });
//    }
}
