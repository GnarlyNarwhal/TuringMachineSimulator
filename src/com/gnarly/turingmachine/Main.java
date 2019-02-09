package com.gnarly.turingmachine;

import com.gnarly.turingmachine.machine.TuringMachine;

public class Main {

	public static void main(String[] args) {
		try {
			TuringMachine unarySubtractor = TuringMachine.generate("examples/UnarySubtractor.gtm");

			String testCase = "11111-1111=";
			String result = unarySubtractor.run(testCase, 0, testCase.length(), 0);

			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
