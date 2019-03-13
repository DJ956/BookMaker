package com.example.george.bookmarker.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.viewer.EditDialogFragment;
import com.example.george.bookmarker.database.DataBaseManager;
import com.example.george.bookmarker.model.AsyncImageDownloader;
import com.example.george.bookmarker.model.ImageCache;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import book.Book;
import book.downloader.ImageDownloader;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class BookAdapter extends ArrayAdapter<Book> implements Serializable {

    private ImageDownloader imageDownloader = ImageDownloader.getInstance();

    private LayoutInflater inflater;
    private Context context;

    private FragmentManager fragmentManager;

    private static class ViewHolder{
        ImageView imageView;
        TextView idTextView;
        TextView titleTextView;
        TextView authorTextView;
        TextView readTextView;
        TextView releaseTextView;
        TextView isbnTextView;
        Button editButton;

    }

    public BookAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Book> objects, FragmentManager manager) {
        super(context, resource, objects);
        this.fragmentManager = manager;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        ViewHolder holder;

        Book book = getItem(position);

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
            TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            TextView authorTextView = (TextView) view.findViewById(R.id.authorTextView);
            TextView readTextView = (TextView) view.findViewById(R.id.readTextView);
            TextView releaseTextView = (TextView) view.findViewById(R.id.releaseTextView);
            TextView isbnTextView = (TextView) view.findViewById(R.id.isbnTextView);
            Button editButton = (Button) view.findViewById(R.id.edit_button);

            holder = new ViewHolder();
            holder.imageView = imageView;
            holder.idTextView = idTextView;
            holder.titleTextView = titleTextView;
            holder.authorTextView = authorTextView;
            holder.readTextView = readTextView;
            holder.releaseTextView = releaseTextView;
            holder.isbnTextView = isbnTextView;
            holder.editButton = editButton;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if(book.getImage() == null) {
            holder.imageView.setTag(book.getTitle());
            AsyncImageDownloader asyncImageDownloader = new AsyncImageDownloader(context, holder.imageView, book);
            asyncImageDownloader.execute();
        }else{
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(book.getImage(),0,book.getImage().length));
        }

        holder.idTextView.setText(String.format(Locale.JAPANESE, "%s:%d", context.getResources().getString(R.string.id), book.getId()));

        holder.titleTextView.setText(String.format(Locale.JAPANESE, "%s", book.getTitle()));

        holder.authorTextView.setText(String.format(Locale.JAPANESE, "%s", book.getAuthor()));
        if (book.read()) {
            holder.readTextView.setText(context.getResources().getString(R.string.read));
        } else {
            holder.readTextView.setText(context.getResources().getString(R.string.un_read));
        }
        holder.releaseTextView.setText(String.format(Locale.JAPANESE, "%s:%d %s",
                context.getResources().getString(R.string.release_day),
                book.getRelease(),
                context.getResources().getString(R.string.year)));

        if (book.getIsbn() != -1) {
            holder.isbnTextView.setText(String.format(Locale.JAPANESE, "%s:%d", context.getResources().getString(R.string.ISBN), book.getIsbn()));
        } else {
            holder.isbnTextView.setText(context.getResources().getString(R.string.ISBN));
        }

        //編集ボタンを押すとダイアログが表示される
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(BookAdapter.this, book);
                editDialogFragment.show(fragmentManager, "");
            }
        });

        return view;
    }

}
