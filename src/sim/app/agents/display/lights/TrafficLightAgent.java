/**
 * 
 */
package sim.app.agents.display.lights;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.agents.data.TrafficLight;
import sim.app.agents.display.DisplayableAgent;
import sim.app.utils.Orientation;
import sim.app.utils.TrafficLightState;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * @author biggie
 */
public class TrafficLightAgent extends DisplayableAgent implements Steppable {

    private static final long serialVersionUID = 1829654668132095868L;
    private static Logger _log;
    private static int _lightCount = 0;
    private final int[] _durationArr;
    private int _timeLeft;
    private int _stateIdx;
    private final Map<Orientation, TrafficLight> _stateMap;
    private Point2D _location;

    private final int ID;

    /**
     * Constructor, Processing
     * 
     * @param duration_
     *            total time
     * @param split_
     *            number ranging from 0 - 1
     * @param log_
     */
    public TrafficLightAgent(PApplet applet, int duration_, double split_, Logger log_) {
	super.applet = applet;
	assert split_ <= 1 && split_ >= 0;

	_log = log_;
	ID = _lightCount;
	_lightCount++;
	_durationArr = new int[2];
	_durationArr[0] = (int) (duration_ * split_);
	_durationArr[1] = (int) (duration_*(1 - split_));

	// Initialize state map
	_stateMap = new HashMap<Orientation, TrafficLight>(2);
	_stateMap.put(Orientation.EAST_WEST, new TrafficLight(TrafficLightState.GREEN));
	_stateMap.put(Orientation.NORTH_SOUTH, new TrafficLight(TrafficLightState.RED));

	_stateIdx = 0;
	resetTimer(_stateIdx);

    }

    /**
     * Constructor, Mason
     * 
     * @param duration_
     *            total time
     * @param split_
     *            number ranging from 0 - 1
     * @param log_
     */
    public TrafficLightAgent(int duration_, double split_,
	    Logger log_)
    {
	this(null, duration_, split_, log_);
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
	    if (_stateIdx >= _durationArr.length )
		_stateIdx = 0;
	    resetTimer(_stateIdx);
	}

    }

    /**
     * Display a traffic light on the applet
     */
    @Override
    public void display() {
	if (null != _location) {
	    applet.smooth();
	    if (TrafficLightState.RED == _stateMap.get(Orientation.NORTH_SOUTH).getState()) {
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
    public TrafficLight getTf(Orientation or_) {
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
	for (Entry<Orientation, TrafficLight> entry : _stateMap.entrySet()) {
	    if (TrafficLightState.GREEN == entry.getValue().getState()) {
		entry.getValue().setState(TrafficLightState.RED);
	    } else {
		entry.getValue().setState(TrafficLightState.GREEN);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    public void step(SimState state_) {
	move(state_);

    }


}
