package com.gnarly.turingmachine.machine.exceptions.runtime;

public class InvalidTapeLengthException extends Exception {

	public InvalidTapeLengthException(int requestedLength, int writeIndex, int inputLength) {
		super("Tape length must be long enough to store the input string starting at the write index! Requested length: " + requestedLength + ", Write index: " + writeIndex + ", Input length: " + inputLength);
	}
}
