package com.example.george.bookmarker.activity.viewer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.george.bookmarker.R;

import com.example.george.bookmarker.activity.MainActivity;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.adapter.BookAdapter;
import com.example.george.bookmarker.adapter.CustomAdapter;
import com.example.george.bookmarker.database.DataBaseManager;

import java.util.ArrayList;
import java.util.List;

import book.Book;

/**
 * Bookをきれいに表示させるActivity
 */
public class ViewerActivity extends AppCompatActivity {

    private BookAdapter adapter;

    private BookAdapter tagBookAdapter;
    private BookAdapter authorBookAdapter;
    private CustomAdapter customAdapter;

    private ListView listView;
    private TextView textView;

    private String custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        adapter = new BookAdapter(getApplicationContext(), R.layout.list_item, new ArrayList<Book>(), getFragmentManager());
        listView = (ListView) findViewById(R.id.viewer_list_view);
        textView = (TextView) findViewById(R.id.viewer_text_view);
        Button homeButton = (Button) findViewById(R.id.viewer_home_button);
        Button backButton = (Button) findViewById(R.id.viewer_back_button);
        Button tagButton = (Button) findViewById(R.id.viewer_tag_button);
        Button authorButton = (Button) findViewById(R.id.viewer_author_button);

        homeButton.setOnClickListener(new HomeClickListenerIMPL());
        backButton.setOnClickListener(new BackClickListenerIMPL());
        tagButton.setOnClickListener(new TagClickListenerIMPL());
        authorButton.setOnClickListener(new AuthorClickListenerIMPL());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListenerIMPL());

        new AsyncSelectBooks().execute("");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(adapter != null){
            adapter.clear();
            adapter = null;
        }

        if(customAdapter != null){
            customAdapter.clear();
            customAdapter = null;
        }

        if(tagBookAdapter != null){
            tagBookAdapter.clear();
            tagBookAdapter = null;
        }

        if(authorBookAdapter != null){
            authorBookAdapter.clear();
            authorBookAdapter = null;
        }
    }

    /**
     * 非同期でBookを取得する
     * 項目別の引数を設定すると引数に基づいた本をListViewに設定する
     */
    private class AsyncSelectBooks extends AsyncTask<String, Void, List<Book>> {

        private String customData;

        @Override
        protected List<Book> doInBackground(String... args) {
            DataBaseManager manager = DataBaseManager.getInstance();
            customData = args[0];
            if(customData.equals(Book.TAG)){
                return manager.selectByTag(getApplicationContext(),args[1]);
            }else if(customData.equals(Book.AUTHOR)){
                return manager.selectByAuthor(getApplicationContext(),args[1]);
            }else{
                return manager.select(getApplicationContext());
            }

        }

        @Override
        protected void onPostExecute(List<Book> result) {
            if(customData.equals(Book.TAG)){
                tagBookAdapter = new BookAdapter(getApplicationContext(), R.layout.list_item, result, getFragmentManager());
                listView.setAdapter(tagBookAdapter);
                textView.setText(tagBookAdapter.getCount() + "冊");
            }else if(customData.equals(Book.AUTHOR)){
                authorBookAdapter = new BookAdapter(getApplicationContext(),R.layout.list_item, result, getFragmentManager());
                listView.setAdapter(authorBookAdapter);
                textView.setText(authorBookAdapter.getCount() + "冊");
            }
            else{
                adapter.addAll(result);
                listView.setAdapter(adapter);
                textView.setText(adapter.getCount() + "冊");
            }

            listView.setOnItemClickListener(new ItemClickListenerIMPL());
        }
    }

    /**
     * 項目別の重複しないデータをListViewに設定する
     */
    private class AsyncSelectCustomData extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... args) {
            DataBaseManager manager = DataBaseManager.getInstance();
            if (args[0].equals(Book.TAG)) {
                return manager.selectTagList(getApplicationContext());
            } else if (args[0].equals(Book.AUTHOR)) {
                return manager.selectAuthorList(getApplicationContext());
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                customAdapter.addAll(result);
                listView.setAdapter(customAdapter);
                listView.setOnItemClickListener(new CustomItemClickListenerIMPL());
            }
        }
    }


    /**
     * 選択しているBookをEditFormに編集として送る
     */
    private class ItemClickListenerIMPL implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Book book = adapter.getItem(position);
            Intent intent = new Intent(getApplicationContext(), EditFormActivity.class);
            intent.putExtra("ActionType", EditorActionType.Update);
            intent.putExtra(EditorActionType.Update.toString(), book);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 項目別のアイテムを選択するとそのアイテムの情報にもとづいて本を選択し表示させる。
     * もし項目別用のAdapterがnullでなければ以前使用したAdapterを設定する
     */
    private class CustomItemClickListenerIMPL implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String customData = customAdapter.getItem(position);
            if(custom.equals(Book.TAG)){
                String[] list = new String[]{Book.TAG, customData};
                new AsyncSelectBooks().execute(list);
            }else if(custom.equals(Book.AUTHOR)){
                String[] list = new String[]{Book.AUTHOR, customData};
                new AsyncSelectBooks().execute(list);
            }
        }
    }

    /**
     * ホームに戻るを押すとMainActivityに戻る。
     */
    private class HomeClickListenerIMPL implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 項目別の表示をクリアにし、すべての本を表示し直す。
     */
    private class BackClickListenerIMPL implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (adapter != null && !adapter.isEmpty()) {
                listView.setAdapter(adapter);
            } else {
                adapter = new BookAdapter(getApplicationContext(), R.layout.list_item, new ArrayList<Book>(), getFragmentManager());
                new AsyncSelectBooks().execute("");
            }
        }
    }

    /**
     * データーベースからタグの情報を取得しListViewに表示する。
     * タグを選択するとそのタグ情報にヒットする本のみを表示させるようにする。
     */
    private class TagClickListenerIMPL implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            customAdapter = new CustomAdapter(getApplicationContext(), R.layout.custom_list_data, new ArrayList<>());
            custom = Book.TAG;
            new AsyncSelectCustomData().execute(custom);
        }
    }

    /**
     * データーベースから著者の情報を取得しListViewに表示する。
     * 著者名を選択するとその著者情報にヒットする本のみを表示させるようにする。
     */
    private class AuthorClickListenerIMPL implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            customAdapter = new CustomAdapter(getApplicationContext(), R.layout.custom_list_data, new ArrayList<>());
            custom = Book.AUTHOR;
            new AsyncSelectCustomData().execute(custom);
        }
    }

}
