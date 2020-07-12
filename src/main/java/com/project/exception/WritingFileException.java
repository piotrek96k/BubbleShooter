package com.project.exception;

public class WritingFileException extends Exception{

	private static final long serialVersionUID = -8934741823308497152L;
	
	private static final String WRITE_ERROR_MESSAGE;
	
	static {
		WRITE_ERROR_MESSAGE = "Nie uda³o siê zapisaæ wyniku do pliku";
	}

	public WritingFileException() {
		super(WRITE_ERROR_MESSAGE);
	}
	
}
