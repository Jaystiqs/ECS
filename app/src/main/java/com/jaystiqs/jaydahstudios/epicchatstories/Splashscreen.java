package com.jaystiqs.jaydahstudios.epicchatstories;

/**
 * Created by Jaystiqs on 7/18/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Splashscreen extends Activity {
    private FirebaseAnalytics mFirebaseAnalytics;

    Thread splashTread;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        int[] ids = new int[]{R.drawable.s1,R.drawable.s2, R.drawable.s3};
        Random randomGenerator = new Random();
        int r= randomGenerator.nextInt(ids.length);
        this.imageView.setImageDrawable(getResources().getDrawable(ids[r]));

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(Splashscreen.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    Splashscreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        TextView jsCopy = (TextView) findViewById(R.id.jsCopyright);
        jsCopy.setText("Jaydah Studios. Copyright © "+ year +". \n All Rights Reserved");
    }

}