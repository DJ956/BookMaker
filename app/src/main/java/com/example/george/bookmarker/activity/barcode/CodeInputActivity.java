package com.example.george.bookmarker.activity.barcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.MainActivity;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.model.AsyncSearchISBN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import book.Book;

/**
 * ISBNコードを手動で入力するActivity
 */
public class CodeInputActivity extends AppCompatActivity {

    private EditText isbnEditText;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_input);

        List<Button> numberButtonList = new ArrayList<>();

        isbnEditText = (EditText)findViewById(R.id.isbnEditText);

        Button oneButton = (Button)findViewById(R.id.oneButton);
        Button twoButton = (Button)findViewById(R.id.twoButton);
        Button threeButton = (Button)findViewById(R.id.threeButton);
        Button fourButton = (Button)findViewById(R.id.fourButton);
        Button fiveButton = (Button)findViewById(R.id.fiveButton);
        Button sixButton = (Button)findViewById(R.id.sixButton);
        Button sevenButton = (Button)findViewById(R.id.sevenButton);
        Button eightButton = (Button)findViewById(R.id.eightButton);
        Button nineButton = (Button)findViewById(R.id.nineButton);
        Button zeroButton = (Button)findViewById(R.id.zeroButton);

        Button okButton = (Button)findViewById(R.id.isbnOkButton);
        Button cancelButton = (Button)findViewById(R.id.isbnCancelButton);
        Button backSpaceButton = (Button)findViewById(R.id.backSpaceButton);

        numberButtonList.add(oneButton);
        numberButtonList.add(twoButton);
        numberButtonList.add(threeButton);
        numberButtonList.add(fourButton);
        numberButtonList.add(fiveButton);
        numberButtonList.add(sixButton);
        numberButtonList.add(sevenButton);
        numberButtonList.add(eightButton);
        numberButtonList.add(nineButton);
        numberButtonList.add(zeroButton);

        for(Button button : numberButtonList){
            button.setOnClickListener(new OnClickButton());
        }

        okButton.setOnClickListener(new OnClickOKButton());
        cancelButton.setOnClickListener(new OnClickCancelButton());
        backSpaceButton.setOnClickListener(new OnClickBackSpaceButton());


        Intent intent = getIntent();
        if(intent.getExtras() != null){
            String isbnString = intent.getExtras().getString("barcode");
            long isbn;
            try {
                isbn = Long.parseLong(isbnString);
                isbnEditText.setText(String.valueOf(isbn));
            }catch (NumberFormatException e){
            }
        }

        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        executorService.shutdown();
    }

    /**
     * 番号ボタンを押すとEditTextにその値が追加される
     */
    private class OnClickButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button b = (Button)view;

            if(isbnEditText.getText() != null) {
                String text = isbnEditText.getText().toString();
                text += b.getText();
                isbnEditText.setText(text);
            }else{
                isbnEditText.setText(b.getText());
            }
        }
    }

    /**
     * OKボタンを押すとisbnを作成し結果をEditFormに表示する
     */
    private class OnClickOKButton implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            try {
                Book book;
                long isbn = Long.parseLong(isbnEditText.getText().toString());

                Future<Book> result = executorService.submit(new AsyncSearchISBN(isbn,true));

                try{
                    book = result.get();
                    Intent intent = new Intent(getApplicationContext(), EditFormActivity.class);
                    intent.putExtra("ActionType", EditorActionType.Insert);
                    intent.putExtra(EditorActionType.Insert.toString(), book);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"該当する本が見つかりませんでした",Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * キャンセルボタンを押すとMainActivityに戻ります
     */
    private class OnClickCancelButton implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * バックスペースキーを押すと文字を一つ消します
     */
    private class OnClickBackSpaceButton implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String text = isbnEditText.getText().toString();
            if(!text.isEmpty()){
                text = text.substring(0, text.length() - 1);
                isbnEditText.setText(text);
            }
        }
    }
}
