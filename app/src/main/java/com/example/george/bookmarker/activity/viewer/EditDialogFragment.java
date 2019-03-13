package com.example.george.bookmarker.activity.viewer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.bookmarker.R;
import com.example.george.bookmarker.activity.editor.EditFormActivity;
import com.example.george.bookmarker.activity.editor.EditorActionType;
import com.example.george.bookmarker.adapter.BookAdapter;
import com.example.george.bookmarker.database.DataBaseManager;


import book.Book;

/**
 * Viewerの編集ボタンを押したときに表示されるDialog
 */

public class EditDialogFragment extends DialogFragment {

    public static EditDialogFragment newInstance(BookAdapter adapter, Book book){
        EditDialogFragment editDialogFragment = new EditDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("book",book);
        args.putSerializable("adapter",adapter);
        editDialogFragment.setArguments(args);

        return editDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit);

        TextView titleTextView = (TextView)dialog.findViewById(R.id.edit_dialog_title_text_view);
        TextView editTextView = (TextView) dialog.findViewById(R.id.edit_dialog_edit_text_view);
        TextView deleteTextView = (TextView)dialog.findViewById(R.id.edit_dialog_delete_text_view);
        TextView webTextView = (TextView)dialog.findViewById(R.id.edit_dialog_web_text_view);

        Button backButton = (Button)dialog.findViewById(R.id.edit_dialog_back_button);
        backButton.setOnClickListener(new BackClickListenerIMPL());

        Book book = (Book) getArguments().get("book");
        BookAdapter adapter = (BookAdapter)getArguments().get("adapter");

        titleTextView.setText(book.getTitle());
        editTextView.setOnClickListener(new EditClickListenerIMPL(book));
        deleteTextView.setOnClickListener(new DeleteClickListenerIMPL(adapter, book));
        webTextView.setOnClickListener(new WebClickListenerIMPL(book));

        return dialog;
    }

    /**
     * ダイアログを閉じる
     */
    private class BackClickListenerIMPL implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            dismiss();
        }
    }

    /**
     * EditFormにBookを編集モードで渡す
     */
    private class EditClickListenerIMPL implements View.OnClickListener{

        private Book book;
        private EditClickListenerIMPL(Book book){
            this.book = book;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), EditFormActivity.class);
            intent.putExtra("ActionType", EditorActionType.Update);
            intent.putExtra(EditorActionType.Update.toString(), book);
            startActivity(intent);
            dismiss();
        }
    }

    /**
     * Bookを削除する
     */
    private class DeleteClickListenerIMPL implements View.OnClickListener{

        private Book book;
        private BookAdapter adapter;
        private DeleteClickListenerIMPL(BookAdapter adapter, Book book) {
            this.adapter = adapter;
            this.book = book;
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(book.getTitle());
            dialog.setMessage(book.getTitle() + "を削除してもよろしいですか?");
            dialog.setPositiveButton("削除",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataBaseManager manager = DataBaseManager.getInstance();
                            manager.delete(getActivity(),book);
                            adapter.remove(book);
                            Toast.makeText(getActivity(),"削除しました",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });

            dialog.setNegativeButton("キャンセル",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(),"キャンセルしました",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });

            dialog.create().show();
        }
    }

    /**
     * Bookのタイトルをを外部ブラウザーで検索する
     */
    private class WebClickListenerIMPL implements View.OnClickListener{

        private Book book;
        private WebClickListenerIMPL(Book book){
            this.book = book;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY,book.getTitle());
            startActivity(intent);
            dismiss();
        }
    }

}
