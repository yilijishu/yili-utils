package com.yilijishu.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.yilijishu.utils.jackson.ObjectMapperConfUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisDataManager {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lua脚本：释放分布式锁
     * 逻辑：key存在，并且value和传入的值相等才删除，返回1成功；否则返回0
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    public RedisDataManager(RedisConnectionFactory factory) {
        this.redisTemplate = createRedisTemplate(factory);
    }
    /**
     * 内部本地构建RedisTemplate，序列化规则和你最初代码完全一致
     */
    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        ObjectMapperConfUtils.config(objectMapper);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    // ====================== String ======================

    /**
     * 存入redis
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

    /**
     * 存入redis
     * @param key 键
     * @param value 值
     * @param expireTime 过期时间
     * @param unit 单位
     */
    public void set(String key, Object value, long expireTime, TimeUnit unit) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, expireTime, unit);
    }

    /**
     * 根据key获取数据
     * @param key 键
     * @param clazz 类型
     * @param <T> 返回范型
     * @return 范型类
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        // 增加类型校验，clazz被使用，避免类型转换异常
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException("redis缓存类型不匹配，预期:" + clazz.getName() + ",实际:" + obj.getClass().getName());
        }
        return (T) obj;
    }

    /**
     * 根据key获取列表数据
     * @param key 键
     * @param <T> 返回类型
     * @return 范型类
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        return (List<T>) obj;
    }

    // ====================== Key通用操作 ======================

    /**
     * 删除
     * @param key 键
     * @return 是否删除
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除
     * @param keys 键列表
     * @return 数量
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置有效期
     * @param key 键
     * @param time 时间
     * @param unit 单位
     * @return 成功或失败
     */
    public Boolean expire(String key, long time, TimeUnit unit) {
        return redisTemplate.expire(key, time, unit);
    }

    /**
     * 是否存在key
     * @param key 键
     * @return 成功或失败
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据Key获取剩余的失效时间（秒）
     * @param key 键
     * @return 秒数
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ====================== Hash ======================

    /**
     * 设置hashmap
     * @param key 键
     * @param hashKey hash 键
     * @param value 值
     */
    public void hSet(String key, String hashKey, Object value) {
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, hashKey, value);
    }

    /**
     * 获取hashmap的值
     * @param key 键
     * @param hashKey hash 键
     * @param clazz 范型类
     * @param <T> 范型
     * @return 范型类
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj == null) {
            return null;
        }
        // 增加类型校验，clazz被使用，避免类型转换异常
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException("redis缓存类型不匹配，预期:" + clazz.getName() + ",实际:" + obj.getClass().getName());
        }
        return (T) obj;
    }

    /**
     * 获取键对应的hashmap全部
     * @param key 键
     * @return map
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取整个hash，返回 Map
     * @param key 键
     * @return map
     */
    public Map<String, Object> hGetAll2(String key) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        if (rawMap == null || rawMap.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>(rawMap.size());
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            String hashField = (String) entry.getKey();
            result.put(hashField, entry.getValue());
        }
        return result;
    }

    /**
     * 获取整个hash，泛型版本，返回 Map
     * @param key redis hash key
     * @param valueClazz hash中value的目标类型
     * @param <T> 范型
     * @return Map 范型map
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hGetAll(String key, Class<T> valueClazz) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        if (rawMap == null || rawMap.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, T> result = new HashMap<>(rawMap.size());
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            String hashField = (String) entry.getKey();
            Object valObj = entry.getValue();
            if (valObj == null) {
                result.put(hashField, null);
                continue;
            }
            if (!valueClazz.isInstance(valObj)) {
                throw new ClassCastException(
                        "Hash value类型不匹配，field:" + hashField
                                + ",预期:" + valueClazz.getName()
                                + ",实际:" + valObj.getClass().getName()
                );
            }
            result.put(hashField, (T) valObj);
        }
        return result;
    }

    /**
     * 删除hash key
     * @param key 键
     * @param hashKeys hash键
     * @return 删除数量
     */
    public Long hDel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断hash key是否存在
     * @param key 键
     * @param hashKey hash键
     * @return 存在或不存在
     */
    public Boolean hExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // ====================== List ======================

    /**
     * 从左插入
     * @param key 键
     * @param value 值
     * @return 位置
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 批量左入队
     * @param key 键
     * @param values 值列表
     * @return 数量
     */
    public Long lPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从右插入
     * @param key 键
     * @param value 值
     * @return 位置
     */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 批量右入队
     * @param key 键
     * @param values 值列表
     * @return 数量
     */
    public Long rPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
    /**
     * 从左查询
     * @param key 键
     * @param start  开始下标
     * @param end 结束下标
     * @param <T> 范型
     * @return 范型List
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> lRange(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 从左取出
     * @param key 键
     * @return object
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    /**
     * 从左取出
     * @param key 键
     * @param clazz 类
     * @param <T> 范型
     * @return 范型类
     */
    public <T> T lPop(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForList().leftPop(key);
        if (obj == null) {
            return null;
        }
        // 增加类型校验，clazz被使用，避免类型转换异常
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException("redis缓存类型不匹配，预期:" + clazz.getName() + ",实际:" + obj.getClass().getName());
        }
        return (T) obj;
    }

    /**
     * 从右取出
     * @param key 键
     * @return object
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 从右取出
     * @param key 键
     * @param clazz 范型class
     * @param <T> 范型
     * @return 范型类
     */
    public <T> T rPop(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForList().rightPop(key);
        if (obj == null) {
            return null;
        }
        // 增加类型校验，clazz被使用，避免类型转换异常
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException("redis缓存类型不匹配，预期:" + clazz.getName() + ",实际:" + obj.getClass().getName());
        }
        return (T) obj;
    }

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁key
     * @param lockValue 锁唯一标识（建议UUID，解锁用来判断持有者）
     * @param expireTime 锁过期时间
     * @param unit 单位
     * @return true 获取成功；false 获取失败
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime, TimeUnit unit) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, unit);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 释放分布式锁（原子Lua脚本，安全释放）
     * @param lockKey 锁key
     * @param lockValue 锁唯一标识
     * @return true：释放成功；false：锁不存在/不是当前线程持有
     */
    public boolean unLock(String lockKey, String lockValue) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long execute = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
        return execute != null && execute == 1;
    }

    /**
     * 发布消息
     * @param channel 频道
     * @param message 消息体
     */
    public void sendMessage(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
