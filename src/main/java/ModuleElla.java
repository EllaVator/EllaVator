// =================================================================
// This file is based on the ModuleExample1.java file by
// 2011-2015 Pierre Lison (plison@ifi.uio.no)
// to be found at
//https://github.com/plison/opendial/blob/master/src/opendial/modules/examples/ModuleExample1.java =================================================================


import java.util.logging.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import opendial.DialogueState;
import opendial.DialogueSystem;
import opendial.modules.Module;

/**
 * Simple example of a synchronous module for the domain specified in
 * domains/examples/example-step-by-step.xml.
 *
 * <p>
 * The example creates a visual grid of size GRID_SIZE and updates the position of
 * the agent in accordance with the movements.
 *
 * @author Pierre Lison (plison@ifi.uio.no)
 */
public class ModuleElla implements Module {

	// logger
	final static Logger log = Logger.getLogger("OpenDial");


	//create controller to speak with the elevator via serial port
	final static ElevatorController controller= new ElevatorController();

	//number of floors (used for JFrame output) and the JFrame itself
	public static int FLOORS = 6;
	JFrame frame;

	boolean paused = true;

	// the controller is commented out at the moment because it is not attached and it crashes if it is not attached
	// the current floor we are on
	//int currentPosition = controller.getCurrentFloor(); // NOT TESTED! uncomment for now
	int currentPosition = 0;

	public ModuleElla(DialogueSystem system) {
	}

	/**
	 * Creates a simple visual grid of size FLOORS and puts the elevator where it is / zero-th floor.
	 */
	@Override
	public void start() {
		frame = new JFrame();

		frame.setLayout(new GridLayout(FLOORS, 1));
		for (int i = 0; i < FLOORS; i++) {
			JLabel label = new JLabel(" ");
			label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			frame.add(label);
		}
		((JLabel) frame.getContentPane().getComponent(FLOORS - currentPosition - 1 ))
				.setText(" HERE");
		frame.setSize(500, 500);
		frame.setVisible(true);
		paused = false;
	}

	/**
	 * If the updated variables contain the system action "a_m" and the action is a
	 * movement, updates the visual grid and the elevator itself in accordance with the movement.
	 */
	@Override
	public void trigger(DialogueState state, Collection<String> updatedVars) {
		if (updatedVars.contains("a_m") && state.hasChanceNode("a_m") && !paused) {
			String actionValue = state.queryProb("a_m").getBest().toString();
			//same as in ModuleExample1 until here
			//System.out.println prints into the console while running
			System.out.println("EllaModule actionValue: \"" + actionValue + "\"");
			if (actionValue.startsWith("Movefloor(")) {
				String togoFloor =
						//actionValue.substring(10 <-- to not take into account the first 10 elements of string, -1 <-- to ignore the closing brackets
						actionValue.substring(10, actionValue.length() - 1);
				//System.out.println prints into  the console while running
				System.out.println("EllaModule:  go to floor: " + togoFloor);
				changePosition(togoFloor);
			}

		}
	}

	/**
	 * Changes the position of the elevator and its visual grid depending on the specified floor.
	 *
	 * @param direction the direction, as a string.
	 */
	private void changePosition(String togoFloor) {
		int newFloor = 0;
		//convert string to internal floor number
		if      (togoFloor.equals("zero")) {
			newFloor = 0;
		}
		else if (togoFloor.equals("zero point five")) {
			newFloor = 1;
		}
		else if (togoFloor.equals("one")) {
			newFloor = 2;
		}
		else if (togoFloor.equals("one point five")) {
			newFloor = 3;
		}
		else if (togoFloor.equals("two")) {
			newFloor = 4;
		}
		else if (togoFloor.equals("three")) {
			newFloor = 5;
		} else {
			System.out.println("EllaModule unknown floor: \"" + togoFloor + "\"");
                }

		System.out.println("EllaModule new floor: " + newFloor);
		//controller commented out again because it is not attached
		//move elevator
		//controller.pushButton(newFloor);

		//change display
		if (newFloor >= 0 && newFloor < FLOORS) {
			((JLabel) frame.getContentPane().getComponent(FLOORS - currentPosition - 1 ))
					.setText(" ");
			currentPosition = newFloor;
			((JLabel) frame.getContentPane().getComponent(FLOORS - currentPosition - 1 ))
					.setText(" HERE");
		}
	}

	/**
	 * Pauses the module.
	 */
	@Override
	public void pause(boolean toPause) {
		paused = toPause;
	}

	/**
	 * Returns true is the module is not paused, and false otherwise.
	 */
	@Override
	public boolean isRunning() {
		return !paused;
	}

}


