package com.gnarly.turingmachine;

import com.gnarly.turingmachine.machine.TuringMachine;

public class Main {

	public static void main(String[] args) {
		try {
			TuringMachine unarySubtractor = TuringMachine.generate("examples/UnaryAdder.gtm");

			String testCase = "111+1111=";
			String result = unarySubtractor.run(testCase);

			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
