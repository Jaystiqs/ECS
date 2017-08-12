package com.jaystiqs.jaydahstudios.epicchatstories;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.jaystiqs.jaydahstudios.epicchatstories.model.StoryInfoModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private int count = 1; //Count for query with respect to row retreival
    private int storyId = 1; //Set as 1 for first story
    private int storyCount, isStoryBreakPointHolder = 1;
    private CustomAdapter adapter;
    private DatabaseHelper mDBHelper;
    private List<ChatModel> lstChat = new ArrayList<>();
    private List <Integer> adbreakPoints = new ArrayList<>();
    private List <Integer> storybreakPoints = new ArrayList<>();
    private ImageView batteryIconImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView settingsIcon;
        TextView storyNameTxt;


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // ------------------------------------------------------------------------------------



        //Checks if DB exists
        mDBHelper = new DatabaseHelper(this);
        File database =  getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        copyDatabase(this);
//        if(!database.exists()){
//            mDBHelper.getReadableDatabase();
//            //Copy db
//            if(copyDatabase(this)){
//                Toast.makeText(this, "All ready. Press Next to start!", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(this, "Database unable to initialize. Restart App.", Toast.LENGTH_LONG).show();
//            }
//        }
        // ------------------------------------------------------------------------------------


        // Initialize shared preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mEditor.apply();
        checkSharedPreferences();
        // ------------------------------------------------------------------------------------

        //Story count
        storyCount = getStoryCount();

        // Get intents data
        Intent intent = getIntent();
        int counter = intent.getIntExtra("counter", 0);
        int storyIdFromBook = intent.getIntExtra("storyIdFromBook", 0);
        String updateCount = intent.getStringExtra("updateCount");

        if(storyIdFromBook != 0){
            storyId = storyIdFromBook;
            count = 0;
            Toast.makeText(this, "THE STORY!", Toast.LENGTH_LONG).show();
        }

        if("add".equals(updateCount))
        {
            count++;
            Toast.makeText(this, "REWARDED!!! ENJOY THE STORY!", Toast.LENGTH_LONG).show();
        }

        if(counter == 1){
                count = counter;
                Toast.makeText(this, "REWIND SUCCESSFUL. ENJOY THE STORY!!", Toast.LENGTH_LONG).show();
        }
        //      End Get intents data


        adbreakPoints = getAdPoints(storyId);
        storybreakPoints = getStoryPoints(storyId);


        if(count != 1 ){
            int sPCount = count;
            Log.i(TAG, "isStoryBreakPointHolder: "+isStoryBreakPointHolder);

            for(int i=0; i < storybreakPoints.size(); i++ ){
                if(count < storybreakPoints.get(i)){
                    isStoryBreakPointHolder = 1;
                    Log.i(TAG, "storybreakPoints -"+ storybreakPoints.get(i));
                }
            }

            for( int i= isStoryBreakPointHolder; i<sPCount; i++){
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

        // Set name of the story
        storyNameTxt = (TextView) findViewById(R.id.storyNameTxt);
        storyNameTxt.setText("- "+ getStoryName()+ " -");

        nextClicked(null);


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

    private StoryInfoModel storyData(){
        return mDBHelper.getStoryData(storyId);
    }

    private String getStoryName(){
        String storyName;
        StoryInfoModel storyDets = storyData();
        storyName = storyDets.getStoryName();

        return storyName;
    }

    private int getStoryCount(){
        int storyCount;
        StoryInfoModel storyDets = storyData();
        storyCount = storyDets.getCount();

        return storyCount;
    }

    private List getAdPoints(int storyId){
        return mDBHelper.getAdPoints(storyId);
    }
    
    private List getStoryPoints(int storyId){
        return mDBHelper.getStoryPoints(storyId);
    }

    //Model for chat format
    private ChatModel setUpMessage(){

        return mDBHelper.getListChat(count, storyId);
    }

    //when Next is tapped
    public void nextClicked(View view){
        Log.d(TAG, "nextClicked: Is Clicked");
        checkBatteryStatus(count);
        
//---------------------Check Breakpoints for the Ads----------------------
        Boolean isAdBreakPoint = false;
        for(int i=0; i < adbreakPoints.size(); i++ ){
            if(count == adbreakPoints.get(i)){
                isAdBreakPoint = true;
            }
        }
//-------------------------------------------------------------------------
        
        if (isAdBreakPoint) {
            Intent intent = new Intent(MainActivity.this, PromoOptionsActivity.class);
            startActivity(intent);

        } else {
            loadList(null);
            Log.i(TAG, "storyCount: "+ storyCount);
        }


    }

    //load list items into listview
    public void loadList(View view){
        try{
            ChatModel chat = setUpMessage();
            if(chat != null){

                //---------------
                Boolean isStoryBreakPoint = false;
                for(int i=0; i < storybreakPoints.size(); i++ ){
                    if(count == storybreakPoints.get(i)){
                        isStoryBreakPoint = true;
                        isStoryBreakPointHolder = count;
                    }
                }

                if(isStoryBreakPoint){
                    lstChat.clear();
                }
                //---------------

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




            }else{

                Toast.makeText(this, "END OF STORY", Toast.LENGTH_LONG).show();
            }
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
        int holder = mPreferences.getInt(getString(R.string.story_progress_start_point), 1);

        if(counter != 1){
            count = counter;
        }

        if(holder != 1){
            isStoryBreakPointHolder = holder;
        }

    }

    private void checkBatteryStatus(int count){

//        if(count == Math.floor(limit/3) || count == Math.floor((limit1-limit)/3) || count == Math.floor((limit2-limit1)/3)){
//            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
//            batteryIconImg.setImageResource(R.drawable.battery2);
//        }
//        if(count == Math.floor(limit/2) || count == Math.floor((limit1-limit)/3) || count == Math.floor((limit2-limit1)/3)){
//            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
//            batteryIconImg.setImageResource(R.drawable.battery1);
//        }
//
//        if(count == limit || count == limit1 || count == limit2) {
//            batteryIconImg = (ImageView) findViewById(R.id.batteryIconImg);
//            batteryIconImg.setImageResource(R.drawable.batteryempty);
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putInt(getString(R.string.story_progress_count), count);
//        mEditor.putInt(getString(R.string.story_progress_start_point), isStoryBreakPointHolder);
        mEditor.commit();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press BACK once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
