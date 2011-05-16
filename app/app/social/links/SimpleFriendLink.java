/**
 * 
 */
package app.social.links;

import sim.graph.social.link.FriendLink;

/**
 * @author biggie
 *
 */
public class SimpleFriendLink extends FriendLink {

    public SimpleFriendLink(Double w_) {
	super(w_);
    }

    public SimpleFriendLink() {
	super(1.0);
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

    @Override
    public String toString()
    {
	return "";
    }

}
