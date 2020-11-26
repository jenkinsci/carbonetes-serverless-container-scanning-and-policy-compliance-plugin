package io.jenkins.plugins.carbonetes;

/**
 * Gate Action
 */

public enum ACTION {

	STOP("STOP"), WARN("WARN"), GO("GO"), PASS("PASS"), FAIL("FAIL"), PASSED("PASSED"), FAILED("FAILED");

	private String description;

	private ACTION(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static ACTION fromName(String value) {
		for (ACTION action : ACTION.values()) {
			if (value.contains(action.getDescription())) {
				return action;
			}
		}
		throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
	}
}
