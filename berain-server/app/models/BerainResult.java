package models;

public class BerainResult {

	public int code;
	public String message;
	public Object data;

	public BerainResult(int code, String message, Object data) {
		this(code, message);
		this.data = data;
	}

	public BerainResult(Status status, Object data) {
		this(status);
		this.data = data;
	}

	public BerainResult(Status status) {
		super();
		this.code = status.getCode();
		this.message = status.getMsg();

	}

	public BerainResult(int code, String message) {
		super();
		this.code = code;
		this.message = message;

	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "BerainResult [code=" + code + ", message=" + message + ", data=" + data + "]";
	}

}