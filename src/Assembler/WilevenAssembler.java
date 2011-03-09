package Assembler;


import java.io.IOException;


/**
 * This class controls the assembler and displays errors that occur.
 * 
 * @author Ben Trivett
 */
public class WilevenAssembler {

	/**
	 * @param args
	 *            The first argument is for the name of the assembly source code
	 *            file. The second argument (optional) is for the name of the
	 *            object file. The third argument (optional) is for the name of
	 *            the pretty printed file.
	 * @throws IOException
	 * @author Ben Trivett
	 */
	public static void main(String[] args) throws IOException {
		// Make sure the arguments are well formed.
		String[] fileNames = checkArgs(args);

		// Instantiate the tables.
		Tables machineTables = new Tables();

		// Execute pass one.
		String firstPassError = PassOne.run(fileNames[0], machineTables);

		// If the first pass ended abruptly and returned an error, display it
		// and exit.
		if (firstPassError != null) {
			System.out.println("ERROR: " + firstPassError);
			System.exit(0);
		}

		// Execute pass two.
		String secondPassError = PassTwo.output(fileNames[1], fileNames[2],
				machineTables);

		// If the second pass ended abruptly and returned an error, display it
		// and exit.
		if (secondPassError != null) {
			System.out.println("ERROR: " + secondPassError);
			System.exit(0);
		}
	}

	/**
	 * Makes sure the arguments that the Wileven Assembler was called with are
	 * properly formed. If they aren't, errors will be displayed and the program
	 * will exit. If they are then a string array with the names of the files is
	 * returned.
	 * 
	 * @param args
	 *            The arguments used on the Wileven Assembler.
	 * @return A string array of length 3 with the source file location in
	 *         position 0, the object file name in position 1, and the pretty
	 *         print file name in position 2.
	 */
	private static String[] checkArgs(String[] args) {
		String[] fileNames = new String[3];
		// Make sure the source file location was the in first argument.
		if (args.length > 0 && args.length < 4) {
			if (args[0].length() == 0) {
				System.out
						.println("ERROR: Source code file location argument is empty.");
				System.exit(0);
			}
			// If the object file argument was empty then use the source file
			// name.
			String objOutName;
			Boolean hasObjName = false;
			if (args.length < 2) {
				// Checked first to avoid out of bounds error.
				// Does not have an object file name.
			} else if (args.length > 1 && args[2].length() == 0) {
				// Does not have an object file name.
			} else {
				hasObjName = true;
			}
			if (!hasObjName) {
				// Find the dot to replace the file extension with ".o".
				int dotLocation = args[0].indexOf(".");
				if (dotLocation == -1) {
					dotLocation = args[1].length();
				}
				objOutName = args[0].substring(0, dotLocation) + ".o";
			} else {
				objOutName = args[1];
			}

			// If the object file argument was empty then use the source file
			// name.
			String ppOutName;
			Boolean hasPpName = false;
			if (args.length < 3) {
				// Checked first to avoid out of bounds error.
				// Does not have a pretty print file name.
			} else if (args[2].length() == 0) {
				// Does not have a pretty print file name.
			} else {
				hasPpName = true;
			}
			if (!hasPpName) {
				// Find the dot to replace the file extension with ".lst".
				int dotLocation = args[0].indexOf(".");
				if (dotLocation == -1) {
					dotLocation = args[0].length();
				}
				ppOutName = args[0].substring(0, dotLocation) + ".lst";
			} else {
				ppOutName = args[2];
			}
			// Check for file extension requirements.
			if (!(objOutName.contains(".o"))) {
				System.out
						.println("Warning: The object file extension is not .o");
			}
			if (!(ppOutName.contains(".lst"))) {
				System.out
						.println("Warning: The pretty print file extension is not .lst");
			}
			// No errors, load names into the array and return them.
			fileNames[0] = args[0];
			fileNames[1] = objOutName;
			fileNames[2] = ppOutName;
		} else {
			System.out.println("ERROR: Incorrect number of arguments.");
			System.exit(0);
		}
		return fileNames;
	}
}