/**
 * The MIT License
 *
 * Copyright (c) 2017, LogMeIn, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.logmein.jenkins.plugins.pipeline.bamboo;

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