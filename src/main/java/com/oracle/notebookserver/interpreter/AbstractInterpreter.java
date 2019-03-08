package com.oracle.notebookserver.interpreter;

import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeSyntaxException;

public interface AbstractInterpreter {
    String execute(String code) throws CodeSyntaxException,CodeExecutionException;

}
