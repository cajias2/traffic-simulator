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
public class PersonalityAgent extends Agent {

    private static final long serialVersionUID = -2342955570432688000L;

    private final double _personality;

    private final double _accepThresh;

    private final static int DIM = 7;

    /**
     * @param state_
     */
    public PersonalityAgent(SimState state_) {
	super(state_);
	_personality = _rand.nextDouble();
	_accepThresh = 0.1;
	_actionDim = DIM;
    }

    /**
     * 
     */
    @Override
    public String toString() {
	return "PersonMsgr_" + _id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#makeFriend(sim.engine.SimState)
     */
    @Override
    protected boolean isNewFriend(Agent ag_) {
	boolean isNewFriend = _rand.nextBoolean();
	if (ag_ instanceof PersonalityAgent) {
	    PersonalityAgent pa = (PersonalityAgent) ag_;
	    if (_accepThresh >= Math.abs((_personality - pa.getPersonality()))) {
		isNewFriend = true;
	    }
	}
	return isNewFriend;
    }


    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#interactWithAgent(sim.agents.Agent,
     * sim.engine.SimState)
     */
    @Override
    protected void interactWithAgent(Agent ag_) {
	if (_socGraph.findEdge(this, ag_) == null) {
	    if (isNewFriend(ag_)) {
		befriend(ag_);
	    }
	}
    }

    public double getPersonality() {
	return _personality;
    }
}
