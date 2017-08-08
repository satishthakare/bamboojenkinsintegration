package com.getgo.jenkins.plugins.pipeline.bamboo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getgo.jenkins.plugins.pipeline.bamboo.exceptions.BambooException;
import hudson.Extension;
import hudson.model.TaskListener;
import jenkins.model.CauseOfInterruption;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 * A Bamboo pipeline step plugin.  Start a Bamboo job from with your pipeline.
 *
 * See {@link BuildBambooStep.Execution#run} method.
 *
 * @author Kyle Flavin
 */
public class BuildBambooStep extends Step {
    private String projectKey;
    private String planKey;
    private String serverAddress;
    private String username;
    private String password;
    private boolean propagate;
    private int checkInterval;

    /**
     * DataBoundConstructor will set propagate to "true" by default.  Parameters given in the constructor,
     * with no setters, are required.
     *
     * @param projectKey: Bamboo project key.  used to construct the job: "projectKey-planKey"
     * @param planKey: Bamboo plan key.  used to construct the job: "planKey-planKey"
     * @param serverAddress: Server address, prefix with protocol.  Example: https://bamboo-server.example.org
     * @param username: Bamboo API user
     * @param password: Bamboo API password
     */
    @DataBoundConstructor
    public BuildBambooStep(String projectKey,
                           String planKey,
                           String serverAddress,
                           String username,
                           String password) {

        this.projectKey = projectKey;
        this.planKey = planKey;
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
        this.propagate = true;
        this.checkInterval = 30;
    }

    /**
     * Project name getter
     * @return String that is the project key. Combined with plan to form the job name.
     */
    public String getProjectKey() {
        return projectKey;
    }

    /**
     * Plan name getter
     * @return String that is the plan key.  Combined with project to form the job name.
     */
    public String getPlanKey() {
        return planKey;
    }

    /**
     * Server address getter
     * @return String that is the Bamboo server address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Username getter
     * @return String that is the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Password getter
     * @return String that is the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Propagate getter
     * @return true if propagation is on (default), else false
     */
    public boolean getPropagate() {
        return propagate;
    }

    /**
     * Set error propagation.
     * @param propagate: true (default) to propagate errors, or false to not propagate.  By default, if there is an
     *                 error with the Bamboo build, it will fail the entire pipeline.
     */
    @DataBoundSetter
    public void setPropagate(boolean propagate) {
        this.propagate = propagate;
    }

    /**
     * The time to wait between HTTP checks, while polling the target server.
     * @param checkInterval: set time in seconds.
     */
    @DataBoundSetter
    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * checkInterval getter
     * @return return checkInterval time in seconds
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * Starts execution of Execution.
     * @param stepContext Step Context
     * @return StepExecution
     * @throws Exception Throws Exception
     */
    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(this, stepContext, new HttpClientFactory());
    }

    /**
     * Holds class metadata, including step name.
     */
    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        /**
         * Returns the required context for the step.
         * @return Set of required classes.
         */
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

        /**
         * This returns the step named used in the Jenkinsfile
         * @return String representing the name of the step, as used in the Jenkinsfile.
         */
        @Override
        public String getFunctionName() {
            return "buildBamboo";
        }

        /**
         * Step display name.
         * @return String representing the display name.
         */
        @Override
        public String getDisplayName() {
            return "Build Bamboo";
        }
    }

    /**
     * The execution to run.  We use a {@link SynchronousNonBlockingStepExecution}, so as not to block the main thread.
     * This will still block an executor on an agent, and should be run with agent set to "none" in your pipeline
     * script.
     */
    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private transient BuildBambooStep step;
        private volatile boolean stopExecution = false;
        private static final long serialVersionUID = 1L;
        private transient PrintStream logger;
        private transient HttpClientFactory httpClientFactory;

        /**
         * Constructor for the execution, invoked by {@link BuildBambooStep#start}
         * @param step BuildBambooStep object.  "this" should be passed in {@link BuildBambooStep#start}
         * @param context StepContext
         * @param httpClientFactory Factory for creating HTTP clients.
         * @throws InterruptedException Throws InterruptedException
         * @throws IOException Throws IOException
         */
        public Execution(BuildBambooStep step,
                         @Nonnull StepContext context,
                         HttpClientFactory httpClientFactory) throws InterruptedException, IOException {
            super(context);
            this.step = step;
            this.logger = getContext().get(TaskListener.class).getLogger();
            this.httpClientFactory = httpClientFactory;
        }

        /**
         * Step getter
         * @return Returns a BuildBambooStep
         */
        public BuildBambooStep getStep() {
            return step;
        }

        /**
         * stopExecution getter.  This is set by {@link BuildBambooStep.Execution#stop} to exit the polling loop.
         * @return false to continue polling, true to halt execution.
         */
        public boolean getStopExecution() {
            return stopExecution;
        }

        /**
         * HttpClientFactory getter
         * @return An HttpClientFactory
         */
        public HttpClientFactory getHttpClientFactory() {
            return httpClientFactory;
        }

        /**
         * logger getter
         * @return A PrintStream logger
         */
        public PrintStream getLogger() {
            return logger;
        }

        /**
         * logger setter
         * @param logger A PrintStream to log to.
         */
        public void setLogger(PrintStream logger) {
            this.logger = logger;
        }

        /**
         * POST data to the server.
         *
         * @param url Bamboo target server URL.  Example: http://bamboo-server.example.org
         * @param username Bamboo API username
         * @param password Bamboo API password
         * @param postData Currently unused
         * @return String of the returned JSON
         */
        public String post(String url, String username, String password, String postData) {
            String result = "";

            HttpClient client = this.httpClientFactory.getHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, credentials);

            PostMethod post = httpClientFactory.getPostMethod(url);
            try {
                int status = client.executeMethod(post);

                this.logger.printf("POST Status Code: %d, URL: %s\n", status, url);
                InputStream in = post.getResponseBodyAsStream();
                result = IOUtils.toString(in, StandardCharsets.UTF_8);
            } catch(IOException e) {
                this.logger.println("Could not execute POST due to IOException.");
            } finally {
                post.releaseConnection();
            }

            this.logger.print("POST result: ");
            this.logger.println(result);
            return result;
        }

        /**
         * GET data from the server.  Used to monitor build status.
         *
         * @param url Bamboo target server URL.  Example: http://bamboo-server.example.org
         * @param username Bamboo API username
         * @param password Bamboo API password
         * @return Returns JSON string from Bamboo server
         */
        public String get(String url, String username, String password) {
            String result = "";

            HttpClient client = this.httpClientFactory.getHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, credentials);

            GetMethod get = httpClientFactory.getGetMethod(url);
            try {
                int status = client.executeMethod(get);
                this.logger.printf("GET Status Code: %d, URL: %s\n", status, url);
                InputStream in = get.getResponseBodyAsStream();
                result = IOUtils.toString(in, StandardCharsets.UTF_8);
            } catch(IOException e) {
                this.logger.println("Could not complete GET due to IOException");
            } finally {
                get.releaseConnection();
            }
            return result;
        }

        /**
         * Get the build number after we start the job.
         * @param text POST data returned from Bamboo server after initiating build.
         * @return The build number as an int.
         */
        public int getBuildNumber(String text) {
            int result = -1;
            ObjectMapper om = new ObjectMapper();
            try {
                JsonNode node = om.readValue(text, JsonNode.class);

                if (node.has("buildNumber")) {
                    result = node.get("buildNumber").asInt();
                } else {
                    this.logger.println("Could not get build number.  Is the job already running?");
                    return result;
                }
            } catch (IOException e) {
                this.logger.println("Failed to read build number.");
                this.logger.println(e);
            }
            return result;
        }

        /**
         * Get the job state and lifecycle state of a Bamboo job.
         *
         * @param text JSON formatted text, returned by Bamboo.
         * @return A map with the "state" and "lifeCycleState".
         */
        public Map<String, String> getBuildStatus(String text) {
            Map<String, String> result = new HashMap<>();
            ObjectMapper om = new ObjectMapper();
            try {
                JsonNode node = om.readValue(text, JsonNode.class);
                result.put("state", node.get("state").asText());
                result.put("lifeCycleState", node.get("lifeCycleState").asText());
            } catch (IOException e) {
                this.logger.println("Could not get build status due to IOException.");
                return null;
            }
            return result;
        }

        /**
         * Perform the actual work.  Start the job on Bamboo server, and then poll it until complete.
         * @return Returns null.
         */
        @Override
        public Void run() throws Exception {
            final String username = this.step.getUsername();
            final String password = this.step.getPassword();
            final String projectKey = this.step.getProjectKey();
            final String planKey = this.step.getPlanKey();
            final String serverAddress = this.step.getServerAddress();
            final boolean propagate = this.step.getPropagate();
            final int checkInterval = this.step.getCheckInterval();
            final String postUrl = serverAddress + "/rest/api/latest/queue/" + projectKey + "-" +
                    planKey + ".json?stage&executeAllStages&os_authType=basic";

            this.logger.println("propagate=" + propagate);
            this.logger.println("checkInterval=" + checkInterval);

            this.logger.println("postURL has been constructed as: " + postUrl);

            // Start the Bamboo job.
            String postText = post(postUrl, username, password, "");
            int buildNumber = getBuildNumber(postText);
            // If the build is already running, either fail or continue the pipeline depending on "propagate" value.
            if (buildNumber == -1) {
                if (propagate) {
                    this.getContext().onFailure(new BambooException("Failed to get build number.  Check authentication and make sure there aren't multiple builds running."));
                    return null;
                } else {
                    this.getContext().onSuccess("Bamboo job appears to already be running.  Continuing...");
                    return null;
                }
            }

            // Use the getUrl to check the build status
            String getUrl = serverAddress + "/rest/api/latest/result/" + projectKey + "-" +
                    planKey + "/" + buildNumber + ".json?os_authType=basic";

            String getText, lifeCycleState, buildState="";

            try {

                Map<String, String> result;
                while (!stopExecution) {
                    getText = get(getUrl, username, password);
                    result = getBuildStatus(getText);
                    if (result == null) {
                        this.logger.println("Failed to get build status, trying again...");
                        Thread.sleep(checkInterval);
                        continue;
                    }
                    buildState = result.get("state");
                    lifeCycleState = result.get("lifeCycleState");

                    if (lifeCycleState.equals("Finished")) {
                        break;
                    }
                    Thread.sleep(checkInterval);
                }
            } catch (InterruptedException e) {
                this.getContext().onFailure(e);
                return null;
            }

            if (buildState.equals("Successful")) {
                this.logger.println("Build number " + buildNumber + " successful.");
                this.getContext().onSuccess("Build number " +  buildNumber + " successful.");
            } else {
                String message;
                if (stopExecution) {
                    message = "Bamboo build number " + buildNumber + " was aborted or timed out.";
                    if (propagate) {
                        this.logger.println(message);
                        this.getContext().onFailure(new BambooException(message));
                    } else {
                        this.logger.println(message + " : proceeding with pipeline");
                        this.getContext().onSuccess(message + " : proceeding with pipeline...");
                    }
                } else {
                    message = "Bamboo build failed. Build number: " + buildNumber;
                    if (propagate) {
                        this.logger.println(message);
                        this.getContext().onFailure(new BambooException(message));
                    } else {
                        this.logger.println(message + " : proceeding with pipeline");
                        this.getContext().onSuccess(message + " : proceeding with pipeline...");
                    }
                }
            }

            return null;
        }

        /**
         * Handles cleanup.  If interrupted by a timeout, or user abort, this method is called.
         * Sets {@link BuildBambooStep.Execution#stopExecution}
         * to exit the poll loop in {@link BuildBambooStep.Execution#run}
         * @param throwable Contains the reason for stopping.
         */
        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {
            this.stopExecution = true;
            this.logger.println("Halting Bamboo step: please wait...");
            if (throwable instanceof FlowInterruptedException) {
                for(CauseOfInterruption c: ((FlowInterruptedException)throwable).getCauses()) {
                    this.logger.println("Cause: " + c.getShortDescription());
                }
            } else {
                this.logger.println("Cause: unknown");
            }
        }
    }
}

