package ru.shatskikh.exceptions;

public class SubjectNotCreatedException extends RuntimeException {
    public SubjectNotCreatedException(String message) {
        super(message);
    }
}
