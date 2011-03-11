package Loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Assembler.Utility;

/**
 * 
 * @author BSchuck
 * 
 */
public class LLPassOne {
	/**
	 * 
	 * @param names
	 * @return
	 * @throws IOException
	 */
	public static String passOne(String[] names) throws IOException {
		int PLA = Utility.HexToDecimalValue(WilevenLoader.IPLA);
		int total = WilevenLoader.totalSegLength;
		String read = "";

		// get the user input for the start address
		System.out.print("Please enter a four digit "
				+ "load location in HEX and hit enter: ");
		Boolean isNotCorrectInput = true;
		while (isNotCorrectInput) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String readIPLA = br.readLine();

			// Make sure they entered a hex address
			if (!Utility.isHexString(readIPLA)) {
				System.out.print("Please enter a valid 4 digit HEX number: ");

				// check to make sure they enter a address that lets it all fit
				// on one page
			} else if ((Utility.HexToDecimalValue(readIPLA) + total) > 65535) {
				System.out
						.print("Please enter a smaller HEX number and hit enter: ");
			} else {
				WilevenLoader.IPLA = readIPLA;
				isNotCorrectInput = false;
			}
		}
		
		// iterate through all files and add data to external symbol table
		int count = 0;
		while (count < names.length) {

			FileReader reader = new FileReader(names[count]);
			BufferedReader file = new BufferedReader(reader);
			
			// read the first line header record
			read = file.readLine();
			// read the next line the first text record
			read = file.readLine();

			while (read.charAt(0) != 'E') {
				if (read.charAt(0) == 'S') {
					// get the index of the equals sign
					int index = read.indexOf('=');
					// get the name of the symbol
					String name = overSubstring(read, 1, index);
					// get the value of the symbol
					String loc = overSubstring(read, index + 1, index + 5);
					// update the pla with the new value
					String updatedPLA = Utility.DecimalValueToHex(PLA
							+ Utility.HexToDecimalValue(loc));
					// check to see if it is already defined
					if (WilevenLoader.machineTables.externalSymbolTable
							.containsKey(name)) {
						return "The .ENT symbol " + name
								+ " has already been defined";
					}
					// put them into the external symbol table
					WilevenLoader.machineTables.externalSymbolTable.put(name,
							updatedPLA);
				}
				// read the next line
				read = file.readLine();
			}
			
			// Close readers for current file.
			reader.close();
			file.close();
			
			// add length of previous segment
			PLA += WilevenLoader.place[count];
			
			// get the next file
			count++;
		}

		return null;
	}

	/**
	 * 
	 * @param str
	 * @param x
	 * @param y
	 * @return
	 */
	private static String overSubstring(String str, int x, int y) {
		Boolean exceptions = true;
		int z = 0;
		while (exceptions && x != y) {
			exceptions = false;
			try {
				str.substring(x, y);
			} catch (Exception e) {
				exceptions = true;
				y--;
				z++;
			}
		}
		String temp = str.substring(x, y);
		while (z > 0) {
			temp = temp + " ";
			z--;
		}
		return temp;
	}

}
