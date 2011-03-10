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
		if ((args.length != 0) || (args[0] != null)) {
			machineModel.fileLocation = args[0];
		} else {
			System.out
					.println("ERROR: The first argument must be the location of the file to be simulated.");
			System.exit(0);
		}
		Reset(args[0]);
	}

	/**
	 * Resets the Wileven Machine by creating new instances of the Model, View,
	 * and Controller.
	 */
	public static void Reset(String args) {
		// Initialize model
		machineModel = new Model();

		// Initialize view
		machineView = new View();
		machineView.setVisible(true);

		// Initialize controller
		machineController = new Controller();
	}
}
