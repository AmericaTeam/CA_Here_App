package org.allamericateam.test.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.allamericateam.test.activities.VoterChoiceActivity;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivityVoterChoiceBinding;
import org.allamericateam.test.databinding.ItemVotersBinding;

import java.util.ArrayList;
import java.util.List;

public class VoterChoiceAdapter extends RecyclerView.Adapter<VoterChoiceAdapter.VoterViewHolder>{

    private ArrayList<VoterInfo> voterList;


    private final int VOTER_CHOOSE_REQUEST = 100;
    private final int UNREGISTERED_VOTER_REQUEST = 104;
    private final int RESULT_OK = 200;
    private final int RESULT_CANCEL = 400;


    public VoterChoiceAdapter(ArrayList<VoterInfo> voterList) {
        this.voterList = voterList;
    }

    @NonNull
    @Override
    public VoterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemVotersBinding itemBinding = ItemVotersBinding.inflate(layoutInflater, parent, false);

        return new VoterViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoterViewHolder holder, int position) {
        VoterInfo voter = voterList.get(position);
        holder.bind(voter);

    }

    @Override
    public int getItemCount() {
        return voterList != null ? voterList.size() : 0;
    }




    public class VoterViewHolder extends RecyclerView.ViewHolder {
        ItemVotersBinding binding;

        public VoterViewHolder(@NonNull ItemVotersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setItemVM(this);

        }

        public void doChooseVoter(VoterInfo voter){
            View view = binding.getRoot();

            Context rootViewContext = VoterChoiceActivity.mContext;

            Intent intent = new Intent();
            if(voter != null){
                intent.putExtra("voter_info", new Gson().toJson(voter));
                ((VoterChoiceActivity)rootViewContext).setResult(RESULT_OK, intent);
                ((VoterChoiceActivity)rootViewContext).finish();
            }


        }

        public void bind(VoterInfo voter){
            binding.setVoter(voter);
            binding.executePendingBindings();
        }
    }
}
