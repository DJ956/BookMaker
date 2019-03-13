package com.example.george.bookmarker.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class BookDbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "tag";

    public static final String DATABASE_NAME = "book.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "book_table";

    public BookDbOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
        "%s TEXT NOT NULL," +
                "%s TEXT," +
                "%s BOOLEAN," +
                "%s TEXT," +
                "%s INTEGER," +
                "%s INTEGER," +
                "%s INTEGER," +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT," +
                "%s BLOB);",
                TABLE_NAME,
                Book.ID,
                Book.TITLE,
                Book.AUTHOR,
                Book.READ,
                Book.SERIES,
                Book.ISBN,
                Book.PAGE,
                Book.RELEASE,
                Book.PUBLISHER,
                Book.COMMENT,
                Book.GENRE,
                TAG,
                Book.IMAGE);

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
