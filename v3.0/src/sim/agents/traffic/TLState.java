/**
 * 
 */
package sim.agents.traffic;

import sim.utils.TrafficLightState;

/**
 * @author biggie
 * 
 */
public class TLState {
    private TrafficLightState _state;

    /**
     * Class constructor
     * 
     * @param state_
     */
    public TLState(TrafficLightState state_) {
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
