package com.example.bookrent2.websocket.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Redis TEST

@RestController
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }


    // 데이터를 저장
    @PostMapping("/redis/save")
    public String save(@RequestParam("key") String key, @RequestParam("value") String value) {
        redisService.save(key, value);
        return "Saved key: " + key + " with value: " + value;
    }


    // 데이터 조회
    @GetMapping("/redis/get")
    public String get(@RequestParam("key") String key) {
        String value = redisService.get(key);
        return "Retrieved key: " + key + " with value: " + value;
    }

}
