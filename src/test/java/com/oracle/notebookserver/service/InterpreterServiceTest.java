package com.oracle.notebookserver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;

import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeExpressionNotValidException;
import com.oracle.notebookserver.exception.CodeSyntaxException;
import com.oracle.notebookserver.exception.UnknownInterpreterException;
import com.oracle.notebookserver.interpreter.InterpreterContainer;
import com.oracle.notebookserver.interpreter.MyPythonInterpreter;
import com.oracle.notebookserver.model.ProgramingLanguageEnum;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InterpreterServiceTest {
	
	@Mock InterpreterContainer interpreterContainer;
	
	@InjectMocks InterpreterService interpreterService;
	
	@Test
	public void testGetInterpreterLanguageAndCodeSucceded() {
		assertEquals(Pair.of(ProgramingLanguageEnum.PYTHON, "print 1"), interpreterService.getInterpreterLanguageAndCode("%python print 1"));
	}
	
	@Test(expected = CodeExpressionNotValidException.class)
	public void testGetInterpreterLanguageAndCodeFailedWithCodeExpressionNotValidException() {
		assertEquals(Pair.of(ProgramingLanguageEnum.PYTHON, "print 1"), interpreterService.getInterpreterLanguageAndCode("%p"));
	}
	
	@Test(expected = UnknownInterpreterException.class)
	public void testGetInterpreterLanguageAndCodeFailedWithUnknownInterpreterException() {
		assertEquals(Pair.of(ProgramingLanguageEnum.PYTHON, "print 1"), interpreterService.getInterpreterLanguageAndCode("%lang print 1"));
	}
	
	@Test
	public void testPythonInterpreterExecuteCode() {
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq(null))).thenReturn(new MyPythonInterpreter());
		assertEquals("2", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print 1+1", null));
	}
	
	@Test(expected = CodeSyntaxException.class)
	public void testPythonInterpreterExecuteCodeWithCodeSyntaxException() {
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq(null))).thenReturn(new MyPythonInterpreter());
		assertEquals("2", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "prin 5", null));
	}
	
	@Test(expected = CodeExecutionException.class)
	public void testPythonInterpreterExecuteCodeWithCodeExecutionException() {
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq(null))).thenReturn(new MyPythonInterpreter());
		assertEquals("2", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print a", null));
	}
	
	@Test
	public void testPythonInterpreterExecuteCodeWithPreservingState() {
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq(null))).thenReturn(new MyPythonInterpreter());
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=1",null);
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=a+5",null);
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print a",null);
		assertEquals("6", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print a", null));

	}
	
	@Test
	public void testPythonInterpreterExecuteCodeWithPreservingStateUsingSessionId() {
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq("sessionId1"))).thenReturn(new MyPythonInterpreter());
		when(interpreterContainer.getInterpreterPerSessionId(eq(ProgramingLanguageEnum.PYTHON), eq("sessionId2"))).thenReturn(new MyPythonInterpreter());
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=1","sessionId1");
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=5","sessionId2");
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=a+5","sessionId2");
		interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "a=a-10","sessionId1");
		assertEquals("-9", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print a", "sessionId1"));
		assertEquals("10", interpreterService.executeCode(ProgramingLanguageEnum.PYTHON, "print a", "sessionId2"));


	}
}
