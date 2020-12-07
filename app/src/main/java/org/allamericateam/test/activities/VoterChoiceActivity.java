package org.allamericateam.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.allamericateam.test.R;
import org.allamericateam.test.adapters.VoterChoiceAdapter;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivityVoterChoiceBinding;

import java.util.ArrayList;

public class VoterChoiceActivity extends AppCompatActivity {

    private final int VOTER_CHOOSE_REQUEST = 100;
    private final int UNREGISTERED_VOTER_REQUEST = 104;
    private final int RESULT_OK = 200;
    private final int RESULT_CANCEL = 400;

    ActivityVoterChoiceBinding binding;

    private static ArrayList<VoterInfo> votersList = new ArrayList<VoterInfo>();

    public static Context mContext;



    private String lookupOption = "registered";     // default lookup option


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voter_choice);
        binding.setVoterChoiceVM(this);
        mContext = this;


        Intent intent = getIntent();
        if(intent != null){
            lookupOption = intent.getStringExtra("lookup_option");

            if (lookupOption == null) {     // set default option value
                lookupOption = "registered";
            }


        }



        fetchData();





        /* draw divider */
        binding.rcVotersList.setLayoutManager(new LinearLayoutManager(this));
        binding.rcVotersList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = binding.rcVotersList;
        VoterChoiceAdapter adapter = new VoterChoiceAdapter(votersList);
        recyclerView.setAdapter(adapter);
        binding.executePendingBindings();


    }

    private void fetchData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();


        // query data
        ValueEventListener voterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("firebase", "Key: " + snapshot.getChildrenCount());
                votersList.clear();


                for(DataSnapshot voters : snapshot.getChildren()){
                    String key = voters.getKey();
                    VoterInfo voterInfo = voters.getValue(VoterInfo.class);

                    if(lookupOption.equals("unregistered")){
                        if(voterInfo != null && !voterInfo.registered){
                            votersList.add(voterInfo);
                        }
                    }else if(lookupOption.equals("registered")){
                        if(voterInfo != null && voterInfo.registered){
                            votersList.add(voterInfo);
                        }
                    }


                    Log.d("firebase", "key: " + key + "\t value: " + voterInfo.toString());
                }

                setupRecyclerView();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase",error.getMessage());
            }
        };

        Query users = firebaseDatabase.getReference().child("users");
        users.addListenerForSingleValueEvent(voterListener);


    }



}