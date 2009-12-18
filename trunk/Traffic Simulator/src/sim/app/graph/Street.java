/**
 * 
 */
package sim.app.graph;

public class Street {
	private final String _label;
	private final double _distance;

	/**
	 * 
	 * @author Raul Cajias StreetEdge
	 */
	public Street(String val_, double distance_) {
		_label = val_;
		_distance = distance_;
		
	}
	
	/**
	 * Getter method for edge distance
	 * @return
	 */
	public double getDistance()
	{
		return _distance;
	}


	/**
     * Getter method for 
     */
	public String toString() {
		return _label;
	}
}
