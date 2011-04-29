/**
 * 
 */
package social.agents;

import java.util.Collection;
import java.util.LinkedList;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;

/**
 * @author biggie
 */
public class CFAgent extends Agent {

    private static final int INIT_Y_VAR = 10;
    private static final int INIT_Y_MEDIAN = 50;
    private static final double PEER_BIAS = 0.5;
    private static final double B0_TIE_RETAINING_COEFF = 0.5;
    private static final double B1_HOMOPHILY_COEFF = 0.0125;

    private final static int DIM = 7;
    private double _personality;// Y_{t_0}
    /**
     * Reset in afterMethod.
     */
    protected Collection<Agent> _friends;

    /**
     * @param state_
     */
    public CFAgent(SimState state_) {
	super(state_);
	_personality = (_rand.nextGaussian() + INIT_Y_MEDIAN) * INIT_Y_VAR;
	_actionDim = DIM;
	_friends = new LinkedList<Agent>();
    }

    double getBasePersonality() {
	return _personality;
    }

    /**
     * @return Y_t1
     */
    double getPeerInfluencedPersonality() {
	double peerPers = _personality;
	if (0 < _friends.size()) {
	    double peerPersonalitySum = 0;
	    for (Agent ag : _friends) {
		if (ag instanceof CFAgent) {
		    CFAgent alter = (CFAgent) ag;
		    peerPersonalitySum += alter.getBasePersonality();
		}
	    }
	    double peerPersAvg = peerPersonalitySum / _friends.size();
	    peerPers = (1 - PEER_BIAS) * (_personality) + PEER_BIAS * peerPersAvg;
	}
	return peerPers;
    }

    /**
     * 
     */
    @Override
    public String toString() {
	return "CF_" + _id;
    }

    /**
     * 
     */
    @Override
    protected void afterStep(SocialSim state_) {
	_friends = state_.network.getJGraph().getNeighbors(this);
	updatePersonality();
    }

    /**
     * 
     */
    @Override
    protected void beforeStep(SocialSim state_) {
	reEvalFriends();
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

    /*
     * (non-Javadoc)
     * A* = b0 + b1*diff
     * A = 1 iff A* > epsilon ~N(0,1)
     * = 0 otherwise
     * 
     * @see sim.agents.Agent#makeFriend(sim.engine.SimState)
     */
    @Override
    protected boolean isNewFriend(Agent ag_) {
	boolean isNewFriend = false;
	if (ag_ instanceof CFAgent) {
	    CFAgent alter = (CFAgent) ag_;
	    double epsilonThresh = _rand.nextGaussian();
	    double pDiff = -Math.abs(getPeerInfluencedPersonality() - alter.getPeerInfluencedPersonality());
	    double aStar = B0_TIE_RETAINING_COEFF + B1_HOMOPHILY_COEFF * pDiff;
	    if (0 < Double.compare(aStar, epsilonThresh)) {
		isNewFriend = true;
	    }
	}
	return isNewFriend;
    }

    /**
     * 
     */
    private void reEvalFriends() {
	Collection<Agent> unfriends = new LinkedList<Agent>();
	for (Agent ag : _friends) {
	    if (ag instanceof CFAgent) {
		CFAgent alter = (CFAgent) ag;
		if (!isNewFriend(alter)) {
		    unfriends.add(alter);
		}
	    }
	}
	for (Agent ag : unfriends) {
	    unfriend(ag);
	}
    }

    /**
     * gives Y_t0 a shock u ~ N(0,5)
     */
    private void updatePersonality() {
	_personality = _personality + (_rand.nextGaussian()) * 5;

    }

}
