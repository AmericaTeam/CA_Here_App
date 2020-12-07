package org.allamericateam.test.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardStatusAdapter extends RecyclerView.Adapter<DashboardStatusAdapter.DashboardStatusViewHolder>{

    // declare list

    public DashboardStatusAdapter() {
        super();
    }

    @NonNull
    @Override
    public DashboardStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardStatusViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DashboardStatusViewHolder extends RecyclerView.ViewHolder{

        public DashboardStatusViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
