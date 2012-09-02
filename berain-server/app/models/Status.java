package models;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	Success(0, "success."),
	Fail(100, "fail."),
	NonExists(200, "No exists."),
	ServerError(300, "server error.");

	private final int code;
	private final String msg;
	private static Map<Integer, Status> cache = new HashMap<Integer, Status>();
	static {
		for (Status sliceType : values()) {
			cache.put(sliceType.code, sliceType);
		}
	}

	private Status(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Status valueOf(int value) {
		return cache.get(value);
	}

	public static Status find(String name) {
		if (name == null || "".equals(name.trim())) {
			return null;
		}
		Status[] fs = values();
		for (Status enumType : fs) {
			if (enumType.name().equalsIgnoreCase(name)) {
				return enumType;
			}

		}
		throw new IllegalArgumentException("Non-exist the enum type,error arg name:" + name);
	}

}
