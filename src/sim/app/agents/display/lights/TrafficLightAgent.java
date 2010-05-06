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

import sim.app.agents.Agent;
import sim.app.agents.display.DisplayableAgent;
import sim.app.processing.Displayable;
import sim.app.utils.Orientation;
import sim.app.utils.TrafficLightState;

/**
 * @author biggie
 */
public class TrafficLightAgent extends DisplayableAgent {

	private static final long serialVersionUID = 1829654668132095868L;
	private static Logger _log;
	private static int _lightCount = 0;
	private int _duration;
	private int _timeLeft;
	private Map<Orientation, TrafficLight> _stateMap;
	private Point2D _location;

	private final String ID;

	/**
	 * 
	 * @param log_
	 */
	public TrafficLightAgent(PApplet applet, Logger log_) {
		super.applet = applet;
		_log = log_;
		ID = "Light_" + _lightCount;
		_lightCount++;
		_duration = 10;

		// Initialize state map
		_stateMap = new HashMap<Orientation, TrafficLight>(2);
		_stateMap.put(Orientation.EAST_WEST, new TrafficLight(
				TrafficLightState.GREEN));
		_stateMap.put(Orientation.NORTH_SOUTH, new TrafficLight(
				TrafficLightState.RED));

		this.resetTimer();

	}

	/**
	 * Count down on the time this light remains on the current state. If the
	 * time runs out, the state changes and the timer is resetted
	 */
	public void move() {
		// Reduce time left at each stage
		_timeLeft--;
		if (0 > _timeLeft) {
			this.updateLights();
			this.resetTimer();
		}

	}

	/**
	 * Display a traffic light on the applet
	 */
	@Override
	public void display() {
		if (null != _location) {
			applet.smooth();
			if(TrafficLightState.RED == _stateMap.get(Orientation.NORTH_SOUTH).getState())
			{
				applet.fill(255, 0, 0);
			}else
			{
				applet.fill(0, 255, 0);				
			}
			applet.ellipse(((Double) _location.getX()).floatValue(),
					((Double) _location.getY()).floatValue(), 4, 4);
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
     * 
     */
	public String toString() {
		return ID;
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
	private void resetTimer() {
		_timeLeft = _duration;
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
}
