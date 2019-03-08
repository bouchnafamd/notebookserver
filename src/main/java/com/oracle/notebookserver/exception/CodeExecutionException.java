package com.oracle.notebookserver.exception;

public class CodeExecutionException extends RuntimeException {
	
	public static final String CODE_TAG = "CODE_EXECUTION_EXCEPTION";

	/**
	 * 
	 */
	private static final long serialVersionUID = -4503931439102125522L;
	
	public CodeExecutionException(String message){
		super(message);
	}

}
