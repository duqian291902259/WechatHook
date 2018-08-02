package site.duqian.wchook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import site.duqian.wchook.database.FriendDbContract.FriendsEntry;
import site.duqian.wchook.model.NearbyFriend;
import site.duqian.wchook.utils.LogUtils;

/**
 * Created by Dusan (duqian) on 2017/5/12 - 21:05.
 * E-mail: duqian2010@gmail.com
 * Description:FriendsDbUtils
 * remarks:
 */
public class FriendsDbUtils {

    private static final String TAG = FriendsDbUtils.class.getSimpleName();
    private static volatile FriendsDbUtils dbUtils;
    private FriendsDbHelper dbHelper;
    private SQLiteDatabase db;

    public FriendsDbUtils(Context context) {
        dbHelper = new FriendsDbHelper(context);
        // Gets the data repository in write mode
    }

    public static FriendsDbUtils init(Context context) {
        if (dbUtils == null) {
            synchronized (FriendsDbUtils.class) {
                if (dbUtils == null) {
                    dbUtils = new FriendsDbUtils(context);
                }
            }
        }
        return dbUtils;
    }

    public boolean deleteAllUsers(){
        try {
            db = dbHelper.getWritableDatabase();
            int delete = db.delete(FriendsEntry.TABLE_NAME, null, null);
            LogUtils.debug(TAG,"delete numbers = "+delete);
            return true;
        }catch (Exception e){
            LogUtils.debug(TAG,"insert error "+e);
        }
        return false;
    }
    public boolean insert(NearbyFriend friend){
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FriendsEntry.COLUMN_USERNAME, friend.getUsername());
            values.put(FriendsEntry.COLUMN_USERINFO, friend.getUserInfo());
            values.put(FriendsEntry.COLUMN_ADD_TIME, System.currentTimeMillis());
            values.put(FriendsEntry.COLUMN_ISADDED, 0);//是否成为好友， 0（false）和 1（true）
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FriendsEntry.TABLE_NAME, null, values);
            if (newRowId > 0) {
                //LogUtils.debug(TAG,"insert newRowId "+newRowId);
                return true;
            }
        }catch (Exception e){
            LogUtils.debug(TAG,"insert error "+e);
        }
        return false;
    }

    public boolean isAdded(String username){
        db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        String[] projection = { FriendsEntry._ID,FriendsEntry.COLUMN_USERNAME };
        // Filter results WHERE "title" = 'My Title'
        String selection = FriendsEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };
        // How you want the results sorted in the resulting Cursor
        String sortOrder = FriendsEntry.COLUMN_USERNAME  + " DESC";

        Cursor cursor = db.query(
                FriendsEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (cursor.moveToNext()) {//while
            long ID = cursor.getLong(cursor.getColumnIndexOrThrow(FriendsEntry._ID));
            //LogUtils.debug(TAG,"id="+ID);
            cursor.close();
            return true;
        }
        return false;
    }

    public List<String> getAllFriends(){
        db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        String[] projection = { FriendsEntry.COLUMN_USERNAME };
        //String selection = FriendsEntry.COLUMN_USERNAME + " = ?";
        //String[] selectionArgs = { username };
        String sortOrder = FriendsEntry.COLUMN_USERNAME  + " DESC";

        Cursor cursor = db.query(
                FriendsEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        while (cursor.moveToNext()) {//while
            String username = cursor.getString(
                    cursor.getColumnIndexOrThrow(FriendsEntry.COLUMN_USERNAME));
            //LogUtils.debug(TAG,"username="+username);
            list.add(username);
        }
        cursor.close();
        return list;
    }

    public int getDbToalCount(){
        List<String> allFriends = getAllFriends();
        return allFriends==null?0:allFriends.size();
    }

}
