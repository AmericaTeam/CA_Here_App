package org.allamericateam.test.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.allamericateam.test.R;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivityCollectVoterDataBinding;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.internal.http.HttpHeaders;

public class CollectVoterDataActivity extends BaseActivity {

    ActivityCollectVoterDataBinding binding;

    public String dateOfBirth;
    static Bundle instanceState;
    static Context applicationContext;

    static VoterInfo.DOB dob;

    static ArrayList<String> arrayIndex = new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collect_voter_data);
        binding.setCollectVoterDataVM(this);
        applicationContext = this;



    }



    // TODO: 2020-08-26 upload these fields to firebase
    public void uploadData(){



        boolean isRegister = false;
        String firstName = "";
        String middleName = "";
        String lastName = "";
        String email = "";
        String dlId = "";
        String ssnLast4Digit = "";


        FirebaseDatabase database = FirebaseDatabase.getInstance();


        if(!checkValidity()){
            Toast.makeText(this, "Besides middle name, all fields are required", Toast.LENGTH_SHORT).show();
            
            return;
        }else{

            int radioButtonID = binding.rgRegisteredVoter.getCheckedRadioButtonId();
            View selectedRadioBtn = binding.rgRegisteredVoter.findViewById(radioButtonID);
            int idx = binding.rgRegisteredVoter.indexOfChild(selectedRadioBtn);
            RadioButton r = (RadioButton) binding.rgRegisteredVoter.getChildAt(idx);


            firstName = binding.etFirstName.getText().toString();
            middleName = binding.etMiddleName.getText().toString();
            lastName = binding.etLastName.getText().toString();
            email = binding.etEmail.getText().toString();
            ssnLast4Digit = binding.etSsnLast4digit.getText().toString();
            dlId = binding.etDlId.getText().toString();
            if(idx == 0){
                isRegister = true;
            }else if(idx == 1){
                isRegister = false;
            }

            VoterInfo.Builder builder = new VoterInfo.Builder()
                    .isRegistered(isRegister)
                    .firstName(firstName)
                    .middleName(middleName)
                    .lastName(lastName)
                    .email(email)
                    .ssnLast4Digit(ssnLast4Digit)
                    .dlId(dlId)
                    .dob(dob);      // this dob value is updated when selectDOB() called

            VoterInfo voterInfo = builder.build();

            DatabaseReference myRef = database.getReference("users/"+dlId);
            DatabaseReference dobRef = database.getReference("users/"+dlId+"/dob");
            myRef.setValue(voterInfo);
            dobRef.setValue(dob);

            Toast.makeText(applicationContext, "Saved registered voter info", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }

    }

    boolean checkValidity(){
        return (!"".equals(binding.etFirstName.getText().toString())
        && !"".equals(binding.etLastName.getText().toString())
        && !"".equals(binding.etEmail.getText().toString())
        && !"".equals(binding.etSsnLast4digit.getText().toString())
        && !"".equals(binding.etDlId.getText().toString())
        && !"".equals(binding.etDob.getText().toString()));
    }




    public VoterInfo.DOB selectDOB(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                binding.etDob.setText("" + (i1+1) + "/" + i2 + "/" + i);
                dob = new VoterInfo.DOB( i2, (i1+1), i);
            }
        };
        DatePickerFragment datePickerFragment = new DatePickerFragment(listener);

        datePickerFragment.onCreateDialog(instanceState).show();

        return dob;
    }

    public static class DatePickerFragment extends DialogFragment{
        DatePickerDialog.OnDateSetListener listener;
        public DatePickerFragment(DatePickerDialog.OnDateSetListener listener) {
            super();
            this.listener = listener;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(applicationContext, listener, year, month, day);
        }

    }

}