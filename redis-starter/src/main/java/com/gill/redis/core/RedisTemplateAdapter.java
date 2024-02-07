package com.gill.redis.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

/**
 * RedisTemplateOperator
 *
 * @author gill
 * @version 2024/01/29
 **/
@Slf4j
public class RedisTemplateAdapter implements Redis {

    private final StringRedisTemplate redisTemplate;

    public RedisTemplateAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private <T> T cast(String jsonStr, Class<T> clazz) {
        if (StrUtil.isBlank(jsonStr)) {
            return null;
        }
        try {
            return JSONUtil.toBean(jsonStr, clazz);
        } catch (Exception e) {
            log.error("redis parse obj to {}, failed, element: {}", clazz.getCanonicalName(),
                jsonStr);
        }
        return null;
    }

    private <T> List<T> cast(List<String> strings, Class<T> clazz) {
        if (CollectionUtil.isEmpty(strings)) {
            return Collections.emptyList();
        }
        return strings.stream().map(str -> cast(str, clazz)).toList();
    }

    /**
     * 设置过期时间
     *
     * @param key     key
     * @param expired ms
     */
    @Override
    public void expire(@NonNull String key, long expired) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        redisTemplate.expire(key, Duration.ofMillis(expired));
    }

    /**
     * set
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void set(@NonNull String key, Object value) {
        if (value == null) {
            redisTemplate.delete(key);
        }
        String valueStr;
        if (value instanceof String str) {
            valueStr = str;
        } else {
            valueStr = JSONUtil.toJsonStr(value);
        }
        redisTemplate.opsForValue().set(key, valueStr);
    }

    /**
     * set
     *
     * @param key     key
     * @param value   value
     * @param expired 过期时间
     */
    @Override
    public void set(@NonNull String key, Object value, long expired) {
        if (value == null) {
            redisTemplate.delete(key);
        }
        String valueStr;
        if (value instanceof String str) {
            valueStr = str;
        } else {
            valueStr = JSONUtil.toJsonStr(value);
        }
        redisTemplate.opsForValue().set(key, valueStr, Duration.ofMillis(expired));
    }

    /**
     * 原子加1
     *
     * @param key key
     * @return +1
     */
    @Override
    public Long increaseAndGet(@NonNull String key) {
        return addAndGet(key, 1);
    }

    /**
     * 原子减1
     *
     * @param key key
     * @return -1
     */
    @Override
    public Long decreaseAndGet(@NonNull String key) {
        return addAndGet(key, -1);
    }

    /**
     * 原子加x
     *
     * @param key   key
     * @param delta delta
     * @return +x
     */
    @Override
    public Long addAndGet(@NonNull String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * get
     *
     * @param key key
     * @return value
     */
    @NonNull
    @Override
    public String get(@NonNull String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse("");
    }

    /**
     * get
     *
     * @param key   key
     * @param clazz type
     * @return value
     */
    @Override
    public <T> T get(@NonNull String key, Class<T> clazz) {
        String value = redisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return cast(value, clazz);
    }

    /**
     * 设置map
     *
     * @param key key
     * @param map map
     */
    @Override
    public void mset(@NonNull String key, Map<String, Object> map) {
        if (CollectionUtil.isEmpty(map)) {
            return;
        }
        Map<String, String> serializedMap = new HashMap<>(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String str) {
                serializedMap.put(entry.getKey(), str);
            } else {
                serializedMap.put(entry.getKey(), JSONUtil.toJsonStr(value));
            }
        }
        redisTemplate.opsForHash().putAll(key, serializedMap);
    }

    /**
     * 设置map
     *
     * @param key key
     * @param k   k
     * @param v   v
     */
    @Override
    public void mset(@NonNull String key, String k, Object v) {
        if (v == null) {
            redisTemplate.opsForHash().delete(key, k);
        }
        String val;
        if (v instanceof String str) {
            val = str;
        } else {
            val = JSONUtil.toJsonStr(v);
        }
        redisTemplate.opsForHash().put(key, k, val);
    }

    /**
     * 获取map
     *
     * @param key key
     * @return map
     */
    @NonNull
    @Override
    public Map<String, Object> mget(@NonNull String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollectionUtil.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(entries.size());
        for (Entry<Object, Object> entry : entries.entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key   key
     * @param clazz clazz
     * @param <T>   type
     * @return map
     */
    @NonNull
    @Override
    public <T> Map<String, T> mget(@NonNull String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (CollectionUtil.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Map<String, T> map = new HashMap<>(entries.size());
        for (Entry<Object, Object> entry : entries.entrySet()) {
            String val = String.valueOf(entry.getValue());
            String k = String.valueOf(entry.getKey());
            map.put(k, cast(val, clazz));
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key key
     * @param k   k
     * @return v
     */
    @NonNull
    @Override
    public String mget(@NonNull String key, String k) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, k))
            .map(String::valueOf)
            .orElse("");
    }

    /**
     * 获取map
     *
     * @param key   key
     * @param k     k
     * @param clazz clazz
     * @return v
     */
    @Override
    public <T> T mget(@NonNull String key, String k, Class<T> clazz) {
        Object val = redisTemplate.opsForHash().get(key, k);
        if (val == null) {
            return null;
        }
        return cast(String.valueOf(val), clazz);
    }

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    @NonNull
    @Override
    public <T> Map<String, T> mget(@NonNull String key, Set<String> ks, Class<T> clazz) {
        List<Object> ksList = new ArrayList<>(ks);
        List<Object> values = redisTemplate.opsForHash().multiGet(key, ksList);
        Map<String, T> map = new HashMap<>(ksList.size());
        for (int i = 0; i < ksList.size(); i++) {
            String k = String.valueOf(ksList.get(i));
            String v = String.valueOf(values.get(i));
            map.put(k, cast(v, clazz));
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    @NonNull
    @Override
    public Map<String, String> mget(@NonNull String key, Set<String> ks) {
        List<Object> ksList = new ArrayList<>(ks);
        List<Object> values = redisTemplate.opsForHash().multiGet(key, ksList);
        Map<String, String> map = new HashMap<>(ksList.size());
        for (int i = 0; i < ksList.size(); i++) {
            map.put(String.valueOf(ksList.get(i)), String.valueOf(values.get(i)));
        }
        return map;
    }

    /**
     * 获取set
     *
     * @param key key
     * @return vals
     */
    @NonNull
    @Override
    public Set<String> sget(@NonNull String key) {
        if (StrUtil.isBlank(key)) {
            return Collections.emptySet();
        }
        Set<String> members = redisTemplate.opsForSet().members(key);
        return Optional.ofNullable(members).orElse(Collections.emptySet());
    }

    /**
     * set添加values
     *
     * @param key  key
     * @param vals values
     * @return cnt
     */
    @Override
    public long sadd(@NonNull String key, String... vals) {
        if (vals == null || vals.length == 0) {
            return 0;
        }
        Long add = redisTemplate.opsForSet().add(key, vals);
        return Optional.ofNullable(add).orElse(0L);
    }

    /**
     * set添加values
     *
     * @param key  key
     * @param vals values
     * @return cnt
     */
    @Override
    public long sadd(@NonNull String key, Collection<String> vals) {
        if (CollectionUtil.isEmpty(vals)) {
            return 0;
        }
        Long add = redisTemplate.opsForSet().add(key, vals.toArray(new String[0]));
        return Optional.ofNullable(add).orElse(0L);
    }

    /**
     * set 删除values
     *
     * @param key  key
     * @param vals vals
     * @return cnt
     */
    @Override
    public long sremove(@NonNull String key, String... vals) {
        if (vals == null || vals.length == 0) {
            return 0;
        }
        Long remove = redisTemplate.opsForSet().remove(key, ArrayUtil.cast(Object.class, vals));
        return Optional.ofNullable(remove).orElse(0L);
    }

    /**
     * set 删除values
     *
     * @param key  key
     * @param vals vals
     * @return cnt
     */
    @Override
    public long sremove(@NonNull String key, Collection<String> vals) {
        if (CollectionUtil.isEmpty(vals)) {
            return 0;
        }
        Long remove = redisTemplate.opsForSet().remove(key, vals.toArray(new Object[0]));
        return Optional.ofNullable(remove).orElse(0L);
    }

    /**
     * set 清空
     *
     * @param key key
     */
    @Override
    public void sclear(@NonNull String key) {
        redisTemplate.delete(key);
    }

    /**
     * set 是否存在
     *
     * @param key key
     * @param val val
     * @return tf
     */
    @Override
    public boolean scontains(@NonNull String key, String val) {
        return Optional.ofNullable(redisTemplate.opsForSet().isMember(key, val)).orElse(false);
    }

    /**
     * set 元素个数
     *
     * @param key key
     * @return 元素个数
     */
    @Override
    public long scount(@NonNull String key) {
        return Optional.ofNullable(redisTemplate.opsForSet().size(key)).orElse(0L);
    }

    /**
     * list 元素个数
     *
     * @param key key
     * @return 元素个数
     */
    @Override
    public int llen(@NonNull String key) {
        return Optional.ofNullable(redisTemplate.opsForList().size(key))
            .map(Long::intValue)
            .orElse(0);
    }

    /**
     * list 返回索引的元素
     *
     * @param key   key
     * @param index 元素
     * @return 索引值
     */
    @Override
    public String lindex(@NonNull String key, int index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * list 返回索引的元素
     *
     * @param key   key
     * @param index 索引位置
     * @param clazz 类型
     * @param <T>   类型
     * @return element
     */
    @Override
    public <T> T lindex(@NonNull String key, int index, Class<T> clazz) {
        return Optional.ofNullable(redisTemplate.opsForList().index(key, index))
            .map(el -> cast(el, clazz))
            .orElse(null);
    }

    /**
     * list head pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public List<String> lpop(@NonNull String key, int count) {
        return Optional.ofNullable(redisTemplate.opsForList().leftPop(key, count))
            .orElse(Collections.emptyList());
    }

    /**
     * list head pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public <T> List<T> lpop(@NonNull String key, Class<T> clazz, int count) {
        List<String> elements = lpop(key, count);
        return cast(elements, clazz);
    }

    /**
     * list tail pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public List<String> lrpop(@NonNull String key, int count) {
        return Optional.ofNullable(redisTemplate.opsForList().rightPop(key, count))
            .orElse(Collections.emptyList());
    }

    /**
     * list tail pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public <T> List<T> lrpop(@NonNull String key, Class<T> clazz, int count) {
        List<String> elements = lrpop(key, count);
        return cast(elements, clazz);
    }

    /**
     * list head push
     *
     * @param key      key
     * @param elements els
     */
    @Override
    public void lpush(@NonNull String key, String... elements) {
        redisTemplate.opsForList().leftPushAll(key, elements);
    }

    /**
     * list head push
     *
     * @param key      key
     * @param elements els
     */
    @Override
    public void lpush(@NonNull String key, Collection<String> elements) {
        redisTemplate.opsForList().leftPushAll(key, elements);
    }

    /**
     * list head push
     *
     * @param key      key
     * @param clazz    clazz
     * @param elements els
     */
    @Override
    public <T> void lpush(@NonNull String key, Class<T> clazz, Collection<T> elements) {
        List<String> els = elements.stream().map(JSONUtil::toJsonStr).toList();
        lpush(key, els);
    }

    /**
     * list tail push
     *
     * @param key      key
     * @param elements els
     */
    @Override
    public void lrpush(@NonNull String key, String... elements) {
        redisTemplate.opsForList().rightPushAll(key, elements);
    }

    /**
     * list tail push
     *
     * @param key      key
     * @param elements els
     */
    @Override
    public void lrpush(@NonNull String key, Collection<String> elements) {
        redisTemplate.opsForList().rightPushAll(key, elements);
    }

    /**
     * list tail push
     *
     * @param key      key
     * @param clazz    clazz
     * @param elements els
     */
    @Override
    public <T> void lrpush(@NonNull String key, Class<T> clazz, Collection<T> elements) {
        List<String> els = elements.stream().map(JSONUtil::toJsonStr).toList();
        lrpush(key, els);
    }

    /**
     * list range
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return els
     */
    @NonNull
    @Override
    public List<String> lrange(@NonNull String key, int start, int end) {
        return Optional.ofNullable(redisTemplate.opsForList().range(key, start, end))
            .orElse(Collections.emptyList());
    }

    /**
     * list range
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @param clazz type
     * @return els
     */
    @NonNull
    @Override
    public <T> List<T> lrange(@NonNull String key, int start, int end, Class<T> clazz) {
        List<String> els = lrange(key, start, end);
        return cast(els, clazz);
    }

    /**
     * list set
     *
     * @param key     key
     * @param index   index
     * @param element el
     */
    @Override
    public void lset(@NonNull String key, int index, String element) {
        redisTemplate.opsForList().set(key, index, element);
    }

    /**
     * list set
     *
     * @param key     key
     * @param index   index
     * @param element el
     * @param clazz   type
     */
    @Override
    public <T> void lset(@NonNull String key, int index, T element, Class<T> clazz) {
        String str = JSONUtil.toJsonStr(element);
        lset(key, index, str);
    }
}
