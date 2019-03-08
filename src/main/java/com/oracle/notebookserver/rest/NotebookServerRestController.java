package com.oracle.notebookserver.rest;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.oracle.notebookserver.exception.CodeTimeoutException;
import com.oracle.notebookserver.model.CodeServerRequest;
import com.oracle.notebookserver.model.ProgramingLanguageEnum;
import com.oracle.notebookserver.model.ServerResponse;
import com.oracle.notebookserver.service.InterpreterService;

import static com.oracle.notebookserver.configuration.GeneralConfiguration.REQUEST_TIMEOUT;;

@RestController
public class NotebookServerRestController {

	@Autowired
	private InterpreterService interpreterService;

	// Use Callable with WebAsyncTask in order to set a timeout 
	@RequestMapping(method = RequestMethod.POST, value = "/execute" , produces = "application/json" , consumes="application/json")
	public @ResponseBody WebAsyncTask<ServerResponse> execute(@RequestBody CodeServerRequest codeRequest){

		Callable<ServerResponse> callable = new Callable<ServerResponse>() {

			@Override
			public ServerResponse call() throws Exception {
				Pair<ProgramingLanguageEnum, String> programingLanguageAndCode = interpreterService
						.getInterpreterLanguageAndCode(codeRequest.getCode());
				String result = interpreterService.executeCode(programingLanguageAndCode.getFirst(),
						programingLanguageAndCode.getSecond(), codeRequest.getSessionId());
				return new ServerResponse(result);
			}

		};
		
		WebAsyncTask<ServerResponse> webAsyncTask = new WebAsyncTask<ServerResponse>(REQUEST_TIMEOUT, callable);
		webAsyncTask.onTimeout(new Callable<ServerResponse>() {

			@Override
			public ServerResponse call() throws CodeTimeoutException {
				throw new CodeTimeoutException();
				
			}
			
		});
		return webAsyncTask ;

	}

}
