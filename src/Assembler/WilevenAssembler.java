package Assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import Loader.WilevenLoader;

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
		String[] fileArray = null;
		if (args.length > 0) {
			if (args[0].toLowerCase().contains(".batch")) {
				// Make sure the batch file with file locations exists.
				if (args.length > 0) {
					File tempFile = new File(args[0]);
					if (!tempFile.exists()) {
						System.out.println("ERROR: The batch file " + args[0]
								+ " does not exist.");
						System.exit(0);
					}
				}
				FileReader reader = new FileReader(args[0]);
				BufferedReader file = new BufferedReader(reader);
				ArrayList<String> stringArray = new ArrayList<String>();

				String read = file.readLine();
				// Read in file names and check to see if they exist.
				while (read != null) {
					File tempFile = new File(read);
					if (!tempFile.exists()) {
						System.out.println("ERROR: The file " + read
								+ " does not exist.");
						System.exit(0);
					}
					stringArray.add(read);
					read = file.readLine();
				}
				fileArray = stringArray.toArray(new String[0]);
			} else {
				int count = 0;
				// Read in file names and check to see if they exist.
				while (count < args.length) {
					String read = args[count];
					File tempFile = new File(read);
					if (!tempFile.exists()) {
						System.out.println("ERROR: The file " + read
								+ " does not exist.");
						System.exit(0);
					}
					count++;
				}
				fileArray = args;
			}
		} else {
			System.out
					.println("ERROR: Invalid argument format. Correct usage: +"
							+ "\"<batchFile.batch>\" OR \"<file1> <file2> <file3>...\"");
			System.exit(0);
		}

		// Make an array of arrays of file names.
		String[][] allFileNames = new String[fileArray.length][3];
		Tables[] allTables = new Tables[fileArray.length];
		int count = 0;

		// Run pass one on all of the files to build the entire external symbol
		// table.
		while (count < fileArray.length) {

			// Add the .o and .lst file names to the array of file names for a
			// file.
			allFileNames[count] = checkArgs(fileArray[count]);

			// Instantiate the tables.
			allTables[count] = new Tables();

			// Execute pass one.
			String firstPassError = PassOne.run(allFileNames[count][0],
					allTables[count]);

			// If the first pass ended abruptly and returned an error, display
			// it and exit.
			if (firstPassError != null) {
				System.out.println("ERROR in file " + allFileNames[count][0]
						+ ": " + firstPassError);
			}

			// Execute pass two.
			String secondPassError = PassTwo.output(allFileNames[count][1],
					allFileNames[count][2], allTables[count]);

			// If the second pass ended abruptly and returned an error, display
			// it and exit.
			if (secondPassError != null) {
				System.out.println("ERROR in file " + allFileNames[count][0]
						+ ": " + secondPassError);
			}
			count++;
		}

		// Generate string array of all the object file names.
		String[] names = new String[allFileNames.length];
		count = 0;
		while (count < allFileNames.length) {
			names[count] = allFileNames[count][1];
			count++;
		}

		// Link all of the object files and build a single object file.
		WilevenLoader.link(names);
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
	private static String[] checkArgs(String args) {
		String[] fileNames = new String[3];
		// If the object file argument was empty then use the source file
		// name.
		String objOutName;

		// Find the dot to replace the file extension with ".o".
		int dotLocation = args.indexOf(".");
		if (dotLocation == -1) {
			dotLocation = args.length();
		}
		objOutName = args.substring(0, dotLocation) + ".o";

		// If the object file argument was empty then use the source file
		// name.
		String ppOutName;

		// Find the dot to replace the file extension with ".lst".
		dotLocation = args.indexOf(".");
		if (dotLocation == -1) {
			dotLocation = args.length();
		}
		ppOutName = args.substring(0, dotLocation) + ".lst";

		// No errors, load names into the array and return them.
		fileNames[0] = args;
		fileNames[1] = objOutName;
		fileNames[2] = ppOutName;

		return fileNames;
	}
}