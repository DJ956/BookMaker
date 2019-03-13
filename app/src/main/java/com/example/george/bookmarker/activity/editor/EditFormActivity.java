package com.example.george.bookmarker.activity.editor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import book.Book;
import book.BookFactory;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.viewer.ViewerActivity;
import com.example.george.bookmarker.database.DataBaseManager;

/**
 * Bookを手動で追加、検索結果から追加、編集などをする際に使用するForm
 */
public class EditFormActivity extends AppCompatActivity {

    private TextView idTextView;
    private ImageView imageView;
    private EditText titleEditText;
    private EditText authorEditText;
    private ToggleButton readToggleButton;
    private EditText isbnEditText;
    private EditText tagEditText;
    private EditText pageEditText;
    private EditText releaseEditText;
    private EditText publisherEditText;
    private EditText commentEditText;
    //private Button imageChangeButton;

    private BookFactory factory = BookFactory.getInstance();
    private DataBaseManager manager = DataBaseManager.getInstance();

    //画像変更のフラグ
    //private boolean isUpdateImage = false;

    //更新用のBook
    private Book oldBook = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        idTextView = (TextView)findViewById(R.id.idTextView);
        imageView = (ImageView)findViewById(R.id.imageView_form);
        titleEditText = (EditText)findViewById(R.id.titleEditText);
        authorEditText = (EditText)findViewById(R.id.authorEditText);
        readToggleButton = (ToggleButton)findViewById(R.id.readToggleButton);
        isbnEditText = (EditText)findViewById(R.id.isbnEditText);

        tagEditText = (EditText)findViewById(R.id.tagEditText);
        releaseEditText = (EditText)findViewById(R.id.releaseEditText);
        pageEditText = (EditText)findViewById(R.id.pageEditText);
        publisherEditText = (EditText)findViewById(R.id.publisherEditText);
        commentEditText = (EditText)findViewById(R.id.commentEditText);
        //imageChangeButton = (Button)findViewById(R.id.imageChangeButton);
        //imageChangeButton.setOnClickListener(new ImageChangeListenerIMPL());

        Button saveButton = (Button)findViewById(R.id.saveButton);

        //このアクティビティをどの目的で使用するかを判断する
        Intent intent = getIntent();
        EditorActionType actionType = (EditorActionType)intent.getExtras().getSerializable("ActionType");
        if(actionType != null){
            switch (actionType){
                //更新なら保存ボタンの処理を更新にする
                case Update:{
                    saveButton.setOnClickListener(new UpdateClickListenerIMPL());
                    oldBook = (Book)intent.getExtras().getSerializable(EditorActionType.Update.toString());
                    setBook(oldBook);
                    break;
                }
                //追加なら保存ボタンの処理を追加にする
                case Insert:{
                    saveButton.setOnClickListener(new SaveClickListenerIMPL());
                    Book book = (Book)intent.getExtras().getSerializable(EditorActionType.Insert.toString());
                    setBook(book);
                   break;
                }
                //新規追加なら保存ボタンの処理を追加にする
                case New:{
                    saveButton.setOnClickListener(new SaveClickListenerIMPL());
                    break;
                }
            }
        }else{
            saveButton.setOnClickListener(new SaveClickListenerIMPL());
        }

    }

    /**
     * 画像変更ボタンが押されると変更のダイアログが表示される
     */
    private class ImageChangeListenerIMPL implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //isUpdateImage = true;
        }
    }

    /**
     * 保存ボタンが押されるとFormからBookを作りデータベースに登録する。
     */
    private class SaveClickListenerIMPL implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Book book = getBook();
            if(!book.getTitle().isEmpty()) {
                manager.insert(getApplicationContext(), book);
                Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    private class UpdateClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Book latest = getBook();
            manager.update(getApplicationContext(),oldBook,latest);

            Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Bookの情報をフォームに設定する
     * @param book
     */
    private void setBook(Book book){
        if(book == null){
            return;
        }

        titleEditText.setText(book.getTitle());

        //著者
        authorEditText.setText(book.getAuthor());

        //既読or未読
        if(book.read()){
            readToggleButton.setChecked(true);
        }else{
            readToggleButton.setChecked(false);
        }

        //ISBN
        isbnEditText.setText(String.valueOf(book.getIsbn()));

        //シリーズ名
        //seriesEditText.setText(book.getSeries());

        //出版社
        publisherEditText.setText(book.getPublisher());

        //コメント
        commentEditText.setText(book.getComment());

        //イメージ
        if(book.getImage() != null){
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(book.getImage(),0,book.getImage().length));
            //isUpdateImage = true;
        }

        //ID
        if(book.getId() != -1){
            idTextView.setText(String.valueOf(book.getId()));
        }

        //リリース
        if(book.getRelease() != -1){
            releaseEditText.setText(String.valueOf(book.getRelease()));
        }

        /*
        //ジャンル
        if(book.getGenre() != null && genreAdapter.getPosition(book.getGenre()) != -1){
            genreSpinner.setSelection(genreAdapter.getPosition(book.getGenre()));
        }
        */

        //ページ数
        if(book.getPage() != -1){
            pageEditText.setText(String.valueOf(book.getPage()));
        }

    }

    /**
     * フォームのパーツからBookを作る
     * @return
     */
    private Book getBook() {
        Book book;

        int id = -1;

        try {
            id = Integer.parseInt(idTextView.getText().toString());
        } catch (NumberFormatException e) {
        }

        long isbn = -1;
        try {
            isbn = Long.parseLong(isbnEditText.getText().toString());
        } catch (NumberFormatException e) {
        }

        int page = -1;
        try {
            page = Integer.parseInt(pageEditText.getText().toString());
        } catch (NumberFormatException e) {
        }

        int releaseDay = -1;
        try{
            releaseDay = Integer.parseInt(releaseEditText.getText().toString());
        }catch (NumberFormatException e){
        }

        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        boolean read = readToggleButton.isChecked();
        String publisher = publisherEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        String tag = tagEditText.getText().toString();

        book = factory.create(title, author, read, "", isbn, page, releaseDay, publisher, comment, "");
        book.setId(id);
        book.setTag(tag);

        //もし新たにImageViewの画像を変更した場合、Bookに値を再設定する
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if(imageView.getDrawable() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] imagedata = outputStream.toByteArray();
                book.setImage(imagedata);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }

}
