package com.gnarly.turingmachine.machine.exceptions.runtime;

public class InvalidStartIndexException extends Exception {

	public InvalidStartIndexException(int tapeLength, int startIndex) {
		super("The start index must be within the bounds of the tape length! Requested length: " + tapeLength + ", Start index: " + startIndex);
	}
}
