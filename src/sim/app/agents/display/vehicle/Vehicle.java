/**
 * 
 */
package sim.app.agents.display.vehicle;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.agents.display.DisplayableAgent;
import sim.app.road.Road;
import sim.app.road.StreetXing;
import sim.app.road.distance.Distance;
import sim.app.road.distance.Kilometers;
import sim.app.road.distance.Meters;
import sim.app.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public abstract class Vehicle extends DisplayableAgent implements Steppable
{

    public Stoppable		       toDiePointer  = null;
    private static int                     _vhclCount    = 0;
    private static int _idToken = 0; // always grows to ensure id uniqueness.
    private static Logger                  _log;
    private static Graph<StreetXing, Road> _city;

    private final String                   ID;
    private Point2D _currLocation; // How close to next intersection
    private Line2D                         _currRoadLine;
    private final Distance                 _currSpeed;
    private long                           _time;
    private final LinkedList<Road>         _trayectoryList;
    private boolean                        _isAlive;
    private int                            _roadLineIdx  = 0;
    private int                            _subRoadIdx   = 0;

    // ANTONIO
    private String                         _trayectoryID = "";
    private final long _initialTime;
    private long _travelTime = 0;
    private long _masonSteps = 0;

    /**
     * Class constructor.
     * 
     * @param trayectory_
     */
    public Vehicle(List<Road> trayectory_, Graph<StreetXing, Road> city_, Logger log_, PApplet parent_)
    {
        super.applet = parent_;
        _isAlive = true;
        ID = "Car_" + _idToken + "_" + _vhclCount;
        _city = city_;
        _vhclCount++;
        _idToken++;
        _trayectoryList = new LinkedList<Road>(trayectory_);
        updateRoad();
        _currSpeed = new Kilometers(0.0);
        _time = System.currentTimeMillis();
        _log = log_;
        // Add car to the streetQue
        currentRoad().getVehiclesOnRoad().add(this);
        _log.log(Level.INFO, "Created: " + this);
    
        // ANTONIO
        _initialTime = System.currentTimeMillis();
        // for(Road r : trayectory_){
        // _trayectoryID += r.ID + "_";
        // }
    }

    /**
     * Class constructor.
     * 
     * @param trayectory_
     */
    public Vehicle(List<Road> trayectory_, Graph<StreetXing, Road> city_, Logger log_) {
        this(trayectory_, city_, log_, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    public void step(SimState state_) {
        if(_masonSteps == 0)
        {
            _masonSteps = state_.schedule.getSteps();
        }
        move(state_);
    }

    /**
     * Updates the location variables of this vehicle. Gets called once per
     * frame.
     * 
     * @throws Exception
     */
    @Override
    public void move(SimState state_)
    {
    
        if (!_trayectoryList.isEmpty())
        {
            // does next xing have a tf?
            if (canMove())
            {
        	moveVehicle();
            } else
            {
        	// Car stops. Reset time and speed.
        	_time = System.currentTimeMillis();
        	_currSpeed.setVal(0.0);
            }
            _time++;
        } else
        {
            die(state_);
        }
    }

    /**
     * Displays the vehicle according to its location variables. Gets called
     * once per frame
     */
    @Override
    public void display()
    {
        super.applet.stroke(0, 0, 255);
        super.applet.strokeWeight(3);
        super.applet.point((float) _currLocation.getX(), (float) _currLocation.getY());
    }

    /**
     * Delete the car from schedule loop.
     * 
     * @param state
     */
    public void die(final SimState state) {
        _log.log(Level.INFO, this + "Dying...");
        _masonSteps = state.schedule.getSteps() - _masonSteps;
        System.out.println(this + "finished after " + _masonSteps + "steps");
        die();
        _vhclCount--;
        if (toDiePointer != null)
            toDiePointer.stop();
    }

    public long getMasonSteps()
    {
	return _masonSteps;
    }

    /**
     * Given in K. Assumed to be K/s
     */
    public abstract Distance getMaxVelocity();

    /**
     * Given in K. Assumed to be K/s^2
     */
    public abstract Distance getAcceleration();

    public int getRoadLineIdx()
    {
	return _roadLineIdx;
    }

    /**
     * Given in m.
     */
    public abstract Distance getSize();

    public Point2D getLocation()
    {
	return _currLocation;
    }

    public Line2D getRoadLine()
    {
	return _currRoadLine;
    }

    public String getTrayectoryID()
    {
        return _trayectoryID;
    }

    public long getTravelDuration()
    {
        return _travelTime;
    }

    /**
     * 
     * @author biggie
     */
    public boolean isAlive()
    {
	return _isAlive;
    }

    /**
     * Returns number of cars currently in play
     * 
     * @return
     */
    public static int getTotVhlCount()
    {
	return _vhclCount;
    }

    /**
     * Overrite toString to print car id: car_[number in simulation]_[number in
     * city < MAX_CAR ]
     */
    @Override
    public String toString()
    {
	return ID;
    }


    /**
     * Returns true if car in front is not too close.
     */
    protected boolean canMove()
    {
	boolean canMove = false;
	boolean isSaturated = false;

	if (!atLastSeg())
	{

	    int nextSegIdx = currentRoad().getSegIdx(_currRoadLine, _currLocation) + 1;
	    if (nextSegIdx < currentRoad().getSegmentList().get(_roadLineIdx).size())
	    {
		Line2D nextSeg = currentRoad().getSegmentList().get(_roadLineIdx).get(nextSegIdx);

		isSaturated = currentRoad().isSegSaturated(nextSeg);
	    } else if ((_roadLineIdx + 1) < currentRoad().getSegmentList().size())
	    {
		Line2D nextSeg = currentRoad().getSegmentList().get(_roadLineIdx + 1).get(0);
		isSaturated = currentRoad().isSegSaturated(nextSeg);
	    }

	    canMove = !isSaturated;
	} else
	{
	    if (_trayectoryList.size() > 1)
	    {
		Line2D nextSeg = _trayectoryList.get(1).getSegmentList().get(0).get(0);
		isSaturated = _trayectoryList.get(1).isSegSaturated(nextSeg);
	    }
	    if (currentRoad().getTf() != null)// hasTrafficLight())
	    {
		Point2D tfLoc = currentXing().getLocation();
		Meters distance = new Meters(getLocation().distance(tfLoc));
		if (distance.toMeters() <= Road.LAYER_SEG.toMeters())
		{
		    TrafficLightState tfState = currentRoad().getTf();
		    canMove = TrafficLightState.RED != tfState && !isSaturated;
		}

	    } else
	    {
		canMove = true;
	    }
	}
	return canMove;
    }

    /**
     * Resets locations variables when a new road is reached.
     */
    private void updateRoad()
    {
	_currLocation = currentRoad().startLoc();
	updateRouteHistory();
	_roadLineIdx = 0;
	_currRoadLine = currentRoad().getLineList().get(_roadLineIdx);
    }

    // ANTONIO
    private void updateRouteHistory()
    {
	String roadID = currentRoad().ID;
	if (!_trayectoryID.endsWith(roadID))
	{
	    _trayectoryID += "_" + roadID;
	}
    }

    /**
     * Add this car to the current street it's cruising
     * 
     * @param car
     */
    private void addtoCurrRoad()
    {

	currentRoad().getVehiclesOnRoad().add(this);
	updateRoad();
    }

    /**
     * Wrapper method to remove this car from the street queue
     */
    private void removeFromCurrRoad()
    {
	currentRoad().removeVFromRoad(this);
	// _trayectory.getFirst().getVehiclesOnRoad().remove(this);
    }

    /**
     * Move car from one street to the next
     */
    private void goToNextRoad()
    {
	removeFromCurrRoad();
	_trayectoryList.removeFirst();
	if (!_trayectoryList.isEmpty())
	{
	    addtoCurrRoad();
	}
    }

    /**
     * TODO Calculate new Speed
     */
    /*
     * private void accelerate() { if (_currSpeed.toMeters() <
     * currentRoad().getMaxVelocity().toMeters() && _currSpeed.toMeters() <
     * getMaxVelocity().toMeters()) {
     * _currSpeed.setVal(getAcceleration().getVal() * _time); }
     * 
     * }
     */


    /**
     * @param state_
     * @return
     */
    private StreetXing currentXing()
    {
	return _city.getDest(currentRoad());
    }

    /**
     * 
     * @return current street
     */
    private Road currentRoad()
    {
	return _trayectoryList.getFirst();
    }

    /**
     * 
     * @return true if closer to the destination than the Distance threshold
     */
    private boolean atEndOfRoad()
    {
	Point2D dest = _city.getDest(currentRoad()).getLocation();
	return (_currLocation.distance(dest) <= Road.DISTANCE_THRESHOLD);
    }

    /**
     * 
     * @return true if closer to the destination than the Distance threshold
     */
    private boolean atLastSeg()
    {
	Point2D dest = _city.getDest(currentRoad()).getLocation();

	return (_currLocation.distance(dest) <= Road.LAYER_SEG.toMeters());
    }

    /**
     * @throws Exception
     * 
     */
    private void moveVehicle()
    {
	// if (!atEndOfRoad()) {
	Point2D newLoc = currentRoad().getNewLocation(_currLocation, 1);
	if (_currLocation != newLoc)
	{
	    _currLocation = newLoc;
	    updateRouteHistory();
	    if (currentRoad().getLineList().size() > _roadLineIdx + 1)
	    {
		Line2D nextLine = currentRoad().getLineList().get(_roadLineIdx + 1);
		int compA = Double.compare(_currRoadLine.ptLineDist(_currLocation), Road.DISTANCE_THRESHOLD);
		int compB = Double.compare(nextLine.ptLineDist(_currLocation), Road.DISTANCE_THRESHOLD);
		if (compA >= compB)
		{
		    updateRoadSeg();
		}
	    }// else if(currentRoad().getSubRoadList().size() > _subRoadIdx)
	     // {
	     // updateSubRoad();
	     // }
	    currentRoad().updateVehicle(this);

	} else if (currentRoad().getLineList().size() > _subRoadIdx)
	{
	    goToNextRoad();
	}
	// } else {
	// goToNextRoad();
	// }
    }

    private void updateSubRoad()
    {
	_subRoadIdx++;
	_roadLineIdx = 0;
	_currRoadLine = currentRoad().getSubRoad(_subRoadIdx).getLine(0);
    }

    /**
	 * 
	 */
    private void updateRoadSeg()
    {
	_roadLineIdx++;
	_currRoadLine = currentRoad().getLine(_roadLineIdx);
    }

    private void die()
    {
	// currentRoad().removeVFromRoad(this);
	_isAlive = false;
	long currentTime = System.currentTimeMillis();
	_travelTime = currentTime - _initialTime;
    }

}
