package org.allamericateam.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayMap;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.allamericateam.test.R;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivitySummaryBinding;

public class SummaryActivity extends AppCompatActivity {

    ActivitySummaryBinding binding;

    public static long totalNumVoters = 0;
    public static long numRegistered = 0;
    public static long numUnregistered = 0;
    public static long numBallotSent = 0;

    ObservableArrayMap userMap = new ObservableArrayMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        binding.setSummaryVM(this);

        initializeNum();
        fetchData();

    }

    private void initializeNum(){
        totalNumVoters = 0;
        numRegistered = 0;
        numUnregistered = 0;
        numBallotSent = 0;

        binding.tvNumBallotSent.setText("0");
        binding.tvNumUnregistered.setText("0");
        binding.tvNumRegistered.setText("0");
        binding.tvNumTotalVoters.setText("0");
    }

    private void fetchData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        ValueEventListener voterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("firebase", "Key: " + snapshot.getChildrenCount());

                totalNumVoters = snapshot.getChildrenCount();
                for(DataSnapshot voters : snapshot.getChildren()){
                    String key = voters.getKey();
                    VoterInfo voterInfo = voters.getValue(VoterInfo.class);
                    if(voterInfo.registered){
                        numRegistered++;
                    }else{
                        numUnregistered++;
                    }

                    if(voterInfo.sentBallot){
                        numBallotSent++;
                    }

                    updateSummary();


                    Log.d("firebase", "key: " + key + "\t value: " + voterInfo.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase",error.getMessage());
            }
        };

        Query users = database.getReference().child("users");
        users.addListenerForSingleValueEvent(voterListener);

    }

    private void updateSummary(){

        userMap.put("totalNumVoters", totalNumVoters);
        userMap.put("numRegistered", numRegistered);
        userMap.put("numUnregistered", numUnregistered);
        userMap.put("numBallotSent", numBallotSent);

        binding.setUserMap(userMap);
        binding.executePendingBindings();
    }
}