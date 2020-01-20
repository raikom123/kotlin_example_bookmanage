package com.example.bookmanage.form

import com.example.bookmanage.WebMvcConfig
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.Validator
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * 書籍管理システムのフォーム情報のテストプログラム
 */
@ExtendWith(SpringExtension::class)
@WebAppConfiguration
@EnableWebMvc
@ContextConfiguration(classes = [WebMvcConfig::class])
internal class BookManageFormTests {
    /**
     * Validator
     */
    @Autowired
    private lateinit var validator: Validator

    /**
     * メッセージリソース
     */
    private lateinit var messageSource: ReloadableResourceBundleMessageSource

    /**
     * フォーム情報
     */
    private val form = BookManageForm()
    /**
     * BindingResult
     */
    private val result: BindingResult = BindException(form, "bookManageForm")

    @BeforeEach
    fun setUp() {
        // 正常な値を設定する
        form.title = TITLE_SUCCESS
        form.author = AUTHOR_SUCCESS

        messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:ValidationMessages")
        messageSource.setDefaultEncoding("UTF-8")
    }

    @Test
    fun `入力値が正常な場合＿エラーが発生しないことの確認`() {
        validator.validate(form, result)
        assertNull(result.fieldError)
    }

    @Test
    fun `タイトルが空の場合_エラーが発生することの確認`() {
        // 想定されるメッセージをリソースから取得する
        val actualMessage = messageSource.getMessage(
            "javax.validation.constraints.NotBlank.message",
            null,
            LocaleContextHolder.getLocale()
        )

        // 不正な値を設定し、チェックを行う
        form.title = TITLE_ERROR_NOT_EMPTY
        validator.validate(form, result)

        // エラーが発生したフィールドとメッセージを確認する
        assertEquals(result.fieldError!!.field, "title")
        assertEquals(result.fieldError!!.defaultMessage, actualMessage)
    }

    @Test
    fun `タイトルの文字数が最大を超えている場合_エラーが発生することの確認`() {
        // 想定されるメッセージをリソースから取得する
        val actualMessage = messageSource.getMessage(
            "validation.max-size",
            null,
            LocaleContextHolder.getLocale()
        )

        // 不正な値を設定し、チェックを行う
        form.title = TITLE_ERROR_MAX_SIZE
        validator.validate(form, result)

        // エラーが発生したフィールドとメッセージを確認する
        assertEquals(result.fieldError!!.field, "title")
        assertEquals(result.fieldError!!.defaultMessage, actualMessage)
    }

    @Test
    fun `著者が空の場合_エラーが発生することの確認`() {
        // 想定されるメッセージをリソースから取得する
        val actualMessage = messageSource.getMessage(
            "javax.validation.constraints.NotBlank.message",
            null,
            LocaleContextHolder.getLocale()
        )

        // 不正な値を設定し、チェックを行う
        form.author = AUTHOR_ERROR_NOT_EMPTY
        validator.validate(form, result)

        // エラーが発生したフィールドとメッセージを確認する
        assertEquals(result.fieldError!!.field, "author")
        assertEquals(result.fieldError!!.defaultMessage, actualMessage)
    }

    @Test
    fun `著者の文字数が最大を超えている場合_エラーが発生することの確認`() {
        // 想定されるメッセージをリソースから取得する
        val actualMessage = messageSource.getMessage(
            "validation.max-size",
            null,
            LocaleContextHolder.getLocale()
        )

        // 不正な値を設定し、チェックを行う
        form.author = AUTHOR_ERROR_MAX_SIZE
        validator.validate(form, result)

        // エラーが発生したフィールドとメッセージを確認する
        assertEquals(result.fieldError!!.field, "author")
        assertEquals(result.fieldError!!.defaultMessage, actualMessage)
    }

    companion object {
        /**
         * タイトルの正常なデータ
         */
        private const val TITLE_SUCCESS = "123456789012345678901234567890"
        /**
         * 著者の正常なデータ
         */
        private const val AUTHOR_SUCCESS = "１２３４５６７８９０１２３４５６７８９０"
        /**
         * タイトルの空入力エラー用のデータ
         */
        private const val TITLE_ERROR_NOT_EMPTY = ""
        /**
         * 著者の空入力エラー用のデータ
         */
        private const val AUTHOR_ERROR_NOT_EMPTY = ""
        /**
         * タイトルの桁の最大値を超えるエラー用のデータ
         */
        private const val TITLE_ERROR_MAX_SIZE = "1234567890123456789012345678901"
        /**
         * 著者の桁の最大値を超えるエラー用のデータ
         */
        private const val AUTHOR_ERROR_MAX_SIZE = "１２３４５６７８９０１２３４５６７８９０１"

    }
}