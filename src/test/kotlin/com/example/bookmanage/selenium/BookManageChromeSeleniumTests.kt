package com.example.bookmanage.selenium

import com.example.bookmanage.selenium.scenario.BookManageSeleniumTestCase
import org.junit.jupiter.api.DisplayName
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Google Chromeによるブラウザテスト
 */
@DisplayName("Google Chromeでの確認")
class BookManageChromeSeleniumTests: BookManageSeleniumTestCase() {

    override fun createWebDriver(): WebDriver {
        System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver")
        return ChromeDriver()
    }

}