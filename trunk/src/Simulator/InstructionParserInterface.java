package Simulator;

public interface InstructionParserInterface {

	/**
	 * This method parses the instruction given by the hex string
	 * instruction. It also increments the programCounter. There are 
	 * five possible return values for this function:
	 * <br /> 
	 * 1.It can return a blank string, which signifies a successful execution. 
	 * <br /> 
	 * 2. It can return a string of hex characters (upto 4); this
	 * signifies a successful operation that has also affected the
	 * programCounter. 
	 * <br /> 
	 * 3. It can return an error message; this will have some indication 
	 * as to the nature of the error (Trying to access a register not there, 
	 * accessing an operand not on the same page as the instruction, etc).
	 * <br />
	 * 4. It can return a string containing "TRAP" plus 2 Hex digits; in 
	 * this case, according to the value of the hex digits, a system call 
	 * will be executed.
	 * <br />
	 * 5. It can return a string containing "DBUG"; in this case the GUI will 
	 * display the contents of the machine registers (PC, general purpose 
	 * registers, CCRs).
	 * @param instruction The hex value of the instruction to be parsed.
	 * @param memoryChanges This array will be written to with the hex value of
	 * the memory location(s) modified.
	 * @param registerChanges This array will be written to with the register
	 * number that was modified.
	 * @return
	 */
	public abstract String parse(String instruction, String[] memoryChanges,
			Integer[] registerChanges);

}