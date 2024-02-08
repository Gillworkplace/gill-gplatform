package com.gill.common.util;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONUtil;
import com.gill.common.bean.BeanA;
import com.gill.common.bean.BeanB;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ObjectUtilTest
 *
 * @author gill
 * @version 2024/02/08
 **/
public class ObjectUtilTest {

    @Test
    public void test_read_yaml() {
        BeanA beanA = ObjectUtil.readYamlFromClasspath("bean.yaml", BeanA.class);
        BeanA expected = new BeanA();
        List<BeanB> beans = new ArrayList<>();
        Map<String, BeanB> beanMap = new HashMap<>();
        BeanB user = new BeanB();
        user.setId(1);
        user.setName("user");
        BeanB test = new BeanB();
        test.setId(2);
        test.setName("test");
        beans.add(user);
        beanMap.put("key", test);
        expected.setBeans(beans);
        expected.setBeanMap(beanMap);
        Assertions.assertEquals(JSONUtil.toJsonStr(expected), JSONUtil.toJsonStr(beanA));
    }

}
