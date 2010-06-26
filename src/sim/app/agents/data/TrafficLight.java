/**
 * 
 */
package sim.app.agents.display.lights;

import sim.app.utils.TrafficLightState;

/**
 * @author biggie
 * 
 */
public class TrafficLight {
    private TrafficLightState _state;

    /**
     * Class constructor
     * 
     * @param state_
     */
    public TrafficLight(TrafficLightState state_) {
	_state = state_;
    }

    /**
     * Get the state of this light
     * 
     * @return
     */
    public TrafficLightState getState() {
	return _state;
    }

    /**
     * Set a new state
     * 
     * @param state_
     */
    public void setState(TrafficLightState state_) {
	_state = state_;
    }

}
