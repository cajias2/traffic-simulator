package sim.app.processing;

import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.simulations.TrafficSim;

@SuppressWarnings("serial")
public abstract class TrafficSimDiplayer extends PApplet {
    
    private static TrafficSim _sim;
    /**
     * Gets called first by PApplet. Creates a new simulation.
     */
    public void setup() {
	Logger log = Logger.getLogger(sketchPath);
	_sim = getNewSimulation(this, log);
	size(_sim.getWidth(), _sim.getHeight());
	frameRate(_sim.getFrameRate());
    }

    /**
     * 
     */
    public void draw() {
        background(100);
        try{
        _sim.display();
        }catch(Exception e)
        {
        	System.out.println(e);
        }
        _sim.update();
    }

    protected abstract TrafficSim getNewSimulation(PApplet applet_,Logger log);
}
