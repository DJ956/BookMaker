package com.example.george.bookmarker.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.george.bookmarker.database.Delete;

import java.util.Locale;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class DeleteBook extends Delete {

    private Context context;

    public DeleteBook(Context context, Book book){
        super(book);
        this.context = context;
    }

    @Override
    public void delete() {
        BookDbOpenHelper helper = null;
        SQLiteDatabase sqLiteDatabase = null;

        try{
            String query = String.format(Locale.JAPANESE,"DELETE FROM %s WHERE id = %d",
                    BookDbOpenHelper.TABLE_NAME,
                    book.getId());

            helper = new BookDbOpenHelper(context);
            sqLiteDatabase = helper.getWritableDatabase();

            sqLiteDatabase.execSQL(query);
        }finally {
            if(helper != null){
                helper.close();
            }
            if(sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }
    }
}
