package Assembler;

import java.util.HashMap;

/**
 * This class contains the public fields needed to store information throughout
 * both passes of assembly.
 * 
 * @author Ben Trivett
 */
public class Tables {

	/**
	 * The machineOpTable is a hash map with the key being a string of the
	 * instruction and the value as a character array. The format of the array
	 * is as follows: Position 0: hex value of instruction Position 1: 0,1,2,3
	 * depending on the number of operands the instruction has
	 */
	public HashMap<String, char[]> machineOpTable;
	/**
	 * The pseudoOpTable is a hash map with the key being the instruction as a
	 * string and the value of the number of operands as an integer.
	 */
	public HashMap<String, Integer> psuedoOpTable;

	/**
	 * The externalSymbolTable is a hash map that has the label as the string
	 * key and the value as a string array. The format of the array is as
	 * follows: Position 0: A hex string representing the location/value of that
	 * label Position 1: '0' for absolute or '1' for relative.
	 */
	public HashMap<String, String[]> externalSymbolTable = new HashMap<String, String[]>();
	
	/**
	 * The symbolLocationTable is a hash map that is similar to the externalSymbolTable
	 * although it only contains the entry symbols of the file being parsed through.
	 */
	public HashMap<String, String[]> symbolLocationTable;
	/**
	 * The symbolTable is a hash map that has the label as the string key and
	 * the value as a string array. The format of the array is as follows:
	 * Position 0: A hex string representing the location/value of that label
	 * Position 1: 0 for absolute or 1 for relative Position 2: 0 if the number
	 * stored was hex, 1 for decimal, 2 for register, 3 for binary
	 */
	public HashMap<String, String[]> symbolTable;
	/**
	 * The literalTable is a hash map with the key being xHEX or #DEC as a
	 * string. The value in the map is a string array with pos 0 being the hex
	 * value of the literal (string length 4).
	 */
	public HashMap<String, String[]> literalTable;
	/**
	 * This sets a value of the last 9 bits of the value concatenated with the
	 * first seven bits of the location couner.
	 */
	public HashMap<String, Integer[]> passOnePgoffsetCheck;
	/**
	 * The locationCounter is represented as an integer.
	 */
	public Integer locationCounter;
	public static Integer initialLocationCounter;
	/**
	 * Is a boolean that is set to true if it is relative and false if it is
	 * absolute.
	 */
	public Boolean isRelative;
	/**
	 * Is a boolean that is set to true if a .BLKW statement has been
	 * encountered with a symbol as its operand.
	 */
	public Boolean isSymbolRelative;
	/**
	 * Stores the starting location as a 4 character string.
	 */
	public String startingLocation;
	/**
	 * The location that the literal table starts.
	 */
	public Integer startOfLiteralTable;
	/**
	 * Sets the max symbols.
	 */
	public final Integer MAX_SYMBOLS = 100;
	/**
	 * Sets the max literals.
	 */
	public final Integer MAX_LITERALS = 50;
	/**
	 * Sets the max records.
	 */
	public final Integer MAX_RECORDS = 200;

	/**
	 * This is a constructor method that creates an instance of each map and
	 * variable.
	 */
	public Tables() {
		machineOpTable = new HashMap<String, char[]>();
		psuedoOpTable = new HashMap<String, Integer>();
		symbolTable = new HashMap<String, String[]>();
		externalSymbolTable = new HashMap<String, String[]>();
		symbolLocationTable = new HashMap<String, String[]>();
		literalTable = new HashMap<String, String[]>();
		passOnePgoffsetCheck = new HashMap<String, Integer[]>();
		isRelative = false;
		startingLocation = "0000";

		initMachineOpTable();
		initPsuedoOpTable();
	}

	/**
	 * This method initializes the Machine Operation table.
	 */
	private void initMachineOpTable() {
		char[] tempChar = new char[2];

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BR   ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRN  ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRZ  ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRP  ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRNZ ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRNP ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRZP ", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '1';
		machineOpTable.put("BRNZP", tempChar);

		tempChar[0] = '0';
		tempChar[1] = '0';
		machineOpTable.put("NOP  ", tempChar);

		tempChar[0] = '1';
		tempChar[1] = '3';
		machineOpTable.put("ADD  ", tempChar);

		tempChar[0] = '2';
		tempChar[1] = '2';
		machineOpTable.put("LD   ", tempChar);

		tempChar[0] = '3';
		tempChar[1] = '2';
		machineOpTable.put("ST   ", tempChar);

		tempChar[0] = '4';
		tempChar[1] = '1';
		machineOpTable.put("JSR  ", tempChar);

		tempChar[0] = '4';
		tempChar[1] = '1';
		machineOpTable.put("JMP  ", tempChar);

		tempChar[0] = '5';
		tempChar[1] = '3';
		machineOpTable.put("AND  ", tempChar);

		tempChar[0] = '6';
		tempChar[1] = '3';
		machineOpTable.put("LDR  ", tempChar);

		tempChar[0] = '7';
		tempChar[1] = '3';
		machineOpTable.put("STR  ", tempChar);

		tempChar[0] = '8';
		tempChar[1] = '0';
		machineOpTable.put("DBUG ", tempChar);

		tempChar[0] = '9';
		tempChar[1] = '2';
		machineOpTable.put("NOT  ", tempChar);

		tempChar[0] = 'A';
		tempChar[1] = '2';
		machineOpTable.put("LDI  ", tempChar);

		tempChar[0] = 'B';
		tempChar[1] = '2';
		machineOpTable.put("STI  ", tempChar);

		tempChar[0] = 'C';
		tempChar[1] = '2';
		machineOpTable.put("JMPR ", tempChar);

		tempChar[0] = 'C';
		tempChar[1] = '2';
		machineOpTable.put("JSRR ", tempChar);

		tempChar[0] = 'D';
		tempChar[1] = '0';
		machineOpTable.put("RET  ", tempChar);

		tempChar[0] = 'E';
		tempChar[1] = '2';
		machineOpTable.put("LEA  ", tempChar);

		tempChar[0] = 'F';
		tempChar[1] = '1';
		machineOpTable.put("TRAP ", tempChar);
	}

	/**
	 * This method initializes the Pseudo Operations table.
	 */
	private void initPsuedoOpTable() {
		psuedoOpTable.put(".ORIG", 1);
		psuedoOpTable.put(".EQU ", 1);
		psuedoOpTable.put(".BLKW", 1);
		psuedoOpTable.put(".END ", 1);
		psuedoOpTable.put(".STRZ", 1);
		psuedoOpTable.put(".FILL", 1);
		psuedoOpTable.put(".ENT ", 4);
		psuedoOpTable.put(".EXT ", 4);
	}
}
