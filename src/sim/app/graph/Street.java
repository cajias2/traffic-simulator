/**
 * 
 */
package sim.app.graph;

import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import sim.app.agents.Car;
import sim.app.utils.Orientation;

public class Street {
	private final String ID;
	private final double LENGTH;
	private final Orientation _orientation;
	private static int _streetCount = 0;
	
	public List<Car> carsOnStreet;
	

	/**
	 * Class constructor
	 * @author biggie
	 */
	public Street(Orientation orientation_, double lenth_) {
		ID = "Street_"+_streetCount;
		_streetCount++;
		
		LENGTH = lenth_;
		_orientation = orientation_;
		carsOnStreet = new LinkedList<Car>();
	}
	
	/**
	 * Getter method for edge distance
	 * @return
	 */
	public double getLength()
	{
		return LENGTH;
	}


	/**
     * Class toString 
     */
	public String toString() {
		return ID;
	}
	
	public Orientation getOrientation()
	{
	    return _orientation;
	}
	
}
