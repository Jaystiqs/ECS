package com.jaystiqs.jaydahstudios.epicchatstories;

//import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Created by Jaystiqs on 7/18/2017.
 */

public class PromoOptionsActivity extends AppCompatActivity implements RewardedVideoAdListener{

    private static final String TAG = "PromoOptionsActivity";

    private FirebaseAnalytics mFirebaseAnalytics;
    private RewardedVideoAd mAd;
    private static final String FORMAT = "%02d:%02d:%02d";
    int miliseconds = 1800000;
    int formerTime;
    int currentTime;
    int timeElapsed;
    TextView countdownTimer;
    CountDownTimer timer;

    private SharedPreferences tPreferences;
    private SharedPreferences.Editor tEditor;

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.i(TAG, "onRewardedVideoAdLoaded: onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.i(TAG, "onRewardedVideoAdOpened: onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i(TAG, "onRewardedVideoStarted: onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.i(TAG, "onRewardedVideoAdClosed: onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.i(TAG, "onRewardedVideoAdLeftApplication: onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.i(TAG, "onRewardedVideoAdFailedToLoad: onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.i(TAG, "onRewarded: onRewarded");
        mAd.destroy(this);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promooptions);
        Log.d(TAG, "onCreate:  Started");

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

//        ------------------------------Initialize mobile Ad--------------------------------------  //

        MobileAds.initialize(getApplicationContext(),"ca-app-pub-3940256099942544/5224354917");
        // Get reference to singleton RewardedVideoAd object
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();

//        ------------------------------End Initialize mobile Ad----------------------------------  //


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize shared preferences
        tPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tEditor = tPreferences.edit();
        checkTimerSharedPreferences();


        final Button watchAdBtn, reload;
        final TextView adHint;
        final ProgressBar progressLoader;

        watchAdBtn = (Button) findViewById(R.id.watchAdBtnPromo);
        watchAdBtn.setEnabled(false);
        watchAdBtn.setTextColor(getResources().getColor(R.color.inactive));
        countdownTimer = (TextView) findViewById(R.id.countdownTimer);
        progressLoader = (ProgressBar) findViewById(R.id.progressLoader);
        progressLoader.setVisibility(View.VISIBLE);

        adHint = (TextView) findViewById(R.id.adHint);

        reload = (Button) findViewById(R.id.reload);
        reload.setVisibility(View.INVISIBLE);
        reload.setEnabled(false);



//-------------------------------Timer Code Start-------------------------------------------------/

        checkSavedTimeSharedPreferences();
        if(miliseconds != 1800000)
        {
            miliseconds = miliseconds-timeElapsed;
            Log.i(TAG, "Time Elapsed: "+ timeElapsed);
        }

        countdownTimer = (TextView) findViewById(R.id.countdownTimer);
        timer = new CountDownTimer(miliseconds,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                countdownTimer.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                miliseconds = ((int)millisUntilFinished);
//                Log.i(TAG, "Time Tick: "+ miliseconds);
            }

            @Override
            public void onFinish(){
                countdownTimer.setText("00:00:00");
                String updateCount = "add";
                tEditor.putInt("SavedTime", 0);
                tEditor.apply();
                tEditor.putInt("Timer", 1800000);
                tEditor.apply();

                if(timer != null){
                    timer.cancel();
                }

                Intent intent = new Intent(PromoOptionsActivity.this, MainActivity.class);
                intent.putExtra("updateCount", updateCount);
                startActivity(intent);
                finish();
            }
        }.start();

//-------------------------------Timer Code End--------------------------------------------------/





        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
//                Toast.makeText(getBaseContext(),"AD LOADED", Toast.LENGTH_SHORT).show();
                watchAdBtn.setEnabled(true);
                watchAdBtn.setTextColor(getResources().getColor(R.color.white));
                adHint.setText("Tap the button.");
                reload.setEnabled(false);
                reload.setVisibility(View.INVISIBLE);
                progressLoader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.i(TAG, "onRewardedVideoAdOpened: Ad opened.");
//                Toast.makeText(getBaseContext(),"Ad opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.i(TAG, "onRewardedVideoStarted: Ad playing.");
//                Toast.makeText(getBaseContext(),"Ad playing", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
//                Toast.makeText(getBaseContext(),"Rewarded!!!!!", Toast.LENGTH_SHORT).show();
                // increment the count for the user

                tEditor.putInt("Timer", 1800000);
                tEditor.commit();

                String updateCount = "add";
                Intent intent = new Intent(PromoOptionsActivity.this, MainActivity.class);
                intent.putExtra("updateCount", updateCount);
                startActivity(intent);
                finish();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.i(TAG, "onRewardedVideoAdLeftApplication: Left the application.");
//                Toast.makeText(getBaseContext(),"Left the application. You have ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Toast.makeText(getBaseContext(),"FAILED. ↻ TAP RELOAD", Toast.LENGTH_SHORT).show();
                adHint.setText("OOPS! TAP RELOAD.");
                progressLoader.setVisibility(View.INVISIBLE);
                reload.setVisibility(View.VISIBLE);
                reload.setEnabled(true);
            }
        });


        watchAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAd.isLoaded()) {
                    mAd.show();
                }
            }
        });


        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardedVideoAd();
                adHint.setText("RELOADING...");
                reload.setVisibility(View.INVISIBLE);
                progressLoader.setVisibility(View.VISIBLE);
            }
        });


    }

    private void checkTimerSharedPreferences(){
        //Check if user has read to a point and has been saved from previous
        int timer = tPreferences.getInt("Timer", 1800000);

        if(timer != 1800000){
            miliseconds = timer;
        }else{
            miliseconds = 1800000;
        }
    }

    private void checkSavedTimeSharedPreferences(){
        //Check if user has read to a point and has been saved from previous
        formerTime = tPreferences.getInt("SavedTime", 0);

        if(formerTime != 0){
//            currentTime = (int)Calendar.getInstance().getTimeInMillis();
            currentTime = (int)System.currentTimeMillis();
            timeElapsed= currentTime - formerTime;
        }

        currentTime = (int)Calendar.getInstance().getTimeInMillis();
        Log.i(TAG, "CurrentTime: "+ currentTime);
        tEditor.putInt("SavedTime", currentTime);
        tEditor.apply();
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    @Override
    protected void onPause() {
        mAd.pause(this);
        super.onPause();
        tEditor.putInt("Timer", miliseconds);
        tEditor.commit();
        if(timer != null){
            timer.cancel();
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tEditor.putInt("Timer", miliseconds);
        tEditor.commit();
        if(timer != null){
            timer.cancel();
        }
        this.finish();
    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
//        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, BroadcastService.class));
//        Log.i(TAG, "Stopped service");
        mAd.destroy(this);
        super.onDestroy();
    }

}


