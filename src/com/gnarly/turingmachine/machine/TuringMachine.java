package com.gnarly.turingmachine.machine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gnarly.turingmachine.machine.exceptions.compile.InvalidStateException;
import com.gnarly.turingmachine.machine.exceptions.compile.InvalidSyntaxException;
import com.gnarly.turingmachine.machine.exceptions.runtime.EndOfTapeException;
import com.gnarly.turingmachine.machine.exceptions.runtime.InvalidStartIndexException;
import com.gnarly.turingmachine.machine.exceptions.runtime.InvalidSymbolException;
import com.gnarly.turingmachine.machine.exceptions.runtime.InvalidTapeLengthException;

public class TuringMachine {

	private static final int HALT = 0;

	private static final int LEFT  = -1;
	private static final int STAY  =  0;
	private static final int RIGHT =  1;
	private State[] states;

	private TuringMachine() {}

	// IncompleteState/IncompleteTransitions have not been validated yet
	private static class IncompleteState {

		public static class IncompleteTransition {
			public int readValue;
			public String targetState;
			public int writeValue;
			public int movement;
		}

		public String name;
		public IncompleteTransition[] transitions;
	}

	public static TuringMachine generate(String path) throws IOException, InvalidSyntaxException, InvalidStateException {
		// Load the contents of the entire into a string and remove comments
		int read;
		StringBuilder file = new StringBuilder();
		FileInputStream input = new FileInputStream(path);
		while ((read = input.read()) != -1)
			file.append((char) read);
		input.close();
		String contents = file.toString().replaceAll("\\r", "").replaceAll("\\/\\*(?:.|\\n)*\\*\\/", "").replaceAll("\\/\\/.*(?:\\n|$)", "");
		
		if (!contents.startsWith("state"))
			contents = contents.replaceFirst("\\s*", "");
		
		// Yes this exists now
		// Excuse while I go gouge my eyes out
		final String SYNTAX_REGEX = "\\s*(?:state\\s+\\w+\\s*\\{\\s*(?:symbol\\s+(?:(?:'(?:[^\\n\\r\\t]|\\\\[nt0])')|(?:0x[0-9a-fA-F]+)|(?:-?\\d+))\\s*\\{\\s*(?:(?:'(?:[^\\n\\r\\t]|\\\\[nt0])')|(?:0x[0-9a-fA-F]+)|(?:-?\\d+))\\s*,\\s*(?:left|right|none)\\s*,\\s*(?:\\w+|HALT!)\\s*}\\s*)*}\\s*)*";
		
		// Checks the contents of the file against the syntax regular expression to validate the files syntax
		// Allows the parser to assume accurate syntax from here on out which means no error checking required. Yay!
		if (!contents.matches(SYNTAX_REGEX))
			throw new InvalidSyntaxException();

		// Split the file contents on the delimiters (e.g. {},) and remove extraneous whitespace
		final String[] split = contents.split("\\s*+[{},]\\s*+[{},]?\\s*+");

		// These are parser states. They indicate what the parser is currently looking for.
		// Looking for the beginning of a new state or a new symbol for the current state
		final int SOMETHING_NEW    = 0;
		// Looking for the target write symbol for the current symbol
		final int TARGET_SYMBOL    = 1;
		// Looking for the target movement direction for the current symbol
		final int TARGET_DIRECTION = 2;
		// Looking for the target state for the current symbol
		final int TARGET_STATE     = 3;
		
		// Start the parser off looking for a new state
		int parserState = SOMETHING_NEW;
		// The state currently being constructed
		IncompleteState currentState = null;
		// The list of completed states
		ArrayList<IncompleteState> states = new ArrayList<IncompleteState>();
		// The current transition being constructed
		IncompleteState.IncompleteTransition currentTransition = null;
		// The list of complete transitions for the current state
		ArrayList<IncompleteState.IncompleteTransition> transitions = new ArrayList<>();
		
		// For each token...
		for (int i = 0; i < split.length; ++i) {
			switch (parserState) {
				// If the parser is looking for something new...
				case SOMETHING_NEW: {
					// Check whether its a new state or new symbol
					// If it's a new state...
					if (split[i].startsWith("state")) {
						// If the current state is not null...
						if (currentState != null) {
							// Collect its transitions and add it to the list of complete states
							currentState.transitions = transitions.toArray(new IncompleteState.IncompleteTransition[transitions.size()]);
							transitions.clear();
							states.add(currentState);
						}
						// Set the current state to a new state and set its name to to the current token
						currentState = new IncompleteState();
						currentState.name = split[i].replaceFirst("state\\s+", "");
					}
					// If it's a new symbol...
					else {
						// Set the current transition to a new transition and set its read symbol to the current token
						currentTransition = new IncompleteState.IncompleteTransition();
						currentTransition.readValue = parseSymbol(split[i].replaceFirst("symbol\\s+", ""));
						// Tell the parser to look for the target state
						parserState = TARGET_SYMBOL;
					}
					break;
				}
				// If the parser is looking for the target symbol...
				case TARGET_SYMBOL: {
					currentTransition.writeValue = parseSymbol(split[i]);
					parserState = TARGET_DIRECTION;
					break;
				}
				// If the parser is looking for the target direction...
				case TARGET_DIRECTION: {
					// Check the current token and set the current transitions movement accordingly
					switch(split[i]) {
						case "left": {
							currentTransition.movement = LEFT;
							break;
						}
						case "right": {
							currentTransition.movement = RIGHT;
							break;
						}
						case "none": {
							currentTransition.movement = STAY;
							break;
						}
					}
					// Tell the parser to look for something new
					parserState = TARGET_STATE;
					break;
				}
				// If the parser is looking for the target state...
				case TARGET_STATE: {
					// Set the current transition's target state to the current token
					currentTransition.targetState = split[i];
					// Since target state is the last part of the transition add the newly completed transition to the list of complete transitions
					transitions.add(currentTransition);
					// Tell the parser to look for the target symbol
					parserState = SOMETHING_NEW;
					break;
				}
			}
		}
		// Make sure to add the last state being constructed
		if (currentState != null) {
			currentState.transitions = transitions.toArray(new IncompleteState.IncompleteTransition[transitions.size()]);
			states.add(currentState);
		}
		
		// Allocate an array for the finalized states
		State[] finalStates = new State[states.size()];
		for (int i = 0; i < finalStates.length; ++i) {
			// Create a new state
			State currentCompleteState = new State();
			// Set its name
			currentCompleteState.name = states.get(i).name;
			// Allocate an array for its transitions
			currentCompleteState.transitions = new State.Transition[states.get(i).transitions.length];
			for (int j = 0; j < currentCompleteState.transitions.length; ++j) {
				// Create a new transition
				State.Transition currentCompleteTransition = new State.Transition();
				// Get the current incomplete transition
				IncompleteState.IncompleteTransition currentIncompleteTransition = states.get(i).transitions[j];
				// Copy over the values which require no validation
				currentCompleteTransition.readValue  = currentIncompleteTransition.readValue;
				currentCompleteTransition.writeValue = currentIncompleteTransition.writeValue;
				currentCompleteTransition.movement   = currentIncompleteTransition.movement;
				// Get the current incomplete transitions target state name
				String currentTargetState = currentIncompleteTransition.targetState;
				// If it is the string literal HALT! then its valid
				if (currentTargetState.equals("HALT!"))
					currentCompleteTransition.targetState = HALT;
				// Otherwise look for it in the list of states
				else {
					for (int k = 0; k < finalStates.length; ++k) {
						// If found set the finalized states targetState and exit the loop
						if (states.get(k).name.equals(currentTargetState)) {
							currentCompleteTransition.targetState = k + 1;
							break;
						}
						// If it is not found by the time the loop ends throw an error because its an invalid state
						else if (k == finalStates.length - 1)
							throw new InvalidStateException(currentTargetState);
					}
				}
				// Set the current state's transtitions
				currentCompleteState.transitions[j] = currentCompleteTransition;
			}
			// Add the current state to the array of final states
			finalStates[i] = currentCompleteState;
		}
		
		// Construct the turing machine object and return it
		TuringMachine ret = new TuringMachine();
		ret.states = finalStates;
		
		return ret;
	}
	
