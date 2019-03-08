package com.oracle.notebookserver.exception;

public class UnknownInterpreterException extends RuntimeException {
	
	public static final String CODE_TAG = "UNKNOWN_INTERPRETER_EXCEPTION";


	/**
	 * 
	 */
	private static final long serialVersionUID = -5394391954357124733L;
	
	private String unknownLanguage;
	
	public UnknownInterpreterException(String inputLanguage){
		this.unknownLanguage = inputLanguage;
	}

	public String getUnknownLanguage() {
		return unknownLanguage;
	}
	
	

}
