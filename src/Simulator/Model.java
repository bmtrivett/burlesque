package Simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * Use this class to store all the memory and register info in one spot since
 * the loader, interpreter, and simulator all need access to it.
 * 
 * @author #1Group
 * 
 */
public class Model implements ModelInterface {
	/**
	 * The entire abstract machine's memory. Each string in the array will have
	 * a length of 4, representing the hex value stored there.
	 */
	public String[] memoryArray;

	/**
	 * The map will store an integer denoting the register number and the string
	 * of hex for the registers.
	 */
	public Map<Integer, String> registerMap;

	/**
	 * The map will store a character denoting the CCR and the associated
	 * boolean is the state of that register.
	 */
	public Map<Character, Boolean> conditionCodeRegisters;

	/**
	 * This string of four characters will have the start value of the
	 * instructions in hex.
	 */
	public String hexStartValue;

	/**
	 * This integer will have the location in the array of the next instruction.
	 */
	public String programCounter;

	/**
	 * The name of the program being loaded into memory. It is retrieved from
	 * the header record.
	 */
	public String programName;

	/**
	 * The potentially user-defined instance of the maximum number of
	 * instructions allowed to be executed. Defined in the controller to be 1 to
	 * 2,147,483,647, or the default.
	 */
	public Integer instructionLimit;

	/**
	 * Constructor for the Wileven Machine model.
	 */
	public Model() {
		registerMap = new HashMap<Integer, String>();
		conditionCodeRegisters = new HashMap<Character, Boolean>();
		memoryArray = new String[65536];
		instructionLimit = DEFAULT_INSTRUCTION_LIMIT;
		conditionCodeRegisters.put('N', false);
		conditionCodeRegisters.put('Z', true);
		conditionCodeRegisters.put('P', false);
		for (int i = 0; i < 8; i++) {
			registerMap.put(i, "0000");
		}
	}

}
