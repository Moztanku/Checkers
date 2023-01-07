package com.tp.Exceptions;

/**
 * Exception thrown when a player tries to make a move that is not allowed
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}