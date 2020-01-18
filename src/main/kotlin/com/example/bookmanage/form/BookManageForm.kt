package com.example.bookmanage.form

import com.example.bookmanage.domain.Book
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * 書籍管理システムの画面のフォーム情報
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class BookManageForm() {

    /**
     * タイトル
     */
    @NotBlank
    @Size(
        max = 30,
        message = "{validation.max-size}"
    )
    var title: String? = null

    /**
     * 著者
     */
    @NotBlank
    @Size(
        max = 20,
        message = "{validation.max-size}"
    )
    var author: String? = null

    /**
     * バージョン
     */
    var version: Long = 0

    /**
     * 新規登録か否か
     */
    var newBook: Boolean? = null

    /**
     * 書籍の一覧
     */
    var books: List<Book>? = null

    constructor(newBook: Boolean, books: List<Book>) : this() {
        this.newBook = newBook
        this.books = books
    }

}