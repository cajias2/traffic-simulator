/**
 * 
 */
package sim.app;

import sim.engine.SimState;
import sim.mason.AgentNetwork;

/**
 * @author biggie
 * 
 */
@SuppressWarnings("serial")
public class SocialSimState extends SimState {

    private AgentNetwork _ntwrk;

    /**
     * 
     * @param seed
     */
    public SocialSimState(long seed) {
	super(seed);
    }

    /**
     * 
     * @param ntwrk_
     */
    public void setNetwork(AgentNetwork ntwrk_) {
	_ntwrk = ntwrk_;
    }

    /**
     * 
     * @return
     */
    public AgentNetwork getNwrk() {
	return _ntwrk;
    }
}
