package io.jenkins.plugins.carbonetes;

import java.util.Collections;
import java.util.logging.Logger;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.google.common.base.Strings;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;

/**
 * @author carbonetes
 *
 */
public class CarbonetesBuilder extends Builder implements SimpleBuildStep {

	private static final Logger	logger	= Logger.getLogger(CarbonetesBuilder.class.getName());

	private String				name;
	private String				username;
	private String				password;
	private int					engineTimeout;
	private boolean				failBuildOnPluginError;
	private boolean				failBuildOnPolicyEvaluationFailResult;
	private String				policyBundleID;
	private String				registryURI;
	private String				image;
	private String				credentialsId;

	// Setters are used by corresponding jelly script of this class
	public String getCredentialsId() {
		return credentialsId;
	}

	@DataBoundSetter
	public void setCredentialsId(String credentialsId) {
		this.credentialsId = credentialsId;
	}

	public String getRegistryURI() {
		return registryURI;
	}

	@DataBoundSetter
	public void setRegistryURI(String registryURI) {
		this.registryURI = registryURI;
	}

	public boolean isFailBuildOnPluginError() {
		return failBuildOnPluginError;
	}

	@DataBoundSetter
	public void setFailBuildOnPluginError(boolean failBuildOnPluginError) {
		this.failBuildOnPluginError = failBuildOnPluginError;
	}

	public boolean isFailBuildOnPolicyEvaluationFailResult() {
		return failBuildOnPolicyEvaluationFailResult;
	}

	@DataBoundSetter
	public void setFailBuildOnPolicyEvaluationFailResult(boolean failBuildOnPolicyEvaluationFailResult) {
		this.failBuildOnPolicyEvaluationFailResult = failBuildOnPolicyEvaluationFailResult;
	}

	public String getPolicyBundleID() {
		return policyBundleID;
	}

	@DataBoundSetter
	public void setPolicyBundleID(String policyBundleID) {
		this.policyBundleID = policyBundleID;
	}

	public int getEngineTimeout() {
		return engineTimeout;
	}

	@DataBoundSetter
	public void setEngineTimeout(int engineTimeout) {
		this.engineTimeout = engineTimeout;
	}

	public String getUsername() {
		return username;
	}

	@DataBoundSetter
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	@DataBoundSetter
	public void setPassword(String password) {
		this.password = password;
	}

	@DataBoundSetter
	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	// Required by Jenkins and is used by config.jelly as well as Getters and
	// Setters
	@DataBoundConstructor
	public CarbonetesBuilder(String name) {
		this.name = name;
	}

