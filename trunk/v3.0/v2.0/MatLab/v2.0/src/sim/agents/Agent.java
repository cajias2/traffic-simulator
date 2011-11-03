/**
 * 
 */
package sim.agents;

import sim.engine.SimState;

/**
 * @author biggie
 * 
 */
public abstract class Agent {

    /**
     * Update the state of this agent.
     * @param state_ TODO
     */
    public abstract void move(SimState state_);
}
