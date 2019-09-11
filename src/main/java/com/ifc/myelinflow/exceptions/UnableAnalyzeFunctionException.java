package com.ifc.myelinflow.exceptions;

public class UnableAnalyzeFunctionException extends Exception {


    public UnableAnalyzeFunctionException() {
    }

    public UnableAnalyzeFunctionException(String message) {
        super(message);
    }

    public UnableAnalyzeFunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
