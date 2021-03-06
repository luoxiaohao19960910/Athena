package com.athena.controller

import com.athena.model.copy.CopyStatus
import com.athena.model.copy.CopyVo
import com.athena.model.publication.Book
import com.athena.model.publication.Publisher
import com.athena.repository.jpa.BookRepository
import com.athena.repository.jpa.copy.BookCopyRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.lordofthejars.nosqlunit.annotation.UsingDataSet
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import util.BookGenerator
import util.IdentityGenerator

@RunWith(SpringRunner::class)
@SpringBootTest
@TestExecutionListeners(TransactionalTestExecutionListener::class, DbUnitTestExecutionListener::class, DependencyInjectionTestExecutionListener::class)
@DatabaseSetup("classpath:books.xml", "classpath:publishers.xml", "classpath:users.xml", "classpath:book_copy.xml", "classpath:copies.xml")
@WebAppConfiguration
open class BookControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext
    @Autowired
    lateinit var applicationContext: ApplicationContext
    @Autowired
    lateinit var bookRepository: BookRepository
    @Autowired
    lateinit var bookCopyRepository: BookCopyRepository

    lateinit var mvc: MockMvc
    private val mockBookGenerator: BookGenerator = BookGenerator()
    @Value("\${web.url.prefix}")
    private var url_prefix: String = ""
    private val identity = IdentityGenerator()

    @Before
    fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply<DefaultMockMvcBuilder>(springSecurity()).build()
    }

    @Test
    fun testBookSearchByPartialTitle() {

        mvc.perform(get(this.url_prefix + "/books?title=elit").with(identity.authentication()))
                .andDo(print())
                .andExpect(content().json("{\"content\":[{\"isbn\":9784099507505,\"publishDate\":\"2016-09-18\",\"categoryId\":\"TC331A\",\"version\":4,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"adipiscing elit\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":520.5,\"publisher\":{\"id\":\"922\",\"name\":\"Test Publisher\",\"location\":\"NewYork\"},\"author\":[\"Steffen Catcherside\"],\"translator\":[]}],\"totalElements\":1,\"last\":true,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":null,\"first\":true,\"numberOfElements\":1}"))

    }

    @Test
    fun testBookSearchByFullTitle() {
        mvc.perform(get(this.url_prefix + "/books?title=consequat in consequat").with(identity.authentication()))
                .andDo(print())
                .andExpect(content().json("{\"content\":[{\"isbn\":9785867649253,\"publishDate\":\"2016-07-17\",\"categoryId\":\"TC331C\",\"version\":5,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"consequat in consequat\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":85.25,\"publisher\":{\"id\":\"817\",\"name\":\"TestDn Publisher\",\"location\":\"NewYork\"},\"author\":[\"Lian Hubback\"],\"translator\":[]}],\"totalElements\":1,\"last\":true,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":null,\"first\":true,\"numberOfElements\":1}"))
                .andExpect(header().string("X-Total-Count", "1")).andExpect(header().string("Links", "<http://localhost/api/v1/books?page=0&title=consequat in consequat>; rel=\"last\",<http://localhost/api/v1/books?page=0&title=consequat in consequat>; rel=\"first\""))
    }


    @Test
    fun testBookSearchByAuthor() {
        mvc.perform(get(this.url_prefix + "/books?author=Lian Hubback")
                .with(identity.authentication()))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("{\"content\":[{\"isbn\":9785867649253,\"publishDate\":\"2016-07-17\",\"categoryId\":\"TC331C\",\"version\":5,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"consequat in consequat\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":85.25,\"publisher\":{\"id\":\"817\",\"name\":\"TestDn Publisher\",\"location\":\"NewYork\"},\"author\":[\"Lian Hubback\"],\"translator\":[]}],\"totalElements\":1,\"last\":true,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":null,\"first\":true,\"numberOfElements\":1}"))
    }

    @Test
    fun testBookSearchByAuthors() {
        mvc.perform(get(this.url_prefix + "/books?author=Aneig dlsa&author=Rdlf dls")
                .with(identity.authentication()))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("{\"content\":[{\"isbn\":9783158101896,\"publishDate\":\"2016-11-13\",\"categoryId\":\"TC331C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"测试作者书籍\",\"titlePinyin\":\"ce,shi,zuo,zhe,shu,ji\",\"titleShortPinyin\":\"cszzsj\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":57.22,\"publisher\":{\"id\":\"127\",\"name\":\"TestDll Publisher\",\"location\":\"NewYork\"},\"author\":[\"Aneig dlsa\",\"Bianfd sld\",\"Rdlf dls\"],\"translator\":[]},{\"isbn\":9783158101897,\"publishDate\":\"2016-11-13\",\"categoryId\":\"TC331C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"测试多个作者书籍\",\"titlePinyin\":\"ce,shi,duo,ge,zuo,zhe,shu,ji\",\"titleShortPinyin\":\"csdgzzsj\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":57.22,\"publisher\":{\"id\":\"127\",\"name\":\"TestDll Publisher\",\"location\":\"NewYork\"},\"author\":[\"Aneig dlsa\",\"Rdlf dls\",\"Zlicn Tlidb\"],\"translator\":[]}],\"totalElements\":2,\"last\":true,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":null,\"first\":true,\"numberOfElements\":2}"))
    }

    @Test
    fun testRateLimit() {
        val processor: RequestPostProcessor = RequestPostProcessor { request ->
            request.remoteAddr = "192.168.1.1"
            request
        }
        for (i in 1..5) {
            // Note that the search.limit.get.times in config.properties must be 3
            if (i < 4) {
                mvc.perform(get(this.url_prefix + "/books?author=Aneig dlsa,Rdlf dls").with(processor)).andExpect(status().isOk)
                System.out.println(i)
            } else {
                mvc.perform(get(this.url_prefix + "/books?author=Aneig dlsa,Rdlf dls").with(processor)).andExpect(status().`is`(429))
            }
        }
    }

    @Test
    @UsingDataSet(locations = arrayOf("/batch.json"), loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    fun testCreateBook() {
        /**
         * Setup
         *
         * */
        var books: ArrayList<Book> = arrayListOf()
        var publisher = Publisher()
        publisher.id = "999"
        for (i in 1..4) {
            val book = this.mockBookGenerator.generateBook()
            book.publisher = publisher
            books.add(book)
        }

        /**
         * Test privilege
         *
         * only admin should be allowed to create book
         */
        mvc.perform(post(this.url_prefix + "/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(books))
                .with(identity.authentication("ROLE_ADMIN"))
        )
                .andExpect(status().isCreated)


        mvc.perform(post(this.url_prefix + "/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(books))
                .with(identity.authentication("ROLE_READER"))
        )
                .andExpect(status().isForbidden)


        /**
         * Test whether the books has been saved
         *
         */
        for (book in books) {
            Assert.assertNotNull(this.bookRepository.findOne(book.isbn))
        }


        /**
         * Test Exception
         *
         * publisher doesn't exist in db
         * */
        books = arrayListOf()
        var errorBook = this.mockBookGenerator.generateBook()
        publisher = Publisher()
        publisher.id = "test" //publisher that doesn't exist
        errorBook.publisher = publisher
        books.add(errorBook)
        mvc.perform(post(this.url_prefix + "/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(books))
                .with(identity.authentication("ROLE_ADMIN"))
        )
                .andExpect(status().isBadRequest)


        /**
         * Test Transaction
         *
         * 1. When exception happens in insert books (Exception in MySQL)
         * 2. When exception happens in insert batch (Exception in MongoDB)
         */
        books = arrayListOf()
        books.add(errorBook)
        var book = this.mockBookGenerator.generateBook()
        publisher = Publisher()
        publisher.id = "922"
        book.publisher = publisher
        books.add(book)

        var result = mvc.perform(post(this.url_prefix + "/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(books))
                .with(identity.authentication("ROLE_ADMIN"))
        )
                .andReturn()

        if (result.response.status != 201) {
            //Exception happens
            Assert.assertNull(this.bookRepository.findOne(book.isbn))
        }


    }


    @Test
    fun testCreateCopy() {
        var copyVo: CopyVo = CopyVo()
        copyVo.status = CopyStatus.BOOKED
        var copyVoList: List<CopyVo> = arrayListOf(copyVo)


        this.mvc.perform(post(this.url_prefix + "/books/9785226422377/copy")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(copyVoList))
                .with(identity.authentication("ROLE_ADMIN"))
        )
                .andDo(print())
                .andExpect(status().isCreated)
    }

    @Test
    fun testGetCopy() {
        // get copy
        this.mvc.perform(get(this.url_prefix + "/books/9787111125643/copy/1")
                .with(identity.authentication("ROLE_READER"))
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("{\"status\":0,\"id\":1,\"createdDate\":null,\"updatedDate\":null,\"borrows\":[],\"book\":{\"isbn\":9787111125643,\"publishDate\":\"2017-01-06\",\"categoryId\":\"TP312C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"C++程序设计指南\",\"titlePinyin\":\"C++,chen,xv,she,ji,zhi,nan\",\"titleShortPinyin\":\"ccxsjzn\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":55.25,\"publisher\":{\"id\":\"999\",\"name\":\"测试出版社\",\"location\":\"BeiJing\"},\"author\":[\"测试作者\"],\"translator\":[]}}"))


        //getByPublications copies
        this.mvc.perform(get(this.url_prefix + "/books/9787111125643/copy")
                .with(identity.authentication("ROLE_READER"))
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json(" [{\"status\":0,\"id\":1,\"createdDate\":null,\"updatedDate\":null,\"borrows\":[],\"book\":{\"isbn\":9787111125643,\"author\":[\"测试作者\"],\"translator\":[],\"publishDate\":\"2017-01-06\",\"categoryId\":\"TP312C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"C++程序设计指南\",\"titlePinyin\":\"C++,chen,xv,she,ji,zhi,nan\",\"titleShortPinyin\":\"ccxsjzn\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":55.25,\"publisher\":{\"id\":\"999\",\"name\":\"测试出版社\",\"location\":\"BeiJing\"}}},{\"status\":0,\"id\":2,\"createdDate\":null,\"updatedDate\":null,\"borrows\":[],\"book\":{\"isbn\":9787111125643,\"author\":[\"测试作者\"],\"translator\":[],\"publishDate\":\"2017-01-06\",\"categoryId\":\"TP312C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"C++程序设计指南\",\"titlePinyin\":\"C++,chen,xv,she,ji,zhi,nan\",\"titleShortPinyin\":\"ccxsjzn\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":55.25,\"publisher\":{\"id\":\"999\",\"name\":\"测试出版社\",\"location\":\"BeiJing\"}}}]", false))
    }


    @Test
    fun testDeleteCopies() {
        this.mvc.perform(delete(this.url_prefix + "/books/9787111125643/copy")
                .with(identity.authentication("ROLE_ADMIN"))
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful)

        Assert.assertEquals(0, this.bookCopyRepository.findByBook(this.bookRepository.findOne(9787111125643L)).size)
    }

    @Test
    fun testGetRequestParamThroughPublicationSearchParam() {
        this.mvc.perform(get(this.url_prefix + "/books?title=adipiscing elit&title=ut erat id&language=English"))
                .andDo(print())
                .andExpect(content().json("{\"content\":[{\"isbn\":9784099507505,\"author\":[\"Steffen Catcherside\"],\"translator\":[],\"publishDate\":\"2016-09-18\",\"categoryId\":\"TC331A\",\"version\":4,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"adipiscing elit\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":520.5,\"publisher\":{\"id\":\"922\",\"name\":\"Test Publisher\",\"location\":\"NewYork\"}},{\"isbn\":9785226422377,\"author\":[\"Georgie Northway\"],\"translator\":[],\"publishDate\":\"2017-04-16\",\"categoryId\":\"TC331C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"ut erat id\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":1204.65,\"publisher\":{\"id\":\"806\",\"name\":\"TestR Publisher\",\"location\":\"NewYork\"}}],\"last\":true,\"totalPages\":1,\"totalElements\":2,\"first\":true,\"numberOfElements\":2,\"size\":20,\"number\":0,\"sort\":null}", false))
        this.mvc.perform(get(this.url_prefix + "/books?title=ut erat id&language=English"))
                .andDo(print())
                .andExpect(content().json("{\"content\":[{\"isbn\":9785226422377,\"author\":[\"Georgie Northway\"],\"translator\":[],\"publishDate\":\"2017-04-16\",\"categoryId\":\"TC331C\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"ut erat id\",\"titlePinyin\":null,\"titleShortPinyin\":null,\"subtitle\":null,\"language\":\"English\",\"price\":1204.65,\"publisher\":{\"id\":\"806\",\"name\":\"TestR Publisher\",\"location\":\"NewYork\"}}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"first\":true,\"numberOfElements\":1,\"size\":20,\"number\":0,\"sort\":null}", false))
    }

    @Test
    fun testGetBooks() {
        this.mvc.perform(get(this.url_prefix + "/books/9783158101901"))
                .andExpect(status().isOk)
                .andExpect(content().json("{\"isbn\":9783158101901,\"author\":[\"Tdicko\"],\"translator\":[],\"publishDate\":\"2017-11-13\",\"categoryId\":\"TC331B\",\"version\":1,\"coverUrl\":null,\"preface\":null,\"introduction\":null,\"directory\":null,\"title\":\"第三部测试书\",\"titlePinyin\":\"di,san,bu,ce,shi,shu\",\"titleShortPinyin\":\"dsbcss\",\"subtitle\":null,\"language\":\"Chinese\",\"price\":152.67,\"publisher\":{\"id\":\"127\",\"name\":\"TestDll Publisher\",\"location\":\"NewYork\"}}", false))
    }
}

