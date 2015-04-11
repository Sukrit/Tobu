package com.journal.Tobu;

import java.util.ArrayList;

import com.journal.Tobu.FeedReaderContract.FeedEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
        FeedEntry._ID + " INTEGER PRIMARY KEY," +
        FeedEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_FEED + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE +
        " )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
		
	}
	
	public long insert(String feed, String date, String type, String location) {
		// Gets the data repository in write mode
		SQLiteDatabase db = this.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_DATE, date);
		values.put(FeedEntry.COLUMN_NAME_FEED, feed);
		values.put(FeedEntry.COLUMN_NAME_TYPE, type);
		values.put(FeedEntry.COLUMN_NAME_LOCATION, location);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
		         FeedEntry.TABLE_NAME,
		         null,
		         values);
		
		return newRowId;
	}
	
	public ArrayList<ArrayList<String>> selectAll(String limit) {
		
		// Gets the data repository in write mode
		SQLiteDatabase db = this.getReadableDatabase();
		
		//String[][] resultList = new String[Integer.parseInt(limit)][4];
		ArrayList<ArrayList<String>> dynamicResult = new ArrayList<ArrayList<String>>();
		Cursor result = db.query(FeedEntry.TABLE_NAME, null, null, null, null, null, FeedEntry._ID+" DESC", limit);
		int i=0;
        // looping through all rows and adding to list
        if (result.moveToFirst()) {
            do {               
            	ArrayList<String> element = new ArrayList<String>();
            	element.add(result.getString(1));
            	element.add(result.getString(2));
            	element.add(result.getString(4));
            	element.add(result.getString(3));
            	//resultList[i][0] = result.getString(1); //date
            	//resultList[i][1] = result.getString(2); //feed
            	//resultList[i][2] = result.getString(4); //location
            	//resultList[i][3] = result.getString(3); //type
            	dynamicResult.add(element);
                //System.out.println(contactList[i]);
                i++;
            } while (result.moveToNext());
        }
        
       
		//System.out.println(result.getCount());
        return dynamicResult;
		
	}
}