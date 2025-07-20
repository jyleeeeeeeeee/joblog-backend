package com.joblog;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedisMockConfig {

        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);

            ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);

            return redisTemplate;
        }
    }