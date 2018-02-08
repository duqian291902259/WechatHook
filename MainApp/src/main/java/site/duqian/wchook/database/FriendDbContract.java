package site.duqian.wchook.database;

import android.provider.BaseColumns;

/**
 * Created by Dusan (duqian) on 2017/5/12 - 20:22.
 * E-mail: duqian2010@gmail.com
 * Description:数据库创建
 * remarks:
 */

public class FriendDbContract {

    /* Inner class that defines the table contents */
    public static class FriendsEntry implements BaseColumns {
        public static final String TABLE_NAME = "friends";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_USERINFO = "userInfo";
        public static final String COLUMN_ADD_TIME = "addTime";
        public static final String COLUMN_ISADDED = "isAdded";
    }


    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FriendsEntry.TABLE_NAME + " (" +
                    FriendsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    FriendsEntry.COLUMN_USERNAME + " TEXT ," +
                    FriendsEntry.COLUMN_USERINFO + " TEXT  DEFAULT null," +
                    FriendsEntry.COLUMN_ADD_TIME + " TIMESTAMP," +
                    FriendsEntry.COLUMN_ISADDED + " INTEGER DEFAULT 0)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME;


}
