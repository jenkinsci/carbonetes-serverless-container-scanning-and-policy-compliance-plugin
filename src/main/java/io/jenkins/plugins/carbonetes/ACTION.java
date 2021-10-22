package io.jenkins.plugins.carbonetes;

/**
 * Gate Action
 */

public enum Action {

	STOP("STOP"), WARN("WARN"), GO("GO"), PASS("PASS"), FAIL("FAIL"), PASSED("PASSED"), FAILED("FAILED");

	private String description;

	private Action(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static Action fromName(String value) {
		for (Action action : Action.values()) {
			if (value.contains(action.getDescription())) {
				return action;
			}
		}
		throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
	}
}
