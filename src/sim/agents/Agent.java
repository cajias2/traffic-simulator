/**
 * 
 */
package sim.agents;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * @author biggie
 * 
 */
public abstract class Agent extends Entity implements Steppable {

    /**
     * Update the state of this agent.
     * @param state_ TODO
     */
    public abstract void move(SimState state_);
}
