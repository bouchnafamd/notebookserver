package com.oracle.notebookserver.model;

public enum ProgramingLanguageEnum {
	PYTHON("python");
	private String languageName;

	ProgramingLanguageEnum(String languageName) {
		this.languageName = languageName;
	}

	public String getLanguageName() {
		return languageName;
	}
}
