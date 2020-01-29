package com.example.bookmanage.selenium.page

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * books画面の操作を行うクラス
 */
class BooksPage(private val driver: WebDriver) {

    /**
     * 書籍を新規登録する。
     *
     * @param title タイトル
     * @param author 著者
     */
    fun createNewBook(title: String, author: String) {
        setTitle(title)
        setAuthor(author)

        val beforeCount = getTableTitles().size
        val wait = WebDriverWait(driver, 10)

        driver.findElement(By.id("createButton")).sendKeys(Keys.ENTER)

        // 10秒間の間にBooks画面のテーブルの行数が変わったら、処理を終了する
        wait.until {
            val afterCount = getTableTitles().size
            return@until beforeCount != afterCount
        }
    }

    /**
     * タイトルを設定する
     *
     * @param title タイトル
     */
    private fun setTitle(title: String) {
        driver.findElement(By.id("input-text-title")).sendKeys(title)
    }

    /**
     * 著者を設定する
     *
     * @param author 著者
     */
    private fun setAuthor(author: String) {
        driver.findElement(By.id("input-text-author")).sendKeys(author)
    }

    /**
     * テーブルのタイトル列に表示されているテキストを取得する。
     *
     * @return タイトル列に表示されているテキスト
     */
    fun getTableTitles(): List<String> {
        return getTableCellTextList(1)
    }

    /**
     * テーブルの著者列に表示されているテキストを取得する。
     *
     * @return 著者列に表示されているテキスト
     */
    fun getTableAuthors(): List<String> {
        return getTableCellTextList(2)
    }

    /**
     * テーブルの指定した列番号に表示されているテキストを取得する。
     *
     * @return 指定した列番号に表示されているテキスト
     */
    private fun getTableCellTextList(colIndex: Int): List<String> {
        // テーブルの要素を取得
        val rows = driver.findElements(By.cssSelector(".table>tbody>tr>td:nth-child($colIndex)"))
        // 埋め込まれている文字列を取得
        return rows.map { it.text }
    }

}