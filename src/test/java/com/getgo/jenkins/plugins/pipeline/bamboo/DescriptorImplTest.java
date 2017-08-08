package com.getgo.jenkins.plugins.pipeline.bamboo;

import hudson.model.TaskListener;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test the {@link BuildBambooStep.DescriptorImpl} class.
 */
public class DescriptorImplTest {
    private BuildBambooStep.DescriptorImpl descriptor;

    @Before
    public void setUp() throws Exception {
        descriptor = new BuildBambooStep.DescriptorImpl();
    }

    @Test
    public void getRequiredContext() throws Exception {
        Set s = descriptor.getRequiredContext();
        assertTrue(s.containsAll(Collections.singleton(TaskListener.class)));
    }

    @Test
    public void getFunctionName() throws Exception {
        assertEquals("buildBamboo", descriptor.getFunctionName());
    }

    @Test
    public void getDisplayName() throws Exception {
        assertEquals("Build Bamboo", descriptor.getDisplayName());
    }
}