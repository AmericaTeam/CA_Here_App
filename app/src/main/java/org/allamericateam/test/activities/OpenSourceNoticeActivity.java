package org.allamericateam.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.allamericateam.test.R;
import org.allamericateam.test.databinding.ActivityOpenSourceNoticeBinding;

public class OpenSourceNoticeActivity extends BaseActivity {
    ActivityOpenSourceNoticeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_open_source_notice);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);

        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.loadUrl("https://github.com/docusign/mobile-android-sdk/blob/e2c7e707f304d888472c431c1c1e3deb2e798bc7/LICENSE.md");


    }


    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}