package com.oracle.notebookserver.model;

public class CodeServerRequest {
	private String code;

	private String sessionId;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public CodeServerRequest(String code, String sessionId) {
		super();
		this.code = code;
		this.sessionId = sessionId;
	}

	public CodeServerRequest() {
		// TODO Auto-generated constructor stub
	}

}
