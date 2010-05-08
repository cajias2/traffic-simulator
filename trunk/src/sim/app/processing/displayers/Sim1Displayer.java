/**
 * 
 */
package sim.app.processing.displayers;

import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.TrafficSim;
import sim.app.processing.TrafficSimDiplayer;
import sim.app.simulations.Sim1;

/**
 * @author biggie
 * 
 */
public class Sim1Displayer extends TrafficSimDiplayer {

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
	// TODO Auto-generated method stub
	return new Sim1(applet, log);
    }

}
