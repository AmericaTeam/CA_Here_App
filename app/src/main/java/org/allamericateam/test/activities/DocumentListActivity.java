package org.allamericateam.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSTemplateDelegate;
import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.dsmodels.DSTemplates;
import com.docusign.androidsdk.dsmodels.DSTemplatesFilter;
import com.docusign.androidsdk.exceptions.DSTemplateException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSCacheTemplateListener;
import com.docusign.androidsdk.listeners.DSTemplateListListener;

import org.allamericateam.test.R;
import org.allamericateam.test.adapters.DocsAdapter;
import org.allamericateam.test.databinding.ActivityDocumentListBinding;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DocumentListActivity extends AppCompatActivity {

    private ActivityDocumentListBinding binding;
    private int totalNum = 0;

    private static List<DSTemplate> docsList = null;
    private boolean isRvVisible = true;

    public int getTotalNum() {
        return docsList != null ? docsList.size() : 0;
    }

    public boolean getIsRvVisible(){
        return isRvVisible;
    }

    public void setIsRvVisible(boolean flag){
        isRvVisible = flag;
    }

    DSTemplateDelegate templateDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_list);




        /* draw divider */
        binding.rcDocumentList.setLayoutManager(new LinearLayoutManager(this));
        binding.rcDocumentList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        try {
            templateDelegate = DocuSign.getInstance().getTemplateDelegate();
        }catch (DocuSignNotInitializedException e){
            e.printStackTrace();
        }

        getTemplates();


    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = binding.rcDocumentList;
        DocsAdapter adapter = new DocsAdapter(docsList);
        recyclerView.setAdapter(adapter);
//        totalNum = Integer.toString(docsList.size());
        totalNum = docsList.size();
        binding.setDocumentListVM(this);
        binding.executePendingBindings();


    }

    void getTemplates(){

        int count = 10;
        int startPosition = 0;
        //final DSTemplateDelegate templateDelegate = DocuSign.getInstance().getTemplateDelegate();
        DSTemplatesFilter filter = new DSTemplatesFilter(count, null, null, startPosition);


        // count is no. of templates to be retrieved
        // startPosition is the startPosition/index of the templates retrieval.
            templateDelegate.getTemplates(filter, new DSTemplateListListener() {
                @Override
                public void onStart() {
                    // TODO: Handle when the templates retrieval process is started

                }

                @Override
                public void onComplete(DSTemplates templates) {
                    // TODO: Handle templates that are retrieved
                    docsList = templates.getTemplates();
                    setupRecyclerView();
                    cache();
                    retrieveDownloadedTemplates();
                    binding.pbLoading.setVisibility(View.GONE);

                }

                @Override
                public void onError(DSTemplateException e) {
                    // TODO: Handle error when there is an exception while retrieving templates
                    setIsRvVisible(false);
                    e.printStackTrace();
                    binding.executePendingBindings();
                }
            }); // end of templateDelegate.getTemplates

    }


    void retrieveDownloadedTemplates(){
        templateDelegate.retrieveDownloadedTemplates(new DSTemplateListListener() {
            @Override
            public void onStart() {
                
            }

            @Override
            public void onComplete(@NotNull DSTemplates dsTemplates) {
                Toast.makeText(DocumentListActivity.this, "Template download has been completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NotNull DSTemplateException e) {
                e.printStackTrace();
                Toast.makeText(DocumentListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void cache(){


            //DSTemplateDelegate templateDelegate = DocuSign.getInstance().getTemplateDelegate();

        if(docsList != null){
            for(int i=0; i<docsList.size(); i++){
                final DSTemplate template = docsList.get(i);

                templateDelegate.cacheTemplate(template.getTemplateId(), new DSCacheTemplateListener() {
                    @Override
                    public void onStart() {
                        // Toast.makeText(DocumentListActivity.this, "Cache start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(@NotNull DSTemplate dsTemplate) {
                        // Toast.makeText(DocumentListActivity.this, "Doc "+ template.getTemplateName() +"is cached", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onError(@NotNull DSTemplateException e) {
                        e.printStackTrace();
                    }
                });


            }
        }






    }
}