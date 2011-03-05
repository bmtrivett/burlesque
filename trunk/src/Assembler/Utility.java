package Assembler;

/**
 * A class that contains utility methods that all the other classes can use.
 * 
 * @author Ben Trivett
 */
public class Utility {

	/**
	 * Converts from a hex string to a 2's complement binary string.
	 * 
	 * @param hex
	 *            The hexadecimal string that will be converted to binary.
	 * @return A string of binary of length 16 that represents the hexadecimal
	 *         parameter.
	 */
	public static String HexToBinary(String hex) {
		StringBuilder binString = new StringBuilder();
		binString.append(Integer.toBinaryString(Integer.parseInt(hex, 16)));
		while (binString.length() < 16) {
			binString.insert(0, "0");
		}
		return binString.toString();
	}

	/**
	 * Converts from a 2's complement binary string to a hex string.
	 * 
	 * @param binary
	 *            The binary string that will be converted to hexadecimal.
	 * @return A string of hexidecimal of length 4 that represents the binary
	 *         parameter.
	 */
	public static String BinaryToHex(String binary) {
		StringBuilder hexString = new StringBuilder();
		hexString.append(Integer.toHexString(Integer.parseInt(binary, 2))
				.toUpperCase());
		while (hexString.length() < 4) {
			hexString.insert(0, "0");
		}
		return hexString.toString();
	}

	/**
	 * Converts from a hex string to a decimal value.
	 * 
	 * @param hex
	 *            The hexadecimal string that will be converted to base 10.
	 * @return An integer that represents the hexadecimal parameter.
	 */
	public static Integer HexToDecimalValue(String hex) {
		StringBuilder temp = new StringBuilder(hex);
		// Remove spaces.
		while (temp.indexOf(" ") != -1) {
			temp.deleteCharAt(temp.indexOf(" "));
		}
		return Integer.parseInt(temp.toString(), 16);
	}

	/**
	 * Converts from a decimal value to a hexadecimal string.
	 * 
	 * @param value
	 *            The decimal value that will be converted to hexadecimal.
	 * @return A string that represents the decimal value parameter.
	 */
	public static String DecimalValueToHex(Integer value) {
		StringBuilder hexString = new StringBuilder();
		hexString.append(Integer.toHexString(value).toUpperCase());
		while (hexString.length() < 4) {
			hexString.insert(0, "0");
		}
		return hexString.toString();
	}

	/**
	 * Tests whether parameter value is a hexadecimal string or not.
	 * 
	 * @param value
	 *            The string that is going to be tested.
	 * @return A boolean true if the string is in all hex, or a false if the
	 *         string is not.
	 */
	public static Boolean isHexString(String value) {
		try {
			Integer.parseInt(value, 16);
			if (value.length() > 4) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Converts from a boolean to a single character string. 0 for false or 1
	 * for true.
	 * 
	 * @param b
	 *            The boolean that will be converted to 0 or 1.
	 * @return A string that represents the boolean.
	 */
	public static String BooleanToString(Boolean b) {
		if (b) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * Converts a decimal value that was in binary 2s complement to its correct
	 * decimal value.
	 * 
	 * @param value
	 *            The decimal value that is going to be converted.
	 * @return The correct decimal value that has been converted from twos
	 *         complement binary.
	 */
	public static Integer convertFromTwosComplement(Integer value) {
		if (value > 32767) {
			value = value - 65536;
		}
		return value;
	}

	/**
	 * Converts a decimal value that will be in binary 2s complement to its
	 * correct decimal value.
	 * 
	 * @param value
	 *            The decimal value that is going to be converted.
	 * @return The correct decimal value that will be converted into twos
	 *         complement binary.
	 */
	public static Integer convertToTwosComplement(Integer value) {
		if (value < 0) {
			value = value + 65536;
		}
		return value;
	}

}
