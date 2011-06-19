/**
 * 
 */
package app.social.agents;

import java.util.HashSet;
import java.util.Set;

import sim.agents.Agent;
import sim.engine.SimState;

/**
 * @author biggie
 * 
 */
public class RandomAgent extends Agent {

    private static final long serialVersionUID = -519454272167626554L;
    private static int COUNT = 0;
    private final static int DIM = 20;
    private final Set<Agent> _unfriends;

    /**
     * 
     * @author biggie RandomAgent
     */
    public RandomAgent(SimState _state) {
	super(_state);
	_unfriends = new HashSet<Agent>();
	_actionDim = DIM;
	_id = COUNT;
	COUNT++;
    }

    /**
     * 
     */
    @Override
    public String toString() {
	return "Rand_" + _id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#makeFriend(sim.engine.SimState)
     */
    @Override
    protected boolean isNewFriend(Agent ag_) {
	return _rand.nextBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#interactWithAgent(sim.agents.Agent,
     * sim.engine.SimState)
     */
    @Override
    protected void interactWithAgent(Agent ag_) {
	if (!_net.hasEdge(this, ag_)) {
	    if (!_unfriends.contains(ag_) && isNewFriend(ag_)) {
		befriend(ag_);
	    } else {
		_unfriends.add(ag_);
	    }
	}
    }
}
