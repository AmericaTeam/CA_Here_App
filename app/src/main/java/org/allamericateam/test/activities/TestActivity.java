package org.allamericateam.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.docusign.androidsdk.DocuSign;
import com.docusign.androidsdk.delegates.DSAuthenticationDelegate;
import com.docusign.androidsdk.exceptions.DSAuthenticationException;
import com.docusign.androidsdk.exceptions.DocuSignNotInitializedException;
import com.docusign.androidsdk.listeners.DSLogoutListener;

import org.allamericateam.test.R;
import org.allamericateam.test.databinding.ActivityTestBinding;

public class TestActivity extends BaseActivity {

    ActivityTestBinding binding;

    Boolean clearCachedData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        binding.setTestVM(this);
    }



    public void doLogoutClick(){



        try {
            DSAuthenticationDelegate docusignAuthDelegate = DocuSign.getInstance().getAuthenticationDelegate();
            docusignAuthDelegate.logout(this, clearCachedData, new DSLogoutListener() {
                @Override
                public void onSuccess() {
                    // TODO: handle successful logout
                    Toast.makeText(TestActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TestActivity.this, MainScreenActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(@NonNull DSAuthenticationException e) {
                    // TODO: handle logout failure here
                    Toast.makeText(TestActivity.this, "Error encountered", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (DocuSignNotInitializedException exception) {
            // TODO: handle error. This means the SDK object was not properly initialized
        }


    }

}