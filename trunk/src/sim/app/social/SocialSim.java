package sim.app.social;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.agents.traffic.vhcl.Vehicle;
import sim.app.NetworkSimState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.utils.xml.traffic.XmlInputTrafficParseService;

@SuppressWarnings("serial")
public class SocialSim extends NetworkSimState {

    private final static String clazz = SocialSim.class.getSimpleName();
    private static Logger _log;
    private static String _simXml;
    private static Random _rand = new Random(System.currentTimeMillis());

    public static int AGENT_COUNT;
    public static int SIM_TIME;

    /**
     * Creates a TrafficSim simulation with the given random number seed.
     */
    public SocialSim(long seed, String simXml_, Logger log_) {
	super(seed);
	simXml_ = System.getProperty("user.dir") + simXml_;
	XmlInputTrafficParseService parsedGraph = new XmlInputTrafficParseService(simXml_, log_);
	setNetwork(parsedGraph.getGraph());
	AGENT_COUNT = parsedGraph.getMaxCars();
	SIM_TIME = parsedGraph.getSimDuration();
	_log = log_;
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public SocialSim(long seed) {
	super(seed);
	XmlInputTrafficParseService parsedGraph = new XmlInputTrafficParseService(_simXml, _log);
	setNetwork(parsedGraph.getGraph());
	AGENT_COUNT = parsedGraph.getMaxCars();
	SIM_TIME = parsedGraph.getSimDuration();
    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start() {
	super.start();
	schedule.reset(); // clear out the schedule

	Steppable carGenerator = new Steppable() {

	    public void step(SimState state) {
		if (SIM_TIME <= schedule.getSteps()) {
		    for (Vehicle v : Vehicle.getActiveVhcl()) {
			v.finalizeLog(schedule.getSteps());
		    }
		    state.finish();
		    System.exit(0);
		}
	    }
	};
	// Schedule the car Generator
	schedule.scheduleRepeating(Schedule.EPOCH, 1, carGenerator, 1);
    }

    /**
     * Main
     * 
     * @param args
     */
    public static void main(String[] args) {
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);

	if (args.length < 2) {
	    printMsgAndExit();
	}

	for (int i = 0; i < args.length; i++) {
	    if ("-sim".equals(args[i])) {
		_simXml = args[++i];
	    } else if ("-verbose".equals(args[i]) || "-v".equals(args[i])) {
		_log.setLevel(Level.INFO);
	    } else if ("-debug".equals(args[i])) {
		_log.setLevel(Level.FINE);
	    }
	}
	if (null == _simXml || "".equals(_simXml)) {
	    printMsgAndExit();
	}
	_simXml = System.getProperty("user.dir") + _simXml;
	doLoop(SocialSim.class, args);
    }

    /**
     * Error message.
     */
    private static void printMsgAndExit() {
	System.err.println("Usage: java -jar " + clazz + ".jar -city [xml file]\n"
	    + "See TrafficSimulation.xsd for details");
	System.exit(1);
    }
}
