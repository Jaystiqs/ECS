package com.jaystiqs.jaydahstudios.epicchatstories;


import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.jaystiqs.jaydahstudios.epicchatstories.adapter.BookAdapter;
import com.jaystiqs.jaydahstudios.epicchatstories.adapter.CustomAdapter;
import com.jaystiqs.jaydahstudios.epicchatstories.database.DatabaseHelper;
import com.jaystiqs.jaydahstudios.epicchatstories.model.Book;
import com.jaystiqs.jaydahstudios.epicchatstories.model.StoryCountModel;
import com.jaystiqs.jaydahstudios.epicchatstories.model.StoryInfoModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
        import java.util.List;

public class StoriesActivity extends AppCompatActivity{
    private static final String TAG = "StoriesActivity";
    private static final int TIME_DELAY = 1700;
    private static long story_pressed;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHelper mDBHelper;

    private List<Book> bookList;
    private BookAdapter bookAdapter;

    private int storiesCount;

    String[] names = new String[storiesCount+1];
    String[] singers = new String[storiesCount+1];
    int[] pics = new int[storiesCount+1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        mDBHelper = new DatabaseHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //Use this setting to improve performance if you know that changes in
        //the content do not change the layout size of the RecyclerView
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        mLayoutManager = new LinearLayoutManager(StoriesActivity.this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        storiesCount = getStoryCount();

        for (int i = 0; i < names.length; i++) {
            names[i] = getStoryName();
            singers[i] = getStoryDesc();
//            pics[i] = R.drawable.album7;
            pics[i] = getResources().getIdentifier(getStoryImage(), "drawable", getPackageName());
        }

        //intializing an arraylist called songlist
        bookList = new ArrayList<>();

        //adding data from arrays to songlist
        for (int i = 0; i < names.length; i++) {
            Book book = new Book(names[i], singers[i], i + 1, pics[i]);
            bookList.add(book);
        }

        //initializing adapter
        bookAdapter = new BookAdapter(bookList);

        //specifying an adapter to access data, create views and replace the content
        mRecyclerView.setAdapter(bookAdapter);
        bookAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (story_pressed + TIME_DELAY > System.currentTimeMillis()) {

//                    Toast.makeText(StoriesActivity.this, "Card at " + position + " is clicked", Toast.LENGTH_SHORT).show();
                    Intent intent=  new Intent(StoriesActivity.this, MainActivity.class);
                    intent.putExtra("storyIdFromBook", position+1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                story_pressed = System.currentTimeMillis();
            }
        }));
    }

    private StoryInfoModel storyData()
    {
        return mDBHelper.getStoryData(storiesCount);
    }

    private int getStoryCount(){
        int storyCnt;
        StoryCountModel storyDets = mDBHelper.getStoryCount();
        storyCnt = storyDets.getCount();
        return storyCnt;
    }

    private String getStoryName(){
        String storyName;
        StoryInfoModel storyDets =  storyData();
        storyName = storyDets.getStoryName();
        Log.i(TAG, "StoryName: "+storyName);
        return storyName;
    }

    private String getStoryDesc(){
        String storyDesc;
        StoryInfoModel storyDets =  storyData();
        storyDesc = storyDets.getStoryDesc();
        Log.i(TAG, "StoryDesc: "+storyDesc);
        return storyDesc;
    }

    private String getStoryImage(){
        String storyImage;
        StoryInfoModel storyDets =  storyData();
        storyImage = storyDets.getStoryImage();
        Log.i(TAG, "StoryImage: "+storyImage);
        return storyImage;
    }


}