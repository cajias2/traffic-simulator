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
    private final int _steps = 0;
    private TrafficLight _trafficLight;
    private static int _xingCount = 0;
    
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
    
}
