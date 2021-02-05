package io.jenkins.plugins.carbonetes;

import hudson.model.Run;
import jenkins.model.Jenkins;
import jenkins.model.RunAction2;

public class CarbonetesAction implements RunAction2 {

	private Run<?, ?>	build;
	private String		policyEvaluationResult;
	private String		vulnerabilitiesResult;
	private String		scaResult;
	private String		malwareResult;
	private String		licenseFinderResult;
	private String		secretsAnalysisResult;
	private String		gateAction;
	private String		fullTag;
	private String		policyBundleId;

	public String getPolicyBundleId() {
		return policyBundleId;
	}

	public void setPolicyBundleId(String policyBundleId) {
		this.policyBundleId = policyBundleId;
	}

	public String getFullTag() {
		return fullTag;
	}

	public void setFullTag(String fullTag) {
		this.fullTag = fullTag;
	}

	public String getPolicyEvaluationResult() {
		return policyEvaluationResult.isEmpty() ? "null" : policyEvaluationResult;
	}

	public void setPolicyEvaluationResult(String policyEvaluationResult) {
		this.policyEvaluationResult = policyEvaluationResult;
	}

	public String getVulnerabilitiesResult() {
		return vulnerabilitiesResult.isEmpty() ? "null" : vulnerabilitiesResult;
	}

	public void setVulnerabilitiesResult(String vulnerabilitiesResult) {
		this.vulnerabilitiesResult = vulnerabilitiesResult;
	}

	public String getScaResult() {
		return scaResult.isEmpty() ? "null" : scaResult;
	}

	public void setScaResult(String scaResult) {
		this.scaResult = scaResult;
	}

	public String getMalwareResult() {
		return malwareResult.isEmpty() ? "null" : malwareResult;
	}

	public void setMalwareResult(String malwareResult) {
		this.malwareResult = malwareResult;
	}

	public String getLicenseFinderResult() {
		return licenseFinderResult.isEmpty() ? "null" : licenseFinderResult;
	}

	public void setLicenseFinderResult(String licenseFinderResult) {
		this.licenseFinderResult = licenseFinderResult;
	}

	public String getSecretsAnalysisResult() {
		return secretsAnalysisResult.isEmpty() ? "null" : secretsAnalysisResult;
	}

	public void setSecretsAnalysisResult(String secretsAnalysisResult) {
		this.secretsAnalysisResult = secretsAnalysisResult;
	}

	public String getGateAction() {
		return gateAction;
	}

	public void setGateAction(String gateAction) {
		this.gateAction = gateAction;
	}

	public Run<?, ?> getBuild() {
		return build;
	}

	public CarbonetesAction(Run<?, ?> build, String policyEvaluationResult, String vulnerabilitiesResult,
	        String scaResult, String malwareResult, String licenseFinderResult, String secretsAnalysisResult,
	        String gateAction, String fullTag, String policyBundleId) {
		super();
		this.build					= build;
		this.policyEvaluationResult	= policyEvaluationResult;
		this.vulnerabilitiesResult	= vulnerabilitiesResult;
		this.scaResult				= scaResult;
		this.malwareResult			= malwareResult;
		this.licenseFinderResult	= licenseFinderResult;
		this.secretsAnalysisResult	= secretsAnalysisResult;
		this.gateAction				= gateAction;
		this.fullTag				= fullTag;
		this.policyBundleId			= policyBundleId;
	}

	@Override
	public String getIconFileName() {
		return Jenkins.RESOURCE_PATH + Constants.PLUGIN_RESOURCE_PATH + "images/carbonetes-action.png";
	}

	@Override
	public String getDisplayName() {
		return "Carbonetes Report (" + getGateAction() + ")";
	}

	@Override
	public String getUrlName() {
		return "results";
	}

	@Override
	public void onAttached(Run<?, ?> build) {
		this.build = build;
	}

	@Override
	public void onLoad(Run<?, ?> build) {
		this.build = build;
	}
}
