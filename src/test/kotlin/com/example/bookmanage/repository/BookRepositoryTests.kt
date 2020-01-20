package com.example.bookmanage.repository

import com.example.bookmanage.domain.Book
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

/**
 * BookRepositoryのテストプログラム<br></br>
 * 主に自動設定される変数の確認を行う。
 */
@SpringBootTest
internal class BookRepositoryTests {
    /**
     * 書籍のリポジトリ
     */
    @Autowired
    private lateinit var repository: BookRepository

    @Test
    @WithMockUser(username = "user")
    fun `新規登録時にエンティティに設定した変数と自動設定される変数が設定されることの確認`() {
        // テストデータ生成
        var book = Book()
        book.title = TEST_TITLE_NEW
        book.author = TEST_AUTHOR_NEW

        // 書籍を新規登録
        newBook = repository.saveAndFlush(book)

        // エンティティに設定した変数を検証
        assertEquals(newBook!!.title, TEST_TITLE_NEW)
        assertEquals(newBook!!.author, TEST_AUTHOR_NEW)

        // 自動設定される変数を検証
        assertNotEquals(newBook!!.id, DEFAULT_LONG_VALUE)
        assertEquals(newBook!!.version, FIRST_VERSION)
        assertNotNull(newBook!!.createdDateTime)
        assertNotNull(newBook!!.updatedDateTime)
        assertNotNull(newBook!!.createdUser)
        assertNotNull(newBook!!.updatedUser)
    }

    @Test
    @WithMockUser(username = "admin")
    fun `更新時にエンティティに設定した変数と自動設定される変数が更新されることの確認`() {
        //MEMO 処理の順番によりエラーが発生するため、注意が必要
        // 書籍の内容を更新
        val book = ModelMapper().map(
            newBook,
            Book::class.java
        )
        book.title = TEST_TITLE_UPD
        book.author = TEST_AUTHOR_UPD
        val updBook =  repository.saveAndFlush(book)

        // エンティティに設定した変数を検証
        assertEquals(updBook.title, TEST_TITLE_UPD)
        assertEquals(updBook.author, TEST_AUTHOR_UPD)

        // 自動設定される変数を検証
        assertEquals(updBook.id, newBook!!.id)
        assertNotEquals(updBook.version, newBook!!.version)
        assertEquals(updBook.createdDateTime, newBook!!.createdDateTime)
        assertNotEquals(updBook.updatedDateTime, newBook!!.updatedDateTime)
        assertEquals(updBook.createdUser, newBook!!.createdUser)
        assertNotEquals(updBook.updatedUser, newBook!!.updatedUser)
    }

    companion object {
        /**
         * テストデータのタイトル(新規作成時)
         */
        private const val TEST_TITLE_NEW = "testタイトル"
        /**
         * テストデータのタイトル(更新時)
         */
        private const val TEST_TITLE_UPD = "testタイトル(更新)"
        /**
         * テストデータの著者名(新規登録時)
         */
        private const val TEST_AUTHOR_NEW = "test著者名"
        /**
         * テストデータの著者名(更新時)
         */
        private const val TEST_AUTHOR_UPD = "test著者名(更新)"
        /**
         * Long値の初期値
         */
        private const val DEFAULT_LONG_VALUE: Long = 0
        /**
         * versionの初期値
         */
        private const val FIRST_VERSION: Long = 0
        /**
         * 新規登録した書籍のエンティティ
         */
        private var newBook: Book? = null
    }

}