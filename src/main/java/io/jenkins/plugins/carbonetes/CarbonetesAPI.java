package io.jenkins.plugins.carbonetes;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Strings;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import hudson.AbortException;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;

/**
 * @author carbonetes
 *
 */
public class CarbonetesAPI extends AbstractAPIWorker {

	private static final Logger logger = Logger.getLogger(CarbonetesAPI.class.getName());

	// Private Fields
	private ObjectMapper mapper = new ObjectMapper();
	private Action finalAction;
	private Action policyResult;
	private JsonNode completeAnalysisResponse;
	private String defaultBundleUUID;
	private boolean resultLoaded;
	private String	bundleName;
	private static final String REQUEST_RETURNED = "Request Returned : ";
	private static final String REQUEST_URI = "Request URI: ";
	private static final String MESSAGE_STRING = "Message: ";

	// End Private Fields

	// Getters & Setters

	public String getBundleName() {
		return this.bundleName;
	}

	public String getDefaultBundleUUID() {
		return defaultBundleUUID;
	}

	public Action getFinalAction() {
		return finalAction;
	}

	public Action getPolicyResult() {
		return policyResult;
	}

	public String getFullTag() {
		return configuration.getRegistryUri() + Constants.SEPARATOR + configuration.getImage();
	}

	public JsonNode getCompleteAnalysisResponse() {
		return completeAnalysisResponse;
	}

