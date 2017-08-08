package com.getgo.jenkins.plugins.pipeline.bamboo.exceptions;

/**
 * <p>
 * A custom Bamboo Exception.
 *
 * @author Kyle Flavin
 */
public class BambooException extends Exception {
    /**
     * Default constructor
     */
    public BambooException() { }

    /**
     * Constructor with message
     * @param message String that is the error message.
     */
    public BambooException(String message) { super(message); }
}
