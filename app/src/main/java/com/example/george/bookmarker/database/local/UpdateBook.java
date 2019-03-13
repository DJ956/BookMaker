package com.example.george.bookmarker.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.george.bookmarker.database.Update;

import java.util.Locale;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class UpdateBook extends Update {

    private Context context;

    public UpdateBook(Context context, Book oldBook, Book latestBook){
        super(oldBook, latestBook);
        this.context = context;
    }

    @Override
    public void update() {
        BookDbOpenHelper helper = null;
        SQLiteDatabase sqLiteDatabase = null;
        SQLiteStatement statement = null;

        try{
            helper = new BookDbOpenHelper(context);
            sqLiteDatabase = helper.getWritableDatabase();

            String query;
            boolean hasChangedImage;
            if(latestBook.getImage() != null && oldBook.getImage() != latestBook.getImage()){
                query = String.format(Locale.JAPANESE,"UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE id = ?",
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
                        Book.TAG,
                        Book.IMAGE);
                hasChangedImage = true;
            }else{
                query = String.format(Locale.JAPANESE,"UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE id = ?",
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
                        Book.TAG);
                hasChangedImage = false;
            }


            statement = sqLiteDatabase.compileStatement(query);

            statement.bindString(1,latestBook.getTitle());
            statement.bindString(2,latestBook.getAuthor());
            if(latestBook.read()){
                statement.bindLong(3,1);
            }else{
                statement.bindLong(3,0);
            }
            statement.bindString(4,latestBook.getSeries());
            statement.bindLong(5,latestBook.getIsbn());
            statement.bindLong(6,latestBook.getPage());
            statement.bindLong(7,latestBook.getRelease());
            statement.bindString(8,latestBook.getPublisher());
            statement.bindString(9,latestBook.getComment());
            statement.bindString(10,latestBook.getGenre());
            statement.bindString(11,latestBook.getTag());

            if(hasChangedImage){
                statement.bindBlob(12,latestBook.getImage());
                statement.bindLong(13,oldBook.getId());
            }else{
                statement.bindLong(12,oldBook.getId());
            }

            statement.execute();
        }finally {
            if(helper != null){
                helper.close();
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
