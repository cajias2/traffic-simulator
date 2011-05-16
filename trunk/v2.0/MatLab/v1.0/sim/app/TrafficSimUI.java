package sim.app;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import sim.utils.JungDisplay;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;

public class TrafficSimUI extends GUIState {

	public JungDisplay jDisplay;
	private static TrafficSim _trafficSim;
    private static Logger _log;
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
        
        _log = Logger.getLogger( "SimLogger" );
        _log.setLevel( Level.SEVERE );      
       
        if ( args.length < 2 || "city".equals( args[0] ) )
        {
            System.err.println( "Usage: java -jar " + clazz + ".jar -city [sim.xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
            
        }
        String cityXml = null;
        for(int i = 0; i< args.length; i++)
        {
            if("-city".equals( args[i] ))
            {
                cityXml = args[++i]; 
            }
            else if( "-verbose".equals( args[i] ) || "-v".equals( args[i] ))
            {
                _log.setLevel( Level.INFO );
            }else if("-debug".equals( args[i] ))
            {
                _log.setLevel( Level.FINE );
            }
        }
        if(null == cityXml || "".equals( cityXml ))
        {
            System.err.println( "Usage: java -jar " + clazz + ".jar -city [sim.xml file]\n"
                    + "See TrafficSimulation.xsd for details" );
            System.exit( 1 );
        }
        _trafficSim = new TrafficSim(System.currentTimeMillis(), cityXml, _log);
    	TrafficSimUI vid = new TrafficSimUI(_trafficSim);
    	Console c = new Console(vid);
    	c.setVisible(true);
    }
}