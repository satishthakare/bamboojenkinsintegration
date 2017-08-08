package com.getgo.jenkins.plugins.pipeline.bamboo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the {@link HttpClientFactory} class.
 */
public class HttpClientFactoryTest {
    private HttpClientFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new HttpClientFactory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getHttpClient() throws Exception {
        assertTrue(factory.getHttpClient() instanceof HttpClient);
    }

    @Test
    public void getPostMethod() throws Exception {
        assertTrue(factory.getPostMethod("http://fake-url") instanceof PostMethod);
    }

    @Test
    public void getGetMethod() throws Exception {
        assertTrue(factory.getGetMethod("http://fake-url") instanceof GetMethod);
    }
}