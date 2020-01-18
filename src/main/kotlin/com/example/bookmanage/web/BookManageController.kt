package com.example.bookmanage.web

import com.example.bookmanage.exception.BookManageValidationException
import com.example.bookmanage.exception.BookNotFoundException
import com.example.bookmanage.form.BookManageForm
import com.example.bookmanage.service.BookManageService
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.security.Principal
import java.util.*

/**
 * 書籍管理システムのMVCコントローラ
 */
@Controller
class BookManageController(
    /**
     * 書籍管理システムのサービス
     */
    private val service: BookManageService,
    /**
     * メッセージソース
     */
    private val messageSource: MessageSource
) {

    companion object {

        /**
         * Logger
         */
        private val log = LoggerFactory.getLogger(this::class.java.enclosingClass)!!

        /**
         * ログイン画面のビュー名
         */
        private const val LOGIN = "login"

        /**
         * 書籍管理システムのビュー名
         */
        private const val BOOKS = "books"

        /**
         * リダイレクトのURL
         */
        private const val REDIRECT_TO_BOOKS =
            "redirect:/$BOOKS"

    }

    /**
     * ルートURLにアクセスする。
     *
     * @return "/books"へのリダイレクト
     */
    @GetMapping(value = ["/"])
    fun root(): ModelAndView {
        return ModelAndView(REDIRECT_TO_BOOKS)
    }

    // ------------------------------------------------------------------------
    // ログイン処理
    // ------------------------------------------------------------------------
    /**
     * ログイン画面にアクセスする。
     *
     * @return モデルビュー
     */
    @GetMapping(value = ["/login"])
    fun login(principal: Principal?): ModelAndView {
        //TODO セッションがあれば、booksにリダイレクト
        return ModelAndView(LOGIN)
    }

    /**
     * ログインに失敗した時の処理。
     *
     * @return モデルビュー
     */
    @GetMapping(value = ["/loginfailure"])
    fun loginfailure(): ModelAndView {
        val modelAndView = ModelAndView(LOGIN)
        modelAndView.addObject("loginFailure", true)
        return modelAndView
    }

    // ------------------------------------------------------------------------
    // ログアウト処理
    // ------------------------------------------------------------------------
    /**
     * ログアウトが成功した時の処理。
     *
     * @return モデルビュー
     */
    @GetMapping(value = ["/logoutsuccess"])
    fun logoutSuccess(): ModelAndView {
        val modelAndView = ModelAndView(LOGIN)
        modelAndView.addObject("logout", true)
        return modelAndView
    }

    // ------------------------------------------------------------------------
    // 書籍管理機能処理
    // ------------------------------------------------------------------------
    /**
     * 書籍一覧を読み込む。
     *
     * @return モデルビュー
     */
    @GetMapping(value = ["/books"])
    fun readBooks(principal: Principal): ModelAndView {
        // 認証情報を取得
        val authentication = principal as Authentication
        val userName = authentication.name
        val form = service.initForm()
        val modelAndView = toBookPages()
        modelAndView.addObject("bookManageForm", form)
        modelAndView.addObject("userName", userName)
        return modelAndView
    }

    /**
     * ビュー名を設定したモデルビューを返却する。
     *
     * @return return ビュー名を設定したモデルビュー
     */
    private fun toBookPages(): ModelAndView {
        return ModelAndView(BOOKS)
    }

    /**
     * 指定したIDに該当する書籍を読み込む。
     *
     * @param id 書籍のID
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @GetMapping(value = ["/books/{id}"])
    @Throws(Throwable::class)
    fun readOneBook(@PathVariable id: Long, locale: Locale): ModelAndView {
        val modelAndView = toBookPages()
        return try {
            val form = service.readOneBook(id)
            modelAndView.addObject("bookId", id)
            modelAndView.addObject("bookManageForm", form)
            modelAndView
        } catch (t: Throwable) {
            handleException(t, locale)
        }
    }

    /**
     * フォーム情報から書籍を新規登録する。
     *
     * @param form フォーム情報
     * @param result Validatorの結果
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @PostMapping(value = ["/books"])
    @Throws(Throwable::class)
    fun createOneBook(
        @Validated @ModelAttribute form: BookManageForm, result: BindingResult,
        locale: Locale
    ): ModelAndView {
        try {
            validateInputFormData(form, result)
            service.createBook(form)
        } catch (t: Throwable) {
            return handleException(form, t, locale)
        }
        return ModelAndView(REDIRECT_TO_BOOKS)
    }

    /**
     * 指定したIDの書籍をフォーム情報の内容に更新する。
     *
     * @param id 書籍のID
     * @param form フォーム情報
     * @param result Validatorの結果
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @PutMapping(value = ["/books/{id}"])
    @Throws(Throwable::class)
    fun updateOneBook(
        @PathVariable id: Long, @Validated @ModelAttribute form: BookManageForm,
        result: BindingResult, locale: Locale
    ): ModelAndView {
        try {
            validateInputFormData(form, result)
            service.updateBook(id, form)
        } catch (e: Exception) {
            val mav = handleException(form, e, locale)
            mav.addObject("bookId", id)
            return mav
        }
        return ModelAndView(REDIRECT_TO_BOOKS)
    }

    /**
     * 指定したIDの書籍を削除する。
     *
     * @param id 書籍のID
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @DeleteMapping(value = ["/books/{id}"])
    @Throws(Throwable::class)
    fun deleteOneBook(@PathVariable id: Long, locale: Locale): ModelAndView {
        try {
            service.deleteBook(id)
        } catch (t: Throwable) {
            return handleException(t, locale)
        }
        return ModelAndView(REDIRECT_TO_BOOKS)
    }

    // ------------------------------------------------------------------------
    // 管理者用処理
    // ------------------------------------------------------------------------
    /**
     * 管理者用画面へのアクセスした時の処理。
     *
     * @param principal 認証情報
     * @return モデルビュー
     */
    @GetMapping("/admin")
    fun admin(principal: Principal): ModelAndView {
        val modelAndView = readBooks(principal)
        modelAndView.viewName = "admin"
        return modelAndView
    }

    // ------------------------------------------------------------------------
    // エラー処理
    // ------------------------------------------------------------------------
    /**
     * セッションが無効になった時の処理。
     *
     * @return モデルビュー
     */
    @GetMapping("/invalidsession")
    fun invalidSession(): ModelAndView {
        val modelAndView = ModelAndView(LOGIN)
        modelAndView.addObject("sessionInvalid", true)
        return modelAndView
    }

    /**
     * フォーム情報の入力内容が妥当か否かを検証する。<br></br>
     * フォーム情報の入力内容に不備がある場合、Exceptionをthrowする。
     *
     * @param form フォーム情報
     * @param result validationの結果
     * @throws BookManageValidationException 入力内容にエラーがあった場合、発生する
     */
    @Throws(BookManageValidationException::class)
    private fun validateInputFormData(form: BookManageForm, result: BindingResult) {
        if (result.hasErrors()) {
            throw BookManageValidationException(result)
        }
    }

    /**
     * 例外を処理する。<br></br>
     * ビジネス例外の場合、エラーメッセージを設定したモデルビューを返却する。
     *
     * @param t 例外
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @Throws(Throwable::class)
    private fun handleException(t: Throwable, locale: Locale): ModelAndView {
        val form = BookManageForm()
        form.newBook = true
        return handleException(form, t, locale)
    }

    /**
     * 例外を処理する。<br></br>
     * ビジネス例外の場合、エラーメッセージを設定したモデルビューを返却する。
     *
     * @param form フォーム情報
     * @param t 例外
     * @param locale ロケール
     * @return モデルビュー
     * @throws Throwable ビジネス例外以外の例外が発生した場合、throwされる
     */
    @Throws(Throwable::class)
    private fun handleException(form: BookManageForm, t: Throwable, locale: Locale): ModelAndView {
        if (t is BookNotFoundException) {
            // 書籍が取得出来ない場合
            val message = messageSource.getMessage("error.booknotfound", null, locale)
            BookManageController.log.warn(message, t)
            return toBookPageForError(form, message)
        } else if (t is ObjectOptimisticLockingFailureException) {
            // 楽観排他でエラーが発生した場合
            val message = messageSource.getMessage("error.optlockfailure", null, locale)
            BookManageController.log.warn(message, t)
            return toBookPageForError(form, message)
        } else if (t is BookManageValidationException) {
            // 入力内容のエラーが発生した場合
            val message = messageSource.getMessage("error.validation", null, locale)
            BookManageController.log.warn(message, t)
            return toBookPageForError(form, message)
        }
        throw t
    }

    /**
     * エラーメッセージを設定したモデルビューを返却する。<br></br>
     * 書籍一覧の設定も行う。
     *
     * @param form フォーム情報
     * @param errorMessage エラーメッセージ
     * @return モデルビュー
     */
    private fun toBookPageForError(form: BookManageForm, errorMessage: String): ModelAndView {
        // 書籍一覧を取得し直す
        val initForm = service.initForm()
        form.books = initForm.books
        val modelAndView = toBookPages()
        modelAndView.addObject("bookManageForm", form)
        modelAndView.addObject("errorMessage", errorMessage)
        return modelAndView
    }

}