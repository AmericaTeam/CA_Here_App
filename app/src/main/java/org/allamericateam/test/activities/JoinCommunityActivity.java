package org.allamericateam.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.models.DSUser;

import org.allamericateam.test.R;
import org.allamericateam.test.databinding.ActivityJoinCommunityBinding;
import org.allamericateam.test.interfaces.RetrofitAPI;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinCommunityActivity extends BaseActivity {

    ActivityJoinCommunityBinding binding;
    DSAuthenticationDelegate authDelegate;

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;
    private Call<ResponseBody> mCallEmailList;
    public String textResult;

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_community);
        binding.setJoinCommunityVM(this);

        if(isLogin){
            setRetrofitInit();
        }else{
            super.docusignLogin();
        }


        try {
            authDelegate = DocuSign.getInstance().getAuthenticationDelegate();
        }catch (DocuSignNotInitializedException e){
            e.printStackTrace();
        }

        setTextResult("Waiting for look-up result");
        binding.executePendingBindings();

    }

    /* setting header */
    private void setRetrofitInit(){
        String uri = getString(R.string.docusign_base_uri) + "accounts/" + getDocusignUser().getAccountId() + "/";

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String token = BaseActivity.accessToken;

                Request newRequest = null;
                if(token != null && !"".equals(token)){
                    newRequest = chain.request().newBuilder().addHeader("Authorization", token)
                            .addHeader("Content-Type", "application/json").build();
                    long expirationDate = tokenExpireDate;
                    if(expirationDate <= System.currentTimeMillis()){
                        // token expired
                        // call refreshToken

                    }

                }


                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();
        mRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(uri)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }


    private Callback<ResponseBody> mRetrofitCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.d("ResponseBody", response.toString());
            String email = binding.etRecipientEmail.getText().toString();
            if(response.message().equals("Bad Request")) {

            }else if(response.message().equals("OK")){

            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            t.printStackTrace();
        }
    };

    public void lookup(){
        String email = binding.etRecipientEmail.getText().toString();
        mCallEmailList = mRetrofitAPI.getEmailList(email);
        mCallEmailList.enqueue(mRetrofitCallBack);

    }


    public void goMain(){
        Intent intent = new Intent(JoinCommunityActivity.this, MainScreenActivity.class);
        startActivity(intent);
    }
}