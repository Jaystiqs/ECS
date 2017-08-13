package com.jaystiqs.jaydahstudios.epicchatstories;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Jaystiqs on 7/19/2017.
 */

public class OptionsMenuActivity extends AppCompatActivity {
    private static final String TAG = "OptionsMenuActivity";
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences tPreferences;
    private SharedPreferences.Editor tEditor;

    Button rewind, share, stories, apps;
    ImageView rewindImg, shareImg, storiesImg, appsImg;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionsmenu);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize shared preferences
        tPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tEditor = tPreferences.edit();

        rewind = (Button)findViewById(R.id.rewindBtn);
        share = (Button)findViewById(R.id.shareBtn);
        stories = (Button)findViewById(R.id.storiesBtn);
        apps = (Button)findViewById(R.id.appsBtn);

        rewindImg = (ImageView) findViewById(R.id.rewindImg);
        shareImg = (ImageView) findViewById(R.id.shareImg);
        storiesImg = (ImageView) findViewById(R.id.storiesImg);
        appsImg = (ImageView) findViewById(R.id.appsImg);

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                askDialogue(OptionsMenuActivity.this, getString(R.string.app_name),
                        "Are you sure you want to rewind? You will lose all your progress.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // OK
                                // do Something

                                Intent intent = new Intent(OptionsMenuActivity.this, MainActivity.class);
                                intent.putExtra("counter",1);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // Cancel
                                // do Something
                            }
                        });



            }
        });

        rewindImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                askDialogue(OptionsMenuActivity.this, getString(R.string.app_name),
                        "Are you sure you want to rewind? You will lose all your progress.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // OK
                                // do Something
                                tEditor.putInt("Timer", 1800000);
                                tEditor.commit();

                                Intent intent = new Intent(OptionsMenuActivity.this, MainActivity.class);
                                intent.putExtra("counter",1);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // Cancel
                                // do Something
                            }
                        });



            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Epic Chat Stories");
                    String sAux = "\nTry this app. It has the coolest stories\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Epic Chat Stories");
                    String sAux = "\nTry this app. It has the coolest stories\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsMenuActivity.this, StoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        storiesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsMenuActivity.this, StoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://dev?id=5557093314074911698"));
                try{
                    startActivity(intent);
                }catch(Exception e){
                    intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5557093314074911698"));
                    startActivity(intent);
                }

            }
        });

        appsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://dev?id=5557093314074911698"));
                try{
                    startActivity(intent);
                }catch(Exception e){
                    intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5557093314074911698"));
                    startActivity(intent);
                }
            }
        });


    }

    public static void askDialogue(final Activity activity, String title, String msg,
                           DialogInterface.OnClickListener okListener,
                           DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("YES, REWIND.", okListener);
        alertDialog.setNegativeButton("CANCEL", cancelListener);
        alertDialog.show();
    }
}
