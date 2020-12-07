package org.allamericateam.test.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import org.allamericateam.test.R;
import org.allamericateam.test.databinding.ActivityInitialBinding;

public class InitialActivity extends BaseActivity {

    ActivityInitialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_initial);
        binding.setInitialVM(this);


//        if(isLogin){
//            Intent intent = new Intent(SplashActivity.this, MainScreenActivity.class);
//            startActivity(intent);
//        }


    }


    public void login(){
        super.docusignLogin();

    }

    public void doOpenSourceNotice(){
        Intent intent = new Intent(InitialActivity.this, OpenSourceNoticeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent(InitialActivity.this, MainScreenActivity.class);
        startActivity(intent);
    }

    public void doShowMap(){
        Intent intent = new Intent(InitialActivity.this, MapActivity.class);
        startActivity(intent);
    }

}