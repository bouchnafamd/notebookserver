package com.oracle.notebookserver.interpreter;

import java.io.ByteArrayOutputStream;

import org.python.core.PyException;
import org.python.core.PySyntaxError;
import org.python.util.PythonInterpreter;

import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeSyntaxException;



public class MyPythonInterpreter implements AbstractInterpreter {
	
    private PythonInterpreter pythonInterpreter;
    
    public MyPythonInterpreter(){
        pythonInterpreter = new PythonInterpreter();
        
    }

	@Override
	public String execute(String code) throws CodeSyntaxException, CodeExecutionException {
		try {
			ByteArrayOutputStream outputCodeStream = new ByteArrayOutputStream();
			pythonInterpreter.setOut(outputCodeStream);
			pythonInterpreter.exec(code);
	        return new String(outputCodeStream.toByteArray());
		}
		catch(PySyntaxError pySyntaxError) {
			throw new CodeSyntaxException(pySyntaxError.value.toString());
		}
		catch(PyException pyException) {
			throw new CodeExecutionException(pyException.value.toString());
		}
	
	}
	
	
	
}
