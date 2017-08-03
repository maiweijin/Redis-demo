package com.bonade.walletpay.spdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	class Node {

	}

	@GetMapping("/get")
	public Long get() {
		Long increment = stringRedisTemplate.opsForValue().increment("index", 1L);
		return increment;
	}
}
