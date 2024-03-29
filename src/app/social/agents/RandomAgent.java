/**
 * 
 */
package app.social.agents;

import sim.agents.Agent;
import sim.engine.SimState;

/**
 * @author biggie
 */
public class RandomAgent extends Agent {

    private static final long serialVersionUID = -519454272167626554L;
    private static int COUNT = 0;
    private final static int DIM = 10;

    /**
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
    protected boolean shouldBefriend(Agent ag_) {
	return _rand.nextBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#interactWithAgent(sim.agents.Agent,
     * sim.engine.SimState)�
     */
    @Override
    protected void interactWithAgent(Agent ag_) {
	if (isFriend(ag_)) {
	    if (shouldBefriend(ag_)) {
		befriend(ag_);
	    }
	} else if (shouldBefriend(ag_)) {
	    unfriend(ag_);
	}
    }
}
