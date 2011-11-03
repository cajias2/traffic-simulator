/**
 * 
 */
package sim.app.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import sim.agents.Car;
import sim.utils.Orientation;

public class Street {
	private final String ID;
	private final double LENGTH;
	private final Orientation _orientation;
	private static int _streetCount = 0;
	private double _maxSpeed;
	
	private static Logger _logger;
	
	public List<Car> carsOnStreet;
	

	/**
	 * Class constructor
	 * @author biggie
	 */
	@Deprecated
	public Street(Orientation orientation_, double lenth_, Logger log_) {
		ID = "Street_"+_streetCount;
		_streetCount++;
		
		LENGTH = lenth_;
		_orientation = orientation_;
		carsOnStreet = new LinkedList<Car>();
		_logger = log_;
	}
	/**
	 * Class Constructor
	 */
    public Street(Orientation orientation_, double lenth_, double maxSpeed_, Logger log_) 
	{
	    this(orientation_, lenth_, log_);
	    _maxSpeed = maxSpeed_;
	    
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
		return _orientation + "__"+ carsOnStreet.size();
	}
	
	public Orientation getOrientation()
	{
	    return _orientation;
	}
	
	public double getMaxSpeed()
	{
	    return _maxSpeed;
	}
	
}
