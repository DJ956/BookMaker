package com.example.george.bookmarker.database;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public abstract class Insert {

    protected Book book;

    public Insert(Book book){
        this.book = book;
    }

    public abstract void insert();

}
