/**
 * 
 */
package sim.app;

import sim.engine.SimState;
import sim.graph.traffic.Road;
import sim.graph.traffic.StreetXing;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 * 
 */
@SuppressWarnings("serial")
public class NetworkSimState extends SimState {

    private Graph<StreetXing, Road> _ntwrk;

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
    public void setNetwork(Graph<StreetXing, Road> ntwrk_) {
	_ntwrk = ntwrk_;
    }

    /**
     * 
     * @return
     */
    public Graph<StreetXing, Road> getNwrk() {
	return _ntwrk;
    }
}
