package Simulator;

/** 
 * This class will parse through the given Instructions. It contains one
 * method that will have the ability to change the general purpose registers,
 * condition code registers, memory, and the programCounter depending on the 
 * instruction received. 
 * @author GerardLouis
 *
 */
public class instructionParser implements InstructionParserInterface {
	
	/* (non-Javadoc)
	 * @see InstructionParserInterface#parse(java.lang.String, java.lang.String[], java.lang.Integer[])
	 */
	@Override
	public String parse(
			String instruction,
			String[] memoryChanges,
			Integer[] registerChanges
	) {
		// Create string error and set to a blank string. Also set
		// binaryRep to the binary representation of the last three hex
		// digits of instruction string.
		String error = null;
		String binaryRep = interpreterUtility.decodeLastThreeHexDigits(instruction);
		
		// Have a switch statement to decide which function to use.
		// In each case assign the return value of the function to error.
		// In the case of BRx, JSR, JSRR, and RET, also set programCounter
		// to value of error if in the proper format.
		switch (instruction.charAt(0)) {
			case '0':
			{
				Interpreter.instruction = "BRx " + binaryRep;
				error = instructionCases.BRx(
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep);
				if (error.length() <= 4) {
					MachineMain.machineModel.programCounter = error;
				}
				break;
			}	
			case '1':
			{
				Interpreter.instruction = "ADD " + binaryRep;
				error = instructionCases.ADD(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.conditionCodeRegisters, binaryRep,
						registerChanges);
				break;
			}
			case '2':
			{
				Interpreter.instruction = "LD " + binaryRep;
				error = instructionCases.LD(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.memoryArray, 
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep,
						registerChanges);
				break;
			}
			case '3':
			{
				Interpreter.instruction = "ST " + binaryRep;
				error = instructionCases.ST(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.memoryArray, 
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep,
						memoryChanges);
				break;
			}
			case '4':
			{
				Interpreter.instruction = "JSR " + binaryRep;
				error = instructionCases.JSR(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.programCounter, binaryRep,
						registerChanges);
				if (error.length() <= 4) {
					MachineMain.machineModel.programCounter = error;
				}
				break;
			}
			case '5':
			{
				Interpreter.instruction = "AND " + binaryRep;
				error = instructionCases.AND(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.conditionCodeRegisters, binaryRep,
						registerChanges);
				break;
			}
			case '6':
			{
				Interpreter.instruction = "LDR " + binaryRep;
				error = instructionCases.LDR(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.conditionCodeRegisters, binaryRep,
						registerChanges);
				break;
			}
			case '7':
			{
				Interpreter.instruction = "STR " + binaryRep;
				error = instructionCases.STR(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.memoryArray, 
						MachineMain.machineModel.conditionCodeRegisters, binaryRep,
						memoryChanges);
				break;
			}
			case '8':
			{
				Interpreter.instruction = "DBUG " + binaryRep;
				error = "DBUG";
				break;
			}
			case '9':
			{
				Interpreter.instruction = "NOT " + binaryRep;
				error = instructionCases.NOT(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.conditionCodeRegisters, binaryRep,
						registerChanges);
				break;
			}
			case 'A':
			{
				Interpreter.instruction = "LDI " + binaryRep;
				error = instructionCases.LDI(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.memoryArray, 
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep,
						registerChanges);
				break;
			}
			case 'B':
			{
				Interpreter.instruction = "STI " + binaryRep;
				error = instructionCases.STI(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.memoryArray, 
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep,
						memoryChanges);
				break;
			}
			case 'C':
			{
				Interpreter.instruction = "JSRR " + binaryRep;
				error = instructionCases.JSRR(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.programCounter, binaryRep,
						registerChanges);
				if (error.length() <= 4) {
					MachineMain.machineModel.programCounter = error;
				}
				break;
			}
			case 'D':
			{
				Interpreter.instruction = "RET " + binaryRep;
				error = instructionCases.RET(
						MachineMain.machineModel.registerMap);
				if (error.length() <= 4) {
					MachineMain.machineModel.programCounter = error;
				}
				break;
			}
			case 'E':
			{
				Interpreter.instruction = "LEA " + binaryRep;
				error = instructionCases.LEA(
						MachineMain.machineModel.registerMap, 
						MachineMain.machineModel.conditionCodeRegisters, 
						MachineMain.machineModel.programCounter, binaryRep,
						registerChanges);
				break;
			}
			case 'F':
			{
				Interpreter.instruction = "TRAP " + binaryRep;
				error = instructionCases.TRAP(binaryRep);
				break;
			}
			default:
			{
				break;
			}
		}
		// Finally return error string.
		return error;
	}
}