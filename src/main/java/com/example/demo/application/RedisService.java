package com.example.demo.application;

public interface RedisService {

	void set(String key, String value);

	void set(String key, String value, long timeout);

	String get(String key);

	Boolean delete(String key);

	/**
	 * generate Redis key
	 * @param objectType
	 * @param id
	 * @param property
	 * @return
	 * @see https://redis.io/topics/data-types-intro
	 */
	String generateTokenKey(String objectType, String id, String property);

}