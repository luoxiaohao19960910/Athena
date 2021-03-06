package com.athena.controller

import com.athena.repository.jpa.JournalRepository
import com.athena.service.publication.JournalService
import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Created by Tommy on 2018/3/21.
 *
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@TestExecutionListeners(TransactionalTestExecutionListener::class, DbUnitTestExecutionListener::class, DependencyInjectionTestExecutionListener::class)
@DatabaseSetup("classpath:journals.xml", "classpath:publishers.xml", "classpath:users.xml")
@WebAppConfiguration
class JournalControllerTest {
    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var applicationContext: ApplicationContext
    @Autowired lateinit var journalService: JournalService
    @Autowired lateinit var journalRepository: JournalRepository

    lateinit var mvc: MockMvc

    @Value("\${web.url.prefix}") private var url_prefix: String = ""
    private val identity = util.IdentityGenerator()

    @Before
    fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity()).build()
    }


    @Test
    fun testSearch() {
        this.mvc.perform(get(url_prefix + "/journals?issn=03718474&year=2016").with(identity.authentication()))
                .andDo(print())
                .andExpect(content().json("{\"content\":[{\"issn\":\"03718474\",\"year\":2016,\"issue\":23,\"title\":\"Python Handbook\",\"titleShortPinyin\":null,\"titlePinyin\":null,\"price\":11.5,\"coverUrl\":null,\"directory\":null,\"publishDate\":null,\"language\":\"English\",\"publisher\":{\"id\":\"922\",\"name\":\"Test Publisher\",\"location\":\"NewYork\"}},{\"issn\":\"03718474\",\"year\":2016,\"issue\":24,\"title\":\"Python Handbook\",\"titleShortPinyin\":null,\"titlePinyin\":null,\"price\":11.5,\"coverUrl\":null,\"directory\":null,\"publishDate\":null,\"language\":\"English\",\"publisher\":{\"id\":\"922\",\"name\":\"Test Publisher\",\"location\":\"NewYork\"}},{\"issn\":\"03718474\",\"year\":2016,\"issue\":25,\"title\":\"Python Handbook\",\"titleShortPinyin\":null,\"titlePinyin\":null,\"price\":11.5,\"coverUrl\":null,\"directory\":null,\"publishDate\":null,\"language\":\"English\",\"publisher\":{\"id\":\"922\",\"name\":\"Test Publisher\",\"location\":\"NewYork\"}}],\"totalElements\":3,\"last\":true,\"totalPages\":1,\"number\":0,\"size\":20,\"sort\":null,\"first\":true,\"numberOfElements\":3}", false))
    }
}