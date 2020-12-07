package org.allamericateam.test.interfaces;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {


    @GET("users")
    Call<ResponseBody> getEmailList(
            @Query("email") String email);


}
