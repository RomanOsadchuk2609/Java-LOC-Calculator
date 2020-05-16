package sample;

import java.util.Map;

public class Result {
	private String key;
	private String value;

	public Result(Map.Entry<LineType, Long> entry) {
		this.key = entry.getKey().toString();
		this.value = entry.getValue().toString();
	}

	public Result(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
