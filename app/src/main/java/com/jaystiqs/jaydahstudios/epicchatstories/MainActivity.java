package com.jaystiqs.jaydahstudios.epicchatstories;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaystiqs.jaydahstudios.epicchatstories.adapter.CustomAdapter;
import com.jaystiqs.jaydahstudios.epicchatstories.database.DatabaseHelper;
import com.jaystiqs.jaydahstudios.epicchatstories.model.ChatModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private int count = 1; //Count for query with respect to row retreival
    private CustomAdapter adapter;
    private DatabaseHelper mDBHelper;
    private List<ChatModel> lstChat = new ArrayList<>();
    private List<ChatModel> newChat = new ArrayList<>();
    int limit = 38;
    int limit1 = 74;
    int limit2 = 115;
    ImageView batteryIconImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mDBHelper = new DatabaseHelper(this);
        ImageView settingsIcon;

        //Checks if DB exists
        File database =  getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if(!database.exists()){
            mDBHelper.getReadableDatabase();
            //Copy db
            if(copyDatabase(this)){
                Toast.makeText(this, "All ready. Press Next to start!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Copy error", Toast.LENGTH_LONG).show();
            }
        }

        // Initialize shared preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        checkSharedPreferences();


//      Get intents data
        Intent intent = getIntent();
        int counter = intent.getIntExtra("counter", 0);
        String updateCount = intent.getStringExtra("updateCount");

        if("add".equals(updateCount))
        {
            count++;
            Toast.makeText(this, "Rewarded!!! Enjoy the story!", Toast.LENGTH_LONG).show();
        }

        if(counter == 1){
                count = counter;
                Toast.makeText(this, "Rewind successful. Enjoy the story!", Toast.LENGTH_LONG).show();
        }
//      End Get intents data

        if(count != 1 ){
            int sPCount = count;
            for( int i= 1; i<sPCount; i++){
                count = i;
                getProgress(null);
            }
            count++;
        }

        settingsIcon = (ImageView) findViewById(R.id.settingsIconImg);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionsMenuActivity.class);
                startActivity(intent);
            }
        });


        checkBatteryStatus(count);
    }

    private boolean copyDatabase(Context context){
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[]buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0){
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity", "DB copied successfully");
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private ChatModel setUpMessage(){
        Log.d(TAG, "setUpMessage: Exec");
        return mDBHelper.getListChat(count);

    }

    public void nextClicked(View view){
        Log.d(TAG, "nextClicked: Is Clicked");

        checkBatteryStatus(count);

        if(count == limit || count == limit1 || count == limit2){
            Log.d(TAG, "nextClicked: Limit Reached");

            Intent intent = new Intent(MainActivity.this, PromoOptionsActivity.class);
            startActivity(intent);

        }else{
            loadList(null);
        }

    }

    //load lst items into listview
    public void loadList(View view){
        try{
            ChatModel chat = setUpMessage();
            lstChat.add(chat);
            final ListView lstView = (ListView)findViewById(R.id.listView);
            final CustomAdapter adapter = new CustomAdapter(lstChat,this);
            lstView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            lstView.setAdapter(adapter);

            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    lstView.setSelection(adapter.getCount() - 1);
                }
            });

            adapter.notifyDataSetChanged();
            Log.i(TAG, "Counter is: "+count);
            count++;
        }catch(Exception e) {
            Log.i(TAG, "loadList: "+e.toString());
            //e.toString();
        }
    }

    //For saved progress of shared preference
    public void getProgress(View view){
        Log.d(TAG, "getProgress: Executed");

        ChatModel chat = setUpMessage();
        lstChat.add(chat);

        final ListView lstView = (ListView)findViewById(R.id.listView);
        final CustomAdapter adapter = new CustomAdapter(lstChat,this);
        lstView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lstView.setAdapter(adapter);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                lstView.setSelection(adapter.getCount() - 1);
            }
        });

        adapter.notifyDataSetChanged();

    }

    private void checkSharedPreferences(){
        //Check if user has read to a point and has been saved from previous
        int counter = mPreferences.getInt(getString(R.string.story_progress_count), 1);

        if(counter != 1){
            count = counter;
        }
    }

    private void checkBatteryStatus(int count){

//        if(count>limit){
//            limit = limit1;
//        }else if(count>limit){
//            limit = limit2;
//        }

        if(count == Math.floor(limit/3) || count == Math.floor((limit1-limit)/3) || count == Math.floor((limit2-limit1)/3)){
            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
            batteryIconImg.setImageResource(R.drawable.battery2);
        }
        if(count == Math.floor(limit/2) || count == Math.floor((limit1-limit)/3) || count == Math.floor((limit2-limit1)/3)){
            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
            batteryIconImg.setImageResource(R.drawable.battery1);
        }

        if(count == limit || count == limit1 || count == limit2) {
            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
            batteryIconImg.setImageResource(R.drawable.batteryempty);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putInt(getString(R.string.story_progress_count), count);
        mEditor.commit();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
