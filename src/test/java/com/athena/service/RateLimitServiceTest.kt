package com.athena.service

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

/**
 * Created by tommy on 2017/6/12.
 *
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class RateLimitServiceTest {
    @Autowired
    private var rateLimitService: RateLimitService? = null

    @Autowired
    private var template: RedisTemplate<String, String>? = null

    @Test
    fun testSchedule() {
        for (i in 1..3) {
            rateLimitService!!.increaseLimit("test")
        }
        Assert.assertEquals(4, rateLimitService!!.increaseLimit("test"))
        Assert.assertTrue(template!!.hasKey("test"))

        //Wait for 1 minute
        Thread.sleep(1000 * 60)
        Assert.assertFalse(template!!.hasKey("test"))

    }
}
