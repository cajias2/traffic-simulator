package sim.app.geo;

/*
 * @author Raul Cajias
 */
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import sim.app.agents.display.lights.TrafficLightAgent;
import sim.app.utils.Orientation;
import sim.app.utils.TrafficLightState;


public class StreetXing 
{
    
    private final String ID;
    private static Logger _log;
    private TrafficLightAgent _trafficLight;
    private static int _xingCount = 0;
    private static double _startOdds = 0;
    private static double _endOdds = 0;
    private Point2D _location;
    private final List<Road> _roads;
    
    public Font nodeFont = new Font( "SansSerif", Font.PLAIN, 12 );

    /**
     * Constructor for class StreetXingNode
     * 
     * @author biggie
     * @param a_
     * @param b_
     */
    public StreetXing(Road a_, Road b_)
    {    	
    	_roads = new ArrayList<Road>();
    	_roads.add(a_); 
    	if(null != b_)
    	{
    		ID = a_.ID+"_X_"+b_.ID+"_"+_xingCount;
        	_roads.add(b_); 
        	_location = a_.findIntersection(b_);        	
    	}else{
    		ID = a_.ID+"_"+_xingCount;
    	}
    	_xingCount++;
    }
    /**
     * Constructor for road star/end xings
     * @param p_
     * @param road_
     */
    public StreetXing(Point2D p_, Road road_)
    {
    	this(road_, null);
    	_location = p_;
    	
    }
    /**
	 * True if xing has a traffic light. False otherwise.
	 * 
	 * @return boolean
	 */
	public boolean hasTrafficLight ()
	{
	    return _trafficLight != null;
	}

	public void setTrafficLight ( TrafficLightAgent tf_ )
	{
	    _trafficLight = tf_;
	    _trafficLight.setLocation(_location);
	}

	/**
	 * Returns traffic Light instance. Null if it's not set. Call {@code
	 * hasTrafficLight()} before to prevent {@code NullPointerException}s
	 * 
	 * @return trafficLight instance
	 */
	public TrafficLightAgent getTrafficLight ()
	{
	    return _trafficLight;
	}

	public String getId()
	{
	    return ID;
	}

	/**
     * @return the _startOdds
     */
    public double getStartOdds ()
    {
        return _startOdds;
    }
    /**
     * @return the _endOdds
     */
    public double getEndOdds ()
    {
        return _endOdds;
    }
    
    /**
     * @return the _location
     */
    public Point2D getLocation() {
	return _location;
    }

	/**
	 * @param startOdds_ the _startOdds to set
	 */
	public void setStartOdds ( double startOdds_ )
	{
	    StreetXing._startOdds = startOdds_;
	}

	/**
	 * @param endOdds_ the _endOdds to set
	 */
	public void setEndOdds ( double endOdds_ )
	{
	    StreetXing._endOdds = endOdds_;
	}

	/**
	 * @author biggie
	 */
	public String toString ()
	{
	    
	    StringBuffer echo = new StringBuffer(ID + "\n");
	    if ( hasTrafficLight() )
	    {
	        for ( Orientation or : Orientation.values() )
	        {
	            TrafficLightState state = _trafficLight.getState( or );
	            if ( state != null )
	            {
	                echo.append(or + ": " + state);
	            }
	            echo.append( "__" );           
	        }
	    }
	    return echo.toString();
	}
    
}
