package Loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import Assembler.Utility;

/**
 * 
 * @author Ben Trivett
 * 
 */

public class LLPassTwo {

	/**
	 * Builds an absolute object file (with the name of the first file in the
	 * array + FINAL) from all the object files inputed.
	 * 
	 * @param names
	 * @return any errors that occur or null for successful execution.
	 * @throws IOException
	 */
	public static String output(String[] names) throws IOException {
		// Receive the String array from the assemble with all the file names
		// Remove the first name
		String firstFile = names[0];
		String read = "";
		String end = "";

		int PLA = Utility.HexToDecimalValue(WilevenLoader.IPLA);
		int total = WilevenLoader.totalSegLength;

		// read from the first file inputed
		FileReader reader = new FileReader(firstFile);
		BufferedReader file = new BufferedReader(reader);

		// append FINAL to end of first file's name for the output file.
		int firstFileIndex = firstFile.indexOf(".o");
		firstFile = firstFile.substring(0, firstFileIndex) + "FINAL.o";
		WilevenLoader.outputFileName = firstFile;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				firstFile));

		// read in the first line
		read = file.readLine();
		String header = "";

		// change the letter to H to match simulator object file format
		if (read.charAt(0) == 'G') {
			header = 'H'
					+ read.substring(1, 7)
					+ Utility.DecimalValueToHex(PLA
							+ Utility.HexToDecimalValue(read.substring(7, 11)))
					+ Utility.DecimalValueToHex(total);
		} else {
			header = read;
		}

		// write it to the new file
		bufferedWriter.write(header);
		bufferedWriter.newLine();

		// read the next line
		read = file.readLine();
		while (read.charAt(0) != 'E') {
			if (read.charAt(0) == 'S') {
				// Do nothing. S lines are ignored.
			} else {
				// Update the load address with the PLA
				read = read.substring(0, 1)
						+ Utility.DecimalValueToHex(Utility
								.HexToDecimalValue(read.substring(1, 5)) + PLA)
						+ read.substring(5);

				// check to see if it is a P and change it to a T and update the
				// last 9 bits with the PLA
				if (read.charAt(0) == 'P') {
					read = 'T'
							+ read.substring(1, 5)
							+ Utility
									.BinaryToHex(Utility.HexToBinary(
											read.substring(5)).substring(0, 7)
											+ Utility
													.HexToBinary(
															Utility.DecimalValueToHex((Utility
																	.HexToDecimalValue(Utility
																			.BinaryToHex(Utility
																					.HexToBinary(
																							read.substring(5))
																					.substring(
																							7))) + PLA)))
													.substring(7));
					// write it to the new file
					bufferedWriter.write(read);
					bufferedWriter.newLine();
				} else if (read.charAt(0) == 'S') {
					// Ignored, they are no longer needed.
				} else if (read.charAt(0) == 'X') {
					// Update the last 9 bits with the value of the external
					// symbol
					String extSymbol = read.substring(9);
					if (WilevenLoader.machineTables.externalSymbolTable
							.containsKey(extSymbol)) {
						extSymbol = WilevenLoader.machineTables.externalSymbolTable
								.get(extSymbol);
						read = read.substring(0, 9);
					} else {
						return "The external symbol " + extSymbol
								+ " was not defined in " + names[0] + ".";
					}
					read = 'T'
							+ read.substring(1, 5)
							+ Utility.BinaryToHex(Utility.HexToBinary(
									read.substring(5)).substring(0, 7)
									+ Utility.HexToBinary(extSymbol).substring(
											7));
					// write it to the new file
					bufferedWriter.write(read);
					bufferedWriter.newLine();
				}
			}
			// read in the next line
			read = file.readLine();
		}

		// Build end record by adding IPLA to it's value.
		end = "E"
				+ Utility.DecimalValueToHex(Utility.HexToDecimalValue(read
						.substring(1)) + PLA);

		int count = 1;
		// while there are more than 1 file keep reading them
		while (count < names.length) {

			// Update PLA with the segment length of the last file.
			PLA += WilevenLoader.place[count - 1];

			// read from the next file
			reader = new FileReader(names[count]);
			file = new BufferedReader(reader);

			// read the header
			read = file.readLine();
			// read in the first record
			read = file.readLine();

			while (read.charAt(0) != 'E') {
				if (read.charAt(0) == 'S') {
					// Do nothing. S lines are ignored.
				} else {
					// Update the load address with the PLA
					read = read.substring(0, 1)
							+ Utility.DecimalValueToHex(Utility
									.HexToDecimalValue(read.substring(1, 5))
									+ PLA) + read.substring(5);

					// check to see if it is a P and change it to a T and update
					// the
					// last 9 bits with the PLA
					if (read.charAt(0) == 'P') {
						read = 'T'
								+ read.substring(1, 5)
								+ Utility
										.BinaryToHex(Utility.HexToBinary(
												read.substring(5)).substring(0,
												7)
												+ Utility
														.HexToBinary(
																Utility.DecimalValueToHex((Utility
																		.HexToDecimalValue(Utility
																				.BinaryToHex(Utility
																						.HexToBinary(
																								read.substring(5))
																						.substring(
																								7))) + PLA)))
														.substring(7));
						// write it to the new file
						bufferedWriter.write(read);
						bufferedWriter.newLine();

					} else if (read.charAt(0) == 'T') {
						// write it to the new file
						bufferedWriter.write(read);
						bufferedWriter.newLine();

					} else if (read.charAt(0) == 'X') {
						// Update the last 9 bits with the value of the external
						// symbol
						String extSymbol = read.substring(9);
						if (WilevenLoader.machineTables.externalSymbolTable
								.containsKey(extSymbol)) {
							extSymbol = WilevenLoader.machineTables.externalSymbolTable
									.get(extSymbol);
							read = read.substring(0, 9);
						} else {
							return "The external symbol " + extSymbol
									+ " was not defined in " + names[count]
									+ ".";
						}
						read = 'T'
								+ read.substring(1, 5)
								+ Utility.BinaryToHex(Utility.HexToBinary(
										read.substring(5)).substring(0, 7)
										+ Utility.HexToBinary(extSymbol)
												.substring(7));
						// write it to the new file
						bufferedWriter.write(read);
						bufferedWriter.newLine();
					}
				}
				// read in the next line
				read = file.readLine();
			}
			count++;

		}

		// write the end record
		bufferedWriter.write(end);
		bufferedWriter.newLine();

		bufferedWriter.close();
		reader.close();
		file.close();

		return null;
	}
}