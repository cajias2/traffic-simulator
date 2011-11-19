/**
 * 
 */
package sim.agents;

import java.awt.Font;
import java.util.Iterator;

import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import ec.util.MersenneTwisterFast;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author biggie
 */
public class Agent implements Steppable {

    public Font nodeFont = new Font("SansSerif", Font.PLAIN, 9);
    private int steps = 0;
    private Double2D desiredLocation = null;
    protected MersenneTwisterFast _rand = null;
    protected static Graph<Agent, String> _socGraph = new UndirectedSparseGraph<Agent, String>();
    protected static Graph<Agent, sim.graph.utils.Edge> _deltaGraph = new UndirectedSparseGraph<Agent, sim.graph.utils.Edge>();
    protected static Network _testNet = new Network();
    protected final int SNAPSHOT;
    private static int _agentCount = 0;
    protected int _id;
    protected int _actionDim = 0;
    protected final boolean IS_TEST;

    public Agent(final SimState state_) {
	SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	SNAPSHOT = ((SocialSimBatchRunner) state_).getSnapshotInterval();
	_rand = socSim.random;
	_id = _agentCount;
	IS_TEST = socSim.isTest();
	if (IS_TEST) {
	    _testNet.addNode(this);
	} else {
	    _socGraph.addVertex(this);
	}
	_agentCount++;
    }

    /**
     * 
     */
    public void step(final SimState state_) {
	@SuppressWarnings("unchecked")
	SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	beforeStep(socSim);
	Double2D currLoc = socSim.env.getObjectLocation(this);
	Bag objs = socSim.env.getObjectsExactlyWithinDistance(new Double2D(currLoc.x, currLoc.y), _actionDim);

	@SuppressWarnings("unchecked")
	Iterator<Agent> iter = objs.iterator();
	while (iter.hasNext()) {
	    Agent ag = iter.next();
	    // make sure not the same obj, and an edge does not already exist.
	    if (this != ag) {
		interactWithAgent(ag);
	    }
	}

	Double2D newLoc = move(state_);
	socSim.env.setObjectLocation(this, newLoc);
	afterStep(socSim);
    }

    /**
     * @author biggie
     * @name interactWithAgent
     * @param
     * @return void
     */
    protected void interactWithAgent(Agent ag_) {
    }

    /**
     * @author biggie
     * @name afterStep Purpose: Hook method to prepare child agents for step
     * @param SimState
     *            The state of the simulation
     * @return void
     */
    protected void afterStep(SocialSimBatchRunner<Agent, String> state_) {
    }

    /**
     * @author biggie
     * @name beforeStep Purpose:Hook method to perform actions after step
     * @param SimState
     *            The state of the simulation
     * @return void
     */
    protected void beforeStep(SocialSimBatchRunner<Agent, String> state_) {
    }

    /**
     * @author biggie
     * @name setRandomEngine
     * @param
     * @return void
     */
    protected void setRandomEngine(MersenneTwisterFast random_) {
	_rand = random_;
    }

    /**
     * @return
     */
    protected boolean isNewFriend(Agent ag_) {
	return false;
    }

    /**
     * @author biggie
     * @name move
     * @param
     * @return Double2D
     */
    protected Double2D move(SimState state_) {
	@SuppressWarnings("unchecked")
	SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	Double2D currLoc = socSim.env.getObjectLocation(this);
	steps--;
	if (desiredLocation == null || steps <= 0) {
	    desiredLocation = new Double2D((state_.random.nextDouble() - 0.5)
		    * ((SocialSimBatchRunner.XMAX - SocialSimBatchRunner.XMIN) / 5 - SocialSimBatchRunner.DIAMETER)
		    + currLoc.x, (state_.random.nextDouble() - 0.5)
		    * ((SocialSimBatchRunner.YMAX - SocialSimBatchRunner.YMIN) / 5 - SocialSimBatchRunner.DIAMETER)
		    + currLoc.y);
	    steps = 50 + state_.random.nextInt(50);
	}

	double dx = desiredLocation.x - currLoc.x;
	double dy = desiredLocation.y - currLoc.y;
	double temp = /* Strict */Math.sqrt(dx * dx + dy * dy);
	if (temp < 1) {
	    steps = 0;
	} else {
	    dx /= temp;
	    dy /= temp;
	}

	return new Double2D(currLoc.x + dx, currLoc.y + dy);
    }

    public final int getID() {
	return _id;
    }

    /**
     * @author biggie
     * @name befriend
     * @param
     * @return void
     */
    protected void befriend(Agent ag_) {
	if (IS_TEST) {
	    Edge e = new Edge(this, ag_, true);
	    // e.)
	    _testNet.addEdge(this, ag_, e);
	} else {
	    _socGraph.addEdge(this + "_" + ag_, this, ag_);
	    updateDeltaGraph(ag_, true);
	}
    }

    /**
     * @param ag_
     */
    protected void unfriend(Agent ag_) {
	if (IS_TEST) {
	    removeEdgeNetwork(this, ag_);
	} else {
	    _socGraph.removeEdge(_socGraph.findEdge(this, ag_));
	    updateDeltaGraph(ag_, false);

	}

    }

    /**
     * @param from_
     * @param to_
     */
    private void removeEdgeNetwork(Object from_, Object to_) {
	Bag edges = new Bag();
	_testNet.getEdges(from_, edges);
	for (int i = 0; i < edges.size(); i++) {
	    Edge edge = (Edge) edges.get(i);
	    if (edge.from() == to_ || edge.to() == to_) {
		_testNet.removeEdge(edge);
	    }
	}

    }

    /**
     * Updates delta graph.
     * 
     * @param ag_
     * @param isCreateEdge_
     */
    private void updateDeltaGraph(Agent ag_, boolean isCreateEdge_) {
	if (!_deltaGraph.containsVertex(this)) {
	    _deltaGraph.addVertex(this);
	}

	if (!_deltaGraph.containsVertex(ag_)) {
	    _deltaGraph.addVertex(ag_);
	}

	boolean result = _deltaGraph.addEdge(new sim.graph.utils.Edge(isCreateEdge_), this, ag_);
	// Removed negated edge.
	if (!result) {
	    _deltaGraph.removeEdge(_deltaGraph.findEdge(this, ag_));
	}
    }

    /**
     * @return the testNet
     */
    public static final Network getTestNet() {
	return _testNet;
    }

    @Override
    public boolean equals(Object o_) {
	return (o_ instanceof Agent && getID() == ((Agent) o_).getID());
    }
}
