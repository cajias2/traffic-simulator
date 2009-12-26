package sim.app.graph;

/*
 * @author Raul Cajias
 */
import java.awt.Font;
import java.util.Queue;

import sim.app.agents.TrafficLight;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

@SuppressWarnings ( "serial" )
public class StreetXing implements Steppable
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
    public StreetXing()
    {
        ID = "Xing_" + _xingCount;
        _xingCount++;
    }
    
    public StreetXing(TrafficLight tf_)
    {
        this();
        _trafficLight = tf_;
    }
    
    /**
     * @author biggie
     */
    public String toString ()
    {
        return ID;
    }
    
    public void step ( SimState state )
    {
        // TODO Auto-generated method stub
        
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
