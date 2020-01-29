package com.example.bookmanage.selenium.page

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * login画面の操作を行うクラス
 */
class LoginPage(private val driver: WebDriver, private val port: Int) {

    companion object {

        /**
         * login画面にアクセスする
         *
         * @param driver WebDriver
         * @param port ポート
         * @return login画面オブジェクト
         */
        fun to(driver: WebDriver, port: Int): LoginPage {
            val loginPage = LoginPage(driver, port)
            loginPage.access()
            return loginPage
        }

    }

    /**
     * ログイン画面にアクセスする
     */
    private fun access() {
        driver.get("http://localhost:${port}")
    }

    /**
     * ログイン処理を行う。
     *
     * @param userName ユーザ名
     * @param password パスワード
     * @return books画面オブジェクト
     */
    fun login(userName: String, password: String): BooksPage {
        driver.findElement(By.id("username")).sendKeys(userName)
        driver.findElement(By.id("password")).sendKeys(password)

        val wait = WebDriverWait(driver, 10)
        driver.findElement(By.id("loginButton")).sendKeys(Keys.ENTER)
        // 10秒間の間にBooks画面から「タイトル」テキストボックスを取得する
        val titleText = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-text-title")))
        println(titleText)
        return BooksPage(driver)
    }

}