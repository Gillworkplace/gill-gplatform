package com.gill.api.common;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SelectorData
 *
 * @author gill
 * @version 2024/03/11
 **/
@Getter
@Setter
@NoArgsConstructor
public class SelectorData<T> {

    private String defaultValue;

    private List<T> options;

    public SelectorData(String defaultValue, List<T> options) {
        this.defaultValue = defaultValue;
        this.options = options;
    }
}
