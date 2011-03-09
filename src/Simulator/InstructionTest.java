package Simulator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class InstructionTest  {

	private Map<Integer, String> rMap;
	private String[] memArray;
	private Map<Character, Boolean> ccReg;
	private String pc;
	private String bRep;
	private Integer[] rChange;
	private String[] mChange;
	private String answer;
	
	@Before
	public void initiliaze() {
		this.rMap = new HashMap<Integer, String>();
		this.memArray = new String[65536];
		this.ccReg = new HashMap<Character, Boolean>();
		this.pc = "3031";
		this.bRep = "100101001011";
		this.rChange = new Integer[5];
		this.mChange = new String[5];
	}

	/**
	 * Bare-bones test for JSRR.
	 */
	@Test
	public void JSRR(){
		this.rMap.put(5, "3040");
		this.answer = instructionCases.JSRR(
				this.rMap, this.pc, this.bRep, this.rChange);

		Assert.assertEquals("Error: JSRR is broken" , "304B", this.answer);

	}
	
	/**
	 * Bare-bones test for JSR.
	 */
	@Test
	public void JSR(){
		rMap.put(5, "3040");
		this.answer = instructionCases.JSR(
				this.rMap, this.pc, this.bRep, this.rChange);

		Assert.assertEquals("Error: JSR is broken", "314B", this.answer);

	}
	/**
	 * Bare-bones test for BRx.
	 */
	/*
	@Test
	public void Branch(){
		ccReg.put('N', true);
		this.answer = instructionCases.BRx(this.ccReg, this.pc, bRep);

		Assert.assertEquals("Error: BRx is broken" , "314B", answer);

	}*/
	
	/**
	 * Bare-bones test for LD.
	 */
	@Test
	public void Load(){
		this.rMap.put(4, "3040");
		this.ccReg.put('N', true);
		this.memArray[12619] = "314B";

		instructionCases.LD(
				this.rMap, this.memArray, this.ccReg, this.pc, this.bRep, this.rChange);
		this.answer = this.rMap.get(4);

		Assert.assertEquals("Error: LD is broken", "314B", this.answer);

	}
	
	/**
	 * Bare-bones test for LDI.
	 */
	@Test
	public void LoadI(){
		this.rMap.put(4, "3040");
		this.ccReg.put('N', true);
		this.memArray[12619] = "3030";
		this.memArray[12336] = "314B";

		instructionCases.LDI(
				this.rMap, this.memArray, this.ccReg, this.pc, this.bRep, this.rChange);
		this.answer = this.rMap.get(4);

		Assert.assertEquals("Error: LDI is broken", "314B", this.answer);

	}
	
	/**
	 * Bare-bones test for LDR
	 */
	@Test
	public void LoadR(){
		this.rMap.put(4, "3040");
		this.rMap.put(5, "0000");
		this.ccReg.put('N', true);
		
		instructionCases.LDR(this.rMap, this.ccReg, this.bRep, this.rChange);
		this.answer = this.rMap.get(4);

		Assert.assertEquals("Error: LDR is broken", "000B", this.answer);

	}
	
	/**
	 * Bare-bones test for LEA.
	 */
	@Test
	public void LoadA(){
		this.rMap.put(4, "3040");
		this.rMap.put(5, "0000");
		this.ccReg.put('N', true);

		instructionCases.LEA(
				this.rMap, this.ccReg, this.pc, this.bRep, this.rChange);
		this.answer = this.rMap.get(4);

		Assert.assertEquals("Error: LEA is broken", "314B", this.answer);

	}
	
	/**
	 * Bare-bones test for ST.
	 */
	@Test
	public void Store(){
		this.rMap.put(4, "3040");
		this.ccReg.put('N', true);

		instructionCases.ST(
				this.rMap, this.memArray, this.ccReg, this.pc, this.bRep, this.mChange);
		this.answer = this.memArray[12619];

		Assert.assertEquals("Error: ST is broken", "3040", this.answer);

	}
	
	/**
	 * Bare-bones test for STI.
	 */
	@Test
	public void StoreI(){
		this.rMap.put(4, "3040");
		this.ccReg.put('N', true);
		this.memArray[12619] = "3040";

		instructionCases.STI(
				this.rMap, this.memArray, this.ccReg, this.pc, this.bRep, this.mChange);
		this.answer = this.memArray[12619];

		Assert.assertEquals("Error: STI is broken", "3040", this.answer);

	}
	
	/**
	 * Bare-bones test for STR.
	 */
	@Test
	public void StoreR(){
		this.rMap.put(4, "3040");
		this.rMap.put(5, "3040");
		this.ccReg.put('N', true);

		instructionCases.STR(
				this.rMap, this.memArray, this.ccReg, this.bRep, this.mChange);
		this.answer = this.memArray[12363];

		Assert.assertEquals("Error: STR is broken", "3040", this.answer);

	}
	
	/**
	 * Bare-bones test for NOT.
	 */
	@Test
	public void Not(){
		this.rMap.put(5, "3040");
		this.ccReg.put('N', true);

		instructionCases.NOT(this.rMap, this.ccReg, this.bRep, this.rChange);
		this.answer = this.rMap.get(4);

		Assert.assertEquals("Error: NOT is broken", "CFBF", this.answer);

	}
	
	/**
	 * Bare-bones test for RET.
	 */
	@Test
	public void Return(){
		this.rMap.put(7, "3040");

		this.answer =instructionCases.RET(this.rMap);

		Assert.assertEquals("Error: STR is broken", "3040", this.answer);

	}
	
	/**
	 * Bare-bones test for ADD, immediate mode.
	 */
	@Test
	public void AddImm(){
		this.bRep = "100101101011";
		this.rMap.put(5, "0001");
		
		instructionCases.ADD(this.rMap, this.ccReg, this.bRep, this.rChange);
		
		this.answer = this.rMap.get(4);
		
		Assert.assertEquals(
				"Error: ADD, immediate mode, is broken", "000C", this.answer);
	}
	
	/**
	 * Bare-bones test for ADD.
	 */
	@Test
	public void Add(){
		this.rMap.put(5, "0001");
		this.rMap.put(3, "1019");
		
		instructionCases.ADD(this.rMap, this.ccReg, this.bRep, this.rChange);
		
		this.answer = this.rMap.get(4);
		
		Assert.assertEquals("Error: ADD is broken", "101A", this.answer);
	}
	
	/**
	 * Bare-bones test for AND, immediate mode.
	 */
	@Test
	public void AndImm(){
		this.bRep = "100101101011";
		this.rMap.put(5, "0001");
		
		instructionCases.AND(this.rMap, this.ccReg, this.bRep, this.rChange);
		
		this.answer = this.rMap.get(4);
		
		Assert.assertEquals(
				"Error: AND, immediate mode, is broken", "0001", this.answer);
	}
	
	/**
	 * Bare-bones test for AND.
	 */
	@Test
	public void And(){
		this.rMap.put(5, "9F8F");
		this.rMap.put(3, "6C3D");
		
		instructionCases.AND(this.rMap, this.ccReg, this.bRep, this.rChange);
		
		this.answer = this.rMap.get(4);
		
		Assert.assertEquals("Error: AND is broken", "0C0D", this.answer);
	}
}