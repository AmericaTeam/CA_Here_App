package org.allamericateam.test.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSEnvelopeDelegate;
import com.docusign.androidsdk.delegates.DSSigningDelegate;
import com.docusign.androidsdk.delegates.DSTemplateDelegate;
import com.docusign.androidsdk.dsmodels.DSEnvelope;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaults;
import com.docusign.androidsdk.dsmodels.DSEnvelopeDefaultsUniqueRecipientSelectorType;
import com.docusign.androidsdk.dsmodels.DSEnvelopeRecipient;
import com.docusign.androidsdk.dsmodels.DSRecipientDefault;
import com.docusign.androidsdk.dsmodels.DSTemplate;
import com.docusign.androidsdk.dsmodels.DSTemplateDefinition;
import com.docusign.androidsdk.exceptions.DSEnvelopeException;
import com.docusign.androidsdk.exceptions.DSSigningException;
import com.docusign.androidsdk.exceptions.DSSyncException;
import com.docusign.androidsdk.exceptions.DSTemplateException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSCacheTemplateListener;
import com.docusign.androidsdk.listeners.DSComposeAndSendEnvelopeListener;
import com.docusign.androidsdk.listeners.DSGetCachedEnvelopeListener;
import com.docusign.androidsdk.listeners.DSSigningListener;
import com.docusign.androidsdk.listeners.DSSyncEnvelopeListener;
import com.docusign.androidsdk.listeners.DSTemplateListener;
import com.docusign.androidsdk.listeners.DSUseTemplateListener;
import com.docusign.androidsdk.models.DSUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.allamericateam.test.R;
import org.allamericateam.test.data.VoterInfo;
import org.allamericateam.test.databinding.ActivityDocumentDetailBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentDetailActivity extends BaseActivity{


    private ActivityDocumentDetailBinding binding;


    public final ObservableField<String> templateID = new ObservableField<>();
    public final ObservableField<String> templateName = new ObservableField<>();

    private DSTemplateDefinition definition;

    // template_id: String
    // template_name: String
    DSTemplateDelegate templateDelegate;
    DSEnvelopeDelegate envelopeDelegate;
    DSSigningDelegate signingDelegate;

    private DSEnvelopeDefaults envelopeDefaults = new DSEnvelopeDefaults();
    DSUser user = null;
    private static int errorCount = 0;

    List<DSRecipientDefault> recipientDefaultList = new ArrayList<DSRecipientDefault>();
    List<DSEnvelopeRecipient> envelopeRecipientList = new ArrayList<DSEnvelopeRecipient>();

    private final int VOTER_CHOOSE_REQUEST = 100;
    private final int RESULT_OK = 200;
    private final int RESULT_CANCEL = 400;

    private String role;
    private Context mContext;
    int dialogInputSelection = -1;

    private static VoterInfo voter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_detail);
        binding.setDocumentDetailVM(this);
        mContext = this;

        Intent intent = getIntent();
        templateID.set(intent.getStringExtra("template_id"));
        templateName.set(intent.getStringExtra("template_name"));
        role = intent.getStringExtra("role");

        binding.pbDocLoad.setVisibility(View.VISIBLE);
        user = super.getDocusignUser();

        if(role.equals("Circulator")){
            binding.radioRegisteredVoter.setVisibility(View.INVISIBLE);
            binding.radioCirculator.setChecked(true);


            // disable for choose a voter
            binding.btnChooseAVoter.setVisibility(View.GONE);
            binding.etRecipientName.setText(user.getName());
            binding.etRecipientEmail.setText(user.getEmail());

        }else if(role.equals("Voter")){
            binding.radioCirculator.setVisibility(View.INVISIBLE);
            binding.btnChooseAVoter.setVisibility(View.VISIBLE);
            binding.radioRegisteredVoter.setChecked(true);
            binding.btnChooseAVoter.setClickable(true);
        }

        if(user == null){
            Toast.makeText(this, "Login information is invalid. Please try again.", Toast.LENGTH_SHORT).show();
            Intent intentToMain = new Intent(this, MainScreenActivity.class);
            startActivity(intentToMain);
        }

        try {
            templateDelegate = DocuSign.getInstance().getTemplateDelegate();
            envelopeDelegate = DocuSign.getInstance().getEnvelopeDelegate();
            signingDelegate = DocuSign.getInstance().getSigningDelegate();

        }catch(DocuSignNotInitializedException e){
            e.printStackTrace();
        }



        //getTemplate();

        



    }

    public void addRecipient() {


        if("".equals(binding.etRecipientName.getText().toString()) || "".equals(binding.etRecipientEmail.getText().toString())){
            Toast.makeText(this, "Name and emails are mandatory required", Toast.LENGTH_LONG).show();
            binding.etRecipientName.requestFocus();
            return;
        }


        /*if("".equals(binding.etRecipientName) || "".equals(binding.etRecipientEmail)){
            Toast.makeText(DocumentDetailActivity.this, "Name and email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }*/

        /*try {

            DSEnvelopeRecipient envelopeRecipient = new DSEnvelopeRecipient.Builder()
                    .hostName(binding.etRecipientName.getText().toString())
                    .hostEmail(binding.etRecipientEmail.getText().toString())
                    .recipientId(1)
                    .routingOrder(1)
                    .signerName(docusignUser.getName())
                    .signerEmail(docusignUser.getEmail())
                    .type(DSRecipientType.IN_PERSON_SIGNER)
                    .build();

//            envelopeRecipientList.add(envelopeRecipient);
        }catch(DSEnvelopeException e){
            e.printStackTrace();
        }*/

        if(recipientDefaultList.size() > 1){
            recipientDefaultList.clear();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Confirm the recipient information")
                .setMessage("Name: " + binding.etRecipientName.getText().toString() + "\n" + "Email: " + binding.etRecipientEmail.getText().toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                        DSRecipientDefault recipientDefault = new DSRecipientDefault(binding.etRecipientEmail.getText().toString(),
                                binding.etRecipientName.getText().toString(),
                                docusignUser.getName(),
                                docusignUser.getEmail(),
                                (long) 1,
                                (binding.radioCirculator.isChecked()?"Circulator":
                                binding.radioRegisteredVoter.isChecked()?"Voter":"Circulator"),
                                DSEnvelopeDefaultsUniqueRecipientSelectorType.ROLE_NAME);

                        if(recipientDefaultList.size() > 1){
                            recipientDefaultList.clear();
                        }


                        recipientDefaultList.add(recipientDefault);
                        getTemplate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        binding.etRecipientEmail.setText(null);
                        binding.etRecipientName.setText(null);
                        binding.etRecipientName.requestFocus();
                        if(recipientDefaultList.size() > 0){
                            recipientDefaultList.remove(recipientDefaultList.size()-1);
                        }

                    }
                });

        final AlertDialog dialog = builder.create();
        builder.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });


    }


    private void getTemplate(){
        templateDelegate.getTemplate(templateID.get(), null, new DSTemplateListener() {
            @Override
            public void onComplete(@NotNull DSTemplateDefinition dsTemplateDefinition) {
                definition = dsTemplateDefinition;
                Log.d("Debug", dsTemplateDefinition.getDescription());
                Toast.makeText(DocumentDetailActivity.this, "Complete load template", Toast.LENGTH_SHORT).show();
                binding.pbDocLoad.setVisibility(View.GONE);

                templateDelegate.cacheTemplate(templateID.get(), new DSCacheTemplateListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(@NotNull DSTemplate dsTemplate) {
                        Log.d("CACHE", "Success! " + dsTemplate.getTemplateId());
                    }

                    @Override
                    public void onError(@NotNull DSTemplateException e) {
                        e.printStackTrace();
                    }
                });
                useTemplate();

            }

            @Override
            public void onError(@NotNull DSTemplateException e) {

                e.printStackTrace();
                binding.pbDocLoad.setVisibility(View.GONE);
                Toast.makeText(DocumentDetailActivity.this, "Error. Try again", Toast.LENGTH_SHORT).show();



            }
        });
    }

    public void doChooseVoter(){

        Intent intent = new Intent(DocumentDetailActivity.this, VoterChoiceActivity.class);
        startActivityForResult(intent, VOTER_CHOOSE_REQUEST);

    }

    public void doGoBackToHome(){
        Intent intent = new Intent(DocumentDetailActivity.this, MainScreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String jsonObj = "";
        if(data != null){
            jsonObj = data.getStringExtra("voter_info");
        }

        voter = new Gson().fromJson(jsonObj, VoterInfo.class);


        String recipientName = "";
        if(voter.firstName != null){
            recipientName += voter.firstName + " ";
        }

        if(voter.middleName != null && !voter.middleName.equals("") ){
            recipientName += voter.middleName + " ";
        }

        if(voter.lastName != null && !voter.lastName.equals("")){
            recipientName += voter.lastName;
        }

        if(role.equals("Voter")){       // this is trick code for "send" instead of "sign"
            recipientName += " ";
        }

        if(requestCode == VOTER_CHOOSE_REQUEST){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Result: " + voter.toString(), Toast.LENGTH_SHORT).show();

                binding.etRecipientName.setText(recipientName);
                binding.etRecipientEmail.setText(voter.email);
            }
        }
    }

    private void useTemplate(){
        // DSEnvelopeDefaults envelopeDefaults - This can be used to pre-fill the template values such as recipients, emails, tabs etc.
        // Refer to javadoc for more info about DSEnvelopeDefaults.

        // recipientDefaults: kotlin.collections.List<com.docusign.androidsdk.dsmodels.DSRecipientDefault>? /* = compiled code */,
        // emailSubject: kotlin.String? /* = compiled code */,
        // emailBlurb: kotlin.String? /* = compiled code */,
        // envelopeTitle: kotlin.String? /* = compiled code */,
        // tabValueDefaults: kotlin.collections.Map<kotlin.String, kotlin.Any>? /* = compiled code */,
        // customFields: com.docusign.androidsdk.dsmodels.DSCustomFields? /* = compiled code */


        String emailSubject = definition.getName();
        String emailBlurb = definition.getDescription();
        final String envelopeTitle = definition.getName();
        Map<String, String> tabValueDefaults = null;
        String customFields = null;


        /*DSRecipientDefault recipientDefault = new DSRecipientDefault("kmraen@gmail.com",
                "Docusign Hackathon",
                "Donghun Han",
                "kmraen@gmail.com",
                (long) 2,
                "Admin",
                DSEnvelopeDefaultsUniqueRecipientSelectorType.ROLE_NAME);

        recipientDefaultList.add(recipientDefault);*/

        envelopeDefaults = new DSEnvelopeDefaults(recipientDefaultList, emailSubject, emailBlurb, envelopeTitle, tabValueDefaults, null);
        try {
            binding.pbDocLoad.setVisibility(View.VISIBLE);





            templateDelegate.useTemplate(this, templateID.get(), envelopeDefaults, true, new DSUseTemplateListener(){
                @Override
                public void onComplete(final String envelopeId) {
                    // TODO: Handle when the template has been successfully signed.
                    Toast.makeText(DocumentDetailActivity.this, ""+envelopeId, Toast.LENGTH_SHORT).show();
                    binding.etRecipientEmail.setText("");
                    binding.etRecipientName.setText("");

                    recipientDefaultList.clear();
                    //todo
                    // make a dialog; Change status of the ballot as sent?
                    // yes - clear the fields
                    // no - clear the di
                    // update sent ballot status


                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Please confirm the change status of the ballot as sent");
                    final CharSequence[] items = {"Yes", "No", "cancel"};

                    builder.setSingleChoiceItems(items, dialogInputSelection, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {


                            if(voter == null){
                                return;
                            }

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference regRef = database.getReference("users/"+voter.dlId+"/registered");
                            DatabaseReference sentBallotRef = database.getReference("users/"+voter.dlId+"/sentBallot");

                            dialogInputSelection = item;
                            if(item == 0){      // yes
                                regRef.setValue(true);
                                sentBallotRef.setValue(true);

                            }else if(item == 1){ // no
                                regRef.setValue(false);
                                sentBallotRef.setValue(false);

                            }else if(item == 2){    // cancel
                                dialog.dismiss();
                            }

                            dialog.dismiss();

                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();






                    envelopeDelegate.getCachedEnvelope(envelopeId, new DSGetCachedEnvelopeListener() {
                        @Override
                        public void onComplete(@NotNull final DSEnvelope dsEnvelope) {
                            dsEnvelope.setEnvelopeName(envelopeTitle);
                                   /* dsEnvelope.getRecipients().get(0).getTabs().get(0);
                                    try {
                                        DSEnvelopeRecipient envelopeRecipient = new DSEnvelopeRecipient.Builder()
                                                .recipientId(3)
                                                .routingOrder(3)
                                                .signerName(docusignUser.getName())
                                                .signerEmail(docusignUser.getEmail())
                                                .tab(dsEnvelope.getRecipients().get(0).getTabs().get(0))
                                                .type(DSRecipientType.CARBON_COPY)
                                                .build();
                                        //envelopeRecipientList.add(envelopeRecipient);
                                    }catch (DSEnvelopeException e){
                                        e.printStackTrace();
                                    }*/

                            //dsEnvelope.setRecipients(envelopeRecipientList);



                            envelopeDelegate.composeAndSendEnvelope(dsEnvelope, new DSComposeAndSendEnvelopeListener() {
                                @Override
                                public void onSuccess(@NotNull String s) {
                                    envelopeDelegate.syncEnvelope(envelopeId, new DSSyncEnvelopeListener() {
                                        @Override
                                        public void onSuccess(@NotNull String s, @Nullable String s1) {

                                        }

                                        @Override
                                        public void onError(@NotNull DSSyncException e, @NotNull String s, @Nullable Integer integer) {
                                            e.printStackTrace();
                                        }
                                    }, false);
                                }

                                @Override
                                public void onError(@NotNull DSEnvelopeException e) {
                                    e.printStackTrace();
                                }
                            });


                            signingDelegate.sign(DocumentDetailActivity.this, dsEnvelope.getEnvelopeId(), true, new DSSigningListener() {
                                @Override
                                public void onSuccess(@NotNull String s) {
//                                    Toast.makeText(DocumentDetailActivity.this, "Signature Success!", Toast.LENGTH_SHORT).show();
                                    envelopeDelegate.composeAndSendEnvelope(dsEnvelope, new DSComposeAndSendEnvelopeListener() {
                                        @Override
                                        public void onSuccess(@NotNull String s) {

                                            envelopeDelegate.syncEnvelope(envelopeId, new DSSyncEnvelopeListener() {
                                                @Override
                                                public void onSuccess(@NotNull String s, @Nullable String s1) {
                                                    Toast.makeText(DocumentDetailActivity.this, "Document is sent", Toast.LENGTH_SHORT).show();
                                                    binding.pbDocLoad.setVisibility(View.INVISIBLE);

                                                }

                                                @Override
                                                public void onError(@NotNull DSSyncException e, @NotNull String s, @Nullable Integer integer) {
                                                    e.printStackTrace();
                                                }
                                            }, false);

                                        }

                                        @Override
                                        public void onError(@NotNull DSEnvelopeException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }

                                @Override
                                public void onCancel(@NotNull String s) {
                                    Toast.makeText(DocumentDetailActivity.this, "Signature Cancel", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(@NotNull DSSigningException e) {
                                    e.printStackTrace();
                                }
                            });

                        }

                        @Override
                        public void onError(@NotNull DSEnvelopeException e) {
                            e.printStackTrace();
                            onBackPressed();
                        }
                    });






                }

                @Override
                public void onCancel(String templateId, String envelopeId) {
                    // TODO: Handle when the signing ceremony is cancelled

                }

                @Override
                public void onError(DSTemplateException exception) {
                    // TODO: Handle error when there is an exception while using the template or during signing
                    exception.printStackTrace();
                }
            });



        }catch(DSSigningException e){
            e.printStackTrace();
            if(errorCount >= 3){
                errorCount = 0;
                Intent intent = new Intent(DocumentDetailActivity.this, DocumentListActivity.class);
                startActivity(intent);
            }

            Toast.makeText(DocumentDetailActivity.this, "Error encountered, please try again.", Toast.LENGTH_SHORT).show();
            errorCount++;
            useTemplate();

        }



    }
}