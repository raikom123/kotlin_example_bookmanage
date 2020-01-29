package com.example.bookmanage.selenium

import com.example.bookmanage.selenium.scenario.BookManageSeleniumTestCase
import org.junit.jupiter.api.DisplayName
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Firefoxによるブラウザテスト
 */
@DisplayName("Firefoxでの確認")
class BookManageFirefoxSeleniumTests: BookManageSeleniumTestCase() {

    override fun createWebDriver(): WebDriver {
        System.setProperty("webdriver.gecko.driver", "/opt/WebDriver/bin/geckodriver")
        return FirefoxDriver()
    }

}