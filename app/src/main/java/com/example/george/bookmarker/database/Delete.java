package com.example.george.bookmarker.database;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public abstract class Delete {

    protected Book book;

    public Delete(Book book){
        this.book = book;
    }

    public abstract void delete();
}
