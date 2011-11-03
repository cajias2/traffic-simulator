/**
 * 
 */
package sim.agents;

import processing.core.PApplet;
import sim.engine.SimState;

/**
 * @author biggie
 * 
 */
public abstract class DisplayableAgent extends Agent {

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public DisplayableAgent(SimState state_) {
	super(state_);
	// TODO Auto-generated constructor stub
    }

    protected PApplet applet;

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#display()
     */
    abstract public void display();
}
