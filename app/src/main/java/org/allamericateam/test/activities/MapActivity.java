package org.allamericateam.test.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;

import org.allamericateam.test.R;
import org.allamericateam.test.databinding.ActivityMapBinding;

import static org.allamericateam.test.activities.BaseActivity.APP_KEY_ID;
import static org.allamericateam.test.activities.BaseActivity.APP_KEY_SECRET;

public class MapActivity extends BaseActivity {

    private ActivityMapBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //InitProvider.initialize(this);
        // setup key
        SDKOptions sdkOptions = new SDKOptions(APP_KEY_ID, APP_KEY_SECRET, this.getCacheDir().getPath());
        SDKNativeEngine sdkNativeEngine = new SDKNativeEngine(sdkOptions);
        SDKNativeEngine.setSharedInstance(sdkNativeEngine);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        binding.setMapVM(this);
        binding.mapView.onCreate(savedInstanceState);       //initialize HERE SDK

        loadMapScene();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    private void loadMapScene(){
        binding.mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback(){
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if(errorCode == null){
                    binding.mapView.getCamera().setTarget(new GeoCoordinates(37.332362, -121.894272));
                    binding.mapView.getCamera().setZoomLevel(14);
                }else{
                    Log.d(TAG, "onLoadScene failed: "+ errorCode.toString());
                }
            }
        });
    }
}