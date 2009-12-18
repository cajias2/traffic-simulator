/**
 * 
 */
package sim.app.graph;

import edu.uci.ics.jung.graph.Graph;
import sim.engine.SimState;

/**
 * @author biggie
 * 
 */
@SuppressWarnings("serial")
public class CitySimState extends SimState {

	private Graph<StreetXing,Street> _city;

	public CitySimState(long seed) {
		super(seed);
	}

	public void setCity(Graph<StreetXing,Street> city_) {
		_city = city_;
	}

	public Graph<StreetXing,Street> getCity()
	{
		return _city;
	}
}
