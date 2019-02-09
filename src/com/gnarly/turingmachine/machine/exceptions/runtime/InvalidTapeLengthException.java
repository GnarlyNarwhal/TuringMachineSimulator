package com.gnarly.turingmachine.machine.exceptions.runtime;

public class InvalidTapeLengthException extends Exception {

	public InvalidTapeLengthException(int requestedLength, int inputLength) {
		super("Requested tape length must be atleast as long as the input's length! Requested length: " + requestedLength + ", Input length: " + inputLength);
	}
}
