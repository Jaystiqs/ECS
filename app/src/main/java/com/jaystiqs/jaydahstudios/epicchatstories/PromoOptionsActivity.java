package com.jaystiqs.jaydahstudios.epicchatstories;

//import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promooptions);
        Log.d(TAG, "onCreate:  Started");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize shared preferences
        tPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tEditor = tPreferences.edit();
        checkTimerSharedPreferences();

        final Button watchAdBtn, reload;
        final TextView adHint;

        watchAdBtn = (Button) findViewById(R.id.watchAdBtnPromo);
        watchAdBtn.setEnabled(false);
        countdownTimer = (TextView) findViewById(R.id.countdownTimer);
        adHint = (TextView) findViewById(R.id.adHint);
        reload = (Button) findViewById(R.id.reload);
        reload.setEnabled(false);

//-------------------------------Timer Code Start-------------------------------------------------/
//        startService(new Intent(this, BroadcastService.class));
//        Log.i(TAG, "Started service");

//-------------------------------Timer Code End--------------------------------------------------/


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
                Log.i(TAG, "Time Tick: "+ miliseconds);
            }

            @Override
            public void onFinish(){
                countdownTimer.setText("00:00:00");
                String updateCount = "add";
                tEditor.putInt("SavedTime", 0);
                tEditor.apply();
                tEditor.putInt("Timer", 1800000);
                tEditor.apply();
                timer.cancel();
                Intent intent = new Intent(PromoOptionsActivity.this, MainActivity.class);
                intent.putExtra("updateCount", updateCount);
                startActivity(intent);
                finish();
            }
        }.start();


//        ----------------------------------------AD Section ---------------------------------------------------
//        ------------------------------------------------------------------------------------------------------


        MobileAds.initialize(getApplicationContext(),"ca-app-pub-3940256099942544/5224354917");
        // Get reference to singleton RewardedVideoAd object
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();

        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Toast.makeText(getBaseContext(),"Ad successfully loaded. Tap button to play!", Toast.LENGTH_SHORT).show();
                watchAdBtn.setEnabled(true);
                adHint.setText("Ad Loaded! Tap the button.");
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
                Toast.makeText(getBaseContext(),"Ad was closed. Reward lost. Try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getBaseContext(),"Ad failed to load. Tap Reload", Toast.LENGTH_SHORT).show();
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
            }
        });

//        ---------------------------------------- End AD Section ----------------------------------------------
//        ------------------------------------------------------------------------------------------------------

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
        timer.cancel();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tEditor.putInt("Timer", miliseconds);
        tEditor.commit();
        timer.cancel();
        this.finish();
    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
//        Log.i(TAG, "Registered broacast receiver");
    }

//    @Override
//    public void onStop() {
//        try {
//            unregisterReceiver(br);
//        } catch (Exception e) {
//            // Receiver was probably already stopped in onPause()
//        }
//        super.onStop();
//    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, BroadcastService.class));
//        Log.i(TAG, "Stopped service");
        mAd.destroy(this);
        super.onDestroy();
    }

//    static class CheckNetworkTime extends AsyncTask<Void, Void, Long> {
//
//        //NTP server list: http://tf.nist.gov/tf-cgi/servers.cgi
////    public static final String TIME_SERVER = "time-c.nist.gov";
//        private static final String TIME_SERVER = "time.nist.gov";
//        private long networkTime;
//
//        @Override
//        protected Long doInBackground(Void... params) {
//            try {
//                networkTime = returnNetworkTime();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return networkTime;
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            super.onPostExecute(aLong);
//        }
//
//        private static long returnNetworkTime() throws IOException{
//            NTPUDPClient timeClient = new NTPUDPClient();
//            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
//            TimeInfo timeInfo = timeClient.getTime(inetAddress);
//            return timeInfo.getMessage().getReceiveTimeStamp().getTime();
//
//        }
//    }
}


