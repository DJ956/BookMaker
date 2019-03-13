package com.example.george.bookmarker.model;

import java.util.concurrent.Callable;

import book.Book;
import book.BookFactory;
import book.downloader.ImageDownloader;

/**
 * Created by GEORGE on 2017/04/23.
 * 非同期でネットワークを使ってisbnコードをもとにBookを取得する
 */
public class AsyncSearchISBN implements Callable<Book> {

    private long isbn;
    private BookFactory factory;
    private boolean downloadImage = false;

    public AsyncSearchISBN(long isbn){
        this.isbn = isbn;
        factory = BookFactory.getInstance();
    }

    public AsyncSearchISBN(long isbn, boolean downloadImage){
        this.isbn = isbn;
        factory = BookFactory.getInstance();
        this.downloadImage = downloadImage;
    }

    @Override
    public Book call() throws Exception {
        if(factory.searchByISBN(isbn).size() == 0){
            return null;
        }

        Book book = factory.searchByISBN(isbn).get(0);
        if(downloadImage){
            byte[] data = ImageCache.get(isbn);
            if(data == null){
                ImageDownloader imageDownloader = ImageDownloader.getInstance();
                data = imageDownloader.download(book.getImageURL());
                if(data != null) {
                    ImageCache.put(isbn, data);
                    book.setImage(data);
                }
            }else{
                book.setImage(data);
            }
        }

        return book;
    }
}
