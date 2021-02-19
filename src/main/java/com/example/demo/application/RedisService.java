package com.example.demo.application;

public interface RedisService {

	/**
	 * set key with default timeout
	 * @param key
	 * @param value
	 */
	void set(String key, String value);

	/**
	 * set key and value with timeout
	 * @param key
	 * @param value
	 * @param timeout (hours)
	 */
	void set(String key, String value, long timeout);

	/**
	 * get value with key
	 * @param key
	 * @return
	 */
	String get(String key);

	/**
	 * delete key and value
	 * @param key
	 * @return
	 */
	Boolean delete(String key);

	/**
	 * generate key value store key
	 * @param objectType
	 * @param id
	 * @param property
	 * @return
	 * @see https://redis.io/topics/data-types-intro
	 */
	String generateKey(String objectType, String id, String property);

}