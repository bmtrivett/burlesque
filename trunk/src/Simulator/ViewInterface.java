package Simulator;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public interface ViewInterface {

	/**
	 * Creates a new GUI with an empty text field and text area.
	 */
	public abstract void resetView();

	/**
	 * Retrieves the input inside the text field and returns it as a string.
	 * 
	 * @return The user inputed text as a String.
	 */
	public abstract String getInput();

	/**
	 * Pops up a message dialog that displays the text from the parameter.
	 * 
	 * @param errMessage
	 *            The text to be displayed.
	 */
	public abstract void showError(String errMessage);

	/**
	 * Displays an input dialog that returns the input as a string.
	 */
	public abstract String showInputDialog(String message);

	/**
	 * Adds the text from the parameter to the end of the text area.
	 * 
	 * @param out
	 *            The text to be added to the end of the text area.
	 */
	public abstract void outputText(String out);

	/**
	 * Replaces the action listener for the text field.
	 * 
	 * @param old
	 *            The action listener to be removed.
	 * @param current
	 *            The action listener to be added.
	 */
	public abstract void setListener(ActionListener old, ActionListener current);

	/**
	 * Replaces the key listener for the text field.
	 * 
	 * @param old
	 *            The key listener to be removed.
	 * @param current
	 *            The key listener to be added.
	 */
	public abstract void setKeyListener(KeyListener old, KeyListener current);

	/**
	 * Clears the text from the text field.
	 */
	public abstract void clearInputField();

}