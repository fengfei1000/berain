package fengfei.berain.client;

public class BerainEntry {

	public String key;
	public String value;
	public String path;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "BerainEntry [key=" + key + ", value=" + value + ", path=" + path + "]";
	}

}
