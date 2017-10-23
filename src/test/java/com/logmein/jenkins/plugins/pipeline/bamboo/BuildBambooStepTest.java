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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the {@link BuildBambooStep} class.
 */
public class BuildBambooStepTest {

    private BuildBambooStep b;

    @Before
    public void setup() {
        b = new BuildBambooStep("project", "plan", "https://bamboo.example.org", "user", "password");
    }

    @Test
    public void getProjectName() throws Exception {
        assertEquals("project", b.getProjectKey());
    }

    @Test
    public void getPlanName() throws Exception {
        assertEquals("plan", b.getPlanKey());
    }

    @Test
    public void getServerAddress() throws Exception {
        assertEquals("https://bamboo.example.org", b.getServerAddress());
    }

    @Test
    public void getUsername() throws Exception {
        assertEquals("user", b.getUsername());
    }

    @Test
    public void getPassword() throws Exception {
        assertEquals("password", b.getPassword());
    }

    @Test
    public void getPropagate() throws Exception {
        assertEquals(true, b.getPropagate());
    }

    @Test
    public void setPropagate() throws Exception {
        b.setPropagate(false);
        assertEquals(false, b.getPropagate());
    }
}