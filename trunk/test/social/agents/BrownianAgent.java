/**
 * 
 */
package social.agents;

import static social.agents.utils.Movements.brownianMotion;
import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.util.Double2D;

/**
 * @author biggie
 *         MatLab Simulation
 *         <p>
 *         <code>
 *         randn('state',100) % set the state of randn
 *         T = 1; N = 500; dt = T/N;
 *         dW = zeros(1,N); % preallocate arrays ...
 *         W = zeros(1,N); % for efficiency
 *         dW(1) = sqrt(dt)*randn; % first approximation outside the loop ...
 *         W(1) = dW(1); % since W(0) = 0 is not allowed
 *         for j = 2:N
 *         dW(j) = sqrt(dt)*randn; % general increment
 *         W(j) = W(j-1) + dW(j);
 *         end
 *         plot([0:dt:T],[0,W],'r-') % plot W against t
 *         xlabel('t','FontSize',16)
 *         ylabel('W(t)','FontSize',16,'Rotation',0)
 * </code>
 */
public class BrownianAgent extends Agent {

    /**
     * 
     */
    private static final double FRIEND_P = 0.7;
    private static final double N = 2.5;
    private static final double T = 1.0;
    private static int COUNT = 0;
    private final static int DIM = 7;

    /**
     * 
     */
    private static final long serialVersionUID = 8377908360389402790L;

    /**
     * @param state_
     */
    public BrownianAgent(SimState state_) {
	super(state_);
	_actionDim = DIM;
    }

    @Override
    protected Double2D move(SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	Double2D currLoc = socSim.fieldEnvironment.getObjectLocation(this);
	return brownianMotion(T, N, currLoc, _rand);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#makeFriend(sim.engine.SimState)
     */
    @Override
    protected boolean makeFriend(Agent ag_) {
	return _rand.nextBoolean(FRIEND_P);
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
	    if (makeFriend(ag_)) {
		befriend(ag_);
	    }
	}
    }


}
