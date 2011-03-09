/**
 * 
 */
package sim.agents;


import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

/**
 * @author biggie
 * 
 */
public abstract class Agent implements Steppable {


    /**
     * Move to a new loc. Default is random.
     * 
     * @param state_
     *            TODO
     */
    protected abstract Double2D move(SimState state_);

    /**
     * @param state_
     * @return
     */
    protected abstract boolean makeFriend(Agent ag_, SimState state_);
}
