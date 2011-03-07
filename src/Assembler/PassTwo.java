package Assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * This class contains the output method that executes the second pass of
 * assembly.
 * 
 * @author Ben Trivett, Bryant Schuck
 */
public class PassTwo {
	/**
	 * This is the main method of the PassTwo
	 * class and performs all of the operations in the class.
	 * 
	 * @param objOutName
	 * @param ppOutName
	 * @param machineTables
	 * @return
	 * @throws IOException
	 */
	public static String output(String objOutName, String ppOutName,
			Tables machineTables) throws IOException {

		// Initialize input and output files.
		StringBuffer stringBuffer = new StringBuffer();
		FileReader reader = new FileReader("intermediate.txt");
		BufferedReader file = new BufferedReader(reader);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				objOutName));
		BufferedWriter prettyPrint = new BufferedWriter(new FileWriter(
				ppOutName));
		String textRecord = null;
		String pTextRecord = null;

		// Read in first line from the intermediate file.
		String read = file.readLine();
		Integer lineCounter = Integer.parseInt(read.substring(0,
				read.indexOf(":")));
		read = read.substring(read.indexOf(":") + 1);

		// Use the label of the .ORIG as the name of the program.
		String name = overSubstring(read, 0, 6);

		// Use starting location determined from the .ORIG operation in pass one.
		String start = machineTables.symbolTable.get(name)[0];
		
		// Determine the length of the program's footprint by subtracting the
		// starting location from the final location counter.
		int adress = Utility.HexToDecimalValue(start);
		String length = Utility.DecimalValueToHex(machineTables.locationCounter
				- adress + 1);
		
		if (length.equals("0000")) {
			return "The program has a length of 0";
		}
		
		// Starting location cannot be inside the literal table
		if (Utility.HexToDecimalValue(machineTables.startingLocation) >= machineTables.startOfLiteralTable){
			return "Starting location cannot be inside of the literal table.";
		}
		
		if (length.equals("FFFF") && start.equals("0000") && machineTables.isRelative) {
			return "The program has a length is greater than xFFFF";
		}
		
		// Set the header letter based on relocation/absolute
		String header = "H";
		if (machineTables.isRelative){
			header = "G";
		}

		String headerRecord = header + name + start + length;
		// Write the header record to the object file.
		bufferedWriter.write(headerRecord);
		bufferedWriter.newLine();

		// Write the header record to pretty print.
		prettyPrint.write("                             " + "(" + lineCounter
				+ ") " + headerRecord);
		prettyPrint.newLine();

		// Read second line of the intermediate file.
		read = file.readLine();
		lineCounter = Integer.parseInt(read.substring(0, read.indexOf(":")));
		read = read.substring(read.indexOf(":") + 1);

		while (read != null) {
			String[] op;
			String binary = "";
			int count = 0;
			stringBuffer = new StringBuffer();

			// Get the operands.
			String operands = "";
			if (read.length() > 17) {
				operands = read.substring(17);
			}
			String delims = ",";

			// Remove whitespace from operands.
			op = operands.split(delims);
			int counterOp = 0;
			while (counterOp < op.length) {
				op[counterOp] = op[counterOp].trim();
				counterOp++;
			}

			// Get the operation and find out which one it is.
			String operations = overSubstring(read, 9, 14);
			if (machineTables.machineOpTable.containsKey(operations)) {

				// Check if the operation is a branch
				if (operations.substring(0, 2).equals("BR")) {
					if (operations.equals("BR   ")) {
						binary = "0000000";
					} else if (operations.equals("BRN  ")) {
						binary = "0000100";
					} else if (operations.equals("BRZ  ")) {
						binary = "0000010";
					} else if (operations.equals("BRP  ")) {
						binary = "0000001";
					} else if (operations.equals("BRNZ ")) {
						binary = "0000110";
					} else if (operations.equals("BRNP ")) {
						binary = "0000101";
					} else if (operations.equals("BRZP ")) {
						binary = "0000011";
					} else if (operations.equals("BRNZP")) {
						binary = "0000111";
					} else {
						return "The operation " + operations
								+ " is undefined on line " + lineCounter + ".";
					}

					// BR operations must have only 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the operand is a symbol, hex, or decimal value,
					// get that value in decimal form.
					Boolean usedRelativeSymbol = false;
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							usedRelativeSymbol = true;
						}
					} else if (op[0].charAt(0) == 'x') {
						bin = Integer.parseInt(op[0].substring(1), 16);
					} else if (op[0].charAt(0) == '#') {
						bin = Integer.parseInt(op[0].substring(1));

						// Branch operands can only be symbols, hex, or decimal.
					} else {
						return "Operand has invalid formatting on line "
								+ lineCounter + ".";
					}

					// pgoffset9 values must be in the range x0-xFFFF.
					if (bin < 0 || bin > 65535) {
						return "Operand has value out of range on line "
								+ lineCounter + ".";
					}

					// Make sure branch does not go outside of the PC's current
					// page.
					op[0] = Utility.HexToBinary(Utility.DecimalValueToHex(bin));
					String pcBinary = Utility.HexToBinary((Utility
							.DecimalValueToHex(adress + 1)));
					if (!pcBinary.substring(0, 7).equals(op[0].substring(0, 7))) {
						return "Branching to different page of memory on line "
								+ lineCounter + ".";
					}

					// Truncate the first seven bits and combine with the rest
					// of the binary.
					op[0] = op[0].substring(7);
					binary = binary + op[0];
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					adress += 1;

					// Build the text record.
					if (usedRelativeSymbol) {
						stringBuffer.append("P").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					} else {
						stringBuffer.append("T").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					}

					// Write the text record to the object and pretty print
					// files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a NOP
				} else if (operations.equals("NOP  ")) {
					binary = "0000000000000000";

					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					adress += 1;

					// Build the text record. Always absolute.
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();

					// Write the text record to the object and pretty print
					// files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is an ADD or AND
				} else if (operations.equals("ADD  ")
						|| operations.equals("AND  ")) {
					if (operations.equals("ADD  ")) {
						binary = "0001";
					} else {
						binary = "0101";
					}

					// ADD and AND operations must have exactly 3 operands.
					if (op.length != 3) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the DR is a symbol or a register,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'R') {
						bin = Integer.parseInt(op[0].substring(1));

						// Add's first operand can only be symbols or registers.
					} else {
						return "Desitination register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Destination register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Destination register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String DR = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the SR1 is a symbol or a register,
					// get that value in decimal form.
					if (machineTables.symbolTable.containsKey(op[1])) {
						String[] temp = machineTables.symbolTable.get(op[1]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[1]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[1].charAt(0) == 'R') {
						bin = Integer.parseInt(op[1].substring(1));

						// Add/And's second operand can only be symbols or
						// registers.
					} else {
						return "Source register 1 is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Source register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Source register 1 value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String SR1 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the SR2 is a symbol, register, decimal, or hex,
					// get that value in decimal form.
					Boolean isImmediate = false;
					if (machineTables.symbolTable.containsKey(op[2])) {
						String[] temp = machineTables.symbolTable.get(op[2]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[2]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
						if (bin < -16 || bin > 31) {
							return "imm5 out of range on line " + lineCounter
									+ ".";
						}
						isImmediate = true;
					} else if (op[2].charAt(0) == 'R') {
						bin = Integer.parseInt(op[2].substring(1));
						// Source register values must be in the range 0-7.
						if (bin < 0 || bin > 7) {
							return "Source register 2 value not in the range 0-7 on line "
									+ lineCounter + ".";
						}
					} else if (op[2].charAt(0) == 'x') {
						bin = Integer.parseInt(op[2].substring(1), 16);
						if (bin < 0 || bin > 31) {
							return "imm5 out of range on line " + lineCounter
									+ ".";
						}
						isImmediate = true;
					} else if (op[2].charAt(0) == '#') {
						bin = Integer.parseInt(op[2].substring(1));
						if (bin < -16 || bin > 15) {
							return "imm5 out of range on line " + lineCounter
									+ ".";
						}
						isImmediate = true;
					} else {
						return "Operand has invalid format on line "
								+ lineCounter + ".";
					}

					String SR2 = "";
					if (isImmediate) {
						// Convert to 5 bits of 2's complement.
						bin = Utility.convertToTwosComplement(bin);
						SR2 = "1"
								+ Utility.HexToBinary(
										Utility.DecimalValueToHex(bin))
										.substring(11);
					} else {
						// Convert the decimal value to 3 digit binary.
						SR2 = "000"
								+ Utility.HexToBinary(
										Utility.DecimalValueToHex(bin))
										.substring(13);
					}

					// Build text record.
					binary = binary + DR + SR1 + SR2;
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a LD, LDI, ST, STI, LEA
				} else if (operations.equals("LD   ")
						|| operations.equals("LDI  ")
						|| operations.equals("ST   ")
						|| operations.equals("STI  ")
						|| operations.equals("LEA  ")) {
					if (operations.equals("LD   ")) {
						binary = "0010";
					} else if (operations.equals("LDI  ")) {
						binary = "1010";
					} else if (operations.equals("ST   ")) {
						binary = "0011";
					} else if (operations.equals("STI  ")) {
						binary = "1011";
					} else if (operations.equals("LEA  ")) {
						binary = "1110";
					} else {
						return "OMGWTFBBQ IMPOSSIBLE?!?!";
					}

					// These operations must have exactly 2 operands.
					if (op.length != 2) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the DR is a symbol or a register,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'R') {
						bin = Integer.parseInt(op[0].substring(1));

						// The first operand can only be symbols or registers.
					} else {
						return "Desitination register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Destination register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Destination register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String DR = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the operand is a symbol, hex, or decimal value,
					// get that value in decimal form.
					Boolean usedRelativeSymbol = false;
					if (machineTables.symbolTable.containsKey(op[1])) {
						String[] temp = machineTables.symbolTable.get(op[1]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							usedRelativeSymbol = true;
						}

						// Only LD operands can have a literal in the second
						// operand.
					} else if (op[1].charAt(0) == '=') {
						if (machineTables.literalTable.containsKey(op[1]
								.substring(1)) && operations.equals("LD   ")) {
							String[] temp = machineTables.literalTable
									.get(op[1].substring(1));
							bin = Utility.HexToDecimalValue(temp[1]);
						} else {
							return "Literal not found in literal table. Fire your programmer.";
						}

					} else if (op[1].charAt(0) == 'x') {
						bin = Integer.parseInt(op[1].substring(1), 16);
					} else if (op[1].charAt(0) == '#') {
						bin = Integer.parseInt(op[1].substring(1));

						// LD operands can only be symbols, hex, or decimal.
					} else {
						return "Operand has invalid formatting on line "
								+ lineCounter + ".";
					}

					// pgoffset9 values must be in the range x0-xFFFF.
					if (bin < 0 || bin > 65535) {
						return "Operand has value out of range on line "
								+ lineCounter + ".";
					}

					// Make sure to not go outside of the PC's current page.
					op[1] = Utility.HexToBinary(Utility.DecimalValueToHex(bin));
					String pcBinary = Utility.HexToBinary((Utility
							.DecimalValueToHex(adress + 1)));
					if (!pcBinary.substring(0, 7).equals(op[1].substring(0, 7))) {
						return "Accessing different page of memory on line "
								+ lineCounter + ".";
					}

					// Truncate the first seven bits and combine with the rest
					// of the binary.
					op[1] = op[1].substring(7);
					binary = binary + DR + op[1];
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					adress += 1;

					// Build the text record.
					if (usedRelativeSymbol) {
						stringBuffer.append("P").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					} else {
						stringBuffer.append("T").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					}

					// Write the text record to the object and pretty print
					// files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a JSR or JMP
				} else if (operations.equals("JSR  ")
						|| operations.equals("JMP  ")) {

					// JSR has L bit set to 1.
					if (operations.equals("JSR  ")) {
						binary = "0100100";
					} else {
						binary = "0100000";
					}

					// These operations must have only 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the operand is a symbol, hex, or decimal value,
					// get that value in decimal form.
					Boolean usedRelativeSymbol = false;
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							usedRelativeSymbol = true;
						}
					} else if (op[0].charAt(0) == 'x') {
						bin = Integer.parseInt(op[0].substring(1), 16);
					} else if (op[0].charAt(0) == '#') {
						bin = Integer.parseInt(op[0].substring(1));

						// These operands can only be symbols, hex, or decimal.
					} else {
						return "Operand has invalid formatting on line "
								+ lineCounter + ".";
					}

					// pgoffset9 values must be in the range x0-xFFFF.
					if (bin < 0 || bin > 65535) {
						return "Operand has value out of range on line "
								+ lineCounter + ".";
					}

					// Make sure pgoffset9 does not go outside of the PC's
					// current page.
					op[0] = Utility.HexToBinary(Utility.DecimalValueToHex(bin));
					String pcBinary = Utility.HexToBinary((Utility
							.DecimalValueToHex(adress + 1)));
					if (!pcBinary.substring(0, 7).equals(op[0].substring(0, 7))) {
						return "Accessing different page of memory on line "
								+ lineCounter + ".";
					}

					// Truncate the first seven bits and combine with the rest
					// of the binary.
					op[0] = op[0].substring(7);
					binary = binary + op[0];
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					adress += 1;

					// Build the text record.
					if (usedRelativeSymbol) {
						stringBuffer.append("P").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					} else {
						stringBuffer.append("T").append(hexAdress)
								.append(textRecord);
						pTextRecord = stringBuffer.toString();
					}

					// Write the text record to the object and pretty print
					// files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a LDR or STR
				} else if (operations.equals("LDR  ")
						|| operations.equals("STR  ")) {
					if (operations.equals("LDR  ")) {
						binary = "0110";
					} else {
						binary = "0111";
					}

					// LDR and STR operations must have exactly 3 operands.
					if (op.length != 3) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the DR is a symbol or a register,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'R') {
						bin = Integer.parseInt(op[0].substring(1));

						// LDR/STR's first operand can only be symbols or
						// registers.
					} else {
						return "Desitination register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Destination register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Destination register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String DR = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the BaseR is a symbol or a register,
					// get that value in decimal form.
					if (machineTables.symbolTable.containsKey(op[1])) {
						String[] temp = machineTables.symbolTable.get(op[1]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[1]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[1].charAt(0) == 'R') {
						bin = Integer.parseInt(op[1].substring(1));

						// LDR/STR's second operand can only be symbols or
						// registers.
					} else {
						return "Base register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Base register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Base register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String SR1 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the index6 is a symbol, decimal or hex,
					// get that value in decimal form.
					if (machineTables.symbolTable.containsKey(op[2])) {
						String[] temp = machineTables.symbolTable.get(op[2]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[2]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[2].charAt(0) == 'x') {
						bin = Integer.parseInt(op[2].substring(1), 16);
					} else if (op[2].charAt(0) == '#') {
						bin = Integer.parseInt(op[2].substring(1));
					} else {
						return "Operand has invalid format on line "
								+ lineCounter + ".";
					}

					// Make sure the index6 is in range.
					if (bin < 0 || bin > 63) {
						return "index6 out of range on line " + lineCounter
								+ ".";
					}

					// Convert to 6 bits of binary.
					String index6 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(10);

					// Build text record.
					binary = binary + DR + SR1 + index6;
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a DBUG or a RET
				} else if (operations.equals("DBUG ")
						|| operations.equals("RET  ")) {
					if (operations.equals("DBUG ")) {
						binary = "1000000000000000";
					} else {
						binary = "1101000000000000";
					}

					// Build text record.
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

				} else if (operations.equals("NOT  ")) {
					binary = "1001";
					// NOT operations must have exactly 2 operands.
					if (op.length != 2) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the DR is a symbol or a register,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'R') {
						bin = Integer.parseInt(op[0].substring(1));

						// NOT's first operand can only be symbols or registers.
					} else {
						return "Desitination register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Destination register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Destination register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String DR = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the source register is a symbol or a register,
					// get that value in decimal form.
					if (machineTables.symbolTable.containsKey(op[1])) {
						String[] temp = machineTables.symbolTable.get(op[1]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[1]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[1].charAt(0) == 'R') {
						bin = Integer.parseInt(op[1].substring(1));

						// NOT's second operand can only be symbols or
						// registers.
					} else {
						return "Source register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Source register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Source register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String SR1 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// Build text record.
					binary = binary + DR + SR1 + "000000";
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is JSRR or JMPR
				} else if (operations.equals("JSRR ")
						|| operations.equals("JMPR ")) {
					if (operations.equals("JSRR ")) {
						binary = "1100100";
					} else {
						binary = "1100000";
					}

					// JSRR/JMPR operations must have exactly 2 operands.
					if (op.length != 2) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the BaseR is a symbol or a register,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'R') {
						bin = Integer.parseInt(op[0].substring(1));

						// JSRR/JMPR's first operand can only be symbols or
						// registers.
					} else {
						return "Base register is not a symbol or register on line "
								+ lineCounter + ".";
					}

					// Base register values must be in the range 0-7.
					if (bin < 0 || bin > 7) {
						return "Base register value not in the range 0-7 on line "
								+ lineCounter + ".";
					}

					// Convert the decimal value to 3 digit binary.
					String baseR = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(13);

					// If the index6 is a symbol, decimal or hex,
					// get that value in decimal form.
					if (machineTables.symbolTable.containsKey(op[1])) {
						String[] temp = machineTables.symbolTable.get(op[1]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[1]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[1].charAt(0) == 'x') {
						bin = Integer.parseInt(op[1].substring(1), 16);
					} else if (op[1].charAt(0) == '#') {
						bin = Integer.parseInt(op[1].substring(1));
					} else {
						return "Operand has invalid format on line "
								+ lineCounter + ".";
					}

					// Make sure the index6 is in range.
					if (bin < 0 || bin > 63) {
						return "index6 out of range on line " + lineCounter
								+ ".";
					}

					// Convert to 6 bits of binary.
					String index6 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(10);

					// Build text record.
					binary = binary + baseR + index6;
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a TRAP.
				} else if (operations.equals("TRAP ")) {
					binary = "11110000";

					// TRAP operations must have exactly 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the trapvector8 is a symbol, decimal or hex,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'x') {
						bin = Integer.parseInt(op[0].substring(1), 16);
					} else if (op[0].charAt(0) == '#') {
						bin = Integer.parseInt(op[0].substring(1));
					} else {
						return "Operand has invalid format on line "
								+ lineCounter + ".";
					}

					// Make sure the index6 is in range.
					if (bin < 0 || bin > 255) {
						return "trapvector8 out of range on line "
								+ lineCounter + ".";
					}

					// Convert to 8 bits of binary.
					String trapvector8 = Utility.HexToBinary(
							Utility.DecimalValueToHex(bin)).substring(8);

					// Build text record.
					binary = binary + trapvector8;
					textRecord = Utility.BinaryToHex(binary);
					String hexAdress = Utility.DecimalValueToHex(adress);
					stringBuffer.append("T").append(hexAdress)
							.append(textRecord);
					pTextRecord = stringBuffer.toString();
					adress += 1;

					// Write text record to object and pretty print files.
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();
				}

				// Check if it is a pseudo operation.
			} else if (machineTables.psuedoOpTable.containsKey(operations)) {

				// The number of operands must match the operation's formating.
				if (machineTables.psuedoOpTable.get(operations) != op.length) {
					return "The pseudo operation has an incorrect number of operands on line "
							+ lineCounter + ".";
				}

				// Check if it is a .EQU.
				if (operations.equals(".EQU ")) {

					// Write to pretty print file.
					prettyPrint.write("                             " + "("
							+ lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a .BLKW.
				} else if (operations.equals(".BLKW")) {

					// .BLKW operations must have only 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the operand is a symbol, hex, or decimal value,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'x') {
						bin = Integer.parseInt(op[0].substring(1), 16);
					} else if (op[0].charAt(0) == '#') {
						bin = Integer.parseInt(op[0].substring(1));

						// .BLKW operands can only be symbols, hex, or decimal.
					} else {
						return "Operand has invalid formatting on line "
								+ lineCounter + ".";
					}

					// .BLKW values must be in the range x1-xFFFF.
					if (bin < 1 || bin > 65535) {
						return "Operand has value out of range on line "
								+ lineCounter + ".";
					}

					// Write to pretty print file.
					String hexAdress = Utility.DecimalValueToHex(adress + 1);
					prettyPrint.write("(" + hexAdress
							+ ")                       (" + lineCounter + ") "
							+ read);
					prettyPrint.newLine();

					// Increment the location counter by the .BLKW's operand.
					adress += bin;

					// Check if it is a .STRZ.
				} else if (operations.equals(".STRZ")) {

					// .STRZ operations must have only 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// Remove quotations.
					op[0] = op[0].trim().substring(1, op[0].length() - 1);

					// Make a text record for each character in the string.
					count = 0;
					while (count < op[0].length()) {

						// Convert character into hex ascii value and binary.
						String hexAdress = Utility.DecimalValueToHex(adress);
						textRecord = Utility.DecimalValueToHex((int) op[0]
								.charAt(count));
						binary = Utility.HexToBinary(textRecord);
						adress++;

						// Write to object file.
						pTextRecord = "T" + hexAdress + textRecord;
						bufferedWriter.write(pTextRecord);
						bufferedWriter.newLine();

						// Write to pretty print.
						if (count == 0) {

							prettyPrint.write("(" + hexAdress + ") "
									+ textRecord + " " + binary + " ("
									+ lineCounter + ") " + read);
							prettyPrint.newLine();
						} else {
							prettyPrint.write("(" + hexAdress + ") "
									+ textRecord + " " + binary + " ("
									+ lineCounter + ")");
							prettyPrint.newLine();
						}

						count++;
					}

					// Write a null to object file.
					String hexAdress = Utility.DecimalValueToHex(adress);
					textRecord = "0000";
					binary = "0000000000000000";
					pTextRecord = "T" + hexAdress + textRecord;
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();
					adress++;

					// Write a null to pretty print.
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ")");
					prettyPrint.newLine();

					// Check if it is a .FILL.
				} else if (operations.equals(".FILL")) {

					// .FILL operations must have only 1 operand.
					if (op.length != 1) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// If the operand is a symbol, hex, or decimal value,
					// get that value in decimal form.
					Integer bin;
					if (machineTables.symbolTable.containsKey(op[0])) {
						String[] temp = machineTables.symbolTable.get(op[0]);
						bin = Integer.parseInt(temp[0], 16);
						if (temp[1].equals("1")) {
							return "The symbol "
									+ op[0]
									+ " does not have an absolute value on line "
									+ lineCounter + ".";
						}
					} else if (op[0].charAt(0) == 'x') {
						bin = Integer.parseInt(op[0].substring(1), 16);
					} else if (op[0].charAt(0) == '#') {
						bin = Integer.parseInt(op[0].substring(1));

						// .FILL values must be in the range #-32768 to #32767.
						if (bin < -32768 || bin > 32767) {
							return "Operand has value out of range on line "
									+ lineCounter + ".";
						}
						bin = Utility.convertToTwosComplement(bin);

						// .FILL operands can only be symbols, hex, or decimal.
					} else {
						return "Operand has invalid formatting on line "
								+ lineCounter + ".";
					}

					// .FILL values must be in the range x0-xFFFF.
					if (bin < 0 || bin > 65535) {
						return "Operand has value out of range on line "
								+ lineCounter + ".";
					}

					// Convert into hex and binary.
					String hexAdress = Utility.DecimalValueToHex(adress);
					textRecord = Utility.DecimalValueToHex(bin);
					binary = Utility.HexToBinary(textRecord);
					adress++;

					// Write to object file.
					pTextRecord = "T" + hexAdress + textRecord;
					bufferedWriter.write(pTextRecord);
					bufferedWriter.newLine();

					// Write to pretty print.
					prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
							+ binary + " (" + lineCounter + ") " + read);
					prettyPrint.newLine();

					// Check if it is a .END.
				} else if (operations.equals(".END ")) {

					// .END operations can have 1 or 0 operands.
					if (!(op.length == 1 || op.length == 0)) {
						return "Invalid number of operands on line "
								+ lineCounter + ".";
					}

					// Write to pretty print.
					prettyPrint.write("                             " + "("
							+ lineCounter + ") " + read);
					prettyPrint.newLine();
				}

				// If it isn't a machine or pseudo op, then the operation is
				// undefined.
			} else {
				return "The operation " + operations + " is undefined on line "
						+ lineCounter + ".";
			}
			// Read in next line from the intermediate file.
			read = file.readLine();
			if (read != null) {
				lineCounter = Integer.parseInt(read.substring(0,
						read.indexOf(":")));
				read = read.substring(read.indexOf(":") + 1);
			}
		}

		// Write the literal table to the object and pretty print files.
		String litArray[] = machineTables.literalTable.keySet().toArray(
				new String[machineTables.literalTable.size()]);
		for (String tempStr : litArray) {
			String tempArr[] = machineTables.literalTable.get(tempStr);
			textRecord = tempArr[0];
			String hexAdress = tempArr[1];
			String binary = Utility.HexToBinary(textRecord);

			// Write to object file.
			pTextRecord = "T" + hexAdress + textRecord;
			bufferedWriter.write(pTextRecord);
			bufferedWriter.newLine();

			// Write to pretty print.
			prettyPrint.write("(" + hexAdress + ") " + textRecord + " "
					+ binary + " (lit)");
			prettyPrint.newLine();
		}

		// Write the end record to the object file.
		textRecord = "E" + machineTables.startingLocation;
		bufferedWriter.write(textRecord);
		bufferedWriter.newLine();

		// Remove the intermediate and comments files, close IO and exit
		// successfully.
		bufferedWriter.close();
		prettyPrint.close();
		reader.close();
		file.close();
		new File("comments.txt").deleteOnExit();
		new File("intermediate.txt").deleteOnExit();
		return null;
	}
	/**
	 * This method performs the same function as the Substring method, however if it
	 * reads null, overSubstring will replace the null with a space character.
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
