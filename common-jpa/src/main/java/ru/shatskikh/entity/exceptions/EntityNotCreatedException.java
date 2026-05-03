package ru.shatskikh.entity.exceptions;

public class EntityNotCreatedException extends RuntimeException {
    public EntityNotCreatedException(String message) {
        super(message);
    }
}
