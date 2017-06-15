package com.athena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tommy on 2017/6/12.
 */
@Service
public class RateLimitService {
    private final String prefix;
    private int expiredTime;
    private int timeRemain;
    private Calendar calendar;
    private RedisTemplate<String, String> redisTemplate;

    public RateLimitService(@Value("${search.limit.expiredtime}") int expiredTime, @Autowired RedisTemplate<String, String> redisTemplate, @Value("${search.limit.prefix}") String prefix) {
        this.expiredTime = expiredTime;
        this.timeRemain = expiredTime;
        this.calendar = Calendar.getInstance();
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    @Scheduled(cron = "0/2 * * * *")
    public void updateExpiredTimeForCache() {
        long currentSecond = calendar.getTimeInMillis() / 1000;
        long mod = currentSecond % expiredTime;
        this.timeRemain = (int) (expiredTime - mod);
    }

    public int increaseLimit(String userInfo) {
        redisTemplate.opsForValue().increment(this.prefix + userInfo, 1);
        redisTemplate.expire(this.prefix + userInfo, this.timeRemain, TimeUnit.SECONDS);
        return Integer.parseInt(redisTemplate.opsForValue().get(this.prefix + userInfo));
    }

}
