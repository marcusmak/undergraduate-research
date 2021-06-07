package hk.ust.mtrec.multisensorcollector.persistence.upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by tanjiajie on 2/15/17.
 */
public interface FileUploadService {

    @Multipart
    @POST("data/upload.action")
    Call<ResponseBody> upload(
            @Part("macAddr") RequestBody macAddr,
            @Part("androidId") RequestBody androidId,
            @Part("filePath") RequestBody filePath,
            @Part MultipartBody.Part file
    );

}

