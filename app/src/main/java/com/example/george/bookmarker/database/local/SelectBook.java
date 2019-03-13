package com.example.george.bookmarker.database.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.george.bookmarker.database.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import book.Book;
import book.BookFactory;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class SelectBook extends Select {

    private Context context;

    public SelectBook(Context context){
        this.context = context;
    }

    private List<Book> execute(String query){
        List<Book> list = new ArrayList<>();
        BookDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        BookFactory factory = BookFactory.getInstance();

        try{
            helper = new BookDbOpenHelper(context);
            db = helper.getWritableDatabase();

            cursor = db.rawQuery(query,null);
            if(cursor != null){
                while (cursor.moveToNext()){
                    boolean read;
                    if(cursor.getLong(cursor.getColumnIndex(Book.READ)) == 1){
                        read = true;
                    }else{
                        read = false;
                    }

                    Book book = factory.create(cursor.getString(cursor.getColumnIndex(Book.TITLE)),
                            cursor.getString(cursor.getColumnIndex(Book.AUTHOR)),
                            read,
                            cursor.getString(cursor.getColumnIndex(Book.SERIES)),
                            cursor.getLong(cursor.getColumnIndex(Book.ISBN)),
                            cursor.getInt(cursor.getColumnIndex(Book.PAGE)),
                            cursor.getInt(cursor.getColumnIndex(Book.RELEASE)),
                            cursor.getString(cursor.getColumnIndex(Book.PUBLISHER)));

                    book.setComment(cursor.getString(cursor.getColumnIndex(Book.COMMENT)));
                    book.setGenre(cursor.getString(cursor.getColumnIndex(Book.GENRE)));

                    book.setId(cursor.getInt(cursor.getColumnIndex(Book.ID)));

                    list.add(book);
                }
            }
        }finally {
            if(helper != null){
                helper.close();
            }
            if(db != null){
                db.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

        return list;
    }

    @Override
    public List<Book> select() {
        String query = String.format(Locale.JAPANESE,"select * from %s",BookDbOpenHelper.TABLE_NAME);
        return execute(query);
    }

    @Override
    public List<Book> selectByTitle(String title) {
        String query = String.format(Locale.JAPANESE, "select * from %s where title = '%s'",BookDbOpenHelper.TABLE_NAME, title);
        return execute(query);
    }

    @Override
    public List<Book> selectByAuthor(String author) {
        String query = String.format(Locale.JAPANESE, "select * from %s where author = '%s'",BookDbOpenHelper.TABLE_NAME, author);
        return execute(query);
    }

    @Override
    public List<Book> selectByWords(String title, String author) {
        String query = String.format(Locale.JAPANESE, "select * from %s where title = '%s' or author = '%s'", BookDbOpenHelper.TABLE_NAME, title, author);
        return execute(query);
    }

    @Override
    public List<Book> selectByTag(String tag) {
        String query = String.format(Locale.JAPANESE, "select * from %s where tag = '%s'", BookDbOpenHelper.TABLE_NAME, tag);
        return execute(query);
    }

    @Override
    public byte[] selectImageData(int id){
        String query = String.format(Locale.JAPANESE,"select * from %s where id = %d", BookDbOpenHelper.TABLE_NAME,id);
        byte[] data = null;

        BookDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            helper = new BookDbOpenHelper(context);
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(query,null);
            if(cursor != null && cursor.moveToFirst()){
                data = cursor.getBlob(cursor.getColumnIndex(Book.IMAGE));
            }
        }finally {
            if(db != null){
                db.close();
            }
            if(helper != null){
                helper.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

        return data;
    }

    /**
     * すべてのタグを重複無しで取得します
     *
     * @return タグのリスト
     */
    @Override
    public List<String> selectTagList() {
        String query = String.format(Locale.JAPANESE,"select distinct tag from %s", BookDbOpenHelper.TABLE_NAME);
        List<String> tagList = new ArrayList<>();

        BookDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            helper = new BookDbOpenHelper(context);
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()){
                tagList.add(cursor.getString(cursor.getColumnIndex(Book.TAG)));
            }
        }finally {
            if(db != null){
                db.close();
            }
            if(helper != null){
                helper.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

        return tagList;
    }

    /**
     * すべての著者名を重複無しで取得します
     *
     * @return 著者名のリスト
     */
    @Override
    public List<String> selectAuthorList() {
        String query = String.format(Locale.JAPANESE,"select distinct author from %s", BookDbOpenHelper.TABLE_NAME);
        List<String> authorList = new ArrayList<>();

        BookDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            helper = new BookDbOpenHelper(context);
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()){
                authorList.add(cursor.getString(cursor.getColumnIndex(Book.AUTHOR)));
            }
        }finally {
            if(db != null){
                db.close();
            }
            if(helper != null){
                helper.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

        return authorList;
    }
}