	private static int parseSymbol(String symbol) {
		// If the symbol is a character...
		if (symbol.startsWith("'")) {
			// If it's an escape sequence...
			if (symbol.charAt(1) == '\\') {
				switch (symbol.charAt(2)) {
					case '0': return '\0';
					case 'n': return '\n';
					case 't': return '\t';
				}
			}
			else
				return symbol.charAt(1);
		}
		// If the symbol is a hexadecimal number
		else if (symbol.startsWith("0x"))
			return Integer.parseInt(symbol.substring(2), 16);
		// If the symbol is a decimal integer
		else
			return Integer.parseInt(symbol);
		// This is beyond science (it's impossible)
		return 0;
	}

	public int[] run(int[] input, int writeIndex, int tapeLength, int startIndex) throws InvalidSymbolException, InvalidTapeLengthException, EndOfTapeException, InvalidStartIndexException {
		// Check that the tape length is atleast as long as the input
		if (tapeLength < writeIndex + input.length)
			throw new InvalidTapeLengthException(tapeLength, writeIndex, input.length);
		// Check that the start index is valid
		if (startIndex < 0 || startIndex >= tapeLength)
			throw new InvalidStartIndexException(startIndex, tapeLength);
		// Allocate an array for the tape and initialize it to the input
		int[] tape = new int[tapeLength];
		for (int i = 0; i < input.length; ++i)
			tape[i] = input[i];
		int readHead = 0;
		int currentState = 1;
		// While the current state is not HALT
		while (currentState != HALT) {
			// Get the current state
			State state = states[currentState - 1];
			// Check each transition's read symbol against the current tape symbol
			for (int i = 0; i < state.transitions.length; ++i) {
				// Get the current transition
				State.Transition transition = state.transitions[i];
				// If it finds a match, set the state, write to the tape, move the read head, and exit the loop
				if (transition.readValue == tape[readHead]) {
					tape[readHead] = transition.writeValue;
					readHead	  += transition.movement;
					currentState   = transition.targetState;
					break;
				}
				// If the current tape symbol does not match any of the read symbols in the current state throw an error
				else if (i == state.transitions.length - 1)
					throw new InvalidSymbolException(state, (char) tape[readHead]);
			}
			// If the read head goes outside the bounds of the tape throw an error
			if (readHead == -1 || readHead == tape.length)
				throw new EndOfTapeException(readHead, tape.length);
		}
		return tape;
	}

