/**
 * 
 */
package sim.agents;

import processing.core.PApplet;

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
