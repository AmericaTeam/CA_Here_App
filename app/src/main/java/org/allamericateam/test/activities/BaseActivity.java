package org.allamericateam.test.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.docusign.androidsdk.DSEnvironment;
import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSAuthenticationListener;
import com.docusign.androidsdk.listeners.DSLogoutListener;
import com.docusign.androidsdk.models.DSUser;
import com.docusign.androidsdk.util.DSMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.engine.InitProvider;

import org.allamericateam.test.R;
import org.jetbrains.annotations.NotNull;

public class BaseActivity extends AppCompatActivity {

    int requestCode = -1;
    static DSUser docusignUser;
    static boolean isLogin = false;
    static boolean clearCachedData = false;
    protected static String accessToken;
    protected static String refreshToken;
    protected static long tokenExpireDate;
    public static String TAG = "CA_Here_App";
    protected static String APP_KEY_ID;
    protected static String APP_KEY_SECRET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP_KEY_ID = getString(R.string.here_app_key_id);
        APP_KEY_SECRET = getString(R.string.here_app_key_secret);


        InitProvider.initialize(getApplicationContext());
        SDKOptions sdkOptions = new SDKOptions(APP_KEY_ID, APP_KEY_SECRET, this.getCacheDir().getPath());
        SDKNativeEngine sdkNativeEngine = new SDKNativeEngine(sdkOptions);
        SDKNativeEngine.setSharedInstance(sdkNativeEngine);

        if(!isLogin){

            //docusignLogin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.requestCode = requestCode;
    }

    static DSAuthenticationDelegate docusignAuthDelegate;

    void docusignLogin(){
        try {

            // initialize docusign
            // params[application context, integration key, client secret key, redirect uri]
            DocuSign.init(
                    this, // the Application Context
                    getString(R.string.docusign_integration_key), // recommend not hard-coding this,
                    getString(R.string.docusign_client_secret_key),
                    getString(R.string.redirect_uri),
                    DSMode.DEBUG // this controls the logging (logcat) behavior
            );
            String authClientId = DocuSign.getInstance().getOauthClientId();

            DocuSign.getInstance().setEnvironment(DSEnvironment.DEMO_ENVIRONMENT);






            docusignAuthDelegate = DocuSign.getInstance().getAuthenticationDelegate();
            Log.d("Debug/BaseActivity", ""+this.requestCode);






            docusignAuthDelegate.login(this.requestCode, this,
                    new DSAuthenticationListener() {
                        @Override
                        public void onSuccess(@NonNull DSUser user) {
                            // TODO: handle successful authentication here
                            docusignUser = user;
                            Log.d("Success!", "Success");



                            isLogin = true;
                            try {
                                accessToken = "Bearer " + docusignAuthDelegate.getAuthSession$androidsdk_release().getAccessToken();
                                refreshToken = docusignAuthDelegate.getAuthSession$androidsdk_release().getRefreshToken();
                                tokenExpireDate = docusignAuthDelegate.getAuthSession$androidsdk_release().getAccessTokenExpiryTime();
                            }catch (DSAuthenticationException e){
                                e.printStackTrace();
                            }
                            Toast.makeText(BaseActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BaseActivity.this, MainScreenActivity.class);
                            startActivity(intent);

                        }

                        @Override
                        public void onError(@NonNull DSAuthenticationException exception) {
                            // TODO: handle authentication failure here
                            Log.d("Error", exception.getMessage());
                            exception.printStackTrace();
                        }
                    }
            );


        }catch (DocuSignNotInitializedException e){
            e.printStackTrace();
            Log.d("PReady", "Docusign API is not initialized!");
        }
    }



    void docusignLogout(){


        docusignAuthDelegate.logout(this, clearCachedData, new DSLogoutListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(BaseActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                isLogin = false;

            }

            @Override
            public void onError(@NotNull DSAuthenticationException e) {

            }
        });

    }


    public DSUser getDocusignUser(){
        return isLogin? docusignUser:null;
    }
}