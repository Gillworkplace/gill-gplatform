package com.gill.datasource;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Datasources
 *
 * @author gill
 * @version 2023/12/18
 **/
@Getter
@Setter
@ConfigurationProperties(value = "datasources")
public class DataSources {

	private Map<String, DataSourceProperties> sources = Collections.emptyMap();

	private String decryptionName = "default";
}
