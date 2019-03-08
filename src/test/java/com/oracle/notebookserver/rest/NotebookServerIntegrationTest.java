package com.oracle.notebookserver.rest;

import static org.junit.Assert.assertEquals;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import com.oracle.notebookserver.SpringBootMainApplication;
import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeExpressionNotValidException;
import com.oracle.notebookserver.exception.CodeSyntaxException;
import com.oracle.notebookserver.exception.UnknownInterpreterException;
import com.oracle.notebookserver.model.CodeServerRequest;
import com.oracle.notebookserver.model.ServerError;
import com.oracle.notebookserver.model.ServerResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootMainApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotebookServerIntegrationTest {
	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testExecuteCodeInPython() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest("%python print 1+1",null);
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);

		ResponseEntity<ServerResponse> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, ServerResponse.class);

		assertEquals(new ServerResponse("2"), response.getBody());

	}
	
	
	@Test
	public void testCodeExpressionWithUnknowInterpreterException() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest("%sq print 1+1",null);
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);

		ResponseEntity<Object> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, Object.class);
		
		LinkedHashMap<String, String> hashmap = (LinkedHashMap<String, String>)response.getBody();

		assertEquals(UnknownInterpreterException.CODE_TAG, hashmap.get("errorCode"));

	}
	
	@Test
	public void testCodeExpressionWithExpressionNotValid() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest("%s",null);
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);

		ResponseEntity<Object> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, Object.class);
		
		LinkedHashMap<String, String> hashmap = (LinkedHashMap<String, String>)response.getBody();

		assertEquals(CodeExpressionNotValidException.CODE_TAG, hashmap.get("errorCode"));

	}
	
	@Test
	public void testCodeExpressionWithSyntaxError() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest("%python prd 1",null);
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);

		ResponseEntity<Object> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, Object.class);
		
		LinkedHashMap<String, String> hashmap = (LinkedHashMap<String, String>)response.getBody();

		assertEquals(CodeSyntaxException.CODE_TAG, hashmap.get("errorCode"));

	}
	
	@Test
	public void testCodeExpressionWithExecutionError() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest("%python print b",null);
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);

		ResponseEntity<Object> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, Object.class);
		
		LinkedHashMap<String, String> hashmap = (LinkedHashMap<String, String>)response.getBody();

		assertEquals(CodeExecutionException.CODE_TAG, hashmap.get("errorCode"));

	}
	
	@Test
	public void testCodeExpressionWithBadRequest() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		CodeServerRequest codeServerRequest = new CodeServerRequest();
		HttpEntity<String> entity = new HttpEntity<String>("{}", headers) ;

		ResponseEntity<Object> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, Object.class);
		
		LinkedHashMap<String, String> hashmap = (LinkedHashMap<String, String>)response.getBody();

		assertEquals(RestExceptionHandler.CONSTRAINT_VIOLATION_EXCEPTION_CODE, hashmap.get("errorCode"));

	}
	
	@Test
	public void testExecuteCodeInPythonWithRetaining() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=1",null));
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=a+5",null));
		String result=execute(createURLWithPort("/execute"),new CodeServerRequest("%python print a",null));
		assertEquals("6", result);
	}
	
	@Test
	public void testExecuteCodeInPythonWith2DifferentSessionId() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=1","session1"));
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=5","session2"));
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=a-20","session2"));
		execute(createURLWithPort("/execute"),new CodeServerRequest("%python a=a+100","session1"));
		String resultSession1=execute(createURLWithPort("/execute"),new CodeServerRequest("%python print a","session1"));
		String resultSession2=execute(createURLWithPort("/execute"),new CodeServerRequest("%python print a","session2"));

		assertEquals("101", resultSession1);
		assertEquals("-15", resultSession2);


	}
	
	private String execute(String url , CodeServerRequest codeServerRequest) {
		HttpEntity<CodeServerRequest> entity = new HttpEntity<CodeServerRequest>(codeServerRequest, headers);
		ResponseEntity<ServerResponse> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, ServerResponse.class);
		return response.getBody().getResult();
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
	

}
