package com.getgo.bamboo;

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