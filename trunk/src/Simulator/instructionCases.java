package Simulator;

import java.util.Map;

/**
 * This class will have static methods for executing all the instructions. In
 * addition, it will also contain private static methods for certain operations
 * that are repeated within instructions.
 * 
 * @author GerardLouis
 * 
 */
public class instructionCases {

	/**
	 * This private method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the ADD operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String ADD(Map<Integer, String> registerMap,
			Map<Character, Boolean> conditionCodeRegisters, String binaryRep,
			Integer[] registerChanges) {
		// Read in DR and SR1 and check if they are in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}
		String SR1 = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(SR1)) {
			return "Source Register 1 out of range.";
		}

		// Set SR1Contents to decimal value of number stored
		// in SR1 and set temp2 to 0.
		int SR1Contents = Utility.HexToDecimalValue(registerMap.get(Integer
				.parseInt(SR1, 2)));
		int temp2 = 0;

		// Check if operation is in 'immediate' mode. If not,
		// read in SR2 and check if it is in range.
		if (binaryRep.charAt(6) == '0') {
			String SR2 = binaryRep.substring(9);
			if (!interpreterUtility.isRegisterValue(SR2)) {
				return "Source Register 2 out of range.";
			}
			// Set temp2 to the decimal value of number stored in
			// SR2.
			temp2 = Utility.HexToDecimalValue(registerMap.get(Integer.parseInt(
					SR2, 2)));
		}
		// If in 'immediate' mode, then read in 'imm5' and set
		// temp2 to the value returned by signExtendBinaryString function.
		else {
			String imm5 = binaryRep.substring(7);
			temp2 = interpreterUtility.signExtendBinaryString(imm5);
		}
		SR1Contents = Utility.convertFromTwosComplement(SR1Contents);
		temp2 = Utility.convertFromTwosComplement(temp2);
		int result = SR1Contents + temp2;

		// Modify CCRs (if result is negative, set N, etc)
		if (result < 0) {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if (result > 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		result = Utility.convertToTwosComplement(result);
		registerMap.put(Integer.parseInt(DR, 2),
				Utility.DecimalValueToHex(result));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the AND operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String AND(Map<Integer, String> registerMap,
			Map<Character, Boolean> conditionCodeRegisters, String binaryRep,
			Integer[] registerChanges) {
		// Read in DR and SR1 and check if they are in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}
		String SR1 = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(SR1)) {
			return "Source Register 1 out of range.";
		}

		// Set SR1Contents to a string of the binary value of
		// number stored in SR1 and set temp2 to a blank string.
		// Also create StringBuffer result.
		String SR1Contents = Utility.HexToBinary(registerMap.get(Integer
				.parseInt(SR1, 2)));
		String temp2 = "";
		StringBuffer result = new StringBuffer();

		// Check if operation is in 'immediate' mode. If not,
		// read in SR2 and check if it is in range.
		if (binaryRep.charAt(6) == '0') {
			String SR2 = binaryRep.substring(9);
			if (!interpreterUtility.isRegisterValue(SR2)) {
				return "Source Register 2 out of range.";
			}
			// Set temp2 to the binary string value of number stored in
			// SR2.
			temp2 = Utility.HexToBinary(registerMap.get(Integer
					.parseInt(SR2, 2)));
		}

		// If in 'immediate' mode, then read in 'imm5' and check
		// the first character of imm5.
		else {
			String imm5 = binaryRep.substring(7);
			// If the first character is '0', set temp2 to the
			// concatenation of 11 zeros and imm5.
			if (imm5.charAt(0) == '0') {
				temp2 = "00000000000" + imm5;
			}
			// Otherwise, set temp2 to the concatenation of
			// 11 ones and imm5.
			else {
				temp2 = "11111111111" + imm5;
			}
		}

		// Then run a while loop with the counter set to temp2's
		// length and decrementing by 1.
		int counter = temp2.length();
		while (counter > 0) {
			// If both the characters at the end position are
			// 1, then insert '1' in the first position of result.
			if (SR1Contents.charAt(counter - 1) == '1'
					&& temp2.charAt(counter - 1) == '1') {
				result.insert(0, '1');
			}
			// Otherwise, insert '0' in the first position of result.
			else {
				result.insert(0, '0');
			}
			counter--;
		}

		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) == '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result.toString(), 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)),
				Utility.BinaryToHex(result.toString()));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the NOT operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String NOT(Map<Integer, String> registerMap,
			Map<Character, Boolean> conditionCodeRegisters, String binaryRep,
			Integer[] registerChanges) {
		// Read in DR and SR and check if they are in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}
		String SR = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(SR)) {
			return "Source Register out of range.";
		}

		// Set SRContents to a string of the binary value of
		// number stored in SR and create StringBuffer result.
		String SRContents = Utility.HexToBinary(registerMap.get(Integer
				.parseInt(SR, 2)));
		StringBuffer result = new StringBuffer();

		// Then run a while loop with the counter set to SRConent's
		// length and decrementing by 1.
		int counter = SRContents.length();
		while (counter > 0) {
			// If the character at pos[counter] is 0, then insert
			// a '1' in the first position of result.
			if (SRContents.charAt(counter - 1) == '0') {
				result.insert(0, '1');
			}
			// Otherwise, insert a '0' in the first position of result.
			else {
				result.insert(0, '0');
			}
			counter--;
		}

		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) < '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result.toString(), 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)),
				Utility.BinaryToHex(result.toString()));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the LEA operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String LEA(Map<Integer, String> registerMap,
			Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep, Integer[] registerChanges) {
		// Read in DR and check if it is in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}

		// Store first 7 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String result = Utility.HexToBinary(programCounter).substring(0, 7);
		result = result + binaryRep.substring(3);
		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) == '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result.toString(), 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)), Utility.BinaryToHex(result));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the LD operation as outlined
	 * by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param memoryArray
	 *            This represents the memory of the machine.
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String LD(Map<Integer, String> registerMap,
			String[] memoryArray,
			Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep, Integer[] registerChanges) {
		// Read in DR and check if it is in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}

		// Store first 6 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String result = Utility.HexToBinary(programCounter).substring(0, 7);
		result = result + binaryRep.substring(3);

		programCounter = interpreterUtility.DecrementHexValue(programCounter);

		if (!interpreterUtility.isOnSamePage(Utility.BinaryToHex(result),
				programCounter)) {
			return "Operand is not on same page as instruction";
		}

		programCounter = interpreterUtility.IncrementHexValue(programCounter);

		result = memoryArray[Utility.HexToDecimalValue(Utility
				.BinaryToHex(result))];
		if (result == null) {
			result = "0000";
		}
		result = Utility.HexToBinary(result);
		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) < '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result.toString(), 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)),
				Utility.BinaryToHex(result.toString()));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the LDI operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param memoryArray
	 *            This represents the memory of the machine.
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String LDI(Map<Integer, String> registerMap,
			String[] memoryArray,
			Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep, Integer[] registerChanges) {
		// Read in DR and check if it is in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}

		// Store first 6 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String result = Utility.HexToBinary(programCounter).substring(0, 7);
		result = result + binaryRep.substring(3);

		// Check if initial operand is on same page as instruction.
		programCounter = interpreterUtility.DecrementHexValue(programCounter);

		if (!interpreterUtility.isOnSamePage(Utility.BinaryToHex(result),
				programCounter)) {
			return "Operand is not on same page as instruction";
		}

		programCounter = interpreterUtility.IncrementHexValue(programCounter);

		result = memoryArray[Integer.parseInt(result, 2)];
		if (result == null) {
			result = "0000";
		}
		result = memoryArray[Integer.parseInt(result, 16)];
		if (result == null) {
			result = "0000";
		}
		result = Utility.HexToBinary(result);
		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) < '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result.toString(), 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)),
				Utility.BinaryToHex(result.toString()));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the LDR operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String LDR(Map<Integer, String> registerMap,
			Map<Character, Boolean> conditionCodeRegisters, String binaryRep,
			Integer[] registerChanges) {
		// Read in DR and BaseR and check if they are in range.
		String DR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(DR)) {
			return "Destination Register out of range.";
		}
		String BaseR = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(BaseR)) {
			return "Base Register out of range.";
		}

		String intermediate = registerMap.get(Integer.parseInt(BaseR, 2));

		int numresult = Integer.parseInt(intermediate, 16);
		numresult = Utility.convertFromTwosComplement(numresult);
		numresult += Utility.convertFromTwosComplement(Integer.parseInt(
				binaryRep.substring(6), 2));
		numresult = Utility.convertToTwosComplement(numresult);
		String result = Integer.toBinaryString(numresult);

		// Modify CCRs (if result is negative, set N, etc)
		if (result.charAt(0) == '1') {
			conditionCodeRegisters.put('N', true);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', false);
		} else if ((Integer.parseInt(result, 2)) != 0) {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', false);
			conditionCodeRegisters.put('P', true);
		} else {
			conditionCodeRegisters.put('N', false);
			conditionCodeRegisters.put('Z', true);
			conditionCodeRegisters.put('P', false);
		}

		// Put hex string value of result in DR, put DR
		// value in registerChanges and return a blank string.
		registerMap.put((Integer.parseInt(DR, 2)), Utility.BinaryToHex(result));
		registerChanges[0] = Integer.parseInt(DR, 2);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the ST operation as outlined
	 * by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param memoryArray
	 *            This represents the memory of the machine.
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param memoryChanges
	 *            This array will be written to with the hex value of the memory
	 *            location(s) modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String ST(Map<Integer, String> registerMap,
			String[] memoryArray,
			Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep, String[] memoryChanges) {
		// Read in SR and check if it is in range.
		String SR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(SR)) {
			return "Source Register out of range.";
		}

		// Store first 6 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String result = Utility.HexToBinary(programCounter).substring(0, 7);
		result = result + binaryRep.substring(3);

		programCounter = interpreterUtility.DecrementHexValue(programCounter);

		if (!interpreterUtility.isOnSamePage(Utility.BinaryToHex(result),
				programCounter)) {
			return "Operand is not on same page as instruction";
		}

		programCounter = interpreterUtility.IncrementHexValue(programCounter);

		// Set memory location at integer value of result to the value at
		// the source register.
		memoryArray[Integer.parseInt(result, 2)] = registerMap.get(Integer
				.parseInt(SR, 2));

		// Put memory location that is changed into memoryChanges and
		// return a blank string.
		memoryChanges[0] = Utility.BinaryToHex(result);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the STI operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param memoryArray
	 *            This represents the memory of the machine.
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param memoryChanges
	 *            This array will be written to with the hex value of the memory
	 *            location(s) modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String STI(Map<Integer, String> registerMap,
			String[] memoryArray,
			Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep, String[] memoryChanges) {
		// Read in SR and check if it is in range.
		String SR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(SR)) {
			return "Source Register out of range.";
		}

		// Store first 6 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String intermediate = Utility.HexToBinary(programCounter).substring(0,
				7);
		intermediate = intermediate + binaryRep.substring(3);

		// Check if operand is on same page as instruction.
		programCounter = interpreterUtility.DecrementHexValue(programCounter);

		if (!interpreterUtility.isOnSamePage(Utility.BinaryToHex(intermediate),
				programCounter)) {
			return "Operand is not on same page as instruction";
		}

		programCounter = interpreterUtility.IncrementHexValue(programCounter);

		// Store memory value at intermediate memory location in string
		// result.
		String result = memoryArray[Integer.parseInt(intermediate, 2)];
		if (result == null) {
			result = "0000";
		}
		// Set memory location at integer value of result to the value at
		// the source register.
		memoryArray[Integer.parseInt(result, 16)] = registerMap.get(Integer
				.parseInt(SR, 2));

		// Put memory location that is changed into memoryChanges and
		// return a blank string.
		memoryChanges[0] = result;
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the STR operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param memoryChanges
	 *            This array will be written to with the hex value of the memory
	 *            location(s) modified.
	 * @return A blank string or if errors are encountered, a string explaining
	 *         the nature of the error.
	 */
	public static String STR(Map<Integer, String> registerMap,
			String[] memoryArray,
			Map<Character, Boolean> conditionCodeRegisters, String binaryRep,
			String[] memoryChanges) {
		// Read in SR and BaseR and check if they are in range.
		String SR = binaryRep.substring(0, 3);
		if (!interpreterUtility.isRegisterValue(SR)) {
			return "Destination Register out of range.";
		}
		String BaseR = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(BaseR)) {
			return "Base Register out of range.";
		}

		String intermediate = registerMap.get(Integer.parseInt(BaseR, 2));

		int numresult = Integer.parseInt(intermediate, 16);
		numresult += Integer.parseInt(binaryRep.substring(6), 2);

		memoryArray[numresult] = registerMap.get(Integer.parseInt(SR, 2));

		// Put memory location that is changed into memoryChanges and
		// return a blank string.
		memoryChanges[0] = Utility.DecimalValueToHex(numresult);
		return null;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the BRx operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param conditionCodeRegisters
	 *            This represents the condition code registers, N, Z, and P.
	 *            Depending on what the value of the number calculated, the
	 *            corresponding register will get set or cleared (N for negative
	 *            numbers, Z for zero values, and P for positive numbers)
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @return A String which has the hex value of the PC.
	 */
	public static String BRx(Map<Character, Boolean> conditionCodeRegisters,
			String programCounter, String binaryRep) {
		String result = programCounter;
		// Create boolean jump and set to false.
		boolean jump = false;
		// Check if incoming binaryRep has N bit set and if so,
		// check if CCR has N bit set. If both are set, then set
		// jump to true. Repeat for Z and P bits.
		if (binaryRep.charAt(0) == '1') {
			if (conditionCodeRegisters.get('N')) {
				jump = true;
			}
		}
		if (binaryRep.charAt(1) == '1') {
			if (conditionCodeRegisters.get('Z')) {
				jump = true;
			}
		}
		if (binaryRep.charAt(2) == '1') {
			if (conditionCodeRegisters.get('P')) {
				jump = true;
			}
		}
		// If boolean jump is true, then return modified programCounter.
		if (jump) {

			// Store first 7 digits of binary conversion of programCounter
			// in string result. Then concatenate result and last 9 digits
			// of binary rep into result.
			result = Utility.HexToBinary(programCounter).substring(0, 7);
			result = result + binaryRep.substring(3);
			result = Utility.BinaryToHex(result).toUpperCase();
		}
		return result;
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the JSR operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A String which has the hex value of the PC.
	 */
	public static String JSR(Map<Integer, String> registerMap,
			String programCounter, String binaryRep, Integer[] registerChanges) {
		// Store first 6 digits of binary conversion of programCounter
		// in string result. Then concatenate result and last 9 digits
		// of binary rep into result.
		String result = Utility.HexToBinary(programCounter).substring(0, 7);
		result = result + binaryRep.substring(3);

		if (binaryRep.charAt(0) == '1') {
			registerMap.put(7, programCounter);
			// Put register location that is changed into registerChanges
			registerChanges[0] = 7;
		}

		// Return hex value of result.
		return Utility.BinaryToHex(result).toUpperCase();
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the JSRR operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there
	 * @param programCounter
	 *            This string of four hex characters represents the PC after the
	 *            fetch phase.
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @param registerChanges
	 *            This array will be written to with the register number that
	 *            was modified.
	 * @return A String which has the hex value of the PC.
	 */
	public static String JSRR(Map<Integer, String> registerMap,
			String programCounter, String binaryRep, Integer[] registerChanges) {
		// Read in BaseR and check if it is in range.
		String BaseR = binaryRep.substring(3, 6);
		if (!interpreterUtility.isRegisterValue(BaseR)) {
			return "Base Register out of range.";
		}

		String intermediate = registerMap.get(Integer.parseInt(BaseR, 2));
		int numresult = Utility.HexToDecimalValue(intermediate);
		if (numresult > 65535) {
			numresult = numresult - 65535;
		}
		numresult += Integer.parseInt(binaryRep.substring(6), 2);

		if (binaryRep.charAt(0) == '1') {
			registerMap.put(7, Utility.DecimalValueToHex(numresult));
			// Put register location that is changed into registerChanges
			registerChanges[0] = 7;
		}

		// Return hex value of changed memory location.
		return Utility.DecimalValueToHex(numresult);
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the RET operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param registerMap
	 *            This represents the general purpose registers that are
	 *            available to the machine. There are 8 available, accessed by
	 *            integers 0 through 7 and an error message will be returned if
	 *            the user tries to access a register that is not there.
	 * @return A String which has the hex value of the PC.
	 */
	public static String RET(Map<Integer, String> registerMap) {
		return registerMap.get(7);
	}

	/**
	 * This public method takes the string binaryRep that is passed in (it
	 * should contain 12 characters), and then does the TRAP operation as
	 * outlined by "Wi-11 Machine Characteristics Document".
	 * 
	 * @param binaryRep
	 *            This string of 12 characters should consist of '1's and '0's
	 *            and each.
	 * @return A String which has the hex value of the last 8 characters of the
	 *         binaryRep passed in.
	 */
	public static String TRAP(String binaryRep) {

		return "TRAP"
				+ Utility.BinaryToHex(binaryRep.substring(4)).substring(2);

	}
}
