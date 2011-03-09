package Simulator;

/**
 * This utility class provides methods that can be used for commonly performed
 * operations in the Interpreter class.
 * 
 * @author GerardLouis
 * 
 */
public class interpreterUtility {

	/**
	 * Public static function to decode the last 3 digits (pos[0:2]) into a
	 * String of binary numbers.
	 * 
	 * @param word
	 *            The four character instruction passed in.
	 * @return A String of binary numbers of length 12.
	 */
	public static String decodeLastThreeHexDigits(String word) {
		// Sets baseTen to integer value of hex digit at the end of the
		// String word, and then adds each subsequent digit to baseTen.
		// Finally returns binary String representation of baseTen integer.
		// String substring = word.substring(1);
		// int baseTen =
		// interpreterUtility.decodeEntireMemoryLocation(substring);
		// return Integer.toBinaryString(baseTen);
		return Utility.HexToBinary(word.substring((word.length() - 3)))
				.substring(4);
	}

	/**
	 * Public static function to return an integer that is a base 10 conversion
	 * of the signed binary string passed in.
	 * 
	 * @param binaryString
	 *            A string consisting of 0s and 1s, representing a signed binary
	 *            number.
	 * @return An integer that represents the base 10 representation of the
	 *         signed binary string passed in.
	 */
	public static int signExtendBinaryString(String binaryString) {
		if ((binaryString.charAt(0) == '1')) {
			while (binaryString.length() < 16) {
				binaryString = "1" + binaryString;
			}
		} else {
			while (binaryString.length() < 16) {
				binaryString = "0" + binaryString;
			}
		}
		return Utility.convertFromTwosComplement(Integer.parseInt(binaryString,
				2));
	}

	/**
	 * This function will check if the binary string passed is a valid value for
	 * a register. It must parse to a value between 0 and 7, and so must have a
	 * maximum length of 3.
	 * 
	 * @param binaryRep
	 *            The binary representation of the register number being passed
	 *            in.
	 * @return True if the binary string passed in represents a decimal number
	 *         between 0 and 7 (inclusive).
	 */
	public static boolean isRegisterValue(String binaryRep) {
		return (Utility.HexToDecimalValue(Utility.BinaryToHex(binaryRep)) < 8 && Utility
				.HexToDecimalValue(Utility.BinaryToHex(binaryRep)) >= 0);
	}

	/**
	 * Increments the given hex string by 1 and returns a new string with the
	 * incremented value.
	 * 
	 * @param hex
	 *            The hexadecimal string that is to be incremented.
	 * @return A hex string that represents the hex string passed in,
	 *         incremented by 1.
	 */
	public static String IncrementHexValue(String hex) {
		if (hex.equals("FFFF")) {
			return "0000";
		} else {
			Integer result = Utility.HexToDecimalValue(hex);
			result++;
			return Utility.DecimalValueToHex(result);
		}
	}

	/**
	 * Decrements the given hex string by 1 and returns a new string with the
	 * decremented value.
	 * 
	 * @param hex
	 *            The hexadecimal string that is to be decremented.
	 * @return A hex string that represents the hex string passed in,
	 *         decremented by 1.
	 */
	public static String DecrementHexValue(String hex) {
		if (hex.equals("0000")){
			return "FFFF";
		}
		Integer result = Utility.HexToDecimalValue(hex);
		result--;
		return Utility.DecimalValueToHex(result);
	}

	/**
	 * Checks if the given operand is on the same page as the instruction. Does
	 * this by making sure the upper 7 bits of operand and programCounter are
	 * equal.
	 * 
	 * @param operand
	 * @param programCounter
	 * @return
	 */
	public static boolean isOnSamePage(String operand, String programCounter) {
		operand = Utility.HexToBinary(operand);
		programCounter = Utility.HexToBinary(programCounter);
		operand = operand.substring(0, 7);
		programCounter = programCounter.substring(0, 7);

		return (Integer.parseInt(operand, 2) == Integer.parseInt(
				programCounter, 2));
	}
}
