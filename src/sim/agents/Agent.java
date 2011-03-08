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
public abstract class Agent implements Steppable {

    /**
     * Update the state of this agent.
     * @param state_ TODO
     */
    public abstract void move(SimState state_);

    /**
     * @param state_
     * @return
     */
    public abstract boolean makeFriend(SimState state_);
}
