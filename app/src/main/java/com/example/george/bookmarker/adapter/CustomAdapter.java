package com.example.george.bookmarker.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.george.bookmarker.R;

import java.util.List;

/**
 * 著者別やタグ別など項目別のアイテムを表示させる。
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group){
        View view = convertView;

        if(view == null){
            view = inflater.inflate(R.layout.custom_list_data,null);
        }

        TextView textView = (TextView) view.findViewById(R.id.custom_textView);
        textView.setText(getItem(position));

        return view;
    }
}
