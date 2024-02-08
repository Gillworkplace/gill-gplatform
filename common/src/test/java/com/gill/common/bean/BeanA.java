package com.gill.common.bean;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean
 *
 * @author gill
 * @version 2024/02/08
 **/
@Getter
@Setter
public class BeanA {

    private List<BeanB> beans;

    private Map<String, BeanB> beanMap;
}
