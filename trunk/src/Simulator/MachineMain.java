package Simulator;

/**
 * The main method for the Wileven Machine that creates instances of a model,
 * view, and controller.
 * 
 * @author Ben Trivett
 * @param args
 */
public class MachineMain {

	public static View machineView;
	public static ControllerInterface machineController;
	public static Model machineModel;

	public static void main(String[] args) {
		Reset();
	}

	/**
	 * Resets the Wileven Machine by creating new instances of the Model, View,
	 * and Controller.
	 */
	public static void Reset() {
		// Initialize model
		machineModel = new Model();

		// Initialize view
		machineView = new View();
		machineView.setVisible(true);

		// Initialize controller
		machineController = new Controller();
	}
}
