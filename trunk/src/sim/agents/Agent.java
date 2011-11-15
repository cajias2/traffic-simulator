/**
 * 
 */
package sim.agents;

import java.awt.Font;
import java.util.Iterator;

import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.utils.Edge;
import sim.util.Bag;
import sim.util.Double2D;
import ec.util.MersenneTwisterFast;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public class Agent implements Steppable {

    public Font nodeFont = new Font("SansSerif", Font.PLAIN, 9);
    private int steps = 0;
    private Double2D desiredLocation = null;
    protected MersenneTwisterFast _rand = null;
    protected Graph<Agent, String> _net;
    protected Graph<Agent, Edge> _temp;
    private static int _agentCount = 0;

    protected int _id;
    protected int _actionDim = 0;

    public Agent(final SimState state_) {
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	_net = socSim.network;
	_rand = socSim.random;
	_id = _agentCount;
	_agentCount++;
    }

    /**
     * 
     */
    public void step(final SimState state_) {

	@SuppressWarnings("unchecked")
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	_temp = socSim._temporalNetwork;
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
    protected void afterStep(SocialSim<Agent, String> state_) {
    }

    /**
     * @author biggie
     * @name beforeStep Purpose:Hook method to perform actions after step
     * @param SimState
     *            The state of the simulation
     * @return void
     */
    protected void beforeStep(SocialSim<Agent, String> state_) {
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
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	Double2D currLoc = socSim.env.getObjectLocation(this);
	steps--;
	if (desiredLocation == null || steps <= 0) {
	    desiredLocation = new Double2D((state_.random.nextDouble() - 0.5)
		    * ((SocialSim.XMAX - SocialSim.XMIN) / 5 - SocialSim.DIAMETER) + currLoc.x,
		    (state_.random.nextDouble() - 0.5) * ((SocialSim.YMAX - SocialSim.YMIN) / 5 - SocialSim.DIAMETER)
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
	_net.addEdge(this + "_" + ag_, this, ag_);
	updateDeltaGraph(ag_, true);
    }

    /**
     * @param ag_
     */
    protected void unfriend(Agent ag_) {
	_net.removeEdge(_net.findEdge(this, ag_));
	updateDeltaGraph(ag_, false);
    }

    /**
     * Updates delta graph.
     * 
     * @param ag_
     * @param isCreateEdge_
     */
    private void updateDeltaGraph(Agent ag_, boolean isCreateEdge_) {
	if (!_temp.containsVertex(this)) {
	    _temp.addVertex(this);
	}

	if (!_temp.containsVertex(ag_)) {
	    _temp.addVertex(ag_);
	}

	boolean result = _temp.addEdge(new Edge(isCreateEdge_), this, ag_);
	// Removed negated edge.
	if (!result) {
	    _temp.removeEdge(_temp.findEdge(this, ag_));
	}
    }

    @Override
    public boolean equals(Object o_) {
	return (o_ instanceof Agent && getID() == ((Agent) o_).getID());
    }
}
