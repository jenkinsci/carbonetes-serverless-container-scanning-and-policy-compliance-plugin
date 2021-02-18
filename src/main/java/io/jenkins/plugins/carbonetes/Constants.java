package io.jenkins.plugins.carbonetes;

/**
 * 
 * Common plugin constants
 * 
 * @author carbonetes
 *
 */
public class Constants {

	// ERROR MESSAGE
	public static final String	ERROR_MESSAGE					= "Carbonetes-plugin marked the build as failed due to error(s). \n";
	public static final String	ERROR_ON_FAIL_POLICY_EVALUATION	= "Carbonetes-plugin marked the build as failed due to Policy Evaluation Failed result.";
	public static final String	POLICY_EVALUATION_IGNORED		= "Policy evaluation result was ignored.";
	public static final String	PLUGIN_ERROR_IGNORED			= "Plugin error result was ignored. \n";

	// API END POINTS
	public static final String	CARBONETES_ENDPOINT_ANALYZE		= "https://api.carbonetes.com/api/v1/analysis/analyze";
	public static final String	CARBONETES_ENDPOINT_GET_RESULT	= "https://api.carbonetes.com/api/v1/analysis/get-result";

	// JSON Fields
	public static final String	JSON_FIELD_REGISTRY_URI			= "registryUri";
	public static final String	JSON_FIELD_REPO_IMAGE_TAG		= "repoImageTag";
	public static final String	JSON_FIELD_USERNAME				= "username";
	public static final String	JSON_FIELD_PASSWORD				= "password";
	public static final String	JSON_FIELD_TIMEOUT				= "timeout";
	public static final String	JSON_FIELD_POlICY_BUNDlE_UUID	= "policyBundleUUID";
	public static final String	JSON_FIELD_POLICY_BUNDLE		= "policyBundle";
	public static final String	JSON_FIELD_REPO_IMAGE_ENV		= "repoImageEnvironments";
	public static final String	JSON_FIELD_POLICY_EVALUATION	= "policyEvaluationLatest";
	public static final String	JSON_FIELD_IMAGE_ANALYSIS		= "imageAnalysisLatest";
	public static final String	JSON_FIELD_SOFTWARE_COMPOSITION	= "scaLatest";
	public static final String	JSON_FIELD_MALWARE				= "malwareAnalysisLatest";
	public static final String	JSON_FIELD_LICENSE_FINDER		= "licenseFinderLatest";
	public static final String	JSON_FIELD_SECRETS				= "secretAnalysisLatest";
	public static final String  JSON_FIELD_BOM					= "billOfMaterialsAnalysisLatest";
	public static final String	JSON_FIELD_POLICY_RESULT		= "policyResult";
	public static final String	JSON_FIELD_FINAL_ACTION			= "finalAction";

	public static final String	PLUGIN_RESOURCE_PATH			= "/plugin/carbonetes-serverless-container-scanning-and-policy-compliance/";
	public static final int		STATUS_CODE_SUCCESS				= 200;

	// Analysis Status
	public static final String	ANALYSIS_STATUS_FAILED			= "failed";
	public static final String	ANALYSIS_STATUS_FAIL			= "fail";
	public static final String	JSON_CONTENT_TYPE				= "application/json";
	// Readable error messages from non successful responses
	public static final String  LICENSE_EXPIRED				    = "License has already expired";
	public static final String  LICENSE_EXPIRED_MESSAGE 		= "Carbonetes License Expired. \n Your contract licenses has expired. Please configure or renew your AWS Contract Licenses to successfully login your account.";
	public static final String  INSUFFICIENT_LICENSE 			= "Insufficent license count";
	public static final String  INSUFFICIENT_LICENSE_MESSAGE 	= "Insufficient Carbonetes License(s). \n Your contract license is insufficient for your current users. Please configure your AWS Contract Licenses to successfully login your account.";
	public static final String  ACCOUNT_CONFLICT  				= "Account not found";
	public static final String  ACCOUNT_CONFLICT_MESSAGE  		= "AWS Customer ID Conflict. \n Your Customer ID is associated to a different company. Please check your account or contact your Administrator to successfully save or update your AWS Contract Licenses.";
	public static final String  SEPARATOR					    = "/";
}
