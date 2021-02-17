package com.example.demo.interfaces.redis.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.RedisService;

@RestController
public class RedisController {

	private Logger logger = LoggerFactory.getLogger(RedisController.class);

	@Autowired
	private RedisService redisService;
	
	@GetMapping(value = "/refresh-tokens/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String read(@PathVariable String userId, 
			HttpServletResponse response) throws DemoException {
		
		logger.info("userId={}", userId);

		String key = getKey(userId);
		
		String value = redisService.get(key);
		if (value == null) {
			throw new DemoException(HttpStatus.NOT_FOUND, "The key does not exist.");
		}

		logger.info("key={}, value={}", key, value);

		JSONObject data = new JSONObject();
		data.put("user_id", userId);
		data.put("key", key);
		data.put("token", value);

		logger.info("data={}", data);

		return data.toString();
	}

	@PostMapping(value = "/refresh-tokens/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String save(@PathVariable String userId, @RequestBody String body) throws DemoException {
		String key = getKey(userId);

		logger.info("body={}", body);
		JSONObject jsonObject = new JSONObject(body);
		
		if (!jsonObject.has("token")) {
			throw new DemoException(HttpStatus.BAD_REQUEST, "The required parameter 'token' does not exist.");
		}

		String value = jsonObject.getString("token");
		redisService.set(key, value);
		
		JSONObject data = new JSONObject();
		data.put("user_id", userId);
		data.put("key", key);
		data.put("token", value);
		
		logger.info("data={}", data);

		return data.toString();
	}

	@DeleteMapping(value = "/refresh-tokens/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String delete(@PathVariable String userId, HttpServletResponse response) throws DemoException {
		String key = getKey(userId);

		if (Boolean.FALSE.equals(redisService.delete(key))) {
			throw new DemoException(HttpStatus.NOT_FOUND, "The key does not exist.");
		}

		JSONObject data = new JSONObject();
		data.put("user_id", userId);
		data.put("key", key);

		logger.info("data={}", data);

		return data.toString();
	}
	
	@ExceptionHandler(value = DemoException.class) 
	private void handleDemoException(HttpServletResponse response, DemoException exception) throws IOException {
		logger.error("RedisController. exception.message={}", exception.getLocalizedMessage(), exception);
		response.sendError(exception.getHttpStatus().value(), exception.getMessage());
	}

	// This method is for testing
	private String getKey(String userId) {
		return redisService.generateTokenKey("user", userId, "refresh-token");
//		return redisService.generateTokenKey("user", userId, "access-token");
	}
}
