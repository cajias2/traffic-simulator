package sim.app.social;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.agents.Agent;
import sim.app.SocialSimState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Edge;
import sim.mason.AgentNetwork;
import sim.util.Double2D;
import sim.utils.xml.social.SocialInputParseService;

@SuppressWarnings("serial")
public class SocialSim extends SocialSimState {

    private static Logger _log;
    private static String _simXml;
    private static Random _rand = new Random(System.currentTimeMillis());
    private static GraphGatherer _gatherer = null;
    private int AGENT_COUNT;
    private Map<Class<Agent>, Double> _agentMap;
    public static final double DIAMETER = 8;
    public int SIM_TIME;
    public static int XMIN = 0;
    public static int XMAX;
    public static int YMIN = 0;
    public static int YMAX;

    public AgentNetwork network = null;
    public Continuous2D fieldEnvironment;

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public SocialSim(Long seed, String[] args) {
	super(seed);
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);
	String simXml = SocialInputParseService.parseCmdLnArgs(args, _log);
	initializeThis(simXml);
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public SocialSim(Long seed, String simXml_) {
	super(seed);
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);
	initializeThis(simXml_);
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     * 
     * @author biggie
     */
    public SocialSim(long seed) {
	super(seed);
	if (null != _simXml) {
	    initializeThis(_simXml);
	}
    }

    /**
     * 
     * TODO Purpose
     * @params 
     * @return void
     * @author biggie
     */
    private void initializeThis(String _simXml) {
	SocialInputParseService parseSrv = new SocialInputParseService(_simXml, _log);
	parseSrv.parseSim();
	_agentMap = parseSrv.parseAgents();
	AGENT_COUNT = parseSrv.getAgentNum();
	SIM_TIME = parseSrv.getSimDuration();
	XMAX = parseSrv.getWidth();
	YMAX = parseSrv.getLen();
	network = new AgentNetwork();
	fieldEnvironment = new Continuous2D(25, (XMAX - XMIN), (YMAX - YMIN));
    }


    /**
     * @author biggie
     * @name acceptablePosition Purpose Validate new position: Make sure not
     *       over the boundaries.
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

	scheduleAgents();
	// schedule.scheduleRepeating(Schedule.EPOCH, 1, new MetricsAgent(), 1);
	// Schedule simKiller last.
	if (null != _gatherer) {
	    schedule.scheduleRepeating(Schedule.EPOCH, 1, _gatherer, 1);
	}
	schedule.scheduleRepeating(Schedule.EPOCH, 1, new SimKiller(), 1);
    }

    /**
     * @author biggie
     * @name scheduleAgents Purpose Schedule agents by set percent
     * @param
     * @return void
     */
    private void scheduleAgents() {
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
    }

    /**
     * Allows the simulation to be called as part of s
     * 
     * @param
     * @return List<Graph<Agent,FriendLink>> A list of the graph evoltuion o
     */
    public List<Edge[][]> runSim(String[] args_, int snapshotSize_) {
	_simXml = SocialInputParseService.parseCmdLnArgs(args_, _log);
	_gatherer = new GraphGatherer(snapshotSize_);
	doLoop(SocialSim.class, args_);
	return _gatherer.getGraphEvol();
    }

    /**
     * @author biggie
     * @name main Purpose TODO
     * @param
     * @return void
     */
    public static void main(String[] args) {
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);

	_simXml = SocialInputParseService.parseCmdLnArgs(args, _log);
	doLoop(SocialSim.class, args);
    }


    /**
     * @author biggie
     * @name instantiateAgentObj Purpose TODO
     * @param
     * @return Agent
     */
    private Agent instantiateAgentObj(Class<Agent> clazz_) throws ClassNotFoundException, SecurityException,
	    NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
	    InvocationTargetException {
	Object[] argObj = new Object[] { this };
	Class[] argClass = new Class[] { SimState.class };

	Constructor<Agent> cons = clazz_.getConstructor(argClass);
	Object obj = cons.newInstance(argObj);
	return (Agent) obj;
    }

    /**
     * Kills the simulation after <code>SIM_TIME</code> steps
     * 
     * @author biggie
     * @param <V>
     */
    private class GraphGatherer implements Steppable {
	private final int SNAPSHOT;
	List<Edge[][]> _graphEvol = new ArrayList<Edge[][]>();

	public GraphGatherer(int snapshotSize_) {
	    SNAPSHOT = snapshotSize_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state_) {
	    SocialSim socSim = (SocialSim) state_;
	    if (0 == socSim.schedule.getSteps() % SNAPSHOT) {
		_graphEvol.add(socSim.network.getAdjacencyMatrix());
	    }
	}

	/**
	 * @return the graphEvol
	 * @author biggie
	 */
	public List<Edge[][]> getGraphEvol() {
	    return _graphEvol;
	}
    }

    /**
     * Kills the simulation after <code>SIM_TIME</code> steps
     * 
     * @author biggie
     */
    private class SimKiller implements Steppable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {
	    if (SIM_TIME <= schedule.getSteps()) {
		state.finish();
	    }
	}
    }

}
