package com.lonedev.slypanel;

import android.provider.BaseColumns;

/**
 * Created by adam on 17/01/15.
 */
public final class DatabaseManager {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseManager() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String CONNECTION_DETAILS = "Connection_Details";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String HOST = "Host";
        public static final String NULLABLE = "null";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.CONNECTION_DETAILS + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.USERNAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.PASSWORD + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.HOST + TEXT_TYPE + COMMA_SEP +
                    // Any other options for the CREATE command
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.CONNECTION_DETAILS;

}
