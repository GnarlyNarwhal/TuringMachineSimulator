package com.gnarly.turingmachine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.gnarly.turingmachine.machine.TuringMachine;
import com.gnarly.turingmachine.machine.exceptions.compile.InvalidStateException;
import com.gnarly.turingmachine.machine.exceptions.compile.InvalidSyntaxException;

public class Main {

	private static TuringMachine machine;
	private static Scanner input;
	
	private static String tapeInput = "";
	private static int writeIndex = 0;
	private static int tapeLength = 16;
	private static int startIndex = 0;
	
	public static void main(String[] args) {
		System.out.println("--- Gnarly's Turing Machine Simulator ---\nWelcome to Gnarly's Turing Machine Simulator CLI!\nTo begin enter a command below. You can enter help\nfor a list of commands.");
		input = new Scanner(System.in);
		do {
			System.out.print("> ");
		} while (executeCommand(input.nextLine()));
		input.close();
		System.out.println("Exited successfully!");
	}
	
	public static boolean executeCommand(String command) {
		String[] commands = command.split("\\s+");
		if (commands.length == 0)
			return true;
		switch (commands[0]) {
			case "help": {
				commandHelp();
				break;
			}
			case "quit": {
				return false;
			}
			case "load": {
				commandLoad(command.replaceFirst("\\s*+load\\s+", ""));
				break;
			}
			case "set": {
				commandSet();
				break;
			}
			case "execute": {
				commandExecute();
				break;
			}
			default: {
				System.out.println("Unrecognized command: " + commands[0]);
				break;
			}
		}
		return true;
	}
	
	public static void commandHelp() {
		System.out.println("-- Help --\nList of commands:\n"
				+ "- quit        - Quits the program\n"
				+ "- load <path> - Loads the turing machine from the file specified\n"
				+ "- set         - Sets the parameters prior to machine execution\n"
				+ "- execute     - Runs the machine");
	}
	
	public static void commandLoad(String args) {
		if (args.length() == 0) {
			System.out.println("Command 'load' requires an argument!");
			return;
		}
		try {
			machine = TuringMachine.generate(args);
			System.out.println("Machine loaded successfully!");
			return;
		} catch (FileNotFoundException e) {
			System.out.println("File '" + args + "' could not be found!");
		} catch (InvalidSyntaxException e) {
			System.out.println(e.getMessage());
		} catch (InvalidStateException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Machine failed to load!");
	}
	
	public static void commandSet() {
		System.out.println("Input string: ");
		tapeInput = input.nextLine();
		System.out.println("Write index: ");
		writeIndex = Integer.parseInt(input.nextLine());
		System.out.println("Tape length: ");
		tapeLength = Integer.parseInt(input.nextLine());
		System.out.println("Starting index: ");
		startIndex = Integer.parseInt(input.nextLine());
	}
	
	public static void commandExecute() {
		try {
			if (machine == null)
				System.out.println("A machine must be loaded first!");
			else {
				System.out.println(machine.run(tapeInput, writeIndex, tapeLength, startIndex));
				System.out.println("Machine execution completed successfully!");
			}
		} catch (Exception e) {
			System.out.println("The machine errored during execution!");
			System.out.println("Error Message: " + e.getMessage());
		}
	}
}
