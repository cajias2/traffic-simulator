package sim.app.social;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.agents.Agent;
import sim.agents.social.DBWriterAgent;
import sim.app.SocialSimState;
import sim.app.social.db.DBManager;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.utils.xml.social.SocialInputParseService;

@SuppressWarnings("serial")
public class SocialSimBatchRunner<V, E> extends SocialSimState {

    private static Logger _log;
    private static String _simXml;
    private static boolean _isTest;

    // private static GraphGatherer<V, E> _gatherer = null;
    private int AGENT_COUNT;
    private Map<Class<V>, Double> _agentMap;
    private DBManager _dbMgr;
    public static final double DIAMETER = 8;
    public int SIM_TIME;
    public static int XMIN = 0;
    public static int XMAX;
    public static int YMIN = 0;
    public static int YMAX;

    private static Integer _simID = null;
    public Continuous2D env;
    private int _snapshotInterval = 1;

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public SocialSimBatchRunner(Long seed, String[] args) {
	super(seed);
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);
	String simXml = SocialInputParseService.parseCmdLnArgs(args, _log).get("-sim");
	initializeThis(simXml);
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public SocialSimBatchRunner(Long seed, Map<String, String> argMap_) {
	super(seed);
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);
	_isTest = argMap_.containsKey("-test");
	initializeThis(argMap_.get("-sim"));
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     * 
     * @author biggie
     */
    public SocialSimBatchRunner(long seed) {
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
	env = new Continuous2D(25, (XMAX - XMIN), (YMAX - YMIN));
    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start() {
	super.start();
	schedule.reset(); // clear out the schedule
	if (!isTest()) {
	    _dbMgr = new DBManager();

	    _simID = _dbMgr.newSimulation(AGENT_COUNT);
	} else {
	    _simID = -1;
	}
	initiateAgents(_simID);
    }

    /**
     * @param simID
     */
    private void initiateAgents(int simID) {
	scheduleAgents();
	if (!_isTest) {
	    persistAgents(simID, AGENT_COUNT);
	    schedule.scheduleRepeating(Schedule.EPOCH, 1, new DBWriterAgent(this), 1);
	}
	schedule.scheduleRepeating(Schedule.EPOCH, 1, new SimKiller(), 1);
    }

    /**
     * @param simID_
     */
    private void persistAgents(int simID_, int totalSimAgents_) {
	int nodeCnt = _dbMgr.getDBNodeCnt();
	if (totalSimAgents_ > nodeCnt) {
	    for (int i = nodeCnt; i < totalSimAgents_; i++) {
		_dbMgr.addNodeToBatch(i);
	    }
	    _dbMgr.insertNodes();
	}
    }

    /**
     * @author biggie
     * @name scheduleAgents Purpose Schedule agents by set percent
     * @param
     * @return void
     */
    private void scheduleAgents() {
	for (int i = 0; i < AGENT_COUNT; i++) {
	    double ticket = random.nextDouble() * 100;
	    double winner = 0.0;

	    Iterator<Entry<Class<V>, Double>> it = _agentMap.entrySet().iterator();
	    while (it.hasNext()) {
		Entry<Class<V>, Double> entry = it.next();
		winner += entry.getValue();
		if (winner >= ticket) {
		    scheduleAgentOfType(entry.getKey());
		}
	    }
	}
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
	    schedule.scheduleRepeating(Schedule.EPOCH, 1, (Agent) ag, 1);
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
     * Allows the simulation to be called as part of s
     * 
     * @param
     * @return List<Graph<V,FriendLink>> A list of the graph evoltuion o
     */
    public void runSim(String[] args_) {
	Map<String, String> args = SocialInputParseService.parseCmdLnArgs(args_, _log);
	_simXml = args.get("-sim");
	_snapshotInterval = Integer.parseInt(args.get("-i"));
	_isTest = args.containsKey("-test");
	doLoop(this.getClass(), args_);
    }

    /**
     * @return The DBManager of this instance
     */
    public DBManager getDBManager() {
	return _dbMgr;
    }

    /**
     * Id of the last simulation run. null if no simulation has run yet
     * 
     * @return
     */
    public Integer getSimID() {
	return _simID;
    }

    /**
     * @return the isTest
     */
    public final boolean isTest() {
	return _isTest;
    }

    /**
     * @return the snapshotInterval
     */
    public final int getSnapshotInterval() {
	return this._snapshotInterval;
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
	Map<String, String> argMap = SocialInputParseService.parseCmdLnArgs(args, _log);
	_simXml = argMap.get("-sim");
	_isTest = argMap.containsKey("-test");
	doLoop(SocialSimBatchRunner.class, args);
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