	// Execution of plug-in build step
	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
	        throws AbortException {

		Configuration		configuration		= new Configuration(name, username, password, engineTimeout,
		        failBuildOnPluginError, failBuildOnPolicyEvaluationFailResult, policyBundleID, registryURI, image);

		CarbonetesAction	carbonetesAction	= null;

		CarbonetesAPI		carbonetesAPI		= null;

		try {

			listener.getLogger().println("Engine Timeout: " + configuration.getEngineTimeout());
			listener.getLogger().println("Fail Build on Plugin Error: " + configuration.isFailBuildOnPluginError());
			listener.getLogger().println("Fail Build on Policy Evaluation Failed Result: "
			        + configuration.isFailBuildOnPolicyEvaluationFinalResult());
			listener.getLogger().println("Registry URI: " + configuration.getRegistryUri());
			listener.getLogger().println("Image Name: " + configuration.getImageName());
			listener.getLogger().println("Image Tag: " + configuration.getTag());

		} catch (AbortException e) {
			if (configuration.isFailBuildOnPluginError()) {
				throw e;
			} else {
				listener.getLogger().println(e.getMessage());
				return;
			}
		}

		if (!Strings.isNullOrEmpty(credentialsId)) {
			try {

				StandardUsernamePasswordCredentials creds = CredentialsProvider.findCredentialById(credentialsId,
				        StandardUsernamePasswordCredentials.class, run, Collections.<DomainRequirement>emptyList());

				if (null != creds) {
					configuration.setUsername(creds.getUsername());
					configuration.setPassword(creds.getPassword().getPlainText());
				} else {
					if (failBuildOnPluginError) {
						throw new AbortException(Constants.ERROR_MESSAGE + "Cannot find Jenkins credentials by ID: \'"
						        + credentialsId + "\'. Ensure credentials are defined in Jenkins before using them");
					} else {
						listener.getLogger()
						        .println(Constants.PLUGIN_ERROR_IGNORED + "Cannot find Jenkins credentials by ID: \'"
						                + credentialsId
						                + "\'. Ensure credentials are defined in Jenkins before using them");
						return;
					}
				}
			} catch (AbortException e) {
				logger.severe(e.getMessage());
				if (failBuildOnPluginError) {
					throw e;
				} else {
					listener.getLogger().println(e.getMessage());
					return;
				}

			} catch (Exception e) {
				logger.severe(e.getMessage());

				if (failBuildOnPluginError) {
					throw new AbortException(
					        Constants.ERROR_MESSAGE + "Error looking up Jenkins credentials by ID: \'" + credentialsId);
				} else {
					listener.getLogger().println(Constants.PLUGIN_ERROR_IGNORED + e.getMessage());
					return;
				}

			}
		} else {
			if (failBuildOnPluginError) {
				throw new AbortException(Constants.ERROR_MESSAGE + "Missing required parameter:  {credentialsId}");
			} else {
				listener.getLogger()
				        .println(Constants.PLUGIN_ERROR_IGNORED + "Missing required parameter:  {credentialsId}");
			}
		}

		try {

			if (Strings.isNullOrEmpty(configuration.getImage())) {
				if (failBuildOnPluginError) {
					throw new AbortException(Constants.ERROR_MESSAGE + "Missing Required Parameter: ${image}");
				} else {
					listener.getLogger()
					        .println(Constants.PLUGIN_ERROR_IGNORED + "Missing Required Parameter: ${image}");
					return;
				}
			}
			if (Strings.isNullOrEmpty(configuration.getRegistryUri())) {
				if (failBuildOnPluginError) {
					throw new AbortException(Constants.ERROR_MESSAGE + "Missing Required Parameter: ${registryUri}");
				} else {
					listener.getLogger()
					        .println(Constants.PLUGIN_ERROR_IGNORED + "Missing Required Parameter: ${image}");
					return;
				}
			}

			carbonetesAPI = new CarbonetesAPI(listener, configuration);

			carbonetesAPI.initialize();

			carbonetesAPI.performComprehensiveAnalysis();

			carbonetesAction = new CarbonetesAction(run, carbonetesAPI.getPolicyEvaluationResult(),
			        carbonetesAPI.getVulnerabilitiesResult(), carbonetesAPI.getSCAResult(),
			        carbonetesAPI.getMalwareResult(), carbonetesAPI.getLicenseFinderResult(),
			        carbonetesAPI.getSecretsAnalysisResult(), carbonetesAPI.getGateAction(), carbonetesAPI.getFullTag(),
			        Strings.isNullOrEmpty(configuration.getPolicyBundleID()) ? carbonetesAPI.getDefaultBundleUUID()
			                : configuration.getPolicyBundleID());

			run.addAction(carbonetesAction);

		} catch (Exception e) {

			logger.severe(e.getMessage());

			if (carbonetesAPI.isResultLoaded()) {

				carbonetesAction = new CarbonetesAction(run, carbonetesAPI.getPolicyEvaluationResult(),
				        carbonetesAPI.getVulnerabilitiesResult(), carbonetesAPI.getSCAResult(),
				        carbonetesAPI.getMalwareResult(), carbonetesAPI.getLicenseFinderResult(),
				        carbonetesAPI.getSecretsAnalysisResult(), carbonetesAPI.getGateAction(),
				        carbonetesAPI.getFullTag(),
				        Strings.isNullOrEmpty(configuration.getPolicyBundleID()) ? carbonetesAPI.getDefaultBundleUUID()
				                : configuration.getPolicyBundleID());

				run.addAction(carbonetesAction);
			}

			if (e.getMessage().contains(Constants.PLUGIN_ERROR_IGNORED)
			        || e.getMessage().contains(Constants.POLICY_EVALUATION_IGNORED)) {
				listener.getLogger().println(e.getMessage());
				return;
			}

			if (configuration.isFailBuildOnPluginError())
				throw new AbortException(e.getMessage());
			else {
				listener.getLogger().println("Plugin encountered error." + "\n" + e);
			}
		}
	}

	@Symbol("carbonetes")
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

		public static final boolean	DEFAULT_FAIL_ON_PLUGIN_ERROR		= true;
		public static final boolean	DEFAULT_FAIL_ON_POLICY_EVALUATION	= true;
		public static final int		DEFAULT_ENGINE_TIMEOUT				= 500;

		public DescriptorImpl() {
			load();
		}

		@Override
		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Carbonetes Serverless Container Scanning and Policy Compliance";
		}

		public ListBoxModel doFillCredentialsIdItems(@QueryParameter String credentialsId) {
			StandardListBoxModel result = new StandardListBoxModel();

			if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
				return result.includeCurrentValue(credentialsId);
			}

			return result.includeEmptyValue().includeMatchingAs(ACL.SYSTEM, Jenkins.get(),
			        StandardUsernamePasswordCredentials.class, Collections.<DomainRequirement>emptyList(),
			        CredentialsMatchers.always());
		}
	}
}
