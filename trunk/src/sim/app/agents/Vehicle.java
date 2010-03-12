/**
 * 
 */
package sim.app.agents;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.geo.Road;
import sim.app.geo.Street;
import sim.app.geo.StreetXing;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public abstract class Vehicle {
    PApplet parent;
    // ************************
    float x;
    float speed;
    float w;
    boolean mouse;
    // ********************

    private static int _vhclCount = 0;
    private static int _idToken = 0; // ID token always grows== ensure id
    // uniqueness.
    private static Logger _log;
    private static Graph<StreetXing, Road> _city;

    private Point2D _currLocation; // How close to next intersection
    private Distance _currSpeed;
    private double distCurrLine = 0;
    private long _time;
    private LinkedList<Road> _trayectory;
    private final String ID;
    private boolean _isAlive;

    /**
     * Given in K. Assumed to be K/s
     */
    public abstract Distance getMaxVelocity();

    /**
     * Given in K. Assumed to be K/s^2
     */
    public abstract Distance getAcceleration();

    /**
     * Given in m.
     */
    public abstract Distance getSize();

    /**
     * Class constructor.
     * 
     * @param trayectory_
     */
    public Vehicle(List<Road> trayectory_, Graph<StreetXing, Road> city_,
	    Logger log_, PApplet parent_) {
	// ***************
	parent = parent_;
	x = 0;
	speed = parent.random(1);
	w = parent.random(10, 30);
	mouse = false;
	// *************
	_isAlive = true;
	ID = "Car_" + _idToken + "_" + _vhclCount;
	_city = city_;
	_vhclCount++;
	_idToken++;
	_trayectory = new LinkedList<Road>(trayectory_);
	_currLocation = currentRoad().startLoc();
	_currSpeed = new Kilometers(0.0);
	_time = System.currentTimeMillis();
	_log = log_;
	// Add car to the streetQue
	currentRoad().getVehiclesOnRoad().add(this);
	// _log.log( Level.INFO, "Created: " + this );
    };

    /**
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    // public void step ( SimState state )
    // {
    //        
    // if ( !_trayectory.isEmpty() )
    // {
    // // does next xing have a tf?
    // if ( currentXing( (CitySimState) state ).hasTrafficLight() )
    // {
    // if ( canMove( (CitySimState) state ) )
    // {
    // move();
    // } else
    // {
    // // Car stops. Reset time and speed.
    // _time = 0;
    // _currSpeed = 0;
    // // wait for it.
    // _log.log(Level.INFO, this + " stopped!" );
    //                    
    // }
    // } else
    // {
    // move();
    // }
    // _time++;
    //            
    // } else
    // {
    // die( state );
    // }
    // }

    /**
     * Add this car to the current street it's cruising
     * 
     * @param car
     */
    private void addtoCurrStreet() {

	currentRoad().getVehiclesOnRoad().add(this);

    }

    /**
     * Wrapper method to remove this car from the street queue
     */
    private void removeFromCurrStreet() {

	_trayectory.getFirst().getVehiclesOnRoad().remove(this);
    }

    /**
     * Move car from one street to the next
     */
    private void goToNextStreet() {
	removeFromCurrStreet();
	_trayectory.pop();
	if (!_trayectory.isEmpty()) {
	    addtoCurrStreet();
	    _currLocation = currentRoad().startLoc();
	}
    }

    /**
     * TODO Calculate new Speed
     */
    private void accelerate() {
	if (_currSpeed.toMeters() < currentRoad().getMaxVelocity().toMeters()
		&& _currSpeed.toMeters() < getMaxVelocity().toMeters()) {
	    _currSpeed.setVal(getAcceleration().getVal() * _time);
	}

    }

    /**
     * Delete the car from schedule loop.
     * 
     * @param state
     */
    // public void die ( final SimState state )
    // {
    // _log.log( Level.INFO, this + "Dying..." );
    // _carCount--;
    //        
    // if ( toDiePointer != null )
    // toDiePointer.stop();
    // }

    /**
     * Returns number of cars currently in play
     * 
     * @return
     */
    public static int getNumberOfCars() {
	return _vhclCount;
    }

    /**
     * Returns true if car in front is not too close.
     */
    protected boolean canMove() {
	boolean canMove = false;

	// Orientation or = currentStreet().getOrientation();

	Vehicle prevCar = getPrevCar();

	if (!atEndOfRoad()) {
	    // If this is not the first car.
	    // if (null != prevCar) {
	    // if (this._currLocation < prevCar._currLocation) {
	    // canMove = true;
	    // }
	    //
	    // } else {
	    // TODO true for now.. must look at later. When must a car stop?
	    canMove = true;
	    // }
	} else {
	    // canMove = TrafficLightState.RED != currentXing( state_
	    // ).getTrafficLight().getState( or );
	    // TODO bug here.. rethink this logic!
	    canMove = true;
	}
	return canMove;
    }

    /**
     * Get the next car in line
     * 
     * @return null if this is the first car
     */
    private Vehicle getPrevCar() {
	Vehicle nextCar = null;
	// Get next car
	try {

	    int thisCarIdx = currentRoad().getVehiclesOnRoad().indexOf(this);
	    nextCar = currentRoad().getVehiclesOnRoad().get(thisCarIdx - 1);
	} catch (IndexOutOfBoundsException e) {
	    // ignore
	}
	return nextCar;
    }

    /**
     * Overrite toString to print car id: car_[number in simulation]_[number in
     * city < MAX_CAR ]
     */
    public String toString() {
	return ID;
    }

    /**
     * @param state_
     * @return
     */
    private StreetXing currentXing() {
	return _city.getDest((Street) currentRoad());
    }

    /**
     * 
     * @return current street
     */
    private Road currentRoad() {
	return _trayectory.getFirst();
    }

    /**
     * 
     * @return true if X or Y are bigger that X,Y of end of road
     */
    private boolean atEndOfRoad() {
	// TODO either x or y? x and y?
    Point2D dest = _city.getDest(currentRoad()).getLocation();
	return (_currLocation.distance(dest) <= 1);

    }

    /**
	 * 
	 */
    public void display() {
	parent.stroke(255);
	parent.strokeWeight(3);
	parent.point((float) _currLocation.getX(), 
		(float) _currLocation.getY());
    }

    public void move() {

	if (!_trayectory.isEmpty()) {
	    // does next xing have a tf?
	    if (currentXing().hasTrafficLight()) {
		if (canMove()) {
		    moveVehicle();
		} else {
		    // Car stops. Reset time and speed.
		    // TODO bug!!!!!v
		    _time = System.currentTimeMillis();
		    _currSpeed.setVal(0.0);
		    // wait for it.
		    _log.log(Level.INFO, this + " stopped!");
		}
	    } else {
		moveVehicle();
	    }
	    _time++;

	} else {
	    die();
	}
    }

    private void moveVehicle() {
	// Move Car logic
	if (atEndOfRoad() && !_trayectory.isEmpty()) {

	    goToNextStreet();
	} else {
	    _currLocation = currentRoad()
		    .getNewLocation(_currLocation, 1);
//	    x += speed;
	}
    }

    private void die() {
	_isAlive = false;
    }

    public boolean isAlive() {
	return _isAlive;
    }
}