	public String run(String input, int writeIndex, int tapeLength, int startIndex) throws InvalidSymbolException, InvalidTapeLengthException, EndOfTapeException, InvalidStartIndexException {
		// Check that the tape length is atleast as long as the input
		if (tapeLength < writeIndex + input.length())
			throw new InvalidTapeLengthException(tapeLength, writeIndex, input.length());
		// Check that the start index is valid
		if (startIndex < 0 || startIndex >= tapeLength)
			throw new InvalidStartIndexException(startIndex, tapeLength);
		// Allocate an array for the tape and initialize it to the input
		char[] tape = new char[tapeLength];
		for (int i = 0; i < input.length(); ++i)
			tape[i] = input.charAt(i);
		int readHead = 0;
		int currentState = 1;
		// While the current state is not HALT
		while (currentState != 0) {
			// Get the current state
			State state = states[currentState - 1];
			// Check each transition's read symbol against the current tape symbol
			for (int i = 0; i < state.transitions.length; ++i) {
				// Get the current transition
				State.Transition transition = state.transitions[i];
				// If it finds a match, set the state, write to the tape, move the read head, and exit the loop
				if (transition.readValue == tape[readHead]) {
					tape[readHead] = (char) transition.writeValue;
					currentState   = transition.targetState;
					readHead	  += transition.movement;
					break;
				}
				// If the read head goes outside the bounds of the tape throw an error
				else if (i == state.transitions.length - 1)
					throw new InvalidSymbolException(state, (char) tape[readHead]);
			}
			// If the read head goes outside the bounds of the tape throw an error
			if (readHead == -1 || readHead == tape.length)
				throw new EndOfTapeException(readHead, tape.length);
		}
		return new String(tape);
	}
}
