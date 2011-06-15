/**
 * 
 */
package app.social.agents;

import sim.agents.Agent;
import sim.engine.SimState;

/**
 * @author biggie
 * 
 */
public class RandomAgent extends Agent {

    private static int COUNT = 0;
    private final static int DIM = 10;

    /**
     * 
     * @author biggie RandomAgent
     */
    public RandomAgent(SimState _state) {
	super(_state);
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
	    if (isNewFriend(ag_)) {
		befriend(ag_);
	    }
	}
    }
}
