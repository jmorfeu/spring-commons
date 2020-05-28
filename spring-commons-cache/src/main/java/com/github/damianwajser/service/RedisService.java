package com.prismamp.todopago.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class RedisService<K, V> {
	public abstract RedisTemplate<K, V> getRedisTemplate();

	//Ejecuta varias operaciones de redis en forma atomica
	public List<Object> executeMultiOperations(Consumer<RedisOperations<K, V>> consumer) {
		return getRedisTemplate().execute(
				new SessionCallback<List<Object>>() {
					@Override
					public List<Object> execute(RedisOperations operations) throws DataAccessException {
						operations.multi();
						consumer.accept(operations);
						return operations.exec();
					}
				}
		);
	}

	//Retorna true si habia alguna key para eliminar o false en el otro caso
	public Boolean delete(K key) {
		List<Object> results =
				this.executeMultiOperations(
						operations -> {
							operations.hasKey(key);
							operations.delete(key);
						}
				);
		return (Boolean) results.get(0);
	}

	//Retorna true si habia alguna key para eliminar o false en el otro caso
	public Boolean deleteFromHash(K key, Object... hKey) {
		List<Object> results =
				this.executeMultiOperations(
						operations -> {
							operations.opsForHash().hasKey(key, hKey);
							operations.opsForHash().delete(key, hKey);
						}
				);
		return (Boolean) results.get(0);
	}
}
