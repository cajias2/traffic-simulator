/*
 * @(#)MsgrAgent.java    %I%    %G%
 * @author biggie
 * 
 */

package app.social.agents;

import java.util.LinkedList;
import java.util.Queue;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;

/**
 * @author biggie
 *
 */
public class MsgrAgent extends Agent {

    private static int COUNT = 0;
    private final static int DIM = 3;

    private final Queue<Agent> _msgs = new LinkedList<Agent>();

    /**
     * 
     * @author biggie RandomAgent
     */
    public MsgrAgent(SimState state_) {
	super(state_);
	_actionDim = DIM;
	_id = COUNT;
	COUNT++;
    }


    /* (non-Javadoc)
     * @see sim.agents.Agent#makeFriend(sim.agents.Agent, sim.engine.SimState)
     */
    @Override
    protected boolean isNewFriend(Agent ag_) {
	return _rand.nextBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#interactWithAgent(sim.agents.Agent)
     */
    @Override
    protected void interactWithAgent(Agent ag_) {
	if (ag_ instanceof MsgrAgent) {
	    MsgrAgent mag = (MsgrAgent) ag_;
	    if (matchCriteria(mag)) {
		mag.sendMg(this);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#beforeStep(sim.engine.SimState)
     */
    @Override
    protected void beforeStep(SocialSim state_) {
	while (!_msgs.isEmpty()) {
	    Agent ag = _msgs.remove();
	    if (isNewFriend(ag)) {
		befriend(ag);
	    }
	}
    }
    /**
     * 
     * @author biggie
     * @name sendMeg Purpose TODO
     * 
     * @param
     * @return void
     */
    public void sendMg(MsgrAgent ag_) {
	_msgs.add(ag_);
    }

    /**
     * 
     * @author biggie
     * @name matchCriteria Purpose TODO
     * 
     * @param
     * @return boolean
     */
    protected boolean matchCriteria(Agent ag_) {
	return _rand.nextBoolean();
    }

    @Override
    public String toString() {
	return "MSGR_" + _id;
    }
}
