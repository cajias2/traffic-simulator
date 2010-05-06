/**
 * 
 */
package sim.app.agents.display;

import processing.core.PApplet;
import sim.app.agents.Agent;

/**
 * @author biggie
 *
 */
public abstract class DisplayableAgent extends Agent {

	protected PApplet applet;	
	/* (non-Javadoc)
	 * @see sim.app.agents.Agent#display()
	 */
	abstract public void display();
}
