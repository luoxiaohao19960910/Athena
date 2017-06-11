package com.athena.service

import com.athena.model.Book
import com.athena.repository.BookRepository
import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableArgumentResolver
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import javax.transaction.Transactional

/**
 * Created by tommy on 2017/4/26.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class, DbUnitTestExecutionListener::class, TransactionalTestExecutionListener::class)
@DatabaseSetup("classpath:books.xml", "classpath:publishers.xml")
open class BookServiceTest {
    @Autowired private val service: BookService? = null

    @Autowired private val repository: BookRepository? = null

    @Before fun setup() {

    }

    @Test fun testSearchByName() {
        val keywords = arrayOf("埃里克森", "程序设计")
        val pageable = PageRequest(0, 20)
        val result = service!!.searchBookByName(pageable, keywords)
        val expects = arrayOf(repository!!.findOne(9783158101891L), repository.findOne(9787111124444L), repository.findOne(9787111125643L))
        Assert.assertEquals(3, result.totalElements) // The total result should be 3
        for (expect in expects) {
            Assert.assertTrue(result.content.contains(expect))
        }
    }

    @Test fun testSearchByFullName() {
        val keyword = "C程序设计"
        val pageable = PageRequest(0, 20)
        val result = service!!.searchBookByFullName(pageable, keyword)
        val expects = repository!!.findOne(9787111124444L)
        Assert.assertEquals(1, result.totalElements)
        Assert.assertEquals(expects, result.content[0])
    }

    @Test fun testSearchByAuthors() {
        var authors = arrayOf("Dneig dlsa", "Rdlf dls")
        val pageable = PageRequest(0, 20)
        var result = service!!.searchBookByAuthors(pageable, authors)
        var expects = HashSet<Book>()
        expects.add(repository!!.findOne(9783158101896L))
        expects.add(repository.findOne(9783158101897L))
        Assert.assertEquals(expects, HashSet<Book>(result.content))

        authors = arrayOf("Rdlf dls", "Dneig dlsa", "Dlicn Tlidb")
        result = service.searchBookByAuthors(pageable, authors)
        expects = HashSet<Book>()
        expects.add(repository.findOne(9783158101897L))
        Assert.assertEquals(expects, HashSet<Book>(result.content))

    }

    @Test fun testSearchByFullAuthors() {
        var authors = arrayOf("Atester","Btester")
        var pageable = PageRequest(0, 20)
        val result = service!!.searchBookByFullAuthors(pageable, authors)
        val expects = ArrayList<Book>()
        expects.add(repository!!.findOne(9783158101899L))
        Assert.assertEquals(expects, result.content)
    }

}
