package com.example.george.bookmarker.activity.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.MainActivity;
import com.example.george.bookmarker.activity.barcode.RequestCode;
import com.example.george.bookmarker.activity.camera.CameraActivity;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.model.AsyncSearchISBN;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import book.Book;

public class ScanActivity extends AppCompatActivity {

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        executorService = Executors.newFixedThreadPool(2);

        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivityForResult(intent, RequestCode.ISBN);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        executorService.shutdown();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == RESULT_CANCELED){
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
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
                        intent = new Intent(getApplicationContext(),EditFormActivity.class);
                        intent.putExtra("ActionType",EditorActionType.Insert);
                        intent.putExtra(EditorActionType.Insert.toString(),book);

                        startActivity(intent);
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();

                    String error = String.format(Locale.JAPANESE,"ISBN:%sの本が見つかりませんでした",isbnString);
                    Toast.makeText(getApplicationContext(), error,Toast.LENGTH_SHORT).show();
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            }
            default:{
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
