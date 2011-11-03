/**
 * 
 */
package sim.agents;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.utils.Orientation;
import sim.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 * @author biggie
 */
public class Car implements Steppable
{
    
    public Stoppable toDiePointer = null;
    
    private final double ACCELERATION = 6.25;
    private final double MAX_SPEED = 80.0;
    private final int SIZE = 2;
    
    private static int _carCount = 0;
    private static int _idToken = 0; // ID token always grows== ensure id uniqueness.
    private static Logger _log;
    
    private double _currLocation; // How close to next intersection
    private double _currSpeed;
    private int _time;
    private LinkedList<Street> _trayectory;
    private final String ID;
    /**
     * Class constructor.
     * 
     * @param trayectory_
     */
    public Car(List<Street> trayectory_, Logger log_)
    {
        
        ID = "Car_" + _idToken + "_" + _carCount;
        _carCount++;
        _idToken++;
        _trayectory = new LinkedList<Street>( trayectory_ );
        _currLocation = 0;
        _currSpeed = 0;
        _time = 0;
        _log = log_;
        // Add car to the streetQue
        currentStreet().carsOnStreet.add( this );
        _log.log( Level.INFO, "Created: " + this );
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
                    // Car stops. Reset time and speed. 
                    _time = 0;
                    _currSpeed = 0;
                    // wait for it.
                    _log.log(Level.INFO, this + " stopped!" );
                    
                }
            } else
            {
                move();
            }
            _time++;
            
        } else
        {
            die( state );
        }
    }
    
    /**
     * Delete the car from schedule loop.
     * 
     * @param state
     */
    public void die ( final SimState state )
    {
        _log.log( Level.INFO, this + "Dying..." );
        _carCount--;
        
        if ( toDiePointer != null )
            toDiePointer.stop();
    }

    /**
     * Returns number of cars currently in play
     * @return
     */
    public static int getNumberOfCars ()
    {
        return _carCount;
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
     * Move the car. If at the end of the street, move to the next street in
     * trayectory. Car start at location 0
     */
    protected void move ()
    {
        
        if ( atEndOfStreet() && !_trayectory.isEmpty() )
        {
            
            goToNextStreet();
        } else
        {
            _currLocation++;
        }
    }

    /**
     * Returns true if car in front is not too close.
     */
    protected boolean canMove ( CitySimState state_ )
    {
        boolean canMove = false;
        
        Orientation or = currentStreet().getOrientation();
        
        Car prevCar = getPrevCar();
        
        if ( !atEndOfStreet() )
        {
            // If this is not the first car.
            if ( null != prevCar )
            {
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
     * Add this car to the current street it's cruising
     * 
     * @param car
     */
    private void addtoCurrStreet ()
    {
        
        currentStreet().carsOnStreet.add( this );
        
    }
    /**
     * Wrapper method to remove this car from the street queue
     */
    private void removeFromCurrStreet ()
    {
        
        _trayectory.getFirst().carsOnStreet.remove( this );
    }
    
    /**
     * Move car from one street to the next
     */
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
     * TODO
     * Calculate new Speed
     */
    private void accelerate ()
    {
        if ( _currSpeed < currentStreet().getMaxSpeed() &&_currSpeed < MAX_SPEED )
        {
            _currSpeed = ACCELERATION*_time;
        }
        
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
     * @param state_
     * @return
     */
    private StreetXing currentXing ( CitySimState state_ )
    {
        return state_.getCity().getDest( currentStreet() );
    }
    /**
     * 
     * @return current street
     */
    private Street currentStreet ()
    {
        return _trayectory.getFirst();
    }
    
    /**
     * 
     * @return true if current location >= street lenght
     */
    private boolean atEndOfStreet ()
    {
        return _currLocation >= currentStreet().getLength();
    }
    
}
