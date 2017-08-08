package com.getgo.jenkins.plugins.pipeline.bamboo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * HttpClient factory class to make testing easier.
 *
 * @author Kyle Flavin
 */
public class HttpClientFactory {

    /**
     * Return an HttpClient
     * @return HttpClient object
     */
    public HttpClient getHttpClient() {
        return new HttpClient();
    }

    /**
     * Return a PostMethod
     * @return PostMethod object
     */
    public PostMethod getPostMethod(String url) {
        return new PostMethod(url);
    }

    /**
     * Return a GetMethod
     * @return GetMethod object
     */
    public GetMethod getGetMethod(String url) {
        return new GetMethod(url);
    }
}
