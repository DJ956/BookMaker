package com.example.george.bookmarker.database;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public abstract class Update {

    protected Book oldBook;
    protected Book latestBook;

    public Update(Book oldBook, Book latestBook){
        this.oldBook = oldBook;
        this.latestBook = latestBook;
    }

    public abstract void update();

}
