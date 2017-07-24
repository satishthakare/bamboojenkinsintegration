package com.getgo.bamboo.exceptions;

import org.junit.Test;

/**
 * Test the {@link BambooException} class.
 */
public class BambooExceptionTest {
    @Test(expected = BambooException.class)
    public void throwABambooExceptionWithMessage() throws Exception {
        throw new BambooException("Throwing an exception");
    }
    @Test(expected = BambooException.class)
    public void throwABambooException() throws Exception {
        throw new BambooException();
    }
}