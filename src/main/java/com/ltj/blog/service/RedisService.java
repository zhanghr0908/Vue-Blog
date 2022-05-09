package com.ltj.blog.service;

import java.util.List;
import java.util.Map;

/**
 * redis业务层
 */
public interface RedisService {

	/**
	 * 通过key从set中加入一个值（对象）
	 */
	void saveValueToSet(String key, Object value);

	void saveValueToZSet(String key, Object member, double score);

	void zUnionAndStore(String currentKey, List<String> keys, String destKey);

	List<Map<String, Object>> getWeekHotBlogByLimit(int limit);

	void zIncrementScore(String key, Object value, double delta);

	/**
	 * 通过key从set中删除一个值（对象）
	 */
	void deleteValueBySet(String key, Object value);

	/**
	 * 通过key查询set中是否有某个值（对象）
	 */
	boolean hasValueInSet(String key, Object value);

	/**
	 * 通过key删除一个redis中的结构（set、String、hash、list、sortedList）
	 */
	void deleteCacheByKey(String key);

	/**
	 * 查询redis中是否存在某个key
	 */
	boolean hasKey(String key);

	boolean hasHashKey(String key, Object value);

	void saveKVToHash(String hash, Object key, Object value);

	void saveMapToHash(String hash, Map map);

	Map getMapByHash(String hash);

	Object getValueByHashKey(String hash, Object key);

	Object getValueByZSetKey(String key, int start, int end);

	void incrementByHashKey(String hash, Object key, int increment);

	/**
	 * 通过hash和key删除一条hash记录
	 */
	void deleteByHashKey(String hash, Object... key);

	int countBySet(String key);

	void deleteAllKeys();

	void incrementByKey(String key, int increment);

	void saveObjectToValue(String key, Object object);

	Object getObjectByValue(String key);

    void expire(Object redisKey, int seconds);
}
