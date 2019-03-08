package com.oracle.notebookserver.interpreter;

import org.springframework.stereotype.Component;

import com.oracle.notebookserver.model.ProgramingLanguageEnum;

@Component
public class InterpreterFactory {
	public AbstractInterpreter getInterpreter(ProgramingLanguageEnum programingLanguageEnum) {
		switch (programingLanguageEnum) {
		case PYTHON:
			return new MyPythonInterpreter();
		default:
			return null;
		}
	}
}
