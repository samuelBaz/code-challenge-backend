package com.example.backend.exceptions;

public class DontDeleteException extends Exception  {
    public DontDeleteException(String entity) {
        super(entity + " cannot be eliminated (dependencies)");
    }
}
