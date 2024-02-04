package com.gill.redis.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Redis
 *
 * @author gill
 * @version 2024/01/29
 **/
public interface Redis {

    /**
     * set
     *
     * @param key   key
     * @param value value
     */
    void set(@NonNull String key, Object value);

    /**
     * get
     *
     * @param key key
     * @return value
     */
    @NonNull
    String get(@NonNull String key);

    /**
     * get
     *
     * @param key   key
     * @param clazz type
     * @param <T>   type
     * @return value
     */
    @Nullable
    <T> T get(@NonNull String key, Class<T> clazz);

    /**
     * 设置map
     *
     * @param key key
     * @param map map
     */
    void mset(@NonNull String key, Map<String, Object> map);

    /**
     * 设置map
     *
     * @param key key
     * @param k   k
     * @param v   v
     */
    void mset(@NonNull String key, String k, Object v);

    /**
     * 获取map
     *
     * @param key key
     * @return map
     */
    @NonNull
    Map<String, Object> mget(@NonNull String key);

    /**
     * 获取map
     *
     * @param key   key
     * @param clazz clazz
     * @param <T>   type
     * @return map
     */
    @NonNull
    <T> Map<String, T> mget(@NonNull String key, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key key
     * @param k   k
     * @return v
     */
    @NonNull
    String mget(@NonNull String key, String k);

    /**
     * 获取map
     *
     * @param key   key
     * @param k     k
     * @param clazz clazz
     * @param <T>   type
     * @return v
     */
    @Nullable
    <T> T mget(@NonNull String key, String k, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key   key
     * @param ks    ks
     * @param clazz clazz
     * @param <T>   type
     * @return vs
     */
    @NonNull
    <T> Map<String, T> mget(@NonNull String key, Set<String> ks, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    @NonNull
    Map<String, String> mget(@NonNull String key, Set<String> ks);

    /**
     * 获取set
     *
     * @param key key
     * @return vals
     */
    @NonNull
    Set<String> sget(@NonNull String key);

    /**
     * set添加values
     *
     * @param key  key
     * @param vals values
     * @return cnt
     */
    long sadd(@NonNull String key, String... vals);

    /**
     * set添加values
     *
     * @param key  key
     * @param vals values
     * @return cnt
     */
    long sadd(@NonNull String key, Collection<String> vals);

    /**
     * set 删除values
     *
     * @param key  key
     * @param vals vals
     * @return cnt
     */
    long sremove(@NonNull String key, String... vals);

    /**
     * set 删除values
     *
     * @param key  key
     * @param vals vals
     * @return cnt
     */
    long sremove(@NonNull String key, Collection<String> vals);

    /**
     * set 清空
     *
     * @param key key
     */
    void sclear(@NonNull String key);

    /**
     * set 是否存在
     *
     * @param key key
     * @param val val
     * @return tf
     */
    boolean scontains(@NonNull String key, String val);

    /**
     * set 元素个数
     *
     * @param key key
     * @return 元素个数
     */
    long scount(@NonNull String key);

    /**
     * list 元素个数
     *
     * @param key key
     * @return 元素个数
     */
    int llen(@NonNull String key);

    /**
     * list 返回元素的索引值
     *
     * @param key     key
     * @param element 元素
     * @return 索引值
     */
    int lindex(@NonNull String key, String element);

    /**
     * list head pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    List<String> lpop(@NonNull String key, int count);

    /**
     * list head pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @param <T>   type
     * @return els
     */
    @NonNull
    <T> List<T> lpop(@NonNull String key, Class<T> clazz, int count);

    /**
     * list tail pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    List<String> lrpop(@NonNull String key, int count);

    /**
     * list tail pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @param <T>   type
     * @return els
     */
    @NonNull
    <T> List<T> lrpop(@NonNull String key, Class<T> clazz, int count);

    /**
     * list head push
     *
     * @param key      key
     * @param elements els
     */
    void lpush(@NonNull String key, String... elements);

    /**
     * list head push
     *
     * @param key      key
     * @param elements els
     */
    void lpush(@NonNull String key, Collection<String> elements);

    /**
     * list head push
     *
     * @param key      key
     * @param clazz    clazz
     * @param elements els
     * @param <T>      type
     */
    <T> void lpush(@NonNull String key, Class<T> clazz, Collection<T> elements);

    /**
     * list tail push
     *
     * @param key      key
     * @param elements els
     */
    void lrpush(@NonNull String key, String... elements);

    /**
     * list tail push
     *
     * @param key      key
     * @param elements els
     */
    void lrpush(@NonNull String key, Collection<String> elements);

    /**
     * list tail push
     *
     * @param key      key
     * @param clazz    clazz
     * @param elements els
     * @param <T>      type
     */
    <T> void lrpush(@NonNull String key, Class<T> clazz, Collection<T> elements);

    /**
     * list range
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @param clazz type
     * @param <T>   type
     * @return els
     */
    @NonNull
    <T> List<T> lrange(@NonNull String key, int start, int end, Class<T> clazz);

    /**
     * list range
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return els
     */
    @NonNull
    List<String> lrange(@NonNull String key, int start, int end);

    /**
     * list set
     *
     * @param key     key
     * @param index   index
     * @param element el
     */
    void lset(@NonNull String key, int index, String element);

    /**
     * list set
     *
     * @param key     key
     * @param index   index
     * @param element el
     * @param clazz   type
     * @param <T>     type
     */
    <T> void lset(@NonNull String key, int index, T element, Class<T> clazz);
}
