package com.oracle.notebookserver.rest;


import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeExpressionNotValidException;
import com.oracle.notebookserver.exception.CodeSyntaxException;
import com.oracle.notebookserver.exception.CodeTimeoutException;
import com.oracle.notebookserver.exception.UnknownInterpreterException;
import com.oracle.notebookserver.helpers.MessageResolver;
import com.oracle.notebookserver.model.ServerError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	public static final String CONSTRAINT_VIOLATION_EXCEPTION_CODE = "CODE_BAD_REQUEST_EXCEPTION";
	
	@Autowired private MessageResolver messageResolver;
	
	@ExceptionHandler(UnknownInterpreterException.class)
	protected ResponseEntity<ServerError> handleUnknownInterpreterException(UnknownInterpreterException unknownInterpreterException){
		return new ResponseEntity<ServerError>(new ServerError(UnknownInterpreterException.CODE_TAG,messageResolver.getMessageValue("exception.unknowninterpreter.message",unknownInterpreterException.getUnknownLanguage())),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CodeExpressionNotValidException.class)
	protected ResponseEntity<ServerError> handleCodeExpressionNotValidException(){
		return new ResponseEntity<ServerError>(new ServerError(CodeExpressionNotValidException.CODE_TAG,messageResolver.getMessageValue("exception.codeexpressionnotvalid.message")),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(CodeExecutionException.class)
	protected ResponseEntity<ServerError> handleCodeExecutionException(CodeExecutionException codeExecutionException ){
		return new ResponseEntity<ServerError>(new ServerError(CodeExecutionException.CODE_TAG,messageResolver.getMessageValue("exception.execution.message",codeExecutionException.getMessage())),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CodeSyntaxException.class)
	protected ResponseEntity<ServerError> handleCodeSyntaxException(CodeSyntaxException codeSyntaxException, WebRequest req){
		return new ResponseEntity<ServerError>(new ServerError(CodeSyntaxException.CODE_TAG,messageResolver.getMessageValue("exception.syntax.message",codeSyntaxException.getMessage())),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ServerError> handleCodeSyntaxException(ConstraintViolationException constraintViolationException){
		return new ResponseEntity<ServerError>(new ServerError(CONSTRAINT_VIOLATION_EXCEPTION_CODE,messageResolver.getMessageValue("exception.badrequest.message",constraintViolationException.getMessage())),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(CodeTimeoutException.class)
	protected ResponseEntity<ServerError> handleCodeSyntaxException(CodeTimeoutException codeTimeoutException){
		return new ResponseEntity<ServerError>(new ServerError(CodeTimeoutException.CODE_TAG,messageResolver.getMessageValue("exception.timeout.message")),HttpStatus.REQUEST_TIMEOUT);
	}
	
	
}
