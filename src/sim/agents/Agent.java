/**
 * 
 */
package sim.agents;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    private static Graph<Agent, Edge> _socGraph = new UndirectedSparseGraph<Agent, Edge>();
    protected static Graph<Agent, Edge> _deltaGraph = new UndirectedSparseGraph<Agent, Edge>();
    private static Network _testNet = new Network();
    protected static List<Agent> _agentList = new ArrayList<Agent>();
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
	_agentList.add(this);
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
    protected boolean shouldBefriend(Agent ag_) {
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

    @Override
    public String toString() {
	return this.getClass().getName() + "_" + _id;
    }

    /**
     * @author biggie
     * @name befriend
     * @param
     * @return void
     */
    protected void befriend(Agent ag_) {
	Edge e = new Edge(this, ag_, true);
	if (IS_TEST) {
	    _testNet.addEdge(this, ag_, e);
	    _socGraph.addEdge(e, this, ag_);
	} else {
	    _socGraph.addEdge(e, this, ag_);
	    updateDeltaGraph(ag_, true);
	}
    }

    /**
     * @author biggie
     * @name befriend
     * @param
     * @return void
     */
    protected void befriend(Agent ag_, double weight_) {
	befriend(this, ag_, weight_);
    }

    protected void befriend(Agent ag1_, Agent ag2_, double weight_) {
	Edge e = new Edge(ag1_, ag2_, weight_);
	if (IS_TEST) {
	    _testNet.addEdge(ag1_, ag2_, e);
	    _socGraph.addEdge(e, ag1_, ag2_);
	} else {
	    _socGraph.addEdge(e, ag1_, ag2_);
	    updateDeltaGraph(ag1_, ag2_, true);
	}
    }
    protected void updateWeight(Agent ag1_, Agent ag2_, double weight_) {
	if (IS_TEST) {
	    removeEdgeNetwork(ag1_, ag2_);
	    befriend(ag1_, ag2_, weight_);
	} else {
	    _socGraph.findEdge(ag1_, ag2_).setInfo(weight_);
	    updateDeltaGraph(ag1_, ag2_, true);
	}
    }

    protected void updateWeight(Agent ag_, double weight_) {
	updateWeight(ag_, ag_, weight_);
    }

    /**
     * @param ag_
     */
    protected void unfriend(Agent ag_) {
	unfriend(this, ag_);
    }

    protected void unfriend(Agent ag1_, Agent ag2_) {
	if (IS_TEST) {
	    removeEdgeNetwork(ag1_, ag2_);
	} else {
	    _socGraph.removeEdge(_socGraph.findEdge(ag1_, ag2_));
	    updateDeltaGraph(ag1_, ag2_, false);
	}
    }
    /**
     * @param ag_
     * @return
     */
    protected boolean isFriend(Agent ag_) {
	return isFriend(this, ag_);
    }

    protected boolean isFriend(Agent ag1_, Agent ag2_) {
	boolean isFriend = false;
	if (IS_TEST) {
	    isFriend = findEdgeTestMode(ag1_, ag2_) != null;
	} else {
	    isFriend = _socGraph.findEdge(ag1_, ag2_) != null;
	}
	return isFriend;
    }

    /**
     * @param ag_
     * @return
     */
    protected double getEdgeWeight(Agent ag_) {
	return getEdgeWeight(this, ag_);
    }

    /**
     * @param ag_
     * @return
     */
    protected double getEdgeWeight(Agent ag1_, Agent ag2_) {
	double weight = -1;
	if (IS_TEST) {
	    weight = findEdgeTestMode(ag1_, ag2_).getWeight();
	} else {
	    weight = _socGraph.findEdge(ag1_, ag2_).getWeight();
	}
	return weight;
    }

    protected Collection<Agent> getNeighbours() {
	return getNeighbours(this);
    }

    protected Collection<Agent> getNeighbours(Agent ag_) {
	return _socGraph.getNeighbors(ag_);
    }


    /**
     * @param ag_
     * @return
     */
    private Edge findEdgeTestMode(Agent ag_) {
	return findEdgeTestMode(this, ag_);
    }

    /**
     * @param ag_
     * @return
     */
    private Edge findEdgeTestMode(Agent ag1_, Agent ag2_) {
	Edge edge = null;
	Bag bag = _testNet.getEdges(ag1_, null);
	{
	    for (Object obj : bag) {
		Edge e = (Edge) obj;
		if (e.from().equals(ag2_) || e.to().equals(ag2_)) {
		    edge = e;
		    break;
		}
	    }
	}
	return edge;
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
	    if (edge.from().equals(to_) || edge.to().equals(to_)) {
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
	updateDeltaGraph(this, ag_, isCreateEdge_);
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void updateDeltaGraph(Agent ag1_, Agent ag2_, boolean isCreateEdge_) {
	if (!_deltaGraph.containsVertex(ag1_)) {
	    _deltaGraph.addVertex(ag1_);
	}
	if (!_deltaGraph.containsVertex(ag2_)) {
	    _deltaGraph.addVertex(ag2_);
	}
	// Remove negated edge
	if (_deltaGraph.findEdge(ag1_, ag2_) != null
		&& ((Boolean) _deltaGraph.findEdge(ag1_, ag2_).getInfo()) != isCreateEdge_) {
	    _deltaGraph.removeEdge(_deltaGraph.findEdge(ag1_, ag2_));
	}else{
	    _deltaGraph.addEdge(new Edge(ag1_, ag2_, isCreateEdge_), ag1_, ag2_);
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

    public final static Graph getSocGraph() {
	return _socGraph;
    }

}
