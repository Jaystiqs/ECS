package com.jaystiqs.jaydahstudios.epicchatstories.database;

import android.content.Context;
import android.database.Cursor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ImageView;

import com.jaystiqs.jaydahstudios.epicchatstories.R;
import com.jaystiqs.jaydahstudios.epicchatstories.model.Book;
import com.jaystiqs.jaydahstudios.epicchatstories.model.ChatModel;
import com.jaystiqs.jaydahstudios.epicchatstories.model.StoryCountModel;
import com.jaystiqs.jaydahstudios.epicchatstories.model.StoryInfoModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Jaystiqs on 7/13/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DBNAME = "storydb.sqlite";
    public static final String DBLOCATION = "/data/data/com.jaystiqs.jaydahstudios.epicchatstories/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static String PACKAGE_NAME;

    public DatabaseHelper(Context context){
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        PACKAGE_NAME = mContext.getPackageName();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase(){
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if(mDatabase != null && mDatabase.isOpen()){
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public void closeDatabase(){
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    public ChatModel getListChat(int id, int storyId) {

        ChatModel chat = null;
        openDatabase();

        try{
            Cursor cursor = mDatabase.rawQuery("SELECT b.id, a.actorName, b.dialogueContent ,a.actorPlacement FROM actors a LEFT JOIN dialogue b ON a.id = b.actorId WHERE b.id ="+id+"  AND b.storyId = "+storyId+" ORDER BY b.id", null);

            cursor.moveToFirst();
//        cursor.moveToNext();
            while (!cursor.isAfterLast()) {
                System.out.println(cursor.getInt(0) + " " + cursor.getInt(1) + " " + cursor.getInt(2) + " " +  cursor.getInt(3));

                chat = new ChatModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2),  cursor.getInt(3));

                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException e)
        {
            Log.e(TAG, "getListChat: ", e);
        }

        closeDatabase();
        return chat;
    }

    public StoryInfoModel getStoryData(int id) {

        StoryInfoModel storyData = null;
        openDatabase();

        try{
            Cursor cursor = mDatabase.rawQuery("SELECT COUNT (b.dialogueContent) AS count, a.id, a.storyName, a.storyDescription, a.storyImage FROM stories a LEFT JOIN dialogue b ON a.id = b.storyId WHERE a.id ="+id+"", null);

            cursor.moveToFirst();
//        cursor.moveToNext();
            while (!cursor.isAfterLast()) {
                Log.i(TAG, "getStoryData: "+ cursor.getInt(0) + " " + cursor.getInt(1) + " " + cursor.getInt(2) + " " +  cursor.getInt(3)+ " " +  cursor.getInt(4));
                storyData = new StoryInfoModel(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException e)
        {
            Log.e(TAG, "getListChat: ", e);
        }

        closeDatabase();
        return storyData;
    }

    public List<Book> getStoryDataAll() {
        Book product = null;

        List<Book> productList = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT storyName, storyDescription, storyImage FROM stories", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            String mDrawableName = cursor.getString(1);
            int resID = mContext.getResources().getIdentifier(mDrawableName , "drawable", PACKAGE_NAME);

            product = new Book(cursor.getString(0), cursor.getString(1),cursor.getInt(1), 0);
            productList.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();

        return productList;
    }

    public StoryCountModel getStoryCount() {
        StoryCountModel bookCount = null;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM stories", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            bookCount = new StoryCountModel(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();

        return bookCount;
    }

    public ChatModel getChat(int id){
        ChatModel chat = null;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT b.id, a.actorName, b.dialogueContent ,a.actorPlacement FROM actors a LEFT JOIN dialogue b ON a.id = b.actorId WHERE b.id = 2 ORDER BY b.id ASC", null);

        while(!cursor.moveToNext()){
            chat = new ChatModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        }
        cursor.close();
        closeDatabase();

        return chat;
    }

    public List getAdPoints(int storyId){

        ArrayList<Integer> adpoints = new ArrayList<Integer>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT num FROM adbreakpoints WHERE storyId= "+storyId+"", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            adpoints.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();

        return adpoints;
    }

    public List getStoryPoints(int storyId){

        ArrayList<Integer> adpoints = new ArrayList<Integer>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT num FROM storybreakpoints WHERE storyId= "+storyId+"", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            adpoints.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();

        return adpoints;
    }
}




