package com.example.demo.interfaces.redis.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.RedisService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@RestController
public class RedisController {

	private final Logger logger = LoggerFactory.getLogger(RedisController.class);

	@Autowired
	private RedisService redisService;
	
	@GetMapping(value = "/refresh-tokens/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntityModel<ResultModel> read(@PathVariable String id) throws DemoException {
		
		logger.info("userId={}", id);

		String key = getKey(id);
		
		String value = redisService.get(key);
		if (value == null) {
			throw new DemoException(HttpStatus.NOT_FOUND, "The key does not exist.");
		}

		logger.info("key={}, value={}", key, value);

		ResultModel resultModel = new ResultModel(id, key, value);
		
		logger.info("resultModel={}", resultModel);

		return EntityModel.of(resultModel, 
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).read(id)).withSelfRel(),
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).update(null)).withRel("update"),
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).delete(id)).withRel("delete"));
	}

	@PostMapping(value = "/refresh-tokens", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntityModel<ResultModel> create(@RequestBody String body, 
			HttpServletResponse response) throws DemoException {
		logger.info("body={}", body);
		JSONObject jsonObject = new JSONObject(body);

		if (!jsonObject.has("id")) {
			throw new DemoException(HttpStatus.BAD_REQUEST, "The required parameter 'id' does not exist.");
		}

		if (!jsonObject.has("token")) {
			throw new DemoException(HttpStatus.BAD_REQUEST, "The required parameter 'token' does not exist.");
		}

		String id = jsonObject.getString("id");
		String value = jsonObject.getString("token");

		String key = getKey(id);

		String currentValue = redisService.get(key);
		if (currentValue != null) {
			throw new DemoException(HttpStatus.CONFLICT, "The key aleady exists.");
		}
		
		redisService.set(key, value);
		
		ResultModel resultModel = new ResultModel(id, key, value);
		
		logger.info("resultModel={}", resultModel);

		response.setStatus(HttpStatus.CREATED.value());
		
		return EntityModel.of(resultModel, 
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).create(body, response)).withSelfRel(),
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).read(id)).withRel("read"));
	}

	@PutMapping(value = "/refresh-tokens", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntityModel<ResultModel> update(@RequestBody String body) throws DemoException {
		logger.info("body={}", body);
		JSONObject jsonObject = new JSONObject(body);

		if (!jsonObject.has("id")) {
			throw new DemoException(HttpStatus.BAD_REQUEST, "The required parameter 'id' does not exist.");
		}

		if (!jsonObject.has("token")) {
			throw new DemoException(HttpStatus.BAD_REQUEST, "The required parameter 'token' does not exist.");
		}

		String userId = jsonObject.getString("id");
		String value = jsonObject.getString("token");

		String key = getKey(userId);

		String currentValue = redisService.get(key);
		if (currentValue == null) {
			throw new DemoException(HttpStatus.NOT_FOUND, "The key does not exist.");
		}
		
		redisService.set(key, value);
		
		ResultModel resultModel = new ResultModel(userId, key, value);
		
		logger.info("resultModel={}", resultModel);

		return EntityModel.of(resultModel, 
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).update(body)).withSelfRel(),
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).read(userId)).withRel("read"));
	}

	@DeleteMapping(value = "/refresh-tokens/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EntityModel<ResultModel> delete(@PathVariable String id) throws DemoException {
		String key = getKey(id);

		if (Boolean.FALSE.equals(redisService.delete(key))) {
			throw new DemoException(HttpStatus.NOT_FOUND, "The key does not exist.");
		}

		ResultModel resultModel = new ResultModel(id, key, null);

		logger.info("resultModel={}", resultModel);

		return EntityModel.of(resultModel, 
				WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RedisController.class).delete(id)).withSelfRel());
	}

	@ExceptionHandler(value = DemoException.class) 
	private void handleDemoException(HttpServletResponse response, DemoException exception) throws IOException {
		logger.error("RedisController. exception.message={}", exception.getLocalizedMessage(), exception);
		response.sendError(exception.getHttpStatus().value(), exception.getMessage());
	}

	// This method is for testing
	private String getKey(String id) {
		return redisService.generateKey("user", id, "refresh-token");
//		return redisService.generateTokenKey("user", id, "access-token");
	}
	
	@JsonInclude(value = Include.NON_NULL)
	private class ResultModel extends RepresentationModel<ResultModel> {
		private String id;
		private String key;
		private String value;
		
		ResultModel (String id, String key, String value) {
			this.id = id;
			this.key = key;
			this.value = value;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
