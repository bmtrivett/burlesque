package Simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Provides the functionality to the Wileven Machine's components by sending and
 * receiving information between the view and the interpreter, loader, and
 * model. Progress the machine's run process is accomplished by successful
 * completion of the action listeners in this order: GetFileLocation,
 * RunOrSetOptions (if "OPTIONS" is chosen: SetOptions then back to
 * RunOrSetOptions), RunModeSelect, (QuietMode, TraceMode, or StepMode), then
 * finally EndOrRestart.
 * 
 * @author Ben Trivett
 */
public class Controller implements ControllerInterface {
	// Contains event listeners for view GUI and the methods that control
	// sending/receiving info from view to/from interpreter/loader.

	// Instantiate action listeners.
	private GetFileLocation getFile;
	private RunOrSetOptions runOrOptions;
	private SetOptions options;
	private RunModeSelect modeSelect;
	private QuietMode quiet;
	private TraceMode trace;
	private StepMode step;
	private EndOrRestart end;

	// Consolidate all instruction text.
	private String getFileInst = "Press Enter to load "
			+ MachineMain.machineModel.fileLocation + " into memory.\n";
	private String getFileInst2 = "Enter the location of batch file you wish to load:\n";
	private String runOrOptionsInst = "Select an option:\n"
			+ "A) Choose run mode.\nB) Set instruction limit.\n";
	private String modeSelectInst = "Select run mode:\n"
			+ "A) Quiet mode.\nB) Trace mode.\nC) Step mode.\n";
	private String optionsInst = "New instruction limit (1 to 2,147,483,647 "
			+ "or -1 for DEFAULT):\n";
	private String endInst = "Execution over, please choose an option:\n"
			+ "A) Load another file.\n" + "B) Reset Wileven Machine.\n"
			+ "C) Quit.\n";
	private String execInst = "Press enter to start ";

	// Step mode requires a field to keep track of the number of instructions
	// that have already been executed.
	private Integer stepExecInst;

	// Flag that is set to true when an action listener execution mode is
	// executing
	private Boolean isExecuting;

	// Flag that is set to true if step mode has achieved an end qualification.
	private Boolean isEnd;

	/**
	 * Constructor for the Wileven Machine controller.
	 */
	public Controller() {
		// Designate first action listener.
		getFile = new GetFileLocation();
		MachineMain.machineView.setListener(null, getFile);

		// Create the rest of the action listeners.
		runOrOptions = new RunOrSetOptions();
		options = new SetOptions();
		modeSelect = new RunModeSelect();
		quiet = new QuietMode();
		trace = new TraceMode();
		step = new StepMode();
		end = new EndOrRestart();

		// Reset isExecuting because nothing has executed yet
		isExecuting = false;

		// Reset step mode's executed instruction count
		stepExecInst = 0;

		// Reset step mode's ending flag
		isEnd = false;

		// Output first instruction to user.
		if (MachineMain.machineModel.fileLocation != null) {
			MachineMain.machineView.outputText(getFileInst);
		} else {
			MachineMain.machineView.outputText(getFileInst2);
		}
	}

	/**
	 * Takes the user input in the text field of the View, clears the field, and
	 * outputs it into the text area of the View.
	 * 
	 * @author Ben Trivett
	 */
	private void echoInput() {
		String text = MachineMain.machineView.getInput();
		MachineMain.machineView.outputText(text + "\n\n");
		MachineMain.machineView.clearInputField();
	}

	/**
	 * Outputs all the memory that has been altered into the text area of the
	 * View.
	 */
	private void displayAllMemory() {
		// Memory header
		MachineMain.machineView
				.outputText("\tMemory Address\tMemory Contents\n");
		MachineMain.machineView
				.outputText("\t--------------\t\t---------------\n");

		// Memory contents that have been altered are displayed
		Integer counter = 0;
		while (counter < MachineMain.machineModel.memoryArray.length) {
			if (MachineMain.machineModel.memoryArray[counter] != null) {
				MachineMain.machineView.outputText('\t'
						+ Utility.DecimalValueToHex(counter).toUpperCase()
						+ "          \t\t"
						+ MachineMain.machineModel.memoryArray[counter] + '\n');
			}
			counter++;
		}
	}

