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
public class NetworkSimState extends SimState {

    private AgentNetwork _ntwrk;

    /**
     * 
     * @param seed
     */
    public NetworkSimState(long seed) {
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
