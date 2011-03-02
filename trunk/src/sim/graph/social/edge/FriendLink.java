/**
 * 
 */
package sim.graph.social.edge;

/**
 * @author biggie
 *
 */
public abstract class FriendLink {

    protected double _w;

    /**
     * @param w_
     *            weight
     */
    public FriendLink(Double w_) {
	_w = w_;
    }

    @Deprecated
    public abstract void reinforce();

    @Deprecated
    public abstract void diminish();
}
