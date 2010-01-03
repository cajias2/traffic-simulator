package sim.app.graph;

/*
 * @author Raul Cajias
 */
import java.awt.Font;
import java.util.Queue;

import sim.app.agents.TrafficLight;
import sim.app.utils.Orientation;
import sim.app.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

@SuppressWarnings ( "serial" )
public class StreetXing 
{
    
    private final String ID;
    private TrafficLight _trafficLight;
    private static int _xingCount = 0;
    private static double _startOdds = 0;
    private static double _endOdds = 0;
    
    public Font nodeFont = new Font( "SansSerif", Font.PLAIN, 12 );
    
    /**
     * Constructor for class StreetXingNode
     * 
     * @author biggie
     */
    @Deprecated
    public StreetXing()
    {
        ID = "Xing_" + _xingCount;
        _xingCount++;
    }
    @Deprecated
    public StreetXing(TrafficLight tf_)
    {
        this();
        _trafficLight = tf_;
    }
    
    /**
     * Class Constructor
     * @param id_
     */
    public StreetXing( String id_)
    {
        ID = id_;
        _xingCount++;        
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

    public void setTrafficLight ( TrafficLight tf_ )
    {
        _trafficLight = tf_;
    }
    
    /**
     * Returns traffic Light instance. Null if it's not set. Call {@code
     * hasTrafficLight()} before to prevent {@code NullPointerException}s
     * 
     * @return trafficLight instance
     */
    public TrafficLight getTrafficLight ()
    {
        return _trafficLight;
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
    /**
     * @param startOdds_ the _startOdds to set
     */
    public void setStartOdds ( double startOdds_ )
    {
        StreetXing._startOdds = startOdds_;
    }
    /**
     * @return the _startOdds
     */
    public double getStartOdds ()
    {
        return _startOdds;
    }
    /**
     * @param endOdds_ the _endOdds to set
     */
    public void setEndOdds ( double endOdds_ )
    {
        StreetXing._endOdds = endOdds_;
    }
    /**
     * @return the _endOdds
     */
    public double getEndOdds ()
    {
        return _endOdds;
    }
    
    public String getId()
    {
        return ID;
    }
    
}
