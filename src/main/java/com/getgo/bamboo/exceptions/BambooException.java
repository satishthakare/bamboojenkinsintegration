package com.getgo.bamboo.exceptions;

/**
 * <p>
 * A custom Bamboo Exception.
 *
 * @author Kyle Flavin
 */
public class BambooException extends Exception {
    public BambooException() { }

    public BambooException(String message) { super(message); }
}
