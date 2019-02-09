package com.gnarly.turingmachine.machine;

public class State {

	public static class Transition {
		public int readValue;
		public int targetState;
		public int writeValue;
		public int movement;
	}

	public String name;
	public Transition[] transitions;
}
