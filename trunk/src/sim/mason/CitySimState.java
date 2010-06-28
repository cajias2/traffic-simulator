/**
 * 
 */
package sim.mason;

import sim.app.road.Road;
import sim.app.road.StreetXing;
import sim.engine.SimState;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 * 
 */
@SuppressWarnings("serial")
public class CitySimState extends SimState {

    private Graph<StreetXing, Road> _city;

    /**
     * 
     * @param seed
     */
    public CitySimState(long seed) {
	super(seed);
    }

    /**
     * 
     * @param city_
     */
    public void setCity(Graph<StreetXing, Road> city_) {
	_city = city_;
    }

    /**
     * 
     * @return
     */
    public Graph<StreetXing, Road> getCity() {
	return _city;
    }
}
