package Loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import Assembler.Utility;

public class LLPassOne {

	public static String passOne(String[] names) throws IOException {
		int total = WilevenLoader.totalSegLength;
		int PLA = 0;
		int newPLA = 0;
		String firstHeader = "";
		String read = "";
		String end = "";

		// read the first file
		FileReader reader = new FileReader(names[0]);
		BufferedReader file = new BufferedReader(reader);

		// read the first line
		read = file.readLine();

		// to get the load location
		firstHeader = overSubstring(read, 7, 10);
		// check to see if it doesnt equal 0000 this means its absolute
		if (!firstHeader.contentEquals("0000")) {
			if (names.length > 1) {
				return "There can not be mutiple files if one is absolute.";
			}
			// put the value of the start to the IPLA
			WilvenLoader.IPLA = firstHeader;
		} else {
			// get the user input for the start address
			System.out.print("Please enter a four digit "
					+ "load location in HEX and hit enter: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			WilevenLoader.IPLA = null;
			try {
				WilevenLoader.IPLA = br.readLine();
			} catch (IOException e) {
				System.out.println("Error!");
				System.exit(1);
			}

			// check to make sure they enter a address that lets it all fit on
			// one page
			while ((Utility.HexToDecimalValue(WilevenLoader.IPLA) + total) > 65535) {
				System.out
						.print("Please enter another four digit, that is smaller "
								+ "load location in HEX and hit enter: ");
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						System.in));
				WilevenLoader.IPLA = null;
				try {
					WilevenLoader.IPLA = br1.readLine();
				} catch (IOException e) {
					System.out.println("Error!");
					System.exit(1);
				}

			}
		}

		// read the next line
		read = file.readLine();
		while (read.charAt(0) == 'S') {
			// set the location
			PLA = WilevenLoader.IPLA + newPLA;
			// add the length of the segment to the loaction
			newPLA += WilevenLoader.place[0];
			// get the index of the equals sign
			int index = read.indexOf('=');
			// get the name of the symbol
			String name = overSubstring(read, 1, index);
			// get the value of the symbol
			String loc = overSubstring(read, index, index + 4);
			// update the pla with the new value
			String updatedPLA = Utility.DecimalValueToHex(Utility
					.HexToDecimalValue(WilevenLoader.IPLA)
					+ Utility.HexToDecimalValue(loc));
			// put them into the external symbol table
			WilevenLoader.externalSymbolTable.put(name, updatedPLA);
			// check to see if it is already defined
			if (WilevenLoader.externalSymbolTable.containsKey(name)) {
				return "The .ENT " + name + " symbol has already been defined";
			}
			// read the next line
			read = file.readLine();
		}

		int count = 1;
		// if there are more than one file
		while (names.length > count) {

			reader = new FileReader(names[count]);
			file = new BufferedReader(reader);

			// read the first line header record
			read = file.readLine();
			// read the next line the first text record
			read = file.readLine();
			while (read.charAt(0) == 'S') {
				// set the location
				PLA = WilevenLoader.IPLA + newPLA;
				// add the length of the segment to the loaction
				newPLA += WilevenLoader.place[0];
				// get the index of the equals sign
				int index = read.indexOf('=');
				// get the name of the symbol
				String name = overSubstring(read, 1, index);
				// get the value of the symbol
				String loc = overSubstring(read, index, index + 4);
				// update the pla with the new value
				String updatedPLA = Utility.DecimalValueToHex(Utility
						.HexToDecimalValue(WilevenLoader.IPLA)
						+ Utility.HexToDecimalValue(loc));
				// put them into the external symbol table
				WilevenLoader.externalSymbolTable.put(name, updatedPLA);
				// check to see if it is already defined
				if (WilevenLoader.externalSymbolTable.containsKey(name)) {
					return "The .ENT " + name
							+ " symbol has already been defined";
				}
				// read the next line
				read = file.readLine();
			}
			//get the next file
			count++;
		}

		return null;

	}

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
