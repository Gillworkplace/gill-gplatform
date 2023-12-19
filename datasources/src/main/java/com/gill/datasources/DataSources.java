package com.gill.datasources;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Datasources
 *
 * @author gill
 * @version 2023/12/18
 **/
@Component("datasources")
@ConfigurationProperties(value = "datasources")
public class DataSources {

	private Map<String, DataSourceProperties> sources;

	private String decryptionName = "default";

	public Map<String, DataSourceProperties> getSources() {
		return sources;
	}

	public void setSources(Map<String, DataSourceProperties> sources) {
		this.sources = sources;
	}

	public String getDecryptionName() {
		return decryptionName;
	}

	public void setDecryptionName(String decryptionName) {
		this.decryptionName = decryptionName;
	}
}
