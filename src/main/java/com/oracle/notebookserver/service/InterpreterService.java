package com.oracle.notebookserver.service;

import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.oracle.notebookserver.exception.CodeExecutionException;
import com.oracle.notebookserver.exception.CodeExpressionNotValidException;
import com.oracle.notebookserver.exception.CodeSyntaxException;
import com.oracle.notebookserver.exception.UnknownInterpreterException;
import com.oracle.notebookserver.interpreter.AbstractInterpreter;
import com.oracle.notebookserver.interpreter.InterpreterContainer;
import com.oracle.notebookserver.interpreter.InterpreterFactory;
import com.oracle.notebookserver.interpreter.MyPythonInterpreter;
import com.oracle.notebookserver.model.ProgramingLanguageEnum;

@Validated
@Service
public class InterpreterService {
	@Autowired
	private InterpreterContainer interpreterContainer;

	private void validateCodeExpression(@NotNull String codeExpression) throws CodeExpressionNotValidException {
		boolean expressionIsValid = Pattern.matches("%\\S*\\S.+", codeExpression);
		if (!expressionIsValid) {
			throw new CodeExpressionNotValidException();
		}
	}

	public Pair<ProgramingLanguageEnum, String> getInterpreterLanguageAndCode(@NotNull String codeExpression)
			throws UnknownInterpreterException,CodeExpressionNotValidException {
		validateCodeExpression(codeExpression);
		ProgramingLanguageEnum programingLanguageEnum = null;
		String codeExpressionTab[] = codeExpression.split(" ");
		String languageName = codeExpressionTab[0].substring(1);
		for (ProgramingLanguageEnum programingLanguage : ProgramingLanguageEnum.values()) {
			if (programingLanguage.getLanguageName().equals(languageName)) {
				programingLanguageEnum = programingLanguage;
			}
		}
		if (programingLanguageEnum == null)
			throw new UnknownInterpreterException(languageName);
		StringBuilder codeStrBuilder = new StringBuilder("");
		for (int cpt = 1; cpt < codeExpressionTab.length; cpt++) {
			codeStrBuilder.append(codeExpressionTab[cpt].trim()).append(" ");
		}

		Pair<ProgramingLanguageEnum, String> programingLanguageAndCode = Pair.of(programingLanguageEnum,
				codeStrBuilder.toString().trim());
		return programingLanguageAndCode;

	}

	public String executeCode(ProgramingLanguageEnum programingLanguageEnum, String code , String sessionId) throws CodeSyntaxException,CodeExecutionException {
		AbstractInterpreter interpreter = interpreterContainer.getInterpreterPerSessionId(programingLanguageEnum, sessionId);
		try {
			String result = interpreter.execute(code);
			return result.trim();
		}
		catch(CodeSyntaxException codeSyntaxException) {
			throw codeSyntaxException;
		}
		catch(CodeExecutionException codeExecutionException) {
			throw codeExecutionException;
		}
		
	}

}
