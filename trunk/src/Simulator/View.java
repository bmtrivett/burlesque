package Simulator;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

/**
 * Contains the GUI for the Wileven Machine. Displays a frame with a text field
 * for input and a text area for output.
 * 
 * @author Ben Trivett
 */
public class View extends JFrame implements ViewInterface {
	private static final long serialVersionUID = 1L;

	private static final Integer DEFAULT_CONSOLE_WIDTH = 80;
	private static final Integer DEFAULT_CONSOLE_HEIGHT = 45;
	private static JTextField inputField;
	private static JTextArea outputField;

	/**
	 * Constructor for the Wileven Machine GUI.
	 */
	public View() {
		this.resetView();
	}

	/* (non-Javadoc)
	 * @see ViewInterface#resetView()
	 */
	@Override
	public void resetView() {
		// Set up display objects.
		inputField = new JTextField(DEFAULT_CONSOLE_WIDTH);
		outputField = new JTextArea(DEFAULT_CONSOLE_HEIGHT,
				DEFAULT_CONSOLE_WIDTH);
		outputField.setEditable(false); // Default is editable
		JScrollPane consolePane = new JScrollPane(outputField);
		

		// Put display objects in a container and lay them out.
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(inputField, BorderLayout.SOUTH);
		content.add(consolePane, BorderLayout.NORTH);

		// Finalize layout by adding content and title.
		this.setContentPane(content);
		this.pack();
		this.setTitle("Wileven Machine");
		this.setResizable(false);
		this.setLocationByPlatform(true);

		// Set the window closing event.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputField.requestFocusInWindow();
	}

	/* (non-Javadoc)
	 * @see ViewInterface#getInput()
	 */
	@Override
	public String getInput() {
		return new String(inputField.getText());
	}

	/* (non-Javadoc)
	 * @see ViewInterface#showError(java.lang.String)
	 */
	@Override
	public void showError(String errMessage) {
		JOptionPane.showMessageDialog(this, errMessage);
		inputField.requestFocusInWindow();
	}

	/* (non-Javadoc)
	 * @see ViewInterface#showInputDialog(java.lang.String)
	 */
	@Override
	public String showInputDialog(String message){
		return JOptionPane.showInputDialog(message);
	}
	
	/* (non-Javadoc)
	 * @see ViewInterface#outputText(java.lang.String)
	 */
	@Override
	public void outputText(String out) {
		outputField.append(out);
		outputField.setCaretPosition(outputField.getDocument().getLength());
	}

	/* (non-Javadoc)
	 * @see ViewInterface#setListener(java.awt.event.ActionListener, java.awt.event.ActionListener)
	 */
	@Override
	public void setListener(ActionListener old, ActionListener current) {
		inputField.removeActionListener(old);
		inputField.addActionListener(current);
	}
	
	/* (non-Javadoc)
	 * @see ViewInterface#setListener(java.awt.event.ActionListener, java.awt.event.ActionListener)
	 */
	public void removeAllListeners() {
		ActionListener[] tempArray = inputField.getActionListeners();
		for(ActionListener tempAction : tempArray){
			inputField.removeActionListener(tempAction);
		}
	}
	
	/* (non-Javadoc)
	 * @see ViewInterface#setKeyListener(java.awt.event.KeyListener, java.awt.event.KeyListener)
	 */
	@Override
	public void setKeyListener(KeyListener old, KeyListener current) {
		inputField.removeKeyListener(old);
		inputField.addKeyListener(current);
	}

	/* (non-Javadoc)
	 * @see ViewInterface#clearInputField()
	 */
	@Override
	public void clearInputField() {
		inputField.setText(null);
	}
}
