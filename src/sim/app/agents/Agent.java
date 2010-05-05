/**
 * 
 */
package sim.app.agents;

/**
 * @author biggie
 *
 */
public abstract class Agent {
	
	/**
	 * Update the sate of this agent.
	 * Ideally this is called before display
	 */
	public abstract void move();
	
	/**
	 * Display an agent. 
	 */
	public abstract void display();

}
