package ru.shatskikh.node.exceptions;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException() {
        super("faculty not found!");
    }
}
