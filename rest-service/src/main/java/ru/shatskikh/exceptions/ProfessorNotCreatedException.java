package ru.shatskikh.exceptions;

public class ProfessorNotCreatedException extends RuntimeException {
    public ProfessorNotCreatedException(String message) {
        super(message);
    }
}
