package com.example.bookmanage.web

import com.example.bookmanage.BookmanageApplication
import com.example.bookmanage.domain.Book
import com.example.bookmanage.exception.BookNotFoundException
import com.example.bookmanage.form.BookManageForm
import com.example.bookmanage.service.BookManageService
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.view.InternalResourceViewResolver

/**
 * BookManageControllerの単体テストプログラム
 */
@SpringBootTest(classes = [BookmanageApplication::class])
class BookManageControllerUnitTests {
    /**
     * テストデータの書籍
     */
    private lateinit var testBook: Book

    /**
     * 書籍管理システムのController
     */
    @InjectMocks
    private lateinit var controller: BookManageController

    /**
     * 書籍管理システムのサービス
     */
    @Mock
    private lateinit var service: BookManageService

    /**
     * メッセージソースのモック
     */
    @Mock
    private lateinit var mockMessageSource: MessageSource

    /**
     * Httpリクエスト・レスポンスを扱うためのMockオブジェクト
     */
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() { // テストデータを生成
        testBook = Book()
        testBook.id = TEST_ID
        testBook.title = TEST_TITLE
        testBook.author = TEST_AUTHOR
        testBook.version = TEST_VERSION

        // [Circular view path]の例外が発生するため、ViewResolverを設定する
        val prefix = "/WEB-INF/pages/"
        val suffix = ".html"
        val viewResolver = InternalResourceViewResolver(prefix, suffix)
        // MVCモックを生成
        mockMvc = standaloneSetup(controller)
            .setControllerAdvice(BookManageExceptionHandler())
            .setViewResolvers(viewResolver)
            .alwaysDo<StandaloneMockMvcBuilder>(log())
            .build()
    }

    /**
     * 登録データが0件の時にgetリクエストでbooksを指定し、
     * httpステータスとビュー名とモデルに設定されている変数で成否を判定
     */
    @Test
    fun `readBooks_データが登録されていない時のステータスとビューとモデルの確認`() {
        // モックを登録
        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf()
        whenever(service.initForm()).thenReturn(initForm)
        // 認証情報のモック
        val mockPrincipal: Authentication = mock {
            on { name }.thenReturn("user")
        }

        // getリクエストでbooksを指定する
        val result = mockMvc.perform(get("/books").principal(mockPrincipal))
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
        assertEquals(form.books!!.size, 0)
    }

    /**
     * 登録データが1件の時にgetリクエストでbooksを指定し、
     * httpステータスとビュー名とモデルに設定されている変数で成否を判定
     */
    @Test
    fun `readBooks_データが1件登録されている時のステータスとビューとモデルの確認`() {
        // モックを登録
        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)
        whenever(service.initForm()).thenReturn(initForm)
        // 認証情報のモック
        val mockPrincipal: Authentication = mock {
            on { name }.thenReturn("user")
        }

