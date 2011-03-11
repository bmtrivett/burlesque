package Simulator;

import java.io.*;

/**
 * To put the input information into the data structures that represent the
 * memory and registers of the machine.
 * 
 * @author BSchuck
 * 
 * 
 */
public class InputInstructions {
	/**
	 * The position to allocate how many text records are to be read in.
	 */
	private final static int POS_OF_INT = 11;

	/**
	 * 
	 * @param args
	 * @throws IOException
	 *             Takes in the Input file and makes an array of all the memory.
	 */
	public static String FindFile(String input) throws IOException {

		int count = 0;

		File inputFile = new File(input);
		boolean fileExists = inputFile.exists();

		if (fileExists == false) {
			return "The file does not exist. Try another one.";
		}

		String read = "";
		FileReader reader = new FileReader(input);
		BufferedReader file = new BufferedReader(reader);
		read = file.readLine();
		if (read.charAt(0) != 'H') {
			return "The header record is incorrect in the file it does " +
					"not begin with H on line 1.";
		}

		if (read.length() != 15) {
			return "The header record is incorrect in the file on line 0. The " +
					"length is not equal to 15";
		}
		// gets just the first line.
		String firstLine = read.toString();
		// Stores the program name.
		MachineMain.machineModel.programName = firstLine.substring(1, 7);
		// cuts off the last four hex characters.

		String startValue = firstLine.substring(7, 11);
		String memorySize = firstLine.substring(POS_OF_INT, 15);

		// converts the hex to figure out how many memory spaces there are.
		int decValueMem = Integer.parseInt(memorySize, 16);
		int decValueStart = Integer.parseInt(startValue, 16);
		int decTotalMem = decValueMem + decValueStart;

		// while the file still has input read every line and put it into an
		// array
		while (read != null) {
			int counter = 2;

			read = file.readLine();

			if (read.substring(0, 1).equals("E")) {
				//i think we changed this and it made it worse????
				if (!(Utility.HexToDecimalValue(read.substring(1, 5)) >= decValueStart)
						|| !(Utility.HexToDecimalValue((read.substring(1, 5))) <= decTotalMem)) {
					return "The end record is out of the allocated bounds.";
				}
				break;
			}
			if (read.charAt(0) != 'T') {
				return "The text record is incorrect in the file on line " +  counter +
				" it does not begin with a T.";
			}

			if (read.length() != 9) {
				return "The text record has an error on line " + counter + " the length does"
				+ " not equal 9.";
			}
			if (counter > decValueMem) {
				return "The textRecords are more than the maximum allocated space";
			}
		

			count = 0;
			while (count < 9) {
				char ch = read.charAt(count);
				if (Character.isLowerCase(ch) || ch == ' ') {
					return "The text record is incorrect on line  " +  counter + " it is" +
							"not all uppercase or it includes a space";
				}
				count++;
			}
			String textInstructions = read.toString();
			String memoryPos = textInstructions.substring(1, 5);
			int decMemPos = Integer.parseInt(memoryPos, 16);
			//
			//or did we change this one??
			//
			if (decMemPos >= decTotalMem || decMemPos < decValueStart) {
				return "The value is out of the allocated memory.";
			}
			String textData = textInstructions.substring(5, 9);

			// convert memory to pos in array then add it
			MachineMain.machineModel.memoryArray[decMemPos] = textData;
			counter++;
		}
		// Set the program counter to the first line to be executed.
		MachineMain.machineModel.programCounter = read.substring(1, 5);
		return null;
	}

}
