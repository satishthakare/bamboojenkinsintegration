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
//package com.getgo.jenkins.plugins.pipeline.bamboo;
//
//import hudson.model.TaskListener;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.io.IOUtils;
//import org.jenkinsci.plugins.workflow.steps.StepContext;
//import org.junit.*;
//import org.mockito.Answers;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//
//import java.io.*;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
///**
// * Test the {@link BuildBambooStep.Execution} class.
// */
//public class ExecutionTest {
//    @Mock
//    BuildBambooStep buildBambooStepMock;
//
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    StepContext stepContextMock;
//
//    @Spy
//    HttpClient httpClientMock;
//
//    @Mock
//    PostMethod postMethodMock;
//
//    @Mock
//    GetMethod getMethodMock;
//
//    @Mock
//    HttpClientFactory httpClientFactoryMock;
//
//    @Mock
//    TaskListener taskListener;
//
//    @Mock
//    PrintStream printStreamMock;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testExecutionReturnsCorrectValues() throws Exception {
//        setupExecutionTests();
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        assertTrue(execution.getLogger() instanceof PrintStream);
//        assertTrue(execution.getStep() instanceof BuildBambooStep);
//        assertTrue(execution.getHttpClientFactory() instanceof HttpClientFactory);
//        assertEquals(false, execution.getStopExecution());
//    }
//
//    private void setupExecutionTests() throws Exception {
//        // Set expectations
//        when(this.stepContextMock.get(TaskListener.class)).thenReturn(taskListener);
//        when(this.stepContextMock.get(TaskListener.class).getLogger()).thenReturn(printStreamMock);
//        when(this.httpClientFactoryMock.getHttpClient()).thenReturn(httpClientMock);
//        when(getMethodMock.getResponseBodyAsStream()).thenReturn(IOUtils.toInputStream("return value", "UTF-8"));
//        when(postMethodMock.getResponseBodyAsStream()).thenReturn(IOUtils.toInputStream("return value", "UTF-8"));
//    }
//
//    @Test
//    public void getShouldReturnExpectedValue() throws Exception {
//        setupExecutionTests();
//        doReturn(getMethodMock).when(httpClientFactoryMock).getGetMethod(anyString());
//        doReturn(200).when(httpClientMock).executeMethod(any(GetMethod.class));
//
//        // Test
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        String result = execution.get("http://fake-url", "user", "pasword");
//        assertEquals("return value", result);
//    }
//
//    @Test
//    public void getShouldReturnEmptyStringOnException() throws Exception {
//        setupExecutionTests();
//        doReturn(getMethodMock).when(httpClientFactoryMock).getGetMethod(anyString());
//        doThrow(IOException.class).when(httpClientMock).executeMethod(any(GetMethod.class));
//
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        System.out.println(execution);
//        String result = execution.get("http://fake-url", "user", "pasword");
//        assertEquals("", result);
//    }
//
//    @Test
//    public void postShouldReturnExpectedValue() throws Exception {
//        setupExecutionTests();
//        doReturn(postMethodMock).when(httpClientFactoryMock).getPostMethod(anyString());
//        doReturn(200).when(httpClientMock).executeMethod(any(PostMethod.class));
//
//        // Test
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        Map<String, Object> m = new HashMap<>();
//        m.put("key1", "value1");
//        String result = execution.post("http://fake-url", "user", "password", m);
//        assertEquals("return value", result);
//    }
//
//    @Test
//    public void postShouldReturnEmptyStringOnException() throws Exception {
//        setupExecutionTests();
//        doReturn(postMethodMock).when(httpClientFactoryMock).getPostMethod(anyString());
//        doThrow(IOException.class).when(httpClientMock).executeMethod(any(GetMethod.class));
//
//        // Test
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        Map<String, Object> m = new HashMap<>();
//        m.put("key1", "value1");
//        String result = execution.post("http://fake-url", "user", "password", m);
//        assertEquals("", result);
//    }
//
//    @Test
//    public void getBuildStatusShouldReturnBuildStatusFromValidInput() throws Exception {
//        setupExecutionTests();
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        String getText = "{\"expand\":\"changes,metadata,plan,artifacts,comments,labels,jiraIssues,stages\",\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/result/P1-P1P1-200\",\"rel\":\"self\"},\"plan\":{\"shortName\":\"P1P1\",\"shortKey\":\"P1P1\",\"type\":\"chain\",\"enabled\":true,\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/plan/P1-P1P1\",\"rel\":\"self\"},\"key\":\"P1-P1P1\",\"name\":\"Project1 - P1P1\",\"planKey\":{\"key\":\"P1-P1P1\"}},\"planName\":\"P1P1\",\"projectName\":\"Project1\",\"buildResultKey\":\"P1-P1P1-200\",\"lifeCycleState\":\"InProgress\",\"id\":886043,\"buildStartedTime\":\"2017-07-21T19:58:32.406Z\",\"prettyBuildStartedTime\":\"Fri, 21 Jul, 07:58 PM\",\"buildDurationInSeconds\":0,\"buildDuration\":0,\"buildRelativeTime\":\"\",\"continuable\":false,\"onceOff\":false,\"restartable\":false,\"notRunYet\":false,\"finished\":false,\"successful\":false,\"buildReason\":\"Manual run by <a href=\\\"http://bamboo-example-server/browse/user/myuser\\\">My user</a>\",\"reasonSummary\":\"Manual run by <a href=\\\"http://bamboo-example-server/browse/user/myuser\\\">My user</a>\",\"artifacts\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"comments\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"labels\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"jiraIssues\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"stages\":{\"size\":3,\"start-index\":0,\"max-result\":3},\"changes\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"metadata\":{\"size\":2,\"start-index\":0,\"max-result\":2},\"progress\":{\"isValid\":true,\"isUnderAverageTime\":true,\"percentageCompleted\":0.016764521551096066,\"percentageCompletedPretty\":\"1%\",\"prettyTimeRemaining\":\"30 mins remaining\",\"prettyTimeRemainingLong\":\"Approximately 30 minutes remaining\",\"averageBuildDuration\":1846101,\"prettyAverageBuildDuration\":\"30 mins\",\"buildTime\":30949,\"prettyBuildTime\":\"30 secs\",\"startedTime\":\"21 Jul 2017, 7:58:32 PM\",\"startedTimeFormatted\":\"2017-07-21T19:58:32\",\"prettyStartedTime\":\"30 seconds ago\"},\"key\":\"P1-P1P1-200\",\"planResultKey\":{\"key\":\"P1-P1P1-200\",\"entityKey\":{\"key\":\"P1-P1P1\"},\"resultNumber\":200},\"state\":\"Unknown\",\"buildState\":\"Unknown\",\"number\":200,\"buildNumber\":200}";
//        Map<String, String> map = execution.getBuildStatus(getText);
//        assertEquals("InProgress", map.get("lifeCycleState"));
//        assertEquals("Unknown", map.get("state"));
//    }
//
//    @Test
//    public void getBuildStatusShouldReturnNullForInvalidInput() throws Exception {
//        setupExecutionTests();
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        String getText = "invalid input";
//        Map<String, String> map = execution.getBuildStatus(getText);
//        assertNull(map);
//    }
//
//    @Test
//    public void getBuildNumberShouldReturnNegativeOneOnInvalidInput() throws Exception {
//        setupExecutionTests();
//
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        String postText = "invalid input!";
//        int buildNumber = execution.getBuildNumber(postText);
//        assertEquals(-1, buildNumber);
//    }
//
//    @Test
//    public void getBuildNumberShouldReturnBuildNumberFromValidInput() throws Exception {
//        setupExecutionTests();
//
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        String postText = "{\"planKey\":\"PlanKey\",\"buildNumber\":199,\"buildResultKey\":\"ProjectName-PlanKey-199\",\"triggerReason\":\"Manual build\",\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/result/ProjectName-PlanKey-199\",\"rel\":\"self\"}}";
//        int buildNumber = execution.getBuildNumber(postText);
//        assertEquals(199, buildNumber);
//    }
//
//    @Test
//    public void stopTest() throws Exception {
//        setupExecutionTests();
//        Throwable throwable = mock(Throwable.class);
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.stop(throwable);
//        assertTrue(execution.getStopExecution());
//    }
//
//
//    public void setupRunTests(String state, String lifeCycleState) throws Exception {
//        setupExecutionTests();
//
//        String postText = "{\"planKey\":\"PlanKey\",\"buildNumber\":199,\"buildResultKey\":\"ProjectName-PlanKey-199\",\"triggerReason\":\"Manual build\",\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/result/ProjectName-PlanKey-199\",\"rel\":\"self\"}}";
//        String getText = "{\"expand\":\"changes,metadata,plan,artifacts,comments,labels,jiraIssues,stages\",\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/result/P1-P1P1-199\",\"rel\":\"self\"},\"plan\":{\"shortName\":\"P1P1\",\"shortKey\":\"P1P1\",\"type\":\"chain\",\"enabled\":true,\"link\":{\"href\":\"http://bamboo-example-server/rest/api/latest/plan/P1-P1P1\",\"rel\":\"self\"},\"key\":\"P1-P1P1\",\"name\":\"Project1 - P1P1\",\"planKey\":{\"key\":\"P1-P1P1\"}},\"planName\":\"P1P1\",\"projectName\":\"Project1\",\"buildResultKey\":\"P1-P1P1-200\",\"lifeCycleState\":\""+lifeCycleState+"\",\"id\":886043,\"buildStartedTime\":\"2017-07-21T19:58:32.406Z\",\"prettyBuildStartedTime\":\"Fri, 21 Jul, 07:58 PM\",\"buildDurationInSeconds\":0,\"buildDuration\":0,\"buildRelativeTime\":\"\",\"continuable\":false,\"onceOff\":false,\"restartable\":false,\"notRunYet\":false,\"finished\":false,\"successful\":false,\"buildReason\":\"Manual run by <a href=\\\"http://bamboo-example-server/browse/user/myuser\\\">My user</a>\",\"reasonSummary\":\"Manual run by <a href=\\\"http://bamboo-example-server/browse/user/myuser\\\">My user</a>\",\"artifacts\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"comments\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"labels\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"jiraIssues\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"stages\":{\"size\":3,\"start-index\":0,\"max-result\":3},\"changes\":{\"size\":0,\"start-index\":0,\"max-result\":0},\"metadata\":{\"size\":2,\"start-index\":0,\"max-result\":2},\"progress\":{\"isValid\":true,\"isUnderAverageTime\":true,\"percentageCompleted\":0.016764521551096066,\"percentageCompletedPretty\":\"1%\",\"prettyTimeRemaining\":\"30 mins remaining\",\"prettyTimeRemainingLong\":\"Approximately 30 minutes remaining\",\"averageBuildDuration\":1846101,\"prettyAverageBuildDuration\":\"30 mins\",\"buildTime\":30949,\"prettyBuildTime\":\"30 secs\",\"startedTime\":\"21 Jul 2017, 7:58:32 PM\",\"startedTimeFormatted\":\"2017-07-21T19:58:32\",\"prettyStartedTime\":\"30 seconds ago\"},\"key\":\"P1-P1P1-199\",\"planResultKey\":{\"key\":\"P1-P1P1-199\",\"entityKey\":{\"key\":\"P1-P1P1\"},\"resultNumber\":199},\"state\":\""+state+"\",\""+state+"\":\"Successful\",\"number\":199,\"buildNumber\":199}";
//        doReturn(postMethodMock).when(httpClientFactoryMock).getPostMethod(anyString());
//        doReturn(getMethodMock).when(httpClientFactoryMock).getGetMethod(anyString());
//        doReturn(200).when(httpClientMock).executeMethod(any(PostMethod.class));
//        doReturn(200).when(httpClientMock).executeMethod(any(GetMethod.class));
//        when(postMethodMock.getResponseBodyAsStream()).thenReturn(IOUtils.toInputStream(postText, "UTF-8"));
//        when(getMethodMock.getResponseBodyAsStream()).thenReturn(IOUtils.toInputStream(getText, "UTF-8"));
//
//        when(this.buildBambooStepMock.getUsername()).thenReturn("myuser");
//        when(this.buildBambooStepMock.getPassword()).thenReturn("mypass");
//        when(this.buildBambooStepMock.getProjectKey()).thenReturn("ProjectName");
//        when(this.buildBambooStepMock.getPlanKey()).thenReturn("PlanName");
//        when(this.buildBambooStepMock.getServerAddress()).thenReturn("ServerAddress");
//        when(this.buildBambooStepMock.getPropagate()).thenReturn(true);
//    }
//
//    @Test
//    public void verifyOnSuccessIsCalledOnSuccessfulRun() throws Exception {
//        setupRunTests("Successful", "Finished");
//
//        // Test
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        execution.run();
//        verify(execution.getContext()).onSuccess(anyString());
//    }
//
//    @Test
//    public void verifyOnFailureIsCalledOnSuccessfulRun() throws Exception {
//        setupRunTests("Failed", "Finished");
//
//        // Test
//        BuildBambooStep.Execution execution = new BuildBambooStep.Execution(this.buildBambooStepMock,
//                this.stepContextMock, this.httpClientFactoryMock);
//        execution.setLogger(mock(PrintStream.class));
//        execution.run();
//        verify(execution.getContext()).onFailure(any(Throwable.class));
//    }
//
//}