        // getリクエストでbooksを指定する
        val result = mockMvc.perform(get("/books").principal(mockPrincipal))
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
        assertEquals(form.books!!.size, 1)
    }

    /**
     * getリクエストでbooks/{id}を指定し、存在しないidを指定した時のhttpステータスとビュー名とモデルに設定されている変数で成否を判定
     */
    @Test
    fun `readOneBook_データが存在するidを指定した時のステータスとビューとモデルの確認`() {
        // モックを登録
        val readOneForm = BookManageForm()
        readOneForm.title = TEST_TITLE
        readOneForm.author = TEST_AUTHOR
        readOneForm.newBook = false
        readOneForm.version = TEST_VERSION
        readOneForm.books = listOf(testBook)
        whenever(service.readOneBook(TEST_ID)).thenReturn(readOneForm)

        // getリクエストでbooks/{id}を指定する
        val result = mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk) // HTTPステータスが200か否か
                .andExpect(view().name("books")) // ビュー名が"books"か否か
                .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertEquals(form.title, TEST_TITLE)
        assertEquals(form.author, TEST_AUTHOR)
        assertEquals(form.newBook, false)
        assertEquals(form.version, TEST_VERSION)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)

        // モデルからメッセージを取得し、リソースファイルのメッセージと同じか評価する
        val message = result.modelAndView!!.model["errorMessage"] as String?
        assertNull(message)
    }

    /**
     * getリクエストでbooks/{id}を指定し、存在しないidを指定した時のhttpステータスとビュー名とモデルに設定されている変数で成否を判定
     */
    @Test
    fun `readOneBook_データが存在しないidを指定した時のステータスとビューとモデルの確認`() {
        // モックを登録
        whenever(service.readOneBook(INVALID_TEST_ID)).thenThrow(BookNotFoundException(INVALID_TEST_ID))
        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
        ).thenReturn(TEST_MESSAGE)

        // getリクエストでbooks/{id}を指定する
        val result = mockMvc.perform(get("/books/2"))
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
        assertEquals(form.books!!.size, 1)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String?
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.booknotfound")
        }
    }

    @Test
    fun `createOneBook_正常に新規登録した場合のステータスとリダイレクトURLの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.title = TEST_TITLE
        inputForm.author = TEST_AUTHOR
        inputForm.newBook = true
        inputForm.version = 0

        // モックを登録
        whenever(service.createBook(inputForm)).thenReturn(testBook)

        // postリクエストでbooksを指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        mockMvc.perform(post("/books").params(params))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `createOneBook_入力エラーが発生した場合のステータスとビューとモデルの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.newBook = true
        inputForm.version = 0

        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.version = 0
        initForm.books = listOf()

        // モックを登録
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                any(),
                ArgumentMatchers.any<Array<Any>>(),
                any()
            )
        ).thenReturn(TEST_MESSAGE)

        // postリクエストでbooksを指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        val result = mockMvc.perform(post("/books").params(params))
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
        assertEquals(form.books!!.size, 0)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.validation")
        }

        // エラーの件数を確認する
        //MEMO bindingResultの詳細な確認は、BookManageFormTestsで行う
        val bindingResult =
            result.modelAndView!!.model["org.springframework.validation.BindingResult.bookManageForm"] as BindingResult
        assertEquals(bindingResult.errorCount, 2)
    }

    @Test
    fun `updateOneBook_正常に更新した場合のステータスとリダイレクトURLの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.title = TEST_TITLE
        inputForm.author = TEST_AUTHOR
        inputForm.newBook = false
        inputForm.version = 0

        // モックを登録
        whenever(service.updateBook(TEST_ID, inputForm)).thenReturn(testBook)

        // putリクエストでbooks/{id}を指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        mockMvc.perform(put("/books/1").params(params))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `updateOneBook_指定したIDのデータが存在しない場合のステータスとビューとモデルの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.title = TEST_TITLE_ERROR
        inputForm.author = TEST_AUTHOR_ERROR
        inputForm.newBook = false
        inputForm.version = TEST_VERSION

        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)

        // モックを登録
        whenever(
            service.updateBook(
                ArgumentMatchers.eq(INVALID_TEST_ID),
                any()
            )
        ).thenThrow(BookNotFoundException(INVALID_TEST_ID))
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
        ).thenReturn(TEST_MESSAGE)

        // putリクエストでbooks/{id}を指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        val result = mockMvc.perform(put("/books/2").params(params))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("books")) // ビュー名が"books"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertEquals(form.title, inputForm.title)
        assertEquals(form.author, inputForm.author)
        assertEquals(form.newBook, inputForm.newBook)
        assertEquals(form.version, inputForm.version)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.booknotfound")
        }
    }

    @Test
    fun `updateOneBook_バージョンが更新されている場合のステータスとビューとモデルの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.title = TEST_TITLE_ERROR
        inputForm.author = TEST_AUTHOR_ERROR
        inputForm.newBook = false
        inputForm.version = TEST_VERSION

        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)

        // モックを登録
        whenever(
            service.updateBook(
                ArgumentMatchers.eq(INVALID_TEST_ID),
                any()
            )
        ).thenThrow(
            ObjectOptimisticLockingFailureException(
                Book::class.java, INVALID_TEST_ID
            )
        )
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
        ).thenReturn(TEST_MESSAGE)

        // putリクエストでbooks/{id}を指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        val result = mockMvc.perform(put("/books/2").params(params))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("books")) // ビュー名が"books"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertEquals(form.title, inputForm.title)
        assertEquals(form.author, inputForm.author)
        assertEquals(form.newBook, inputForm.newBook)
        assertEquals(form.version, inputForm.version)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.optlockfailure")
        }
    }

    @Test
    fun `updateOneBook_入力エラーが発生する場合のステータスとビューとモデルの確認`() {
        // テストデータ作成
        val inputForm = BookManageForm()
        inputForm.title = ""
        inputForm.author = ""
        inputForm.newBook = false
        inputForm.version = TEST_VERSION

        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)

        // モックを登録
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
        ).thenReturn(TEST_MESSAGE)

        // putリクエストでbooks/{id}を指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", inputForm.title)
        params.add("author", inputForm.author)
        params.add("newBook", inputForm.newBook.toString())
        params.add("version", inputForm.version.toString())
        val result = mockMvc.perform(put("/books/1").params(params))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("books")) // ビュー名が"books"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertEquals(form.title, inputForm.title)
        assertEquals(form.author, inputForm.author)
        assertEquals(form.newBook, inputForm.newBook)
        assertEquals(form.version, inputForm.version)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.validation")
        }

        // エラーの件数を確認する
        //MEMO bindingResultの詳細な確認は、BookManageFormTestsで行う
        val bindingResult =
            result.modelAndView!!.model["org.springframework.validation.BindingResult.bookManageForm"] as BindingResult
        assertEquals(bindingResult.errorCount, 2)
    }

    @Test
    fun `deleteOneBook_正常に削除した場合のステータスとリダイレクトURLの確認`() {
        // モックを登録
        doNothing().whenever(service).deleteBook(TEST_ID)

        // deleteリクエストでbooksを指定する
        mockMvc.perform(delete("/books/1"))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `deleteOneBook_指定したIDのデータが存在しない場合のステータスとビューとモデルの確認`() {
        // モックを登録
        doThrow(BookNotFoundException(INVALID_TEST_ID)).whenever(service).deleteBook(INVALID_TEST_ID)
        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)
        whenever(service.initForm()).thenReturn(initForm)
        whenever(
            mockMessageSource.getMessage(
                ArgumentMatchers.any(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
        ).thenReturn(TEST_MESSAGE)

        // deleteリクエストでbooks/{id}を指定する
        val result = mockMvc.perform(delete("/books/2"))
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
        assertEquals(form.books!!.size, 1)

        // モデルにメッセージが設定されているかを評価する
        val message = result.modelAndView!!.model["errorMessage"] as String
        assertEquals(message, TEST_MESSAGE)

        // メッセージソースの引数を確認する
        argumentCaptor<String> {
            verify(mockMessageSource).getMessage(
                capture(),
                ArgumentMatchers.any<Array<Any>>(),
                ArgumentMatchers.any()
            )
            assertEquals(firstValue, "error.booknotfound")
        }
    }

    @Test
    fun `ルートURLを指定した場合のステータスとビュー名の確認`() {
        // getリクエストで"/"を指定する
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().is3xxRedirection) // HTTPステータスが3xxか否か(リダイレクト)
            .andExpect(redirectedUrl("/books")) // /booksにリダイレクトするか否か
    }

    @Test
    fun `idに文字を指定した場合のステータスとビュー名の確認`() {
        // putリクエストでbooks/{id}を指定する
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("title", TEST_TITLE)
        params.add("author", TEST_AUTHOR)
        params.add("newBook", false.toString())
        params.add("version", 0.toString())
        mockMvc.perform(put("/books/a").params(params))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("error")) // ビュー名がerrorか否か
    }

    @Test
    fun `login_ログイン画面にアクセスした場合のステータスとビュー名とモデルの確認`() {
        mockMvc.perform(get("/login"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
            .andExpect(model().attributeDoesNotExist("loginFailure", "logout", "sessionInvalid"))
    }

    @Test
    fun `loginfailure_ログイン失敗時のステータスとビュー名とモデルの確認`() {
        mockMvc.perform(get("/loginfailure"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
            .andExpect(model().attribute("loginFailure", true))
            .andExpect(model().attributeDoesNotExist("logout", "sessionInvalid"))
    }

    @Test
    fun `invalidsession_セッションが無効になった場合のステータスとビュー名とモデルの確認`() {
        mockMvc.perform(get("/invalidsession"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
            .andExpect(model().attribute("sessionInvalid", true))
            .andExpect(model().attributeDoesNotExist("logout", "loginFailure"))
    }

    @Test
    fun `logoutsuccess_ログアウトに成功した場合のステータスとビュー名とモデルの確認`() {
        mockMvc.perform(get("/logoutsuccess"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
            .andExpect(model().attribute("logout", true))
            .andExpect(model().attributeDoesNotExist("sessionInvalid", "loginFailure"))
    }

    @Test
    fun `admin_管理者機能にアクセスした場合のステータスとビュー名とモデルの確認`() {
        // モックを登録
        val initForm = BookManageForm()
        initForm.newBook = true
        initForm.books = listOf(testBook)
        whenever(service.initForm()).thenReturn(initForm)
        // 認証情報のモック
        val mockPrincipal: Authentication = mock {
            on { name }.thenReturn("user")
        }

        // getリクエストでbooksを指定する
        val result = mockMvc.perform(get("/admin").principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk) // HTTPステータスが200か否か
            .andExpect(view().name("admin")) // ビュー名が"books"か否か
            .andReturn()

        // モデルからformを取得し、変数を評価する
        val form = result.modelAndView!!.model["bookManageForm"] as BookManageForm
        assertNull(form.title)
        assertNull(form.author)
        assertEquals(form.newBook, true)
        assertEquals(form.version, 0)
        assertNotNull(form.books)
        assertEquals(form.books!!.size, 1)
    }

    companion object {
        /**
         * テストデータのID
         */
        private const val TEST_ID: Long = 1
        /**
         * 不正なテストデータのID
         */
        private const val INVALID_TEST_ID: Long = 2
        /**
         * テストデータのタイトル
         */
        private const val TEST_TITLE = "書籍のタイトル"
        /**
         * テストデータのタイトル(エラー)
         */
        private const val TEST_TITLE_ERROR = "書籍のタイトル(エラー)"
        /**
         * テストデータの著者名
         */
        private const val TEST_AUTHOR = "書籍の著者名"
        /**
         * テストデータの著者名(エラー)
         */
        private const val TEST_AUTHOR_ERROR = "書籍の著者名(エラー)"
        /**
         * テストデータのバージョン
         */
        private const val TEST_VERSION: Long = 2
        /**
         * テスト用のメッセージ
         */
        private const val TEST_MESSAGE = "test message"
    }
}