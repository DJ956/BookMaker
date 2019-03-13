package com.example.george.bookmarker.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.george.bookmarker.database.Insert;

import java.util.Locale;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class InsertBook extends Insert {

    private Context context;

    public InsertBook(Context context, Book book){
        super(book);
        this.context = context;
    }

    @Override
    public void insert() {
        BookDbOpenHelper bookDbOpenHelper = null;
        SQLiteDatabase sqLiteDatabase = null;
        SQLiteStatement statement = null;

        try{
            bookDbOpenHelper = new BookDbOpenHelper(context);
            sqLiteDatabase = bookDbOpenHelper.getWritableDatabase();

            String query = String.format(Locale.JAPANESE,"INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                    BookDbOpenHelper.TABLE_NAME,
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
                    Book.IMAGE,
                    Book.TAG);

            statement = sqLiteDatabase.compileStatement(query);
            statement.bindString(1,book.getTitle());
            statement.bindString(2,book.getAuthor());
            if(book.read()){
                statement.bindLong(3,1);
            }else{
                statement.bindLong(3,0);
            }
            statement.bindString(4,book.getSeries());
            statement.bindLong(5,book.getIsbn());
            statement.bindLong(6,book.getPage());
            statement.bindLong(7,book.getRelease());
            statement.bindString(8,book.getPublisher());
            statement.bindString(9,book.getComment());
            statement.bindString(10,book.getGenre());
            if(book.getImage() != null){
                statement.bindBlob(11,book.getImage());
            }else {
                statement.bindNull(11);
            }

            statement.bindString(12,book.getTag());

            statement.executeInsert();
        }finally {
            book = null;
            if(bookDbOpenHelper != null){
                bookDbOpenHelper.close();
            }
            if(sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
            if(statement != null){
                statement.close();
            }
        }
    }
}
