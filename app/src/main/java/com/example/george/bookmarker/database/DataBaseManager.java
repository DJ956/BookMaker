package com.example.george.bookmarker.database;

import android.content.Context;

import com.example.george.bookmarker.database.local.DeleteBook;
import com.example.george.bookmarker.database.local.InsertBook;
import com.example.george.bookmarker.database.local.SelectBook;
import com.example.george.bookmarker.database.local.UpdateBook;

import java.util.List;

import book.Book;
import book.database.DatabaseManager;
import book.database.DefaultExecutor;
import book.database.Profile;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class DataBaseManager {

    private static final Profile PROFILE = new Profile("com.mysql.jdbc.Driver",
            "jdbc:mysql://192.168.1.17:3306/book_maker","book_table",
            "george","e9fgy7kz");

    private static final DataBaseManager manager = new DataBaseManager();
    private static final DatabaseManager remoteManager = new DatabaseManager(PROFILE,new DefaultExecutor());

    private static boolean isLocal = false;

    private DataBaseManager(){
    }

    public static DataBaseManager getInstance(){
        return manager;
    }

    public void setLocal(boolean local){
        if(local){
            isLocal = true;
        }else{
            isLocal = false;
        }
    }

    public boolean isLocal(){
        return isLocal;
    }

    public void insert(Context context, Book book){
        if(isLocal){
            InsertBook insertBook = new InsertBook(context,book);
            insertBook.insert();
        }else{
            remoteManager.insert(book);
        }
    }

    public void delete(Context context, Book book){
        if(isLocal){
            DeleteBook deleteBook = new DeleteBook(context,book);
            deleteBook.delete();
        }else{
            remoteManager.delete(book);
        }
    }

    public void update(Context context, Book oldBook, Book latestBook){
        if(isLocal){
            UpdateBook updateBook = new UpdateBook(context,oldBook,latestBook);
            updateBook.update();
        }else{
            remoteManager.update(oldBook,latestBook);
        }
    }

    public List<Book> select(Context context){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.select();
        }else{
            return remoteManager.select();
        }
    }

    public List<Book> selectByTitle(Context context, String title) {
        if (isLocal) {
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectByTitle(title);
        } else {
            return remoteManager.selectByTitle(title);
        }
    }

    public List<Book> selectByAuthor(Context context, String author){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectByAuthor(author);
        }else {
            return remoteManager.selectByAuthor(author);
        }
    }

    public List<Book> selectByWords(Context context, String title, String author){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectByWords(title,author);
        }else {
            return remoteManager.selectByWords(title,author);
        }
    }

    public List<Book> selectByTag(Context context, String tag){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectByTag(tag);
        }else {
            return remoteManager.selectByTag(tag);
        }
    }

    public byte[] selectImageData(Context context, int id){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectImageData(id);
        }else {
            return remoteManager.selectImageData(id);
        }
    }

    public List<String> selectTagList(Context context){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectTagList();
        }else{
            return remoteManager.selectTagList();
        }
    }

    public List<String> selectAuthorList(Context context){
        if(isLocal){
            SelectBook selectBook = new SelectBook(context);
            return selectBook.selectAuthorList();
        }else {
            return remoteManager.selectAuthorList();
        }
    }


}
