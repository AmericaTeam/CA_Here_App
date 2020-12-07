package org.allamericateam.test.activities;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;

import org.allamericateam.test.R;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivityMainScreenBinding;

public class MainScreenActivity extends BaseActivity {

    ActivityMainScreenBinding binding;

    private final int VOTER_CHOOSE_REQUEST = 100;
    private final int UNREGISTERED_VOTER_REQUEST = 104;
    private final int RESULT_OK = 200;
    private final int RESULT_CANCEL = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_screen);
        binding.setMainScreenVM(this);
    }


    public void doVoterData(){
        Intent intent = new Intent(MainScreenActivity.this, CollectVoterDataActivity.class);
        startActivity(intent);
    }


    public void showDocumentsForCirculator(){
        Intent intent = new Intent(MainScreenActivity.this, DocumentListActivity.class);
        intent.putExtra("Role", "Circulator");
        startActivity(intent);
    }

    public void doLogout(){
        super.docusignLogout();
        Intent intent = new Intent(MainScreenActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    public void showDocumentsForVoters(){
        Intent intent = new Intent(MainScreenActivity.this, DocumentListActivity.class);
        intent.putExtra("Role", "Circulator");
        startActivity(intent);
    }

    public void doShowSummary(){
        Intent intent = new Intent(MainScreenActivity.this, SummaryActivity.class);
        startActivity(intent);
    }

    public void doSendEmailForUnregisteredVoter(){

        Intent intent = new Intent(MainScreenActivity.this, VoterChoiceActivity.class);
        intent.putExtra("lookup_option", "unregistered");
        startActivityForResult(intent, UNREGISTERED_VOTER_REQUEST);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == UNREGISTERED_VOTER_REQUEST){

            String jsonObj = "";
            if(data != null){
                jsonObj = data.getStringExtra("voter_info");
            }

            VoterInfo voter = new Gson().fromJson(jsonObj, VoterInfo.class);


            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"+voter.email)); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, voter.email);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.main_screen_email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.main_screen_email_content));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        }


    }
}