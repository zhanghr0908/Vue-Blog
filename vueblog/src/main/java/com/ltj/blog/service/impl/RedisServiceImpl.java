package com.ltj.blog.service.impl;


import com.ltj.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 读写Redis相关操作
 */
@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	@Qualifier("redisTemplateJackson")
	private RedisTemplate jsonRedisTemplate;

	/**
	 * 通过key向set中加入一个值（对象）
	 */
	@Override
	public void saveValueToSet(String key, Object value) {
		jsonRedisTemplate.opsForSet().add(key, value);
	}

    @Override
    public void saveValueToZSet(String key, Object member, double score ) {
        jsonRedisTemplate.opsForZSet().add(key, member, score);
    }

	@Override
	public void zUnionAndStore(String currentKey, List<String> keys, String destKey) {
		jsonRedisTemplate.opsForZSet().unionAndStore(currentKey, keys, destKey);
	}

	@Override
	public List<Map<String, Object>> getWeekHotBlogByLimit(int limit) {
		Set<ZSetOperations.TypedTuple> lastWeekRank = getValueByZSetKey("weekRank:", 0, 6);
		List<Map<String, Object>> hotBlogList = new ArrayList<>();
		int temp = 1;
		for(ZSetOperations.TypedTuple typedTuple : lastWeekRank) {
			if (temp > limit) break;
			Map<String, Object> map = new HashMap<>();
			// 防止出现空数据
			if (getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogId:") != null &&
					!getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogId:").equals("") &&
					getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogTitle:") != null &&
					!getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogTitle:").equals("")) {
				map.put("id", getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogId:"));
				map.put("title", getValueByHashKey("rankBlog:" + typedTuple.getValue(), "blogTitle:"));
				hotBlogList.add(map);
				temp++;
			}
		}
		return hotBlogList;
	}

	@Override
	public void zIncrementScore(String key, Object value, double delta) {
		jsonRedisTemplate.opsForZSet().incrementScore(key, value, delta);
	}

	@Override
	public void deleteMemberFromZSet(String key, Object member, Object score) {
		jsonRedisTemplate.opsForZSet().remove(key, member, score);
	}

	/**
	 * 通过key从set中删除一个值（对象）
	 */
	@Override
	public void deleteValueBySet(String key, Object value) {
		jsonRedisTemplate.opsForSet().remove(key, value);
	}

	/**
	 * 通过key查询set中是否有某个值（对象）
	 */
	@Override
	public boolean hasValueInSet(String key, Object value) {
		return jsonRedisTemplate.opsForSet().isMember(key, value);
	}

	/**
	 * 通过key删除一个redis中的结构（set、String、hash、list、sortedList）
	 */
	@Override
	public void deleteCacheByKey(String key) {
		jsonRedisTemplate.delete(key);
	}

	/**
	 * 查询redis中是否存在某个key
	 */
	@Override
	public boolean hasKey(String key) {
		return jsonRedisTemplate.hasKey(key);
	}

	@Override
	public void saveKVToHash(String hash, Object key, Object value) {
		jsonRedisTemplate.opsForHash().put(hash, key, value);
	}

	@Override
	public void saveMapToHash(String hash, Map map) {
		jsonRedisTemplate.opsForHash().putAll(hash, map);
	}

	@Override
	public Map getMapByHash(String hash) {
		return jsonRedisTemplate.opsForHash().entries(hash);
	}

	@Override
	public Object getValueByHashKey(String hash, Object key) {
		return jsonRedisTemplate.opsForHash().get(hash, key);
	}

	@Override
	public Set<ZSetOperations.TypedTuple> getValueByZSetKey(String key, int start, int end) {
		return jsonRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
	}

	@Override
	public boolean hasHashKey(String hash, Object key) {
		return jsonRedisTemplate.opsForHash().hasKey(hash,key);
	}

	@Override
	public void incrementByHashKey(String hash, Object key, int increment) {
		if (increment < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		jsonRedisTemplate.opsForHash().increment(hash, key, increment);
	}

	/**
	 * 通过hash和key删除一条hash记录
	 */
	@Override
	public void deleteByHashKey(String hash, Object... key) {
		jsonRedisTemplate.opsForHash().delete(hash, key);
	}

	@Override
	public int countBySet(String key) {
		return jsonRedisTemplate.opsForSet().size(key).intValue();

	}

	@Override
	public void deleteAllKeys() {
		jsonRedisTemplate.delete(jsonRedisTemplate.keys("*"));
	}

	@Override
	public void expire(Object key, int secondes) {
		jsonRedisTemplate.expire(key,secondes, TimeUnit.SECONDS);
	}

	@Override
	public void incrementByKey(String key, int increment) {
		if (increment < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		jsonRedisTemplate.opsForValue().increment(key, increment);
	}

	@Override
	public void saveObjectToValue(String key, Object object) {
		jsonRedisTemplate.opsForValue().set(key, object);
	}

	@Override
	public  Object getObjectByValue(String key) {
		Object redisResult = jsonRedisTemplate.opsForValue().get(key);
		return redisResult;
	}


}
