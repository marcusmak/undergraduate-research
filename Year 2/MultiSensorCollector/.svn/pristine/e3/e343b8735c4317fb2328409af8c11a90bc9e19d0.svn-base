package hk.ust.mtrec.multisensorcollector.persistence.upload;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hk.ust.mtrec.multisensorcollector.utils.JsonUtils;
import hk.ust.mtrec.multisensorcollector.utils.dev.DeviceUtils;
import hk.ust.mtrec.multisensorcollector.utils.file.FileUtils;
import hk.ust.mtrec.multisensorcollector.utils.http.ServiceGenerator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tanjiajie on 2/14/17.
 */
public class Uploader {

    public static final String BROADCAST_INTENT_FILE_UPLOAD_FINISH = "upload_finish";

    private static final int UPLOAD_STATUS_READY = 0;
    private static final int UPLOAD_STATUS_SUCCESS = 1;
    private static final int UPLOAD_STATUS_FAILURE = -1;

    private Context context;
    private ProgressDialog progressDialog;

    private List<UploadTask> uploadTaskList;

    private FileUploadService uploadService;

    public Uploader(Context context) {
        this.context = context;
        this.uploadService = ServiceGenerator.createService(FileUploadService.class);
    }

    public void doUploadJob() {
        createUploadTasks();

        this.progressDialog = new ProgressDialog(context);
        for (UploadTask task : uploadTaskList) {
            upload(task);
            Log.v("Upload", "Call upload(task) for file " + task.getFile().getAbsolutePath());
        }
        progressDialog.show();

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int successCount = countSpecificTypeInAllTasks(UPLOAD_STATUS_SUCCESS);
                int failureCount = countSpecificTypeInAllTasks(UPLOAD_STATUS_FAILURE);
                StringBuilder sb = new StringBuilder()
                        .append("Uploading ...\n")
                        .append("completed: ").append(successCount)
                        .append(", failed: ").append(failureCount)
                        .append(", total: ").append(uploadTaskList.size());
                progressDialog.setMessage(sb.toString());
                if (successCount + failureCount == uploadTaskList.size()) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "All Uploading tasks complete.", Toast.LENGTH_SHORT)
                            .show();
                    progressDialog = null;
                    context.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(BROADCAST_INTENT_FILE_UPLOAD_FINISH));
    }

    private void createUploadTasks() {
        File uploadDir = FileUtils.getDataCollectionDirectory();
        uploadTaskList = new ArrayList<>();
        Collection<File> files = org.apache.commons.io.FileUtils.listFiles(uploadDir, null, true);
        for (File file : files) {
            String relativePath = FileUtils.getRelativePath(file, uploadDir);
            UploadTask task = new UploadTask(file, relativePath);
            uploadTaskList.add(task);
        }
    }

    private UploadTask findUploadTask(Call<ResponseBody> call) {
        for (UploadTask task : uploadTaskList) {
            if (task.getCall().equals(call))
                return task;
        }
        return null;
    }

    private int countSpecificTypeInAllTasks(int type) {
        int count = 0;
        for (UploadTask task : uploadTaskList) {
            if (task.getStatus() == type)
                count++;
        }
        return count;
    }

    public void upload(UploadTask task) {
        Call<ResponseBody> call = task.getCall();
        if (call == null)
            return;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String data = null;
                try {
                    data = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("Upload", "response: " + data);
                try {
                    UploadTask task = findUploadTask(call);
                    RespMessage respMsg = JsonUtils.getMapper().readValue(
                            data,
                            Uploader.RespMessage.class);
                    if (respMsg.isSuccess()) {
//                    if (response.message().equals("ok")) {
                        task.setStatus(UPLOAD_STATUS_SUCCESS);
                        Toast.makeText(context, "Succeed in uploading " + task.getFile().getAbsolutePath(), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        task.setStatus(UPLOAD_STATUS_FAILURE);
                        Toast.makeText(context, "Fail to upload " + task.getFile().getAbsolutePath(), Toast.LENGTH_LONG)
                                .show();
                        Log.w("upload", "Uploading file " + task.getFile().getAbsolutePath() + " occurs some errors. Message: " + respMsg.getMessage());
                    }
                    context.sendBroadcast(new Intent(BROADCAST_INTENT_FILE_UPLOAD_FINISH));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Upload", e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload", t.getMessage());
            }
        });
    }

    private class UploadTask {

        private File file;
        private String filePath;
        private Call<ResponseBody> call;
        private int status;

        UploadTask(File file, String filePath) {
            this.file = file;
            this.filePath = filePath;
            this.call = buildCall();
            this.status = UPLOAD_STATUS_READY;
        }

        private Call<ResponseBody> buildCall() {
            RequestBody reqMacAddr = RequestBody.create(MultipartBody.FORM, DeviceUtils.getMacAddr(context));
            RequestBody reqAndroidId = RequestBody.create(MultipartBody.FORM, DeviceUtils.getAndroidId(context));
            RequestBody reqFilePath = RequestBody.create(MultipartBody.FORM, filePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("text/plain"), file);
            // finally, execute the request
            Call<ResponseBody> call = uploadService.upload(
                    reqMacAddr,
                    reqAndroidId,
                    reqFilePath,
                    MultipartBody.Part.createFormData("file", file.getName(), reqFile));
            return call;
        }

        public File getFile() {
            return file;
        }

        public Call<ResponseBody> getCall() {
            return call;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

    static class RespMessage {

        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
