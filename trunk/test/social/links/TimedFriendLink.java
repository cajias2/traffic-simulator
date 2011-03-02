/**
 * 
 */
package social.links;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.social.edge.FriendLink;

/**
 * @author biggie
 *
 */
public class TimedFriendLink extends FriendLink implements Steppable {

    private int _duration;

    public TimedFriendLink(Double w_) {
	super(w_);
	_duration = 1;
    }

    /* (non-Javadoc)
     * @see sim.graph.social.FriendLink#reinforce()
     */
    @Override
    public void reinforce() {
	// TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see sim.graph.social.FriendLink#diminish()
     */
    @Override
    public void diminish() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    public void step(SimState state_) {
	_duration++;

    }

}
