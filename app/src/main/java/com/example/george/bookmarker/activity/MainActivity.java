package com.example.george.bookmarker.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.barcode.CodeInputActivity;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.activity.registry.RegistryFormActivity;
import com.example.george.bookmarker.activity.scan.ScanActivity;
import com.example.george.bookmarker.activity.search.SearchActivity;
import com.example.george.bookmarker.activity.viewer.ViewerActivity;
import com.example.george.bookmarker.adapter.HomeAdapter;

public class MainActivity extends AppCompatActivity {

    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView)findViewById(R.id.main_grid_view);
        homeAdapter = new HomeAdapter(getApplicationContext(),R.layout.home_grid_item,getResources().getStringArray(R.array.home));

        gridView.setAdapter(homeAdapter);
        gridView.setOnItemClickListener(new ClickListenerIMPL());
    }


    private class ClickListenerIMPL implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String type = homeAdapter.getItem(i);

            Intent intent = null;

            final String viewType = getString(R.string.view);
            final String manual = getString(R.string.manual);
            final String scan = getString(R.string.scan);
            final String codeInput = getString(R.string.input_code);
            final String autoInput = getString(R.string.input_auto);
            final String search = getString(R.string.search);

            if(type.equals(viewType)){
                intent = new Intent(getApplicationContext(),ViewerActivity.class);
            }else if(type.equals(manual)){
                intent = new Intent(getApplicationContext(),EditFormActivity.class);
                intent.putExtra("ActionType", EditorActionType.New);
            }else if(type.equals(scan)){
                intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }else if(type.equals(codeInput)){
                intent = new Intent(getApplicationContext(),CodeInputActivity.class);
            }else if(type.equals(autoInput)){
                intent = new Intent(getApplicationContext(),RegistryFormActivity.class);
            }else if(type.equals(search)){
                intent = new Intent(getApplicationContext(), SearchActivity.class);
            }

            if(intent != null){
                startActivity(intent);
            }

        }
    }
}