	public String getPolicyEvaluationResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_REPO_IMAGE_ENV)
				.findPath(Constants.JSON_FIELD_POLICY_EVALUATION).toString();
	}

	public String getVulnerabilitiesResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_IMAGE_ANALYSIS).toString();
	}

	public String getSCAResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_SOFTWARE_COMPOSITION).toString();
	}

	public String getMalwareResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_MALWARE).toString();
	}

	public String getLicenseFinderResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_LICENSE_FINDER).toString();
	}

	public String getSecretsAnalysisResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_SECRETS).toString();
	}

	public String getBillOfMaterialsResult() {
		return completeAnalysisResponse.findPath(Constants.JSON_FIELD_BOM).toString();
	}

	public String getGateAction() {
		String gateAction = completeAnalysisResponse.findPath(Constants.JSON_FIELD_REPO_IMAGE_ENV)
				.findPath(Constants.JSON_FIELD_POLICY_EVALUATION).findPath(Constants.JSON_FIELD_FINAL_ACTION).asText();
		return gateAction.isEmpty() ? "ERROR" : gateAction;
	}

	public boolean isResultLoaded() {
		return resultLoaded;
	}
	// End Getters & Setters

	public CarbonetesAPI(TaskListener listener, Configuration configuration) {
		this.listener = listener;
		this.configuration = configuration;
	}

	public void initialize() {
		this.initializeAPICall();
	}

	/**
	 * Performs Comprehensive Analysis
	 * 
	 * @throws IOException when the API call encounters problem or the connection was aborted.
	 * @throws AbortException to mark the build as failed based on the configuration.
	 */
	public void performComprehensiveAnalysis() throws IOException, AbortException {

		listener.getLogger().println("Performing Comprehensive Analysis...");

		JSONObject jsonBody = new JSONObject();

		jsonBody.put(Constants.JSON_FIELD_REGISTRY_URI, configuration.getRegistryUri());
		jsonBody.put(Constants.JSON_FIELD_REPO_IMAGE_TAG, configuration.getImage());
		jsonBody.put(Constants.JSON_FIELD_USERNAME, configuration.getUsername());
		jsonBody.put(Constants.JSON_FIELD_PASSWORD, configuration.getSecretPassword().getPlainText());
		jsonBody.put(Constants.JSON_FIELD_TIMEOUT, configuration.getEngineTimeout());
		jsonBody.put(Constants.JSON_FIELD_POLICY_BUNDLE_UUID, configuration.getPolicyBundleID());

		String url = Constants.CARBONETES_ENDPOINT_ANALYZE;
		httpPost = new HttpPost(url);

		String			body	= jsonBody.toString();
		StringEntity	content	= new StringEntity(body);

		content.setContentType(Constants.JSON_CONTENT_TYPE);
		httpPost.setEntity(content);

		response		= httpclient.execute(httpPost, context);
		statusCode		= response.getStatusLine().getStatusCode();
		responseBody	= EntityUtils.toString(response.getEntity());
		
		if (statusCode == Constants.STATUS_CODE_SUCCESS) {
			JsonNode requestBodyForResultRequest = mapper.readTree(responseBody);
			getResult(requestBodyForResultRequest);
		} else {
			this.setErrorMessageFromResponse();
			if (configuration.isFailBuildOnPluginError()) {
				throw new AbortException(Constants.ERROR_MESSAGE 
						+ REQUEST_RETURNED 	 + response.getStatusLine().getStatusCode() 
						+ " " +  response.getStatusLine().getReasonPhrase()
				        + "\n" + REQUEST_URI + httpPost.getURI() 
				        + "\n" + MESSAGE_STRING + message);
			} else {
				throw new AbortException(Constants.PLUGIN_ERROR_IGNORED 
						+ REQUEST_RETURNED 	 + response.getStatusLine().getStatusCode() 
						+ " " +  response.getStatusLine().getReasonPhrase()
				        + "\n" + REQUEST_URI + httpPost.getURI()  
				        + "\n" + MESSAGE_STRING + message);
			}
		}

		listener.getLogger().println("Analysis Finished");
		listener.getLogger().println("Policy Evaluation Result");

		if (Strings.isNullOrEmpty(configuration.getPolicyBundleID())) {
			defaultBundleUUID = completeAnalysisResponse.findPath(Constants.JSON_FIELD_POLICY_BUNDLE)
			        .findPath(Constants.JSON_FIELD_POLICY_BUNDLE_UUID).asText();
		}

		ArrayNode policyEvaluationResult = mapper.readValue(
		        completeAnalysisResponse.findPath(Constants.JSON_FIELD_REPO_IMAGE_ENV).toString(), ArrayNode.class);

				
		
		policyResult	= Action.fromName(policyEvaluationResult.findPath(Constants.JSON_FIELD_POLICY_EVALUATION)
		        .findPath(Constants.JSON_FIELD_POLICY_RESULT).asText());
		finalAction		= Action.fromName(policyEvaluationResult.findPath(Constants.JSON_FIELD_POLICY_EVALUATION)
		        .findPath(Constants.JSON_FIELD_FINAL_ACTION).asText());

		bundleName = policyEvaluationResult.findPath(Constants.JSON_FIELD_POLICY_BUNDLE).findPath(Constants.JSON_FIELD_BUNDLE_NAME).asText();	
		listener.getLogger().println("Policy Result : " + policyResult);
		listener.getLogger().println("Final Action : " + finalAction);

		resultLoaded = true;
		
		if (configuration.isFailBuildOnPolicyEvaluationFinalResult()
		        && (policyResult.toString().equalsIgnoreCase(Constants.ANALYSIS_STATUS_FAILED)
		                || policyResult.toString().equalsIgnoreCase(Constants.ANALYSIS_STATUS_FAIL))) {
			throw new AbortException(Constants.ERROR_ON_FAIL_POLICY_EVALUATION);
		} else {
			if (policyResult.toString().equalsIgnoreCase(Constants.ANALYSIS_STATUS_FAILED)
			        || policyResult.toString().equalsIgnoreCase(Constants.ANALYSIS_STATUS_FAIL)) {
				throw new AbortException(Constants.POLICY_EVALUATION_IGNORED);
			}
		}

	}

	/**
	 * Get Comprehensive Analysis Result
	 * 
	 * @param checkerData
	 * @throws AbortException
	 */
	private void getResult(JsonNode checkerData) throws AbortException {

		try {

			String url = Constants.CARBONETES_ENDPOINT_GET_RESULT;
			httpPost = new HttpPost(url);
			String			body	= checkerData.toString();
			StringEntity	content	= new StringEntity(body);
			content.setContentType(Constants.JSON_CONTENT_TYPE);
			httpPost.setEntity(content);

			response		= httpclient.execute(httpPost, context);
			statusCode		= response.getStatusLine().getStatusCode();
			responseBody	= EntityUtils.toString(response.getEntity());

			if (statusCode == Constants.STATUS_CODE_SUCCESS) {
				completeAnalysisResponse = mapper.readTree(responseBody);
			} else {
				this.setErrorMessageFromResponse();
				if (configuration.isFailBuildOnPluginError()) {
					throw new AbortException(Constants.ERROR_MESSAGE 
						+ REQUEST_RETURNED 	 + response.getStatusLine().getStatusCode() 
						+ " " +  response.getStatusLine().getReasonPhrase()
						+ "\n" + REQUEST_URI + httpPost.getURI() 
						+ "\n" + MESSAGE_STRING + message);
				} else {
					throw new AbortException(Constants.PLUGIN_ERROR_IGNORED 
						+ REQUEST_RETURNED 	 + response.getStatusLine().getStatusCode() 
						+ " " +  response.getStatusLine().getReasonPhrase()
				        + "\n" + REQUEST_URI + httpPost.getURI()  
				        + "\n" + MESSAGE_STRING + message);
				}
			}

			// URL url = new URL(Constants.CARBONETES_ENDPOINT_GET_RESULT);
			// HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// connection.setRequestProperty("accept", "application/json");
			// connection.setRequestProperty("Content-Type", "application/json");
			// connection.setRequestMethod("POST");
			// connection.setDoOutput(true);
			// OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			// writer.write(checkerData.toString());
			// writer.flush();
			// connection.getOutputStream().close();
			// writer.close();
			// connection.connect();

			//  // This line makes the request
			//  InputStream responseStream = connection.getInputStream();

			//  // Manually converting the response body InputStream to APOD using Jackson
			//  ObjectMapper mapper = new ObjectMapper();
			//  JsonNode response = mapper.readValue(responseStream, JsonNode.class);

			// String result;
			// BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			// ByteArrayOutputStream buf = new ByteArrayOutputStream();
			// int result2 = bis.read();
			// while(result2 != -1) {
			// 	buf.write((byte) result2);
			// 	result2 = bis.read();
			// }
			// result = buf.toString();
			// completeAnalysisResponse = mapper.readTree(result);
		} catch (IOException e) {

			logger.severe(e.getMessage());

			if (configuration.isFailBuildOnPluginError()) {
				throw new AbortException(Constants.ERROR_MESSAGE + e.toString());
			} else {
				throw new AbortException(Constants.PLUGIN_ERROR_IGNORED + e.toString());
			}

		}

	}
}
