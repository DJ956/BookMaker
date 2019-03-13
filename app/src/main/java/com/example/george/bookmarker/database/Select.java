package com.example.george.bookmarker.database;

import java.util.List;

import book.Book;

/**
 * Created by GEORGE on 2017/04/22.
 */

public abstract class Select {

    /**
     * すべての本を取得します。
     * @return BookのList
     */
    public abstract List<Book> select();

    /**
     * 本を取得します
     * @param title 本のタイトル
     * @return タイトルと一致した本
     */
    public abstract List<Book> selectByTitle(String title);

    /**
     * 本を取得します
     * @param author 著者名
     * @return 著者名と一致した本
     */
    public abstract List<Book> selectByAuthor(String author);

    /**
     *  本を取得します
     * @param title タイトル
     * @param author 著者名
     * @return タイトルと著者名が一致した本
     */
    public abstract List<Book> selectByWords(String title, String author);

    /**
     *  本を取得します
     * @param tag タグ
     * @return タグと一致した本
     */
    public abstract List<Book> selectByTag(String tag);

    /**
     * 本の画像データを取得します
     * @param id 本のid
     * @return 画像データ
     */
    public abstract byte[] selectImageData(int id);

    /**
     * すべてのタグを重複無しで取得します
     * @return タグのリスト
     */
    public abstract List<String> selectTagList();

    /**
     * すべての著者名を重複無しで取得します
     * @return 著者名のリスト
     */
    public abstract List<String> selectAuthorList();
}
