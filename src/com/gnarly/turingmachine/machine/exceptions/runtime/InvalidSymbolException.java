package com.gnarly.turingmachine.machine.exceptions.runtime;

import com.gnarly.turingmachine.machine.State;

public class InvalidSymbolException extends Exception {

	public InvalidSymbolException(State state, int symbol) {
		super("Encountered invalid symbol " + symbol + " during state '" + state.name + "'!");
	}

	public InvalidSymbolException(State state, char symbol) {
		super("Encountered invalid symbol '" + symbol + "' during state '" + state.name + "'!");
	}
}
