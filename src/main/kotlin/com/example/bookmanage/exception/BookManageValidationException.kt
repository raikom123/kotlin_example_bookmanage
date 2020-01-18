package com.example.bookmanage.exception

import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import java.text.MessageFormat

/**
 * 入力内容に不正があった時の例外処理
 */
class BookManageValidationException(result: BindingResult) : Exception(createMessage(result)) {
    companion object {
        /**
         * validatorの結果からエラーメッセージを生成します。
         *
         * @param result validatorの結果
         * @return エラーメッセージ
         */
        private fun createMessage(result: BindingResult): String {
            return result.fieldErrors.stream()
                .map {
                        e: FieldError -> createMessage(e)
                }
                .reduce("validation error!") {
                        s1: String, s2: String -> s1 + System.lineSeparator() + s2
                }
        }

        /**
         * フィールドエラーからエラーメッセージを生成します。
         *
         * @param e フィールドエラー
         * @return エラーメッセージ
         */
        private fun createMessage(e: FieldError): String {
            return MessageFormat.format(e.defaultMessage!!, e.codes)
        }
    }
}