package com.example.bookmanage.selenium.scenario

import com.example.bookmanage.BookmanageApplication
import com.example.bookmanage.selenium.page.LoginPage
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ブラウザテストのテストケースを実装したクラス
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [BookmanageApplication::class],
    webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BookManageSeleniumTestCase {

    /**
     * WebDriver
     */
    lateinit var driver: WebDriver

    /**
     * WebDriverを生成する。
     */
    abstract fun createWebDriver(): WebDriver

    /**
     * ポート
     */
    @LocalServerPort
    val port = 0

    @BeforeEach
    fun before() {
        driver = createWebDriver()
    }

    @Test
    fun `login画面からbooks画面への画面遷移の確認`() {
        val loginPage = LoginPage.to(driver, port)

        // Exceptionが発生しなければ正常
        loginPage.login("user", "pass")
    }

    @Test
    fun `books画面のタイトルと著者がサニタイジングされているか否かの確認`() {
        // login画面からbooks画面に遷移する
        val loginPage = LoginPage.to(driver, port)
        val booksPage = loginPage.login("user", "pass")

        // タイトルと著者を入力して、登録する
        val title = "<iframe src='https://www.google.com/'>"
        val author = "author"
        booksPage.createNewBook(title, author)

        // Seleniumでは、ブラウザに表示されている文字を取得するため、
        // 入力値と画面に表示されている文字が一致することを確認する
        val targetTitle = booksPage.getTableTitles().first()
        assertEquals(title, targetTitle)

        val targetAuthor = booksPage.getTableAuthors().first()
        assertEquals(author, targetAuthor)
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
    }

}