package com.tc.bean;

public class ResponseResult<T> {

	private int code;
	private String message;
	private T data;

	public ResponseResult() {
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public ResponseResult(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public ResponseResult setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ResponseResult(int code) {
		super();
		this.code = code;
	}

	public ResponseResult setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "ResponseResult{" +
				"code=" + code +
				", message='" + message + '\'' +
				", data=" + data +
				'}';
	}
}
