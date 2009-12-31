/**
 * 
 */
package sim.app.agents;

import java.util.LinkedList;
import java.util.List;

import sim.app.TrafficSim;
import sim.app.antsforage.AntsForage;
import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.app.utils.Orientation;
import sim.app.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 * @author biggie
 */
public class Car implements Steppable
{
    
    public Stoppable toDiePointer = null;
    
    private final double ACCELERATION = 5.0;
    private final double MAX_SPEED = 35.0;
    private final int SIZE = 2;
    
    private static int _carCount = 0;
    private static int _idToken = 0; // ID token always grows== ensure id
    // uniqueness.
    
    private double _currLocation; // How close to next intersection
    private double _currSpeed;
    private LinkedList<Street> _trayectory;
    private final String ID;
    private long lastTime = 0;
    
    /**
     * Class constructor.
     * 
     * @param trayectory_
     */
    public Car(List<Street> trayectory_)
    {
        
        ID = "Car_" + _idToken + "_" + _carCount;
        _carCount++;
        _idToken++;
        _trayectory = new LinkedList<Street>( trayectory_ );
        _currLocation = 0;
        _currSpeed = 0;
        // Add car to the streetQue
        currentStreet().carsOnStreet.add( this );
        System.out.println( "Created: " + this );
    };
    
    /**
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    public void step ( SimState state )
    {
        if ( !_trayectory.isEmpty() )
        {
            // does next xing have a tf?
            if ( currentXing( (CitySimState) state ).hasTrafficLight() )
            {
                if ( canMove( (CitySimState) state ) )
                {
                    move();
                } else
                {
                    // wait for it.
                    System.out.println( this + " stopped!" );
                    
                }
            } else
            {
                move();
            }
            
        } else
        {
            die( state );
        }
    }
    
    /**
     * Add this car to the current street it's cruising
     * 
     * @param car
     */
    private void addtoCurrStreet ()
    {
        
        currentStreet().carsOnStreet.add( this );
        
    }
    
    private void removeFromCurrStreet ()
    {
        
        _trayectory.getFirst().carsOnStreet.remove( this );
    }
    
    /**
     * Move the car. If at the end of the street, move to the next street in
     * trayectory. Car start at location 0
     */
    private void move ()
    {
        
        if ( atEndOfStreet() && !_trayectory.isEmpty() )
        {
            
            goToNextStreet();
        } else
        {
            _currLocation++;
        }
    }
    
    private void goToNextStreet ()
    {
        _currLocation = 0;
        removeFromCurrStreet();
        _trayectory.pop();
        if ( !_trayectory.isEmpty() )
        {
            addtoCurrStreet();
        }
    }
    
    /**
     * Calculate new Speed
     */
    private void accelerate ()
    {
        if ( _currSpeed < MAX_SPEED )
        {
            
        }
        
    }
    
    /**
     * Not implemented yet because too complicated. v2.0? TODO
     */
    @Deprecated
    private void breaking ()
    {
        
    }
    
    /**
     * Delete the car from schedule loop.
     * 
     * @param state
     */
    public void die ( final SimState state )
    {
        System.out.println( this + "Dying..." );
        _carCount--;
        
        if ( toDiePointer != null )
            toDiePointer.stop();
    }
    
    public static int getNumberOfCars ()
    {
        return _carCount;
    }
    
    /**
     * Returns true if car in front is not too close.
     */
    public boolean canMove ( CitySimState state_ )
    {
        boolean canMove = false;
        
        Orientation or = currentStreet().getOrientation();
        
        Car prevCar = getPrevCar();
        
        if ( !atEndOfStreet() )
        {
            // If this is not the first car.
            if ( null != prevCar )
            {
                // TODO replace this static value for how much the car would
                // move.
                // TODO if the car cant move, it stops i.e speed = 0
                if ( this._currLocation + 1 < prevCar._currLocation )
                {
                    canMove = true;
                }
                
            } else
            {
                canMove = true;
            }
        } else
        {
            canMove = TrafficLightState.RED != currentXing( state_ ).getTrafficLight().getState( or );
        }
        return canMove;
    }
    
    /**
     * Get the next car in line
     * 
     * @return null if this is the first car
     */
    private Car getPrevCar ()
    {
        Car nextCar = null;
        // Get next car
        try
        {
            
            int thisCarIdx = currentStreet().carsOnStreet.indexOf( this );
            nextCar = currentStreet().carsOnStreet.get( thisCarIdx - 1 );
        } catch ( IndexOutOfBoundsException e )
        {
            // ignore
        }
        return nextCar;
    }
    
    /**
     * Overrite toString to print car id: car_[number in simulation]_[number in
     * city < MAX_CAR ]
     */
    public String toString ()
    {
        return ID;
    }
    
    /**
     * @param state_
     * @return
     */
    private StreetXing currentXing ( CitySimState state_ )
    {
        return state_.getCity().getDest( currentStreet() );
    }
    
    private Street currentStreet ()
    {
        return _trayectory.getFirst();
    }
    
    private boolean atEndOfStreet ()
    {
        return _currLocation >= currentStreet().getLength();
    }
    
}
