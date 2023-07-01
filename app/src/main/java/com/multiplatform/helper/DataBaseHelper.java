package com.multiplatform.helper;

import android.content.Context;
import com.multiplatform.helper.Constant;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper 
{ 
private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window 
//destination path (location) of our database on device 
private static String TABLE_NAME = "mysms";
// Contacts Table Columns names
private static final String KEY_ID = "_id";
private static final String KEY_text = "text";
private static final String KEY_type = "type";
private static final String KEY_VoteNum = "VoteNum";
private static final String KEY_isVoted = "isVoted";
private static final String KEY_Vote = "Vote";

private static String DB_PATH = "";  
private static String DB_NAME = "database.db";// Database name
private SQLiteDatabase mDataBase;  
private final Context mContext; 
 
public DataBaseHelper(Context context)  
{ 
    super(context, DB_NAME, null, 1);// 1? its Database Version 
    DB_PATH = "/data/data/" + context.getPackageName() + "/databases/"; 
    this.mContext = context; 
}    
 
 public void createDataBase() throws IOException 
{ 
    //If database not exists copy it from the assets 
 
    boolean mDataBaseExist = checkDataBase(); 
    if(!mDataBaseExist) 
    { 
        this.getReadableDatabase(); 
        this.close(); 
        try  
        { 
            //Copy the database from assests 
            copyDataBase(); 
            Log.e(TAG, "createDatabase database created"); 
        }  
        catch (IOException mIOException)  
        { 
            throw new Error("ErrorCopyingDataBase"); 
        } 
    } 
} 
    //Check that the database exists here: /data/data/your package/databases/Da Name 
    private boolean checkDataBase() 
    { 
        File dbFile = new File(DB_PATH + DB_NAME); 
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists()); 
        return dbFile.exists(); 
    } 
 
    //Copy the database from assets 
    private void copyDataBase() throws IOException 
    { 
        InputStream mInput = mContext.getAssets().open(DB_NAME); 
        String outFileName = DB_PATH + DB_NAME; 
        OutputStream mOutput = new FileOutputStream(outFileName); 
        byte[] mBuffer = new byte[1024]; 
        int mLength; 
        while ((mLength = mInput.read(mBuffer))>0) 
        { 
            mOutput.write(mBuffer, 0, mLength); 
        } 
        mOutput.flush(); 
        mOutput.close(); 
        mInput.close(); 
    } 
 
    //Open the database, so we can query it 
    public boolean openDataBase() throws SQLException 
    { 
        String mPath = DB_PATH + DB_NAME; 
        //Log.v("mPath", mPath); 
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY); 
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
        return mDataBase != null; 
    } 
 
    @Override 
    public synchronized void close()  
    { 
        if(mDataBase != null) 
            mDataBase.close(); 
        super.close(); 
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	} 
	
	
	
	public void set_table(String name)
	{
		TABLE_NAME=name;
	}
	
	// get one sms by id
//	public smsObject getsms(int id) 
//	 {
//		    SQLiteDatabase db = this.getReadableDatabase();		 
//		    Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
//		    		KEY_text, KEY_type,KEY_VoteNum, KEY_isVoted}, KEY_ID + "=?",
//		            new String[] { String.valueOf(id) }, null, null, null, null);
//		    if (cursor != null)
//		        cursor.moveToFirst();
//		    smsObject sms=new smsObject(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)));
//		    // return contact
//		    return sms;
//	}
//	
//	// get all sms one type*********************************************
//	public List<smsObject> getAllSmstype(int type) {
//	    List<smsObject> list = new ArrayList<smsObject>();
//	    // Select All Query
//	    SQLiteDatabase db = this.getWritableDatabase();
//	    Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
//	    		KEY_text, KEY_type,KEY_VoteNum, KEY_isVoted}, KEY_type + "=?",
//	            new String[] { String.valueOf(type) }, null, null, null, null);
//	    // looping through all rows and adding to list // be tartibe soton ha cursor por mishavad 
//	    if (cursor.moveToFirst()) {
//	        do {
//	        	
//	        	smsObject i=new smsObject(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)));
//	            list.add(i);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    // return contact list
//	    return list;
//	}
	//**********************************************************
//	public List<smsObject> getAllSms() {
//	    List<smsObject> list = new ArrayList<smsObject>();
//	    // Select All Query
//	    String selectQuery = "SELECT  * FROM " + TABLE_NAME;
//	    SQLiteDatabase db = this.getWritableDatabase();
//	    Cursor cursor;
//	    
//	    cursor = db.rawQuery(selectQuery, null);
//	   
//	    // looping through all rows and adding to list // be tartibe soton ha cursor por mishavad 
//	    if (cursor.moveToFirst()) {
//	        do {
//	        	
//	        	smsObject i=new smsObject(Integer.parseInt(cursor.getString(4)), cursor.getString(0), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
//	            list.add(i);
//	        } while (cursor.moveToNext());
//	    }
//	 
//	    // return contact list
//	    return list;
//	}
//	
//    // Adding new sms
//	public void addsms(smsObject sms) {
//	    SQLiteDatabase db = this.getWritableDatabase();
//	 
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_type, sms.gettype()); 
//	    values.put(KEY_VoteNum, sms.getVotenum()); 
//	    values.put(KEY_isVoted, sms.getIsVoted()); 
//	    values.put(KEY_text, sms.gettext()); 
//	    // Inserting Row
//	    db.insert(TABLE_NAME, null, values);
//	    db.close(); // Closing database connection
//	}
//	//*************************************
//    // Updating single contact
//	public int updatesms(smsObject sms) {
//	    SQLiteDatabase db = this.getWritableDatabase();
//	    ContentValues values = new ContentValues();
//	    values.put(KEY_text, sms.gettext()); 
//	    values.put(KEY_type, sms.gettype()); 
//	    values.put(KEY_VoteNum, sms.getVotenum()); 
//	    values.put(KEY_isVoted, sms.getIsVoted()); 
//	    // updating row
//	    return db.update(TABLE_NAME, values, KEY_ID + " = ?",
//	            new String[] { String.valueOf(sms.getID()) });
//	}
//	// ****************************************
//    // Deleting single sms
//	public void deletesms(smsObject contact) {
//    SQLiteDatabase db = this.getWritableDatabase();
//    db.delete(TABLE_NAME, KEY_ID + " = ?",
//            new String[] { String.valueOf(contact.getID()) });
//    db.close();
//	}
//	// get last sms
//	public int getlastsms()
//	{
//		String query="SELECT _id from "+TABLE_NAME+" order by _id DESC ";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor;
//		cursor = db.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		return Integer.parseInt(cursor.getString(0));
//	}
} 

 
