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
import sim.agents.social.MetricsAgent;
import sim.app.NetworkSimState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
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

    public static int XMIN = 0;
    public static int XMAX;
    public static int YMIN = 0;
    public static int YMAX;

    public static final double DIAMETER = 8;

    private final int AGENT_COUNT;
    public final int SIM_TIME;
    private final Map _agentMap;
    public AgentNetwork network = null;
    public Continuous2D fieldEnvironment;

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     * 
     * @author biggie SocialSim
     */
    public SocialSim(long seed) {
	this(seed, _simXml);
    }

    public SocialSim(long seed, String _simXml) {
	super(seed);
	SocialInputParseService parseSrv = new SocialInputParseService(_simXml, _log);
	parseSrv.parseSim();
	_agentMap = parseSrv.parseAgents();
	AGENT_COUNT = parseSrv.getAgentNum();
	SIM_TIME = parseSrv.getSimDuration();
	XMAX = parseSrv.getWidth();
	YMAX = parseSrv.getLen();
	network = new AgentNetwork();
	createGrids();
    }

    /**
     * 
     * @author biggie
     * @name createGrids Purpose TODO
     * 
     * @param
     * @return void
     */
    private void createGrids() {
	fieldEnvironment = new Continuous2D(25, (XMAX - XMIN), (YMAX - YMIN));
    }

    /**
     * @author biggie
     * @name acceptablePosition Purpose Validate new position: Make sure not
     *       over the boundaries.
     * 
     * @param node_
     *            a given node
     * @param location_
     *            proposed location
     * @return boolean
     */
    public boolean acceptablePosition(final Agent node_, final Double2D location_) {
	if (location_.x < DIAMETER / 2 || location_.x > (XMAX - XMIN) - DIAMETER / 2 || location_.y < DIAMETER / 2
		|| location_.y > (YMAX - YMIN) - DIAMETER / 2)
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

	// Dummy agent that kills sim after set steps
	Steppable simKiller = new Steppable() {
	    public void step(SimState state) {
		if (SIM_TIME <= schedule.getSteps()) {
		    state.finish();
		    System.exit(0);
		}
	    }
	};
	/*
	 * Schedule agents by set percent
	 */
	for (int i = 0; i < AGENT_COUNT; i++) {
	    double ticket = _rand.nextDouble() * 100;
	    double winner = 0.0;
	    Agent ag;

	    Iterator<Entry<Class<Agent>, Double>> it = _agentMap.entrySet().iterator();
	    while (it.hasNext()) {
		Entry<Class<Agent>, Double> entry = it.next();
		winner += entry.getValue();
		if (winner >= ticket) {
		    try {			
			ag = instantiateAgentObj(entry.getKey());
			fieldEnvironment.setObjectLocation(ag, new Double2D(random.nextDouble()
				* (XMAX - XMIN - DIAMETER) + XMIN + DIAMETER / 2, random.nextDouble()
				* (YMAX - YMIN - DIAMETER) + YMIN + DIAMETER / 2));
			network.addNode(ag);
			schedule.scheduleRepeating(Schedule.EPOCH, 1, ag, 1);
		    } catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		    }
		}
	    }
	}
	schedule.scheduleRepeating(Schedule.EPOCH, 1, new MetricsAgent(), 1);
	// Schedule simKiller last.
	schedule.scheduleRepeating(Schedule.EPOCH, 1, simKiller, 1);
    }


    /**
     * 
     * @author biggie
     * @name instantiateAgentObj Purpose TODO
     * 
     * @param
     * @return Agent
     */
    private Agent instantiateAgentObj(Class<Agent> clazz_)
 throws ClassNotFoundException, SecurityException,
	    NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
	    InvocationTargetException {
	Constructor<Agent> cons = clazz_.getConstructor();
	Object obj = cons.newInstance();
	return (Agent) obj;
    }

    /**
     * 
     * @author biggie
     * @name main Purpose TODO
     * 
     * @param
     * @return void
     */
    public static void main(String[] args) {
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);

	_simXml = SocialInputParseService.parseCmdLnArgs(args, _log);
	doLoop(SocialSim.class, args);
    }


}
