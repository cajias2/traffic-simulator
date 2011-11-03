/**
 * 
 */
package sim.agents.display;

import processing.core.PApplet;
import sim.agents.Agent;

/**
 * @author biggie
 * 
 */
public abstract class DisplayableAgent extends Agent {

    protected PApplet applet;

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#display()
     */
    abstract public void display();
}
