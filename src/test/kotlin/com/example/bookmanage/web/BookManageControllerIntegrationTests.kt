package com.example.bookmanage.web

import com.example.bookmanage.BookmanageApplication
import com.example.bookmanage.form.BookManageForm
import com.example.bookmanage.web.BookManageControllerUnitTests.Companion.toMultiValueMap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(classes = [BookmanageApplication::class])
internal class BookManageControllerIntegrationTests {

    @Autowired
    private lateinit var context: WebApplicationContext

    /**
     * Httpリクエスト・レスポンスを扱うためのMockオブジェクト
     */
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() { // MVCモックを生成
        mockMvc = webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .alwaysDo<DefaultMockMvcBuilder>(log())
            .build()
    }

    @Test
    fun `login処理でログインに成功した場合の確認`() {
        // ログイン処理を行う
        mockMvc.perform(formLogin("/authenticate").user("user").password("pass"))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `login処理でログインに失敗した場合の確認`() {
        // ログイン処理を行う
        mockMvc.perform(formLogin("/authenticate").user("user").password("xxxxx"))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/loginfailure")) // /loginfailureにリダイレクトするか否か
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = ["ROLE_USER"])
    fun `認証ありでgetリクエストでbooksにアクセスする場合のステータスとビューとモデルの確認`() {
        // getリクエストでbooksを指定する
        val result = mockMvc.perform(get("/books"))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("books")) // ビュー名が"books"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertNull(form.title)
        assertNull(form.author)
        assertEquals(form.newBook, true)
        assertEquals(form.version, 0)
        assertNotNull(form.books)
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = ["ROLE_USER"])
    fun `認証ありでpostリクエストでbooksにアクセスする場合のステータスとリダイレクトURLの確認`() {
        // postリクエストでbooksを指定する
        //MEMO csrfを設定しないとセッションが無効になる
        mockMvc.perform(post("/books").with(csrf()).params(toMultiValueMap(BookManageForm().apply {
                title = TEST_TITLE
                author = TEST_AUTHOR
                newBook = true
                version = 0
            })))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `認証なしでbooksにアクセスしようとした場合の確認`() {
        // getリクエストでbooksにアクセス
        mockMvc.perform(get("/books"))
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("http://localhost/login"))
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = ["ROLE_ADMIN"])
    fun `管理者権限があるユーザでadminにアクセスしようとした場合の確認`() {
        // getリクエストでadminを指定する
        val result = mockMvc.perform(get("/admin"))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("admin")) // ビュー名が"admin"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertNull(form.title)
        assertNull(form.author)
        assertEquals(form.newBook, true)
        assertEquals(form.version, 0)
        assertNotNull(form.books)
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = ["ROLE_USER"])
    fun `管理者権限がないユーザでadminにアクセスしようとした場合の確認`() {
        // getリクエストでadminにアクセス
        //MEMO csrfを設定しないとセッションが無効になる
        mockMvc.perform(get("/admin").with(csrf()))
            .andDo(print())
            .andExpect(status().isForbidden) // クライアントエラー(403)
    }

    @Test
    fun `ログアウトした場合の確認`() {
        // getリクエストでlogoutにアクセス
        mockMvc.perform(get("/logout"))
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/logoutsuccess"))
    }

    companion object {
        private const val TEST_TITLE = "タイトル"
        private const val TEST_AUTHOR = "著者"
    }
}