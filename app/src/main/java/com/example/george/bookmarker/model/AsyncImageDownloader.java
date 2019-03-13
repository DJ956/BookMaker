package com.example.george.bookmarker.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;


import com.example.george.bookmarker.database.DataBaseManager;

import book.Book;
import book.downloader.ImageDownloader;

/**
 * Created by GEORGE on 2017/04/24.
 */

public class AsyncImageDownloader extends AsyncTask<Void, Void, byte[]> {

    private Book book;
    private Context context;
    private ImageView imageView;
    private String tag;

    public AsyncImageDownloader(Context context, ImageView imageView, Book book) {
        this.book = book;
        this.context = context;
        this.imageView = imageView;

        if(imageView != null){
            this.tag = imageView.getTag().toString();
        }
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        byte[] data = null;

        if(context != null) {
            DataBaseManager manager = DataBaseManager.getInstance();
            data = manager.selectImageData(context,book.getId());
        }

        if(data == null){
            data = ImageCache.get(book.getIsbn());
        }

        if(data == null){
            ImageDownloader imageDownloader = ImageDownloader.getInstance();
            data = imageDownloader.download(book.getIsbn());
        }

        return data;
    }

    @Override
    protected void onPostExecute(byte[] result){
        if(result != null){
            book.setImage(result);
        }else {
            imageView.setImageBitmap(null);
            return;
        }

        if(imageView != null && tag.equals(imageView.getTag().toString())) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(result, 0, result.length));
        }

    }
}
