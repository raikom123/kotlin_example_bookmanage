package com.example.bookmanage.selenium

import com.example.bookmanage.selenium.scenario.BookManageSeleniumTestCase
import org.junit.jupiter.api.DisplayName
import org.openqa.selenium.WebDriver
import org.openqa.selenium.safari.SafariDriver

/**
 * Safariによるブラウザテスト
 */
@DisplayName("Safariでの確認")
class BookManageSafariSeleniumTests: BookManageSeleniumTestCase() {

    override fun createWebDriver(): WebDriver {
        return SafariDriver()
    }

}