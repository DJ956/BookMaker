package com.example.george.bookmarker.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.george.bookmarker.R;

import java.util.List;

/**
 * Created by GEORGE on 2017/04/22.
 */

public class HomeAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public HomeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup group){
        String name = getItem(position);

        if(view == null){
            view = inflater.inflate(R.layout.home_grid_item,null);
        }

        String show = getContext().getResources().getString(R.string.view);
        String manual = getContext().getResources().getString(R.string.manual);
        String code = getContext().getResources().getString(R.string.scan);
        String codeManual = getContext().getResources().getString(R.string.input_code);
        String search = getContext().getResources().getString(R.string.search);
        String addContinue = getContext().getResources().getString(R.string.input_auto);

        TextView textView = (TextView)view.findViewById(R.id.homeTextView);
        ImageView imageView = (ImageView)view.findViewById(R.id.homeImageView);

        textView.setText(name);

        if(name.equals(show)){
            imageView.setImageResource(R.drawable.ic_show);
        }else if(name.equals(manual)){
            imageView.setImageResource(R.drawable.ic_add_manual);
        }else if(name.equals(code)){
            imageView.setImageResource(R.drawable.ic_scan);
        }else if(name.equals(codeManual)){
            imageView.setImageResource(R.drawable.ic_add_code);
        }else if(name.equals(search)){
            imageView.setImageResource(R.drawable.ic_add_search);
        }else if(name.equals(addContinue)){
            imageView.setImageResource(R.drawable.ic_add_continue);
        }

        return view;
    }
}
