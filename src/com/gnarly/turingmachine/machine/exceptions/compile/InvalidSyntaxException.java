package com.gnarly.turingmachine.machine.exceptions.compile;

public class InvalidSyntaxException extends Exception {

	public InvalidSyntaxException() {
		super("This here lexer has no concept of line numbers \"...so you get nothing! You lose! Good day sir!\"");
	}
}
