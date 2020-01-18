package com.example.bookmanage.exception

/**
 * 書籍が存在しない場合の例外処理
 */
class BookNotFoundException
/**
 * コンストラクタ
 *
 * @param id 書籍のID
 */(id: Long) : Exception(String.format(MESSAGE_FORMAT, id)) {
    companion object {
        /**
         * メッセージのフォーマット
         */
        private const val MESSAGE_FORMAT = "Book is not found. (id = %d)"
    }
}