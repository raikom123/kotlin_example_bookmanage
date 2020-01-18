package com.example.bookmanage.web

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * 書籍管理の例外を処理する
 */
@ControllerAdvice
class BookManageExceptionHandler {

    companion object {

        /**
         * Logger
         */
        private val log = LoggerFactory.getLogger(this::class.java.enclosingClass)!!

    }

    /**
     * 例外を処理する。<br></br>
     * 例外をログ出力し、エラー画面のHTML名を返却する。
     *
     * @param e 例外
     * @return エラー画面のHTML名
     */
    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception?): String {
        BookManageExceptionHandler.log.error("system error!", e)
        return "error"
    }

}