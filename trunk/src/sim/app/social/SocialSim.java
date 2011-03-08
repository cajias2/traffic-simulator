package sim.app.social;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.agents.Agent;
import sim.app.NetworkSimState;
import sim.engine.Schedule;
import sim.field.continuous.Continuous2D;
import sim.mason.AgentNetwork;
import sim.util.Double2D;
import sim.utils.xml.social.SocialInputParseService;

@SuppressWarnings("serial")
public class SocialSim extends NetworkSimState {

    private final static String clazz = SocialSim.class.getSimpleName();
    private static Logger _log;
    private static String _simXml;
    private static Random _rand = new Random(System.currentTimeMillis());

    private final int AGENT_COUNT;
    private final int SIM_TIME;
    private Map _agentMap;
    public Continuous2D environment = null;
    public AgentNetwork network = null;
    /**
     * Creates a TrafficSim simulation with the given random number seed.
     */
    public SocialSim(long seed, String simXml_, Logger log_) {
	super(seed);
	simXml_ = System.getProperty("user.dir") + simXml_;
	SocialInputParseService parsedIn = new SocialInputParseService(simXml_, log_);

	AGENT_COUNT = parsedIn.getAgentNum();
	SIM_TIME = parsedIn.getSimDuration();
	_log = log_;
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public SocialSim(long seed) {
	super(seed);
	SocialInputParseService parseSrv = new SocialInputParseService(_simXml, _log);
	parseSrv.parseSim();
	_agentMap = parseSrv.parseAgents();
	AGENT_COUNT = parseSrv.getAgentNum();
	SIM_TIME = parseSrv.getSimDuration();
    }

    boolean acceptablePosition(final Agent node, final Double2D location) {
	if (location.x < DIAMETER / 2 || location.x > (XMAX - XMIN) - DIAMETER / 2 || location.y < DIAMETER / 2
		|| location.y > (YMAX - YMIN) - DIAMETER / 2)
	    return false;
	return true;
    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start() {
	super.start();
	schedule.reset(); // clear out the schedule
	
	for (int i = 0; i < AGENT_COUNT; i++) {
	    double ticket = _rand.nextDouble() * 100;
	    double winner = 0.0;
	    Iterator<Entry<Class<Agent>, Double>> it = _agentMap.entrySet().iterator();
	    while (it.hasNext()) {
		Entry<Class<Agent>, Double> entry = it.next();
		winner += entry.getValue();
		if (winner >= ticket) {
		    try {
			schedule.scheduleRepeating(Schedule.EPOCH, 1, instantiateAgentObj(entry.getKey()), 1);
		    } catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		    }
		}
	    }
	}
    }

    /**
     * @param className_
     * @param agentArgs_
     * @param agentArgsClass_
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Agent instantiateAgentObj(Class<Agent> clazz_)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	Constructor<Agent> cons = clazz_.getConstructor();
	Object obj = cons.newInstance();
	return (Agent) obj;
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