	/**
	 * Outputs the contents of the general purpose registers, the program
	 * counter, and the condition code registers into the text area of the View.
	 */
	private void displayAllRegisters() {
		// General purpose registers header
		MachineMain.machineView
				.outputText("\nGeneral Purpose Registers:\tR0  \tR1  \tR2  \tR3  "
						+ "\tR4  \tR5  \tR6  \tR7  \n");
		MachineMain.machineView.outputText("\t\t----\t----\t----\t----"
				+ "\t----\t----\t----\t----\n");
		MachineMain.machineView.outputText("\t                          ");

		// General purpose register contents display
		int counter = 0;
		while (counter <= 7) {
			MachineMain.machineView
					.outputText('\t' + MachineMain.machineModel.registerMap
							.get(counter));
			counter++;
		}

		// Program counter and CCR header
		MachineMain.machineView
				.outputText("\n\nProgram Counter\tCondition Code Registers:\tN\tZ\tP\n");
		MachineMain.machineView.outputText("---------------\t\t\t\t-\t-\t-\n");

		// Program counter contents and CCR contents display
		MachineMain.machineView
				.outputText(MachineMain.machineModel.programCounter
						+ "           \t                         \t\t\t"
						+ Utility
								.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
										.get('N'))
						+ '\t'
						+ Utility
								.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
										.get('Z'))
						+ '\t'
						+ Utility
								.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
										.get('P')) + '\n');
	}

	/**
	 * Displays all the memory and registers with either a initial header or a
	 * final header.
	 * 
	 * @param header
	 *            If 0, the header will be set to initial; if 1, to final.
	 */
	private void displayFull(int header) {
		if (header == 0) {
			MachineMain.machineView
					.outputText("=============================="
							+ "======================== Initial memory state ==========="
							+ "=========================================\n");
		} else if (header == 1) {
			MachineMain.machineView
					.outputText("=============================="
							+ "======================== Final memory state ============="
							+ "========================================\n");
		}
		displayAllMemory();
		displayAllRegisters();
		MachineMain.machineView.outputText("=============================="
				+ "========================================================="
				+ "====================================\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ControllerInterface#executeTrap(java.lang.String)
	 */
	@Override
	public String executeTrap(String executeError) {
		String error = null;
		String trapType = executeError.substring(4, 6);
		if (trapType.equals("21")) {
			// OUT: Write the character in R0[7:0] to the console.
			trapOut();
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("22")) {
			// PUTS: Write the null-terminated string pointed to by R0 to the
			// console.
			trapPuts();
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("23")) {
			// IN: Print a prompt on the screen and read a single character from
			// the keyboard. The character is copied to the screen and its ASCII
			// code is copied to R0. The high 8 bits of R0 are cleared.
			Boolean containsError = true;
			while (containsError) {
				String input = MachineMain.machineView
						.showInputDialog("Enter a character: ");
				if (input.length() > 1) {
					MachineMain.machineView.showError("Input too long.");
					containsError = true;
				} else {
					char ch = input.charAt(0);
					containsError = keyTyped(ch);
				}
			}
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("25")) {
			// HALT: Halt execution and print a message to the console.
			error = "HALT";
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("31")) {
			// OUTN: Write the value of R0 to the console as a decimal integer.
			Integer decimal = Utility
					.HexToDecimalValue(MachineMain.machineModel.registerMap
							.get(0));
			// Convert from 2's complement
			decimal = Utility.convertFromTwosComplement(decimal);
			MachineMain.machineView.outputText(decimal.toString());
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("33")) {
			// INN: Print a prompt on the screen and read a decimal number from
			// the keyboard. The number is echoed to the screen and stored in
			// R0. Must be in the range -32768 < x < 32767.
			Boolean containsError = true;
			while (containsError) {
				String input = MachineMain.machineView
						.showInputDialog("Enter an integer: ");
				containsError = readTrapInteger(input);
			}
			// Set register 7 to the program counter per specifications.
			MachineMain.machineModel.registerMap.put(7,
					MachineMain.machineModel.programCounter);
			// Register 7 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 7;

		} else if (trapType.equals("43")) {
			// RND: Store a random number in R0.
			Random generator = new Random();
			Integer randomInteger = generator.nextInt(65536);
			int number = Utility.convertFromTwosComplement(randomInteger);
			MachineMain.machineModel.registerMap.put(0,
					Utility.DecimalValueToHex(randomInteger));
			// Update CCRs
			if (number > 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', true);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			} else if (number < 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', true);
			} else {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', true);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			}
			// Register 0 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 0;
		} else {
			error = "Trap type invalid.";
		}
		// Set register 7 to the program counter per specifications.
		MachineMain.machineModel.registerMap.put(7,
				MachineMain.machineModel.programCounter);
		// Register 7 was altered, so record that in the interpreter.
		Integer counter = 0;
		while (Interpreter.registerChanges[counter] != null) {
			counter++;
		}
		Interpreter.registerChanges[counter] = 7;
		return error;
	}

	/**
	 * Write the character in R0[2,4] to the console.
	 */
	private void trapOut() {
		int decimal = Utility
				.HexToDecimalValue(MachineMain.machineModel.registerMap.get(0)
						.substring(2, 4));
		String output = Character.toString((char) decimal);
		MachineMain.machineView.outputText(output);
	}

	/**
	 * Write the null-terminated string pointed to by R0 to the console.
	 */
	private void trapPuts() {
		int count = Utility
				.HexToDecimalValue(MachineMain.machineModel.registerMap.get(0));
		while (!(MachineMain.machineModel.memoryArray[count] == null)) {
			if (MachineMain.machineModel.memoryArray[count].equals("0000")) {
				break;
			}
			int decimal = Utility
					.HexToDecimalValue(MachineMain.machineModel.memoryArray[count]);
			String output = Character.toString((char) decimal);
			MachineMain.machineView.outputText(output);
			count++;
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and sends it to the loader. If the loader doesn't return an error,
	 * then the action listener is changed to the next step in the program
	 * process, RunOrSetOptions.
	 * 
	 * @author Ben Trivett
	 */
	public class GetFileLocation implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String getFileError;

			// Take in the input.
			String text = MachineMain.machineView.getInput();
			if (MachineMain.machineModel.fileLocation == null) {
				MachineMain.machineModel.fileLocation = text;
			}
			// Send the input to the loader.
			try {
				getFileError = InputInstructions
						.FindFile(MachineMain.machineModel.fileLocation);
			} catch (IOException e1) {
				getFileError = "An error has occurred while loading this file.";
			}

			// Check if the loader returned an error finding the file.
			if (getFileError != null) {
				echoInput();
				MachineMain.machineView.showError(getFileError);
				MachineMain.machineView.showError(getFileInst2);
			} else {
				// If no error output new instructions and change
				// action listener to next.
				MachineMain.machineView.outputText("Loading...");
				MachineMain.machineView.outputText(runOrOptionsInst);
				MachineMain.machineView.setListener(getFile, runOrOptions);
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and decides whether to change to run mode select or set instruction
	 * limit.
	 * 
	 * @author Ben Trivett
	 */
	public class RunOrSetOptions implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Take in the input.
			String text = MachineMain.machineView.getInput();
			echoInput();

			if (text.equals("a") || text.equals("A")) {
				// Display instructions and change action listener to run mode
				// select
				MachineMain.machineView.outputText(modeSelectInst);
				MachineMain.machineView.setListener(runOrOptions, modeSelect);
			} else if (text.equals("b") || text.equals("B")) {
				// Display instructions and change action listener to options
				MachineMain.machineView.outputText("CURRENT: "
						+ MachineMain.machineModel.instructionLimit + '\n'
						+ optionsInst);
				MachineMain.machineView.setListener(runOrOptions, options);
			} else {
				// Display error and instructions again.
				MachineMain.machineView
						.showError("Invalid response. Valid responses: A, B");
				MachineMain.machineView.outputText(runOrOptionsInst);
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View, sets that as the new instruction limit, then goes back to the run
	 * or options action listener.
	 * 
	 * @author Ben Trivett
	 */
	public class SetOptions implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Take in the input.
			String text = MachineMain.machineView.getInput();
			echoInput();

			// Check if input was -1, if so set instruction limit to default
			// and return to previous action listener.
			if (text.equals("-1")) {
				MachineMain.machineModel.instructionLimit = Model.DEFAULT_INSTRUCTION_LIMIT;
				MachineMain.machineView.outputText(runOrOptionsInst);
				MachineMain.machineView.setListener(options, runOrOptions);
			} else {
				// Check for possible exception
				Boolean errorExists = false;
				try {
					Integer.parseInt(text);
				} catch (NumberFormatException err) {
					errorExists = true;
				}

				// If there was no exception, convert text to integer
				if (!errorExists) {
					int newInstructionLimit = Integer.parseInt(text);
					if (newInstructionLimit >= 1
							&& newInstructionLimit <= Integer.MAX_VALUE) {
						// Set new instruction limit, display instructions and
						// change action listener to run or options.
						MachineMain.machineModel.instructionLimit = newInstructionLimit;
						MachineMain.machineView.outputText(runOrOptionsInst);
						MachineMain.machineView.setListener(options,
								runOrOptions);
					} else {
						// Display error and instructions again.
						MachineMain.machineView
								.showError("Invalid response. Valid responses: 1 to 2,147,483,647, -1");
						MachineMain.machineView.outputText("CURRENT: "
								+ MachineMain.machineModel.instructionLimit
								+ '\n' + optionsInst);
					}
				} else {
					// Display error and instructions again.
					MachineMain.machineView
							.showError("Invalid response. Valid responses: 1 to 2,147,483,647, -1");
					MachineMain.machineView.outputText("CURRENT: "
							+ MachineMain.machineModel.instructionLimit + '\n'
							+ optionsInst);
				}
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and decides what mode to run the program in.
	 * 
	 * @author Ben Trivett
	 */
	public class RunModeSelect implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Take in the input.
			String text = MachineMain.machineView.getInput();
			echoInput();

			if (text.equals("a") || text.equals("A")) {
				// Display instructions and change action listener to quiet
				MachineMain.machineView.outputText(execInst
						+ MachineMain.machineModel.programName + ".\n\n");
				MachineMain.machineView.setListener(modeSelect, quiet);
			} else if (text.equals("b") || text.equals("B")) {
				// Display instructions and change action listener to trace
				MachineMain.machineView.outputText(execInst
						+ MachineMain.machineModel.programName + ".\n\n");
				MachineMain.machineView.setListener(modeSelect, trace);
			} else if (text.equals("c") || text.equals("C")) {
				// Set step instruction counter to 0.
				stepExecInst = 0;
				// Display instructions and change action listener to step
				MachineMain.machineView.outputText(execInst
						+ MachineMain.machineModel.programName + ".\n\n");
				MachineMain.machineView.setListener(modeSelect, step);
			} else {
				// Display error and instructions again.
				MachineMain.machineView
						.showError("Invalid response. Valid responses: A, B, C");
				MachineMain.machineView.outputText(modeSelectInst);
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and outputs it into the text area of the View.
	 * 
	 * @author Ben Trivett
	 */
	public class QuietMode implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!isExecuting) {
				isExecuting = true;
				MachineMain.machineView.outputText("Executing...\n");

				// Run the interpreter until the instruction limit is reached.
				int instructionCount = 0;
				while (instructionCount < MachineMain.machineModel.instructionLimit) {
					// Create an instance of the interpreter
					Interpreter interQuiet = new Interpreter();
					// Execute an instruction and get information on what
					// happened.
					String executeError = interQuiet.ExecuteAnInstruction();
					// Check if the loader returned an error finding the file.
					if (executeError != null) {
						// Execute trap or debug instructions if they occurred
						if (executeError.substring(0, 4).equals("TRAP")) {
							// Execute the trap
							String trapError = executeTrap(executeError);
							// Check for errors or HALT command.
							if (trapError != null) {
								if (trapError.equals("HALT")) {
									break;
								} else {
									MachineMain.machineView
											.outputText(trapError + '\n');
								}
							}
						} else if (executeError.substring(0, 4).equals("DBUG")) {
							// Display registers if DBUG instruction occurs.
							displayAllRegisters();
						} else if (Utility.isHexString(executeError.substring(
								0, 4))) {
							// Program counter was altered successfully.
						} else {
							MachineMain.machineView
									.outputText(executeError + '\n');
						}
					}

					instructionCount++;
				}
				// Output new instructions and change action listener to end.
				MachineMain.machineView.outputText('\n' + endInst);
				isExecuting = false;
				MachineMain.machineView.setListener(quiet, end);
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and outputs it into the text area of the View.
	 * 
	 * @author Ben Trivett
	 */
	public class TraceMode implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!isExecuting) {
				isExecuting = true;
				MachineMain.machineView.outputText("Executing...\n");
				// Output initial memory contents
				displayFull(0);

				// Run the interpreter until the instruction limit is reached.
				int instructionCount = 0;
				while (instructionCount < MachineMain.machineModel.instructionLimit) {
					// Display current program counter contents
					MachineMain.machineView.outputText("\nProgram Counter: "
							+ MachineMain.machineModel.programCounter);

					// Create an instance of the interpreter
					Interpreter interTrace = new Interpreter();

					// Execute an instruction and get information on what
					// happened.
					String executeError = interTrace.ExecuteAnInstruction();
					String instName = Interpreter.instruction;

					// Display the instruction being executed
					MachineMain.machineView.outputText("\n\nInstruction: "
							+ instName + '\n');

					// Check if the loader returned an error finding the file.
					if (executeError != null) {
						// Execute trap or debug instructions if they occurred
						if (executeError.substring(0, 4).equals("TRAP")) {
							// Execute the trap
							String trapError = executeTrap(executeError);

							// Check for errors or HALT command.
							if (trapError != null) {
								if (trapError.equals("HALT")) {
									break;
								} else {
									MachineMain.machineView
											.outputText(trapError + '\n');
								}
							}
						} else if (executeError.substring(0, 4).equals("DBUG")) {
							// Display registers if DBUG instruction occurs.
							displayAllRegisters();
						} else if (Utility.isHexString(executeError.substring(
								0, 4))) {
							// Program counter was altered successfully.
						} else {
							MachineMain.machineView
									.outputText(executeError + '\n');
						}
					}
					String[] memAltered = Interpreter.memoryChanges;
					Integer[] regAltered = Interpreter.registerChanges;
					// If memory was altered, display it
					if (memAltered[0] != null) {
						// Memory header
						MachineMain.machineView
								.outputText("\n\tMemory Address\tNew Memory Contents\n");
						MachineMain.machineView
								.outputText("\t--------------\t\t-------------------\n");

						// Memory contents that have been altered are displayed
						Integer counter = 0;
						while (memAltered[counter] != null) {
							MachineMain.machineView
									.outputText('\t'
											+ memAltered[counter]
											+ "          \t\t"
											+ MachineMain.machineModel.memoryArray[Utility
													.HexToDecimalValue(memAltered[counter])]
											+ '\n');
							counter++;
						}
					}

					// If registers were altered, display them
					if (regAltered[0] != null) {
						// Register header
						MachineMain.machineView
								.outputText("\n\tRegister Number\tNew Register Contents\n");
						MachineMain.machineView
								.outputText("\t---------------\t\t---------------------\n");

						// Register contents that have been altered are
						// displayed
						Integer counter = 0;
						while (regAltered[counter] != null) {
							MachineMain.machineView.outputText("\tR"
									+ Utility.DecimalValueToHex(
											regAltered[counter]).substring(3)
									+ "             \t\t"
									+ MachineMain.machineModel.registerMap
											.get(regAltered[counter]) + '\n');

							counter++;
						}

						// Display condition code registers regardless of
						// whether they have changed or not
						MachineMain.machineView
								.outputText("\n\tCondition Code Registers:\tN\tZ\tP\n");
						MachineMain.machineView.outputText("\t\t\t-\t-\t-\n");
						MachineMain.machineView
								.outputText("\t\t                         \t"
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('N'))
										+ '\t'
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('Z'))
										+ '\t'
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('P')) + '\n');
					}

					instructionCount++;
				}

				// If the instruction limit was met, display error
				if (instructionCount == MachineMain.machineModel.instructionLimit) {
					MachineMain.machineView
							.showError("Instruction Limit Reached.");
				}

				// Output final memory contents
				displayFull(1);

				// Output new instructions and change action listener to end.
				MachineMain.machineView.outputText('\n' + endInst);
				isExecuting = false;
				MachineMain.machineView.setListener(trace, end);
			}
		}
	}

	/**
	 * An action listener that displays the memory and registers as they are
	 * altered during execution by the interpreter. Only one instruction is
	 * executed at a time. The action listener switches to end when the HALT
	 * instruction is executed. Has to use a private field to keep track of the
	 * number of instructions executed.
	 * 
	 * @author Ben Trivett
	 */
	public class StepMode implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!isExecuting) {
				isExecuting = true;
				// Output initial memory contents if the first instruction has
				// not been executed
				if (stepExecInst == 0) {
					MachineMain.machineView.outputText("Executing...\n");
					displayFull(0);
				}

				// Run the interpreter until the instruction limit is reached.
				if (stepExecInst < MachineMain.machineModel.instructionLimit) {
					// Display current program counter contents
					MachineMain.machineView.outputText("\nProgram Counter: "
							+ MachineMain.machineModel.programCounter);

					// Create an instance of the interpreter
					Interpreter interStep = new Interpreter();

					// Execute an instruction and get information on what
					// happened.
					String executeError = interStep.ExecuteAnInstruction();
					String instName = Interpreter.instruction;

					// Display the instruction being executed
					MachineMain.machineView.outputText("\n\nInstruction: "
							+ instName + '\n');

					// Check if the loader returned an error finding the file.
					if (executeError != null) {
						// Execute trap or debug instructions if they occurred
						if (executeError.substring(0, 4).equals("TRAP")) {
							// Execute the trap
							String trapError = executeTrap(executeError);

							// Check for errors or HALT command.
							if (trapError != null) {
								if (trapError.equals("HALT")) {
									isEnd = true;
								} else {
									MachineMain.machineView
											.outputText(trapError + '\n');
								}
							}
						} else if (executeError.substring(0, 4).equals("DBUG")) {
							// Display registers if DBUG instruction occurs.
							displayAllRegisters();
						} else if (Utility.isHexString(executeError.substring(
								0, 4))) {
							// Program counter was altered successfully.
						} else {
							MachineMain.machineView
									.outputText(executeError + '\n');
						}
					}
					String[] memAltered = Interpreter.memoryChanges;
					Integer[] regAltered = Interpreter.registerChanges;
					// If memory was altered, display it
					if (memAltered[0] != null) {
						// Memory header
						MachineMain.machineView
								.outputText("\n\tMemory Address\tNew Memory Contents\n");
						MachineMain.machineView
								.outputText("\t--------------\t\t-------------------\n");

						// Memory contents that have been altered are displayed
						Integer counter = 0;
						while (memAltered[counter] != null) {
							MachineMain.machineView
									.outputText('\t'
											+ memAltered[counter]
											+ "          \t\t"
											+ MachineMain.machineModel.memoryArray[Utility
													.HexToDecimalValue(memAltered[counter])]
											+ '\n');
							counter++;
						}
					}

					// If registers were altered, display them
					if (regAltered[0] != null) {
						// Register header
						MachineMain.machineView
								.outputText("\n\tRegister Number\tNew Register Contents\n");
						MachineMain.machineView
								.outputText("\t---------------\t\t---------------------\n");

						// Register contents that have been altered are
						// displayed
						Integer counter = 0;
						while (regAltered[counter] != null) {
							MachineMain.machineView.outputText("\tR"
									+ Utility.DecimalValueToHex(
											regAltered[counter]).substring(3)
									+ "             \t\t"
									+ MachineMain.machineModel.registerMap
											.get(regAltered[counter]) + '\n');

							counter++;
						}

						// Display condition code registers regardless of
						// whether they have changed or not
						MachineMain.machineView
								.outputText("\n\tCondition Code Registers:\tN\tZ\tP\n");
						MachineMain.machineView.outputText("\t\t\t-\t-\t-\n");
						MachineMain.machineView
								.outputText("\t\t                         \t"
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('N'))
										+ '\t'
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('Z'))
										+ '\t'
										+ Utility
												.BooleanToString(MachineMain.machineModel.conditionCodeRegisters
														.get('P')) + '\n');
					}
				} else {
					MachineMain.machineView
							.showError("Instruction Limit Reached.");
					isEnd = true;
				}

				if (isEnd) {
					// Output final memory contents
					displayFull(1);

					// Output new instructions and change action listener to
					// end.
					MachineMain.machineView.outputText('\n' + endInst);
					isExecuting = false;
					isEnd = false;
					MachineMain.machineView.setListener(step, end);
				} else {
					stepExecInst++;
					MachineMain.machineView
							.outputText("\nPress enter to continue execution.\n\n");
					isExecuting = false;
				}
			}
		}
	}

	/**
	 * An action listener that takes the user input in the text field of the
	 * View and either changes the action listener to GetFileLocation, restarts
	 * the Wileven Machine, or exits.
	 * 
	 * @author Ben Trivett
	 */
	public class EndOrRestart implements ActionListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see ControllerInterface#actionPerformed(java.awt.event.ActionEvent)
		 */

		public void actionPerformed(ActionEvent e) {
			// Take in the input.
			String text = MachineMain.machineView.getInput();
			echoInput();

			if (text.equals("a") || text.equals("A")) {
				// Display instructions and change action listener to getFile
				MachineMain.machineView.outputText(getFileInst);
				MachineMain.machineView.setListener(end, getFile);
			} else if (text.equals("b") || text.equals("B")) {
				// Restart Wileven Machine
				MachineMain.machineView.dispose();
				String emptyString = null;
				MachineMain.Reset(emptyString);
			} else if (text.equals("c") || text.equals("C")) {
				// Close Wileven Machine
				System.exit(0);
			} else {
				// Display error and instructions again.
				MachineMain.machineView
						.showError("Invalid response. Valid responses: A, B, C");
				MachineMain.machineView.outputText(endInst);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ControllerInterface#keyTyped(char)
	 */
	@Override
	public Boolean keyTyped(char ch) {
		if ((int) ch >= 0 && (int) ch <= 255) {
			Character output = ch;
			int number = ch;
			MachineMain.machineView.outputText(output.toString());
			MachineMain.machineModel.registerMap.put(0,
					Utility.DecimalValueToHex((int) ch));
			// Update CCRs
			if (number > 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', true);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			} else if (number < 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', true);
			} else {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', true);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			}
			// Register 0 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 0;
			return false;
		} else {
			return true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ControllerInterface#readTrapInteger(java.lang.String)
	 */
	@Override
	public Boolean readTrapInteger(String text) {
		// Check for possible exception
		Boolean errorExists = false;
		try {
			Integer.parseInt(text);
		} catch (NumberFormatException err) {
			errorExists = true;
		}
		if (!errorExists) {
			if (Integer.parseInt(text) < -32768
					|| Integer.parseInt(text) > 32767) {
				errorExists = true;
			}
		}
		// If there was no exception, convert text to integer
		if (!errorExists) {
			int number = Integer.parseInt(text);
			// Update CCRs
			if (number > 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', true);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			} else if (number < 0) {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', false);
				MachineMain.machineModel.conditionCodeRegisters.put('P', true);
			} else {
				MachineMain.machineModel.conditionCodeRegisters.put('N', false);
				MachineMain.machineModel.conditionCodeRegisters.put('Z', true);
				MachineMain.machineModel.conditionCodeRegisters.put('P', false);
			}
			// Convert to 2's complement and store in R0
			number = Utility.convertToTwosComplement(number);
			MachineMain.machineModel.registerMap.put(0,
					Utility.DecimalValueToHex(number));
			// Register 0 was altered, so record that in the interpreter.
			Integer counter = 0;
			while (Interpreter.registerChanges[counter] != null) {
				counter++;
			}
			Interpreter.registerChanges[counter] = 0;
		} else {
			// Display error and instructions again.
			MachineMain.machineView
					.showError("Invalid response. Valid responses: -32,768 to 32767");
		}
		return errorExists;
	}

}
