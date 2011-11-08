package sim.app.social;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.agents.Agent;
import sim.app.SocialSimState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.utils.xml.social.SocialInputParseService;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

@SuppressWarnings("serial")
public class SocialSim<V, E> extends SocialSimState {

    private static Logger _log;
    private static String _simXml;
    // private static GraphGatherer<V, E> _gatherer = null;
    private int AGENT_COUNT;
    private Map<Class<V>, Double> _agentMap;
    public static final double DIAMETER = 8;
    public int SIM_TIME;
    public static int XMIN = 0;
    public static int XMAX;
    public static int YMIN = 0;
    public static int YMAX;

    public Graph<V, E> network = null;
    public Continuous2D env;

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
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void initializeThis(String _simXml) {
	SocialInputParseService<V> parseSrv = new SocialInputParseService<V>(_simXml, _log);
	parseSrv.parseSim();
	_agentMap = parseSrv.parseAgents();
	AGENT_COUNT = parseSrv.getAgentNum();
	SIM_TIME = parseSrv.getSimDuration();
	XMAX = parseSrv.getWidth();
	YMAX = parseSrv.getLen();
	network = new UndirectedSparseGraph<V, E>();
	env = new Continuous2D(25, (XMAX - XMIN), (YMAX - YMIN));
    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start() {
	super.start();
	schedule.reset(); // clear out the schedule
	List<V> agList = scheduleAgents();
	persistAgents((List<Agent>) agList);
	schedule.scheduleRepeating(Schedule.EPOCH, 1, new SimKiller(), 1);
    }

    /**
     * 
     */
    private void persistAgents(List<Agent> agentList_) {

	for (Agent ag : agentList_) {
	    //TODO Persist
	}
    }

    /**
     * @author biggie
     * @name scheduleAgents Purpose Schedule agents by set percent
     * @param
     * @return void
     */
    private List<V> scheduleAgents() {
	List<V> agentList = new LinkedList<V>();
	for (int i = 0; i < AGENT_COUNT; i++) {
	    double ticket = random.nextDouble() * 100;
	    double winner = 0.0;

	    Iterator<Entry<Class<V>, Double>> it = _agentMap.entrySet().iterator();
	    while (it.hasNext()) {
		Entry<Class<V>, Double> entry = it.next();
		winner += entry.getValue();
		if (winner >= ticket) {
		    agentList.add(scheduleAgentOfType(entry.getKey()));
		}
	    }
	}
	return agentList;
    }

    /**
     * @param entry
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private V scheduleAgentOfType(Class<V> entry) {
	V ag = null;
	try {
	    ag = instantiateAgentObj(entry);
	    env.setObjectLocation(ag, intialLoc());
	    scheduleAgent(ag);
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    System.exit(-1);
	}
	return ag;
    }

    /**
     * @return
     */
    private Double2D intialLoc() {
	return new Double2D(random.nextDouble() * (XMAX - XMIN - DIAMETER) + XMIN + DIAMETER / 2, random.nextDouble()
		* (YMAX - YMIN - DIAMETER) + YMIN + DIAMETER / 2);
    }

    /**
     * Schedule a single agent.
     * 
     * @param ag
     */
    private void scheduleAgent(V ag) {
	network.addVertex(ag);
	schedule.scheduleRepeating(Schedule.EPOCH, 1, (Agent) ag, 1);
    }

    /**
     * Allows the simulation to be called as part of s
     * 
     * @param
     * @return List<Graph<V,FriendLink>> A list of the graph evoltuion o
     */
    public List<Graph<V, E>> runSim(String[] args_, int snapshotSize_) {
	_simXml = SocialInputParseService.parseCmdLnArgs(args_, _log);
	doLoop(SocialSim.class, args_);
	return null;
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
     * Reflective instantiation of whatever class name was passed in the xml
     * description file
     * 
     * @author biggie
     * @name instantiateAgentObj
     * @param
     * @return V
     */
    private V instantiateAgentObj(Class<V> clazz_) throws ClassNotFoundException, SecurityException,
	    NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
	    InvocationTargetException {
	Object[] argObj = new Object[] { this };
	Class[] argClass = new Class[] { SimState.class };

	Constructor<V> cons = clazz_.getConstructor(argClass);
	Object obj = cons.newInstance(argObj);
	return (V) obj;
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
