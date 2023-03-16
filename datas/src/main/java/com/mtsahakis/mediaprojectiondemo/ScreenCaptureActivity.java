package com.mtsahakis.mediaprojectiondemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.databinding.DataBindingUtil;

import com.mtsahakis.mediaprojectiondemo.databinding.ActivityScreenCaptureBinding;


public class ScreenCaptureActivity extends Activity {
    ActivityScreenCaptureBinding binding;
    private static final int REQUEST_CODE = 100;

    /****************************************** Activity Lifecycle methods ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras()!=null)
        {
            String parent_id =getIntent().getStringExtra("parent_id");
            String child_id =getIntent().getStringExtra("child_id" ) ;
            SharedPreferenceUtility.getInstance(getApplication()).putString("parent_id" ,parent_id);
            SharedPreferenceUtility.getInstance(getApplication()).putString("child_id"  ,child_id );
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_screen_capture);
        startProjection();
        // start projection

        binding.startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startProjection();
                Log.e("TAG", "onCreate: "+SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id"));
                Log.e("TAG", "onCreate: "+  SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id"));
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        // stop projection
        binding.stopButton.setOnClickListener(v -> {
            stopProjection();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(ScreenCaptureService.getStartIntent(
                        this, resultCode, data));
            }
        }
    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(com.mtsahakis.mediaprojectiondemo.ScreenCaptureService.getStopIntent(this));
        finish();
    }

}