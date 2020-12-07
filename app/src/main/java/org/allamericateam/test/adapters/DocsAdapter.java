package org.allamericateam.test.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.docusign.androidsdk.dsmodels.DSTemplate;

import org.allamericateam.test.activities.DocumentDetailActivity;
import org.allamericateam.test.activities.DocumentListActivity;
import org.allamericateam.test.databinding.ItemDocsBinding;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.DocsViewHolder> {


    private List<DSTemplate> docsList;

    public DocsAdapter(List<DSTemplate> docsList) {
        this.docsList = docsList;
    }

    @NonNull
    @Override
    public DocsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDocsBinding itemBinding = ItemDocsBinding.inflate(layoutInflater, parent, false);

        return new DocsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsViewHolder holder, int position) {
        DSTemplate doc = docsList.get(position);
        holder.bind(doc);
    }

    @Override
    public int getItemCount() {
        return docsList != null ? docsList.size() : 0;
    }





    public class DocsViewHolder extends RecyclerView.ViewHolder {

        private ItemDocsBinding binding;
        public DocsViewHolder(@NonNull final ItemDocsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setItemVM(this);
        }

        public void doShowDetails(){
            View view = binding.getRoot();
            int docsPos = getAdapterPosition();
            String templateName = docsList.get(docsPos).getTemplateName();
            String templateId = docsList.get(docsPos).getTemplateId();
            Intent intent = new Intent(view.getContext(), DocumentDetailActivity.class);
            intent.putExtra("template_id", templateId);
            intent.putExtra("template_name", templateName);

            Pattern p = Pattern.compile("(Circulator)+");
            Pattern p2 = Pattern.compile("(Voter)+");

            Matcher m = p.matcher(templateName);
            Matcher m2 = p2.matcher(templateName);

            if(templateName.contains("Circulator")){
                intent.putExtra("role", "Circulator");
            }
            else if(templateName.contains("Voter")){
                intent.putExtra("role", "Voter");
            }

            Toast.makeText(view.getContext(), ""+docsList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            view.getContext().startActivity(intent);
        }

        public void bind(DSTemplate doc){
            binding.setDocuments(doc);
            binding.executePendingBindings();

        }



    }


}
