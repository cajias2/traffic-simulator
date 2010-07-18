/**
 * 
 */
package sim.app.processing.simulations.displayers;

import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.TrafficSim;
import sim.app.simulations.Sim1;
import sim.processing.TrafficSimDisplayer;

/**
 * @author biggie
 * 
 */
public class Sim1Displayer extends TrafficSimDisplayer {

    /**
     * 
     */
    private static final long serialVersionUID = 5840573678420820299L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * sim.app.processing.TrafficSimDiplayer#getNewSimulation(processing.core
     * .PApplet, java.util.logging.Logger)
     */
    @Override
    protected TrafficSim getNewSimulation(final PApplet applet, final Logger log) {
	return new Sim1(applet, log);
    }

}