/**
 * 
 */
package sim.agents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.utils.Orientation;
import sim.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * @author biggie
 */
public class TrafficLight implements Steppable
{
    
    private static final long serialVersionUID = 1829654668132095868L;
    private static Logger _log;
    private static int _lightCount = 0;
    private Map<Orientation, Map<Attr, Object>> _stateMap;
    
    
    private final String ID;
    
    public static enum Attr
    {
        DURATION, TIME_LEFT, STATE
    };
    
    public TrafficLight(Logger log_)
    {
        _log = log_;
        ID = "Light_" + _lightCount;
        _lightCount++;
        
        // Initialize state map
        _stateMap = new HashMap<Orientation, Map<Attr, Object>>( 2 );
        _stateMap.put( Orientation.EAST_WEST, new HashMap<Attr, Object>() );
        _stateMap.put( Orientation.NORTH_SOUTH, new HashMap<Attr, Object>() );
        
        // Initialize state map internals
        for ( Entry<Orientation, Map<Attr, Object>> entry : _stateMap.entrySet() )
        {
            entry.getValue().put( Attr.DURATION, 10 );
            entry.getValue().put( Attr.TIME_LEFT, 10 );
        }
        // Set state list. One should be green, all others red.
        LinkedList<TrafficLightState> nsStates = new LinkedList<TrafficLightState>( Arrays.asList(
                TrafficLightState.GREEN, TrafficLightState.RED ) );
        _stateMap.get( Orientation.NORTH_SOUTH ).put( Attr.STATE, nsStates );
        LinkedList<TrafficLightState> ewStates = new LinkedList<TrafficLightState>( Arrays.asList(
                TrafficLightState.RED, TrafficLightState.GREEN ) );
        _stateMap.get( Orientation.EAST_WEST ).put( Attr.STATE, ewStates );
        
    }
    
    /**
     * Something that can be stepped
     */
    public void step ( SimState state )
    {
        // Reduce time left at each stage
        for ( Entry<Orientation, Map<Attr, Object>> entry : _stateMap.entrySet() )
        {
            int timeLeft = (Integer) entry.getValue().get( Attr.TIME_LEFT ) - 1;
            entry.getValue().put( Attr.TIME_LEFT, timeLeft );
            // If time ran out.. reset time and update state
            if ( timeLeft < 0 )
            {
                _log.log( Level.INFO, this + " Was: " + getState( entry.getKey() ) );
                upateState( entry.getKey() );
                _log.log( Level.INFO, " Now:" + getState( entry.getKey() ) );
                
            }
        }
        
    }
    
    /**
     * Returns the current state of the traffic light
     * 
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public TrafficLightState getState ( Orientation or_ )
    {
        return ( (LinkedList<TrafficLightState>) _stateMap.get( or_ ).get( Attr.STATE ) ).getFirst();
    }
    
    /**
     * Gets the first element of the state list and puts it at the end of the
     * list Also resets the time left time, by copying over the duration value.
     * 
     * @param or_
     */
    @SuppressWarnings ( "unchecked" )
    private void upateState ( Orientation or_ )
    {
        // Cicle through the state list
        TrafficLightState state = (TrafficLightState) ( (LinkedList<TrafficLightState>) _stateMap.get( or_ ).get(
                Attr.STATE ) ).poll();
        ( (LinkedList<TrafficLightState>) _stateMap.get( or_ ).get( Attr.STATE ) ).add( state );
        
        // Copy duration over
        int duration = (Integer) _stateMap.get( or_ ).get( Attr.DURATION );
        _stateMap.get( or_ ).put( Attr.TIME_LEFT, duration );
    }
    
    public String toString ()
    {
        return ID;
    }
}
