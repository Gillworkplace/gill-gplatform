package com.gill.api.common;

import java.util.List;
import lombok.Getter;

/**
 * SelectorData
 *
 * @author gill
 * @version 2024/03/11
 **/
@Getter
public class SelectorData<T> {

    private final String defaultValue;

    private final List<T> options;

    public SelectorData(String defaultValue, List<T> options) {
        this.defaultValue = defaultValue;
        this.options = options;
    }
}
