package com.project.exception;

public class ReadingFileException extends Exception {

	private static final long serialVersionUID = -4896010667634235667L;
	
	private static final String READ_ERROR_MESSAGE;
	
	static {
		READ_ERROR_MESSAGE = "Nie uda³o siê otworzyæ pliku z wynikami graczy";
	}
	
	public ReadingFileException() {
		super(READ_ERROR_MESSAGE);
	}

}
