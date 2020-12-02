package io.jenkins.plugins.carbonetes;

import hudson.AbortException;
import hudson.util.Secret;

/**
 * Contains Plugin Configuration Parameters
 */
public class Configuration {

	private String	name;
	private int		engineTimeout;
	private boolean	failBuildOnPluginError;
	private boolean	failBuildOnPolicyEvaluationFinallResult;
	private String	policyBundleID;
	private String	registryUri;
	private String	fulltag;
	private String	image;
	private Secret	secretPassword;
	private String	username;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImageName() throws AbortException {
		if (this.image.contains(":"))
			return this.image.split(":")[0];
		else if (this.failBuildOnPluginError) {
			throw new AbortException(
			        Constants.ERROR_MESSAGE + "Input image was not in expected format. {repository_name:image_tag}");
		} else {
			throw new AbortException(Constants.PLUGIN_ERROR_IGNORED
			        + "Input image was not in expected format. {repository_name:image_tag}");
		}
	}

	public String getFullTag() {
		return fulltag;
	}

	public void setFullTag(String fulltag) {
		this.fulltag = fulltag;
	}

	public String getTag() throws AbortException {
		if (this.image.contains(":"))
			return this.image.split(":")[1];
		else if (this.failBuildOnPluginError) {
			throw new AbortException(
			        Constants.ERROR_MESSAGE + "Input image was not in expected format. {repository_name:image_tag}");
		} else {
			throw new AbortException(Constants.PLUGIN_ERROR_IGNORED
			        + "Input image was not in expected format. {repository_name:image_tag}");
		}

	}

	public boolean isFailBuildOnPluginError() {
		return failBuildOnPluginError;
	}

	public boolean isFailBuildOnPolicyEvaluationFinalResult() {
		return failBuildOnPolicyEvaluationFinallResult;
	}

	public void setFailBuildOnPolicyEvaluationFinallResult(boolean failBuildOnPolicyEvaluationFinallResult) {
		this.failBuildOnPolicyEvaluationFinallResult = failBuildOnPolicyEvaluationFinallResult;
	}

	public String getPolicyBundleID() {
		return policyBundleID;
	}

	public void setPolicyBundleID(String policyBundleID) {
		this.policyBundleID = policyBundleID;
	}

	public String getRegistryUri() {
		return registryUri;
	}

	public void setRegistryUri(String registryUri) {
		this.registryUri = registryUri;
	}

	public void setFailBuildOnPluginError(boolean failBuildOnPluginError) {
		this.failBuildOnPluginError = failBuildOnPluginError;
	}

	public int getEngineTimeout() {
		return engineTimeout;
	}

	public void setEngineTimeout(int engineTimeout) {
		this.engineTimeout = engineTimeout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Secret getSecretPassword() {
		return secretPassword;
	}

	public void setSecretPassword(Secret secretPassword) {
		this.secretPassword = secretPassword;
	}

	public Configuration(String name, int engineTimeout, boolean failBuildOnPluginError,
	        boolean failBuildOnPolicyEvaluationFinallResult, String policyBundleID, String registryUri, String image) {
		this.name										= name;
		this.engineTimeout								= engineTimeout;
		this.failBuildOnPluginError						= failBuildOnPluginError;
		this.failBuildOnPolicyEvaluationFinallResult	= failBuildOnPolicyEvaluationFinallResult;
		this.policyBundleID								= policyBundleID;
		this.registryUri								= registryUri;
		this.image										= image;
	}

}
