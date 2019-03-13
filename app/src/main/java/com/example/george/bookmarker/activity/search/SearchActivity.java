package com.example.george.bookmarker.activity.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.adapter.BookAdapter;
import com.example.george.bookmarker.model.AsyncImageDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import book.Book;
import book.BookFactory;

/**
 * キーワードをもとにBookを検索するActivity
 */
public class SearchActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText authorEditText;

    private ListView searchListView;

    private BookAdapter adapter;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        adapter = new BookAdapter(getApplicationContext(),R.layout.list_item, new ArrayList<Book>(), getFragmentManager());

        titleEditText = (EditText)findViewById(R.id.search_title_editText);
        authorEditText = (EditText)findViewById(R.id.search_author_editText);
        searchListView = (ListView)findViewById(R.id.search_listView);

        searchListView.setOnItemClickListener(new ItemClickListenerIMPL());
        searchListView.setAdapter(adapter);

        Button searchButton = (Button)findViewById(R.id.search_Button);
        searchButton.setOnClickListener(new SearchClickListenerIMPL());

        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * 検索ボタンを押すとEditTextに入力された文字をもとに検索を開始しする
     */
    private class SearchClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String title = titleEditText.getText().toString();
            String author = authorEditText.getText().toString();

            AsyncSearchByWords asyncSearchByWords = new AsyncSearchByWords(title, author);
            asyncSearchByWords.execute(title,author);
        }
    }

    /**
     *選択したBookをRegistryActivityに追加タイプで飛ばす
     */
    private class ItemClickListenerIMPL implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Book book = adapter.getItem(i);

            if(book.getImage() == null) {
                AsyncImageDownloader asyncImageDownloader = new AsyncImageDownloader(getApplicationContext(),null, book);
                asyncImageDownloader.execute();
            }

            Intent intent = new Intent(getApplicationContext(), EditFormActivity.class);
            intent.putExtra("ActionType", EditorActionType.Insert);
            intent.putExtra(EditorActionType.Insert.toString(), book);
            startActivity(intent);
            finish();
        }
    }


    private class AsyncSearchByWords extends AsyncTask<String, Integer, List<Book>> {

        private ProgressDialog progressDialog;

        private String title;
        private String author;

        private AsyncSearchByWords(String title, String author){
            this.title = title;
            this.author = author;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(SearchActivity.this,ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("検索中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Book> doInBackground(String... strings) {
            BookFactory factory = BookFactory.getInstance();

            if(!title.isEmpty() && author.isEmpty()){
                //もしタイトルが空文字でなく、著者名が空文字ならタイトル検索を行う
                return factory.searchByTitle(title);
            }else if(!author.isEmpty() && title.isEmpty()){
                //もし著者名が空文字でなく、タイトルが空文字ならば著者名検索を行う
                return factory.searchByAuthor(author);
            }else if(!title.isEmpty() && !author.isEmpty()){
                //もし両方共から文字でなかったらAND検索を行う
                return factory.searchByWords(title,author);
            }else{
                //以下は起きないはず
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Book> result){
            progressDialog.dismiss();
            if(result != null) {
                adapter.clear();
                adapter.addAll(result);
            }
        }
    }
}
