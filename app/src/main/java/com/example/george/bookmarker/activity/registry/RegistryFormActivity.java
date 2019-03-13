package com.example.george.bookmarker.activity.registry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.MainActivity;
import com.example.george.bookmarker.activity.barcode.RequestCode;
import com.example.george.bookmarker.activity.camera.CameraActivity;
import com.example.george.bookmarker.adapter.BookAdapter;
import com.example.george.bookmarker.database.DataBaseManager;
import com.example.george.bookmarker.model.AsyncSearchISBN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import book.Book;

/**
 * 複数のBookを登録するActivity
 */
public class RegistryFormActivity extends AppCompatActivity {

    private BookAdapter adapter = null;
    private List<Book> bookList = null;

    private ExecutorService executorService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry_form);

        ListView listView = (ListView)findViewById(R.id.reader_list_view);

        Button scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new ScanClickListenerIMPL());

        Button addButton = (Button)findViewById(R.id.add_all_button);
        addButton.setOnClickListener(new AddAllClickListenerIMPL());

        Button backButton = (Button)findViewById(R.id.registry_back_button);
        backButton.setOnClickListener(new BackClickListenerIMPL());

        bookList = new ArrayList<>();
        adapter = new BookAdapter(getApplicationContext(),R.layout.list_item,bookList,getFragmentManager());
        listView.setAdapter(adapter);

        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * CameraActivityから受け取ったコード(ISBN)を取得する
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case RequestCode.ISBN:{
                String isbnString = intent.getExtras().getString("barcode");
                try{
                    long isbn = Long.parseLong(isbnString);
                    Future<Book> result = executorService.submit(new AsyncSearchISBN(isbn,true));

                    Book book = result.get();
                    if(book != null){
                        adapter.add(book);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),isbnString + "の本が見つかりませんでした",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * スキャンを実行をCameraActivityに投げる
     */
    private class ScanClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivityForResult(intent,RequestCode.ISBN);
        }
    }

    /**
     * 非同期で一時的に取得した本を登録する
     */
    private class AddAllClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(bookList.size() > 0) {
                AsyncInsertBooks asyncInsertBooks = new AsyncInsertBooks();
                asyncInsertBooks.execute(bookList);
            }
        }
    }

    /**
     * ホームに戻る
     */
    private class BackClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistryFormActivity.this);

                    builder.setCancelable(true).
                    setMessage("ホームに戻りますか?").
                    setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            builder.create().dismiss();
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).create();

            builder.show();
        }
    }

    /**
     * 非同期でListの本をデータベースに登録する
     */
    private class AsyncInsertBooks extends AsyncTask<List<Book>,Integer,Integer>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog =  new ProgressDialog(RegistryFormActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("保存中です");
            progressDialog.setMessage("保存中です...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(List<Book>... lists) {
            if(lists != null && lists[0].size() > 0) {
                DataBaseManager manager = DataBaseManager.getInstance();
                for (Book book : lists[0]) {
                    manager.insert(getApplicationContext(),book);
                }
            }else{
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result){
            progressDialog.dismiss();
            if(result > 0){
                Toast.makeText(getApplicationContext(),adapter.getCount()  + "保存が完了しました",Toast.LENGTH_SHORT).show();
                adapter.clear();
            }
        }
    }
}
