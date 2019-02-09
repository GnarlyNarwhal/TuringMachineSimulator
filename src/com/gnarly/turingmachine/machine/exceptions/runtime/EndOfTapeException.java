package com.gnarly.turingmachine.machine.exceptions.runtime;

public class EndOfTapeException extends Exception {

	public EndOfTapeException(int index, int length) {
		super("Read head has exceeded the end of the tape! Read head index: " + index + ", Tape length: " + length);
	}
}
