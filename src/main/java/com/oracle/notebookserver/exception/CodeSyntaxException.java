package com.oracle.notebookserver.exception;

public class CodeSyntaxException extends RuntimeException {
	
	public static final String CODE_TAG = "CODE_SYNTAX_EXCEPTION";


	/**
	 * 
	 */
	private static final long serialVersionUID = 4143371153192138385L;
	
	
	public CodeSyntaxException(String message){
		super(message);
	}

	
	

}
