package Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Simulator.Utility;

import Assembler.Tables;

/**
 * This class controls the loader and displays errors that occur.
 * 
 * @author Ben Trivett
 */
public class WilevenLoader {

	public static String IPLA = null;
	public static int[] place = null;
	public static int totalSegLength = 0;
	public static Tables machineTables;
	public static Boolean warnings = true;

	/**
	 * @param args
	 *            The first argument is for the name of the assembly object
	 *            file. The second (and third, and so on) argument (optional) is
	 *            for the name of the object file to be linked.
	 * @throws IOException
	 * @author Ben Trivett
	 */
	public static void main(String[] args) throws IOException {
		link(args);
	}

	/**
	 * Links the object files and outputs a single absolute object file with the
	 * name: "<first file in the file list>FINAL.o".
	 */
	public static void link(String[] args) throws IOException{
		// Make sure the arguments are well formed.
		String[] fileNames = checkArgs(args);

		// Instantiate the tables.
		WilevenLoader.machineTables = new Tables();
		WilevenLoader.place = new int[args.length];

		int count = 0;
		while (count < fileNames.length) {
			// Make sure the object files exist.
			File inputFile = new File(fileNames[count]);
			boolean fileExists = inputFile.exists();
			if (fileExists == false) {
				System.out.println("ERROR: The file "
						+ fileNames[count] + " does not exist.");
				System.exit(0);
			}
			String read = new BufferedReader(new FileReader(fileNames[count])).readLine();
			if (read.charAt(0) != 'G' && read.charAt(0) == 'H' && fileNames.length > 1) {
				System.out.println("ERROR: Cannot use absolute programs when " 
						+ "linking two or more files.");
				System.exit(0);
			}
			if (read.substring(11).length() == 4 
					&& Utility.isHexString(read.substring(11))) {
				place[count] = Utility.HexToDecimalValue(read.substring(11));
			} else {
				System.out.println("ERROR: Invalid format for segment length in file "
						+ fileNames[count] + " .");
				System.exit(0);
			}
			totalSegLength += place[count];
			count++;
		}
		
		// Execute pass one.
		String firstPassError = LLPassOne.passOne(fileNames);

		// If the first pass ended abruptly and returned an error, display it
		// and exit.
		if (firstPassError != null) {
			System.out.println("ERROR: " + firstPassError);
			System.exit(0);
		}

//		// Execute pass two.
//		String secondPassError = PassTwo.output(fileNames[1], fileNames[2],
//				machineTables);
//
//		// If the second pass ended abruptly and returned an error, display it
//		// and exit.
//		if (secondPassError != null) {
//			System.out.println("ERROR: " + secondPassError);
//			System.exit(0);
//		}
	}
	/**
	 * Makes sure there is at least one argument. If not, it produces an error
	 * message that also specifies the correct format of the arguments to be
	 * passed in.
	 * 
	 * @param args
	 *            The arguments used on the Wileven Loader.
	 * @return A string array of with the same contents as the args array passed
	 *         in, unless the args array length is 0. In that case, the program
	 *         exits and an error message is displayed.
	 */
	private static String[] checkArgs(String[] args) {
		String[] fileNames = new String[args.length];

		if (fileNames.length == 0) {
			System.out.println("Error: No arguments detected.");
			System.out
					.println("Format of a properly formatted call is as follows:");
			System.out.println("java WilevenLoader file1.o file2.o "
					+ "(Note: number of files is NOT limited to 2");
			System.exit(0);
		}

		int count = 0;
		while (count < fileNames.length) {
			fileNames[count] = args[count];
			if (WilevenLoader.warnings && !(fileNames[count].contains(".o"))) {
				System.out.println("Warning: Argument Number " + count + 1
						+ " file extension is not .o");
			}
			count++;
		}

		return fileNames;
	}
}