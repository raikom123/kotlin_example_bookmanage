package com.example.bookmanage.service

import com.example.bookmanage.BookmanageApplication
import com.example.bookmanage.domain.Book
import com.example.bookmanage.exception.BookNotFoundException
import com.example.bookmanage.form.BookManageForm
import com.example.bookmanage.repository.BookRepository
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.modelmapper.ModelMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.util.*

/**
 * BookManageServiceのテストプログラム
 */
@SpringBootTest(classes = [BookmanageApplication::class])
internal class BookManageServiceTests {
    /**
     * 書籍管理システムのサービス
     */
    @InjectMocks
    private lateinit var service: BookManageService

    /**
     * 書籍管理システムのリポジトリ
     */
    @Mock
    private lateinit var repository: BookRepository

    /**
     * テストデータの書籍
     */
    private lateinit var testBook: Book

    @BeforeEach
    fun setup() { // テストデータの生成
        testBook = Book()
        testBook.id = TEST_ID
        testBook.title = TEST_TITLE
        testBook.author = TEST_AUTHOR
        testBook.version = TEST_VERSION
    }

    @Test
    fun `initForm_戻り値の変数とメソッドの呼び出しの確認`() {
        // モック
        Mockito.`when`(repository.findAll())
            .thenReturn(listOf(testBook))
        // initFormの呼び出し
        val form = service.initForm()
        // 変数を評価する
        assertNull(form.title)
        assertNull(form.author)
        assertEquals(form.newBook, true)
        assertEquals(form.version, 0)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)
        // booksにrepository.findAllの結果が設定されているか評価する
        val book: Book = form.books!![0]
        assertEquals(book.title, TEST_TITLE)
        assertEquals(book.author, TEST_AUTHOR)
        assertEquals(book.id, TEST_ID)
        assertEquals(book.version, TEST_VERSION)
        // repositoryのメソッドの呼び出しを確認
        Mockito.verify(repository, Mockito.times(1)).findAll()
    }

    @Test
    fun `readOneBook_戻り値とメソッドの呼び出しの確認`() {
        // モック
        Mockito.`when`(
            repository.findById(
                TEST_ID
            )
        ).thenReturn(Optional.of(testBook))
        Mockito.`when`(repository.findAll())
            .thenReturn(listOf(testBook))

        // readOneBookを呼び出す
        val form = service.readOneBook(TEST_ID)
        // 変数を評価する
        assertEquals(form.title, TEST_TITLE)
        assertEquals(form.author, TEST_AUTHOR)
        assertEquals(form.newBook, false)
        assertEquals(form.version, TEST_VERSION)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)
        // repositoryのメソッドの呼び出しを確認
        Mockito.verify(repository, Mockito.times(1)).findAll()
        Mockito.verify(repository, Mockito.times(1))
            .findById(TEST_ID)
    }

    @Test
    fun `readOneBook_指定したIDのデータが取得できない場合_例外が発生することの確認`() {
        // モック
        Mockito.`when`(
            repository.findById(
                TEST_ID
            )
        ).thenReturn(Optional.ofNullable(null))
        Mockito.`when`(repository.findAll())
            .thenReturn(listOf())
        // readOneBookを呼び出し、Exceptionが発生することを確認する
        assertThrows(BookNotFoundException::class.java) {
            service.readOneBook(TEST_ID)
        }
    }

    @Test
    fun `updateBook_戻り値と保存処理の呼び出しの確認`() {
        // モック
        Mockito.`when`(
            repository.findById(
                TEST_ID
            )
        ).thenReturn(Optional.of(testBook))
        Mockito.`when`(repository.save(testBook))
            .thenReturn(testBook)
        // updateBookを呼び出す
        val form = BookManageForm()
        form.title = TEST_TITLE
        form.author = TEST_AUTHOR
        form.version = TEST_VERSION
        val book = service.updateBook(TEST_ID, form)
        // 戻り値と同じ値か否かを評価
        assertThat(book).isEqualTo(testBook)
        // saveが呼び出されることを確認
        Mockito.verify(repository, Mockito.times(1))
                .save(testBook)
    }

    @Test
    fun `updateBook_DBのバージョンと異なるバージョンを指定した場合_例外が発生することの確認`() {
        // モック
        Mockito.`when`(
            repository.findById(
                TEST_ID
            )
        ).thenReturn(Optional.of(testBook))
        // updateBookを呼び出す
        val form = BookManageForm()
        form.title = TEST_TITLE
        form.author = TEST_AUTHOR
        form.version = INVALID_TEST_VERSION

        // updateBookを呼び出し、楽観排他の例外が発生することを確認する
        assertThrows(ObjectOptimisticLockingFailureException::class.java) {
            service.updateBook(TEST_ID, form)
        }
    }

    @Test
    fun `updateBook_指定したIDでデータが取得できない場合_例外が発生することの確認`() {
        // モック
        Mockito.`when`(
            repository.findById(
                TEST_ID
            )
        ).thenReturn(Optional.ofNullable(null))
        // updateBookを呼び出す
        val form = BookManageForm()
        form.title = TEST_TITLE
        form.author = TEST_AUTHOR
        form.version = TEST_VERSION

        // updateBookを呼び出し、例外が発生することを確認する
        assertThrows(BookNotFoundException::class.java) {
            service.updateBook(TEST_ID, form)
        }
    }

    @Test
    fun `createBook_戻り値と保存処理の呼び出しを確認`() {
        // 引数を作成
        val form = BookManageForm()
        form.title = TEST_TITLE
        form.author = TEST_AUTHOR
        // モック
        whenever(repository.save(ArgumentMatchers.any(Book::class.java))).thenReturn(testBook)
        // createBookを呼び出す
        val book = service.createBook(form)
        // 戻り値と同じ値か否かを評価
        assertThat(book).isEqualTo(testBook)
        // saveが呼び出されることを確認
        Mockito.verify(repository, Mockito.times(1))
            .save(ArgumentMatchers.any())
    }

    @Test
    fun `deleteBook_削除処理の呼び出しの確認`() {
        // モック
        Mockito.`when`(repository.existsById(TEST_ID)).thenReturn(true)
        // deleteBookを呼び出す
        service.deleteBook(TEST_ID)
        // deleteByIdが呼び出されることを確認
        Mockito.verify(repository, Mockito.times(1))
            .deleteById(TEST_ID)
    }

    @Test
    fun `deleteBook_指定したIDのデータが存在しない場合_例外が発生することの確認`() {
        // モック
        Mockito.`when`(repository.existsById(TEST_ID)).thenReturn(false)

        // deleteBookを呼び出し、例外が発生することを確認する
        assertThrows(BookNotFoundException::class.java) {
            service.deleteBook(TEST_ID)
        }
    }

    companion object {
        /**
         * テストデータのID
         */
        private const val TEST_ID: Long = 1
        /**
         * テストデータのタイトル
         */
        private const val TEST_TITLE = "testタイトル"
        /**
         * テストデータの著者名
         */
        private const val TEST_AUTHOR = "test著者名"
        /**
         * テストデータのバージョン
         */
        private const val TEST_VERSION: Long = 2
        /**
         * 不正なテストデータのバージョン
         */
        private const val INVALID_TEST_VERSION: Long = 3
    }

}