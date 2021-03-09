package com.project.exception;

import com.project.resources.Resources;

public class ReadingFileException extends Exception {

    private static final long serialVersionUID = -4896010667634235667L;

    private static final String READ_ERROR_MESSAGE;

    static {
        READ_ERROR_MESSAGE = Resources.RESOURCE_BUNDLE.getString("ReadingFileException.READ_ERROR_MESSAGE");
    }

    public ReadingFileException() {
        super(READ_ERROR_MESSAGE);
    }

}
