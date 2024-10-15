package com.example.bookrent2.websocket.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// Redis TEST

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 데이터를 redis 에 저장
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // redis 에서 데이터를 가져옴
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

}
