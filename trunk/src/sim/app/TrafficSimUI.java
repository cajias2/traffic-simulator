package sim.app;

import sim.app.utils.JungDisplay;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;

public class TrafficSimUI extends GUIState {

	public JungDisplay jDisplay;


	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TrafficSimUI vid = new TrafficSimUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}

	/**
	 * Class constructor
	 */
	public TrafficSimUI() {
		super(new TrafficSim(System.currentTimeMillis()));
	}

	/**
	 * Class constructor
	 * @param state
	 */
	public TrafficSimUI(SimState state) {
		super(state);
	}

	/**
	 * Test Name
	 * @return
	 */
	public static String getName() {
		return "Traffic Test";
	}

	/**
	 * Start simulation
	 */
	public void start() {
		super.start();
		jDisplay.reset();
	}

	/**
	 * Load preexisting simulation
	 */
	public void load(SimState state) {
		super.load(state);
		jDisplay.reset();
	}
	
	/**
	 * Initialize visualizer window
	 */
	public void init(Controller c) {
		super.init(c);

		// Instantiate JungDisplay
		jDisplay = new JungDisplay(this);
		jDisplay.frame.setTitle("Preferential attachment graph");
		c.registerFrame(jDisplay.frame);
		jDisplay.frame.setVisible(true);		
	}

	/**
	 * Code cleanup upon quitting.
	 */
	public void quit() {
		super.quit();

		if (jDisplay.frame != null)
			jDisplay.frame.dispose();
		jDisplay.frame = null;
		jDisplay = null;
	}
}