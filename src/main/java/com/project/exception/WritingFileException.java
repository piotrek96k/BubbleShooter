package com.project.exception;

import com.project.resources.Resources;

public class WritingFileException extends Exception {

	private static final long serialVersionUID = -8934741823308497152L;

	private static final String WRITE_ERROR_MESSAGE;

	static {
		WRITE_ERROR_MESSAGE = Resources.RESOURCE_BUNDLE.getString("WritingFileException.WRITE_ERROR_MESSAGE");
	}

	public WritingFileException() {
		super(WRITE_ERROR_MESSAGE);
	}

}
