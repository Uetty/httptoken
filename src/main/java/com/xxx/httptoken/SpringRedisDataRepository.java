package com.xxx.httptoken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


public class SpringRedisDataRepository implements DataRepository, InitializingBean {

	RedisTemplate<String, Object> template;
	
	StringRedisSerializer stringSerializer;
	RedisSerializer<Object> hashValueSerializer;

	String projectPrefix = "";
	String tokenPrefix = "httptoken:values:";
	String projectFlag = "project";

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		template.setKeySerializer(template.getStringSerializer());
		template.setHashKeySerializer(template.getStringSerializer());
		template.setHashValueSerializer(template.getDefaultSerializer());
		if (hashValueSerializer == null) {
			hashValueSerializer = (RedisSerializer<Object>) template.getDefaultSerializer();
		}
		if(stringSerializer == null){
			stringSerializer = (StringRedisSerializer) template.getStringSerializer();
		}
	}

	@Override
	public void setAttribute(String tokenId, String name, Object obj, long expire) {
		template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + tokenId);
				byte[] field = stringSerializer.serialize(name);
				byte[] value = hashValueSerializer.serialize(obj);
				conn.hSet(key, field, value);
				conn.expire(key, expire);
				return null;
			}
		});
	}

	@Override
	public Object getAttribute(String tokenId, String name) {
		HashOperations<String, String, Object> opsForHash = template.opsForHash();
		Object object = opsForHash.get(projectPrefix + tokenPrefix + tokenId, name);
		opsForHash = null;
		return object;
	}

	@Override
	public void setAttribute(String tokenId, Map<String, Object> map, long expire) {
		if (map == null || map.size() <= 0) {
			return;
		}
		template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + tokenId);
				Set<Entry<String, Object>> entrySet = map.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();

				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					byte[] hkey = stringSerializer.serialize(entry.getKey());
					byte[] value = hashValueSerializer.serialize(entry.getValue());
					conn.hSet(key, hkey, value);
				}
				conn.expire(key, expire);
				entrySet = null;
				return null;
			}
		});
	}

	@Override
	public void removeAttribute(String tokenId, String... attrs) {
		if (attrs.length <= 0) {
			return;
		}
		Object[] oattrs = attrs;
		HashOperations<String, String, Object> opsForHash = template.opsForHash();
		opsForHash.delete(projectPrefix + tokenPrefix + tokenId, oattrs);
		opsForHash = null;
		oattrs = null;
	}
	
	@Override
	public void removeToken(String tokenId){
		template.delete(projectPrefix + tokenPrefix + tokenId);
	}

	@Override
	public void refreshToken(String tokenId, long expire) throws TokenException {
		Long expire2 = template.getExpire(projectPrefix + tokenPrefix + tokenId);
		if(expire2 <= 0){
			throw new TokenException("token data not found in repository");
		}
		template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + tokenId);
				conn.expire(key, expire);
				
				return null;
			}
		});
	}

	@Override
	public Set<String> getAtrributeNames(String tokenId) {
		HashOperations<String, String, Object> opsForHash = template.opsForHash();
		Set<String> keys = opsForHash.keys(projectPrefix + tokenPrefix + tokenId);
		opsForHash = null;
		return keys;
	}
	
	@Override
	public Long getExpire(String tokenId) throws TokenException {
		Long expire2 = template.getExpire(projectPrefix + tokenPrefix + tokenId);
		if(expire2 <= 0){
			throw new TokenException("token data not found in repository");
		}
		return expire2;
	}
	
	@Override
	public void setProjectAttribute(String name, Object obj) {
		template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + projectFlag);
				byte[] field = stringSerializer.serialize(name);
				byte[] value = hashValueSerializer.serialize(obj);
				conn.hSet(key, field, value);
				return null;
			}
		});
	}

	@Override
	public Object getProjectAttribute(String name) {
		HashOperations<String, String, Object> opsForHash = template.opsForHash();
		Object object = opsForHash.get(projectPrefix + tokenPrefix + projectFlag, name);
		opsForHash = null;
		return object;
	}

	@Override
	public void removeProjectAttribute(String... attrs) {
		if (attrs.length <= 0) {
			return;
		}
		Object[] oattrs = attrs;
		HashOperations<String, String, Object> opsForHash = template.opsForHash();
		opsForHash.delete(projectPrefix + tokenPrefix + projectFlag, oattrs);
		opsForHash = null;
		oattrs = null;
	}

	@Override
	public void setProjectAttribute(Map<String, Object> map) {
		if (map == null || map.size() <= 0) {
			return;
		}
		template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + projectFlag);
				Set<Entry<String, Object>> entrySet = map.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();

				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					byte[] hkey = stringSerializer.serialize(entry.getKey());
					byte[] value = hashValueSerializer.serialize(entry.getValue());
					conn.hSet(key, hkey, value);
				}
				entrySet = null;
				return null;
			}
		});
	}

	@Override
	public List<Object> getMultiAttributes(String tokenId, List<String> tokenAttrs, List<String> projectAttrs) {
		if((tokenAttrs == null || tokenAttrs.size() == 0) && (projectAttrs == null || projectAttrs.size() == 0)){
			return new ArrayList<Object>();
		}
		
		List<Object> executePipelined = template.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				if(tokenAttrs != null && tokenAttrs.size() > 0){
					byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + tokenId);
					for (int i = 0; i < tokenAttrs.size(); i++) {
						byte[] hkey = stringSerializer.serialize(tokenAttrs.get(i));
						conn.hGet(key, hkey);
					}
				}
				if(projectAttrs != null && projectAttrs.size() > 0){
					byte[] key = stringSerializer.serialize(projectPrefix + tokenPrefix + projectFlag);
					for (int i = 0; i < projectAttrs.size(); i++) {
						byte[] hkey = stringSerializer.serialize(projectAttrs.get(i));
						conn.hGet(key, hkey);
					}
				}
				return null;
			}
		});
		return executePipelined;
	}

	public String getTokenPrefix() {
		return tokenPrefix;
	}

	public void setTokenPrefix(String tokenPrefix) {
		this.tokenPrefix = tokenPrefix;
	}

	public String getProjectPrefix() {
		return projectPrefix;
	}

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}

	public RedisTemplate<String, Object> getTemplate() {
		return template;
	}

	public void setTemplate(RedisTemplate<String, Object> template) {
		this.template = template;
	}

}
