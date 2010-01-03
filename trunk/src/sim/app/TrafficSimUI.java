package sim.app;

import sim.app.utils.JungDisplay;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;

public class TrafficSimUI extends GUIState {

	public JungDisplay jDisplay;
	private static TrafficSim _trafficSim;
    
    private static final String clazz = TrafficSimUI.class.getSimpleName();


	/**
	 * Class constructor
	 */
	@Deprecated
	public TrafficSimUI() {
		super(new TrafficSim(System.currentTimeMillis()));
	}
	
	public TrafficSimUI(TrafficSim _sim)
	{
	    super(_sim);
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

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        if ( args.length != 2 || "city".equals( args[0] ) )
        {
            System.err.println( "Usage: java " + clazz + ".jar -city [xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
            
        }
        _trafficSim = new TrafficSim(System.currentTimeMillis(), args[1]);
    	TrafficSimUI vid = new TrafficSimUI(_trafficSim);
    	Console c = new Console(vid);
    	c.setVisible(true);
    }
}