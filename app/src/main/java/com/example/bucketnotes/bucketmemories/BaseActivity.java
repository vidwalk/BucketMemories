package com.example.bucketnotes.bucketmemories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.bucketnotes.bucketmemories.enums.ActivityRequest;
import com.example.bucketnotes.bucketmemories.enums.BundleKey;
import com.example.bucketnotes.bucketmemories.validators.Constants;

public abstract class BaseActivity extends AppCompatActivity {

    private long currentTime;
    private boolean needCheckCurrentTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ActivityRequest.ON_CHECK_PIN_PRESSED_BACK.ordinal()){
            if(getCallingActivity() != null){
                setResult(ActivityRequest.ON_CHECK_PIN_PRESSED_BACK.ordinal());
            }
            finish();
        }
        Log.d("RESULTS", resultCode+"  BASE");
        switch (ActivityRequest.values()[requestCode]) {
            case CHECK_PASSWORD:
                if (resultCode != Activity.RESULT_OK) {
                    setResult(ActivityRequest.ON_CHECK_PIN_PRESSED_BACK.ordinal());
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(needCheckCurrentTime && System.currentTimeMillis() - currentTime > Constants.TimeForCheckPassword){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(BundleKey.NEED_CHECK_PASSWORD.name(), true);
            Log.d("inside", "inside onStart");
            startActivityForResult(intent, ActivityRequest.CHECK_PASSWORD.ordinal());
            needCheckCurrentTime = false;
        } else {
            needCheckCurrentTime = true;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        currentTime = System.currentTimeMillis();
    }

    protected void setNeedCheckCurrentTime(boolean needToCheck){
        this.needCheckCurrentTime = needToCheck;
    }
}
