package io.jenkins.plugins.carbonetes;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import hudson.model.TaskListener;

/**
 * Contains REST API Setups
 */
public abstract class AbstractAPIWorker {

	protected TaskListener			listener;
	protected HttpClientContext		context;
	protected CloseableHttpResponse	response;
	protected int					statusCode;
	protected String				responseBody;
	protected CloseableHttpClient	httpclient;
	protected Configuration			configuration;
	protected HttpPost				httpPost;

	/**
	 * Initialization for REST API call
	 * 
	 */
	protected void initializeAPICall() {
		httpclient	= HttpClients.custom()
		        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
		context		= HttpClientContext.create();
	}
}
