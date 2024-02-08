package com.gill.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.setting.yaml.YamlUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ObjectUtil
 *
 * @author gill
 * @version 2024/02/08
 **/
public class ObjectUtil {

    /**
     * 读取yaml 文件为Bean对象
     *
     * @param yamlPath yaml路径
     * @param clazz    type
     * @param <T>      type
     * @return bean对象
     */
    public static <T> T readYamlFromClasspath(String yamlPath, Class<T> clazz) {
        return readYaml(new ClassPathResource(yamlPath).getAbsolutePath(), clazz);
    }

    /**
     * 读取yaml 文件为Bean对象
     *
     * @param yamlPath yaml路径
     * @param clazz    type
     * @param <T>      type
     * @return bean对象
     */
    public static <T> T readYaml(String yamlPath, Class<T> clazz) {
        Map<?, ?> map = YamlUtil.loadByPath(yamlPath, Map.class);
        formatKey(map);
        return BeanUtil.toBeanIgnoreError(map, clazz);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void formatKey(Map map) {
        if (map == null) {
            return;
        }
        Map<Object, Object> addMap = new HashMap<>();
        Set<Entry> set = map.entrySet();
        for (Map.Entry entry : set) {
            if (entry.getValue() instanceof Map subMap) {
                formatKey(subMap);
            }
            if (entry.getKey() instanceof String key) {
                String formatKey = convertToCamelCase(key);
                if (!formatKey.equals(key)) {
                    addMap.put(formatKey, entry.getValue());
                }
            }
        }
        map.putAll(addMap);
    }

    private static String convertToCamelCase(String input) {
        StringBuilder camelCase = new StringBuilder();
        boolean toUpper = false;
        for (char c : input.toCharArray()) {
            if (c == '-') {
                toUpper = true;
            } else {
                if (toUpper) {
                    camelCase.append(Character.toUpperCase(c));
                    toUpper = false;
                } else {
                    camelCase.append(c);
                }
            }
        }
        return camelCase.toString();
    }

}
