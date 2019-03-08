package com.oracle.notebookserver.interpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.notebookserver.model.ProgramingLanguageEnum;

@Component
public class InterpreterContainer {
	private Map<String, AbstractInterpreter> interpretersMapPerSessionId
    = Collections.synchronizedMap(new HashMap<String, AbstractInterpreter>());
	
	@Autowired private InterpreterFactory interpreterFactory;
	
	public AbstractInterpreter getInterpreterPerSessionId(ProgramingLanguageEnum programingLanguageEnum,String sessionId) {
		if(!interpretersMapPerSessionId.containsKey(sessionId)) {
			interpretersMapPerSessionId.put(sessionId,interpreterFactory.getInterpreter(programingLanguageEnum));
		}
		
		System.out.println("sessionId " + sessionId);
		
		return interpretersMapPerSessionId.get(sessionId);
	}

}
