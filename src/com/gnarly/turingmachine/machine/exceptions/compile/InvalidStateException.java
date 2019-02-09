package com.gnarly.turingmachine.machine.exceptions.compile;

public class InvalidStateException extends Exception {

	public InvalidStateException(String requestedState) {
		super("Could not find state: \"" + requestedState + "\"");
	}
}
