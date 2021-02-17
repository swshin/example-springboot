package com.example.demo.infrastructure;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.demo.application.RedisService;

@Service
public class RedisServiceImpl implements RedisService {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public void set(String key, String value) {
		int timeout = 24;
		set(key, value, timeout);
	}

	@Override
	public void set(String key, String value, long timeout) {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		valueOps.set(key, value, timeout, TimeUnit.HOURS);
	}

	@Override
	public String get(String key) {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		return valueOps.get(key);
	}

	@Override
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}

	@Override
	public String generateTokenKey(String object, String id, String property) {
		return String.format("%s:%s:%s", object, id, property);
	}
}
