/**
 * 
 */
package sim.agents.traffic;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import sim.agents.DisplayableAgent;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.traffic.Road;
import sim.utils.Orientation;
import sim.utils.TrafficLightState;

/**
 * @author biggie
 */
public class TLAgent extends DisplayableAgent implements Steppable {

    private static final long serialVersionUID = 1829654668132095868L;
    private static Logger _log;
    private static int _lightCount = 0;
    private final int[] _durationArr;
    private int _split;
    private final List<Road> _roadList = new LinkedList<Road>();
    private int _timeLeft;
    private int _stateIdx;
    private final Map<Orientation, TLState> _stateMap;
    private Point2D _location;

    private final int ID;
    private final int _duration;

    /**
     * Constructor, Mason
     * 
     * @param duration_
     *            total time
     * @param split_
     *            number ranging from 0 - 1
     * @param log_
     */
    public TLAgent(int duration_, double split_,
	    Logger log_)
    {
        assert split_ <= 1 && split_ >= 0;
        _split = (int) (100.0 * split_);
        _duration = duration_;
        _log = log_;
        ID = _lightCount;
        _lightCount++;
        _durationArr = new int[2];
        resetDurationArr();
    
        // Initialize state map
        _stateMap = new HashMap<Orientation, TLState>(2);
        _stateMap.put(Orientation.EW, new TLState(TrafficLightState.GREEN));
        _stateMap.put(Orientation.NS, new TLState(TrafficLightState.RED));
    
        _stateIdx = 0;
        resetTimer(_stateIdx);
    
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    public void step(SimState state_) {
        move(state_);
    }

    /**
     * Count down on the time this light remains on the current state. If the
     * time runs out, the state changes and the timer is resetted
     */
    @Override
    public void move(SimState state_)
    {
        // Reduce time left at each stage
        _timeLeft--;
        if (0 > _timeLeft) {
            updateLights();
            _stateIdx++;
            if (_stateIdx >= _durationArr.length)
        	_stateIdx = 0;
            resetTimer(_stateIdx);
        }
    
    }

    public void setSplit(int newSplit_) {
        if (100 >= newSplit_ && 0 <= newSplit_)
            _split = newSplit_;
        resetDurationArr();
    }

    /**
     * Display a traffic light on the applet
     */
    @Override
    public void display() {
	if (null != _location) {
	    applet.smooth();
	    if (TrafficLightState.RED == _stateMap.get(Orientation.NS).getState()) {
		applet.fill(255, 0, 0);
	    } else {
		applet.fill(0, 255, 0);
	    }
	    applet.strokeWeight(0);
	    applet.ellipse(((Double) _location.getX()).floatValue(), ((Double) _location.getY()).floatValue(), 20, 20);
	}

    }

    /**
     * Returns the current state of the traffic light
     * 
     * @return
     */
    public TrafficLightState getState(Orientation or_) {
	return _stateMap.get(or_).getState();
    }

    /**
     * Returns the the traffic light object
     * 
     * @return
     */
    public TLState getTf(Orientation or_) {
	return _stateMap.get(or_);
    }

    /**
     * 
     */
    @Override
    public String toString() {
	return "light_" + ID;
    }

    public void setLocation(Point2D _location) {
	this._location = _location;
    }

    /**
     * @param roadList_
     *            the roadList to set
     */
    public void addRoadList(Road road_) {
	_roadList.add(road_);
    }

    /**
     * @param roadList_
     *            the roadList to set
     */
    public List<Road> getRoads() {
	return _roadList;
    }
    public Point2D getLocation() {
	return _location;
    }

    /**
     * Reset the traffic light timer
     */
    private void resetTimer(int state_) {
	_timeLeft = _durationArr[state_];
    }

    /**
     * Gets the first element of the state list and puts it at the end of the
     * list Also resets the time left time, by copying over the duration value.
     */
    private void updateLights() {
	// Cicle through the state list
	for (Entry<Orientation, TLState> entry : _stateMap.entrySet()) {
	    if (TrafficLightState.GREEN == entry.getValue().getState()) {
		entry.getValue().setState(TrafficLightState.RED);
	    } else {
		entry.getValue().setState(TrafficLightState.GREEN);
	    }
	}
    }

    /**
     * @author biggie
     * @name   resetDurationArr
     * Purpose TODO
     * 
     * @param 
     * @return void
     */
    private void resetDurationArr() {
        _durationArr[0] = (int) (_duration * _split / 100.0);
        _durationArr[1] = (int) (_duration * (1 - _split / 100.0));
    }

}
