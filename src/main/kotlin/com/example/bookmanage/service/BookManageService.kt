package com.example.bookmanage.service

import com.example.bookmanage.domain.Book
import com.example.bookmanage.exception.BookNotFoundException
import com.example.bookmanage.form.BookManageForm
import com.example.bookmanage.repository.BookRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍管理システムのサービス
 */
@Service
class BookManageService(
    /**
     * 書籍のリポジトリ
     */
    private val bookRepository: BookRepository
) {

    /**
     * フォーム情報の初期化を行う。
     *
     * @return フォーム情報
     */
    @Transactional(readOnly = true)
    fun initForm(): BookManageForm { // 一覧を取得する
        val books = bookRepository.findAll()
        return BookManageForm(true, books)
    }

    /**
     * 指定したIDに該当する書籍を取得し、フォーム情報を返却する。
     *
     * @param id 書籍のID
     * @return フォーム情報
     * @throws BookNotFoundException 書籍が取得できない場合に発生する
     */
    @Transactional(readOnly = true)
    @Throws(BookNotFoundException::class)
    fun readOneBook(id: Long): BookManageForm { // IDでエンティティを取得する
        val book = bookRepository.findById(id)
            .orElseThrow { BookNotFoundException(id) }
        // 一覧を取得する
        val books = bookRepository.findAll()
        val form = BookManageForm(false, books)
        // エンティティの内容をフォームに反映する
        val modelMapper = ModelMapper()
        modelMapper.map(book, form)
        return form
    }

    /**
     * 指定したIDに該当する書籍をフォーム情報の内容に更新する。
     *
     * @param id 書籍のID
     * @param form フォーム情報
     * @return 更新後の書籍
     * @throws BookNotFoundException 書籍が取得できない場合に発生する
     */
    @Transactional(readOnly = false)
    @Throws(BookNotFoundException::class, ObjectOptimisticLockingFailureException::class)
    fun updateBook(id: Long, form: BookManageForm): Book { // IDでエンティティを取得する
        val book = bookRepository.findById(id)
            .orElseThrow { BookNotFoundException(id) }
        // 楽観排他
        if (book.version != form.version) {
            throw ObjectOptimisticLockingFailureException(Book::class.java, id)
        }
        // フォームの内容をエンティティに更新する
        val modelMapper = ModelMapper()
        modelMapper.map(form, book)
        // エンティティの更新
        return bookRepository.save(book)
    }

    /**
     * フォーム情報から書籍を新規作成する
     *
     * @param form フォーム情報
     * @return 新規作成した書籍
     */
    @Transactional(readOnly = false)
    fun createBook(form: BookManageForm?): Book { // フォーム情報を使って、エンティティを生成する
        val modelMapper = ModelMapper()
        val book = modelMapper.map(form, Book::class.java)
        // エンティティを登録する
        return bookRepository.save(book)
    }

    /**
     * 指定したIDに該当する書籍を削除する。
     *
     * @param id 書籍のID
     * @throws BookNotFoundException 書籍が取得できない場合に発生する
     */
    @Transactional(readOnly = false)
    @Throws(BookNotFoundException::class)
    fun deleteBook(id: Long) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
        } else {
            throw BookNotFoundException(id)
        }
    }

}