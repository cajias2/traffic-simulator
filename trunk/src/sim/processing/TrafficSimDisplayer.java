package sim.processing;

import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.TrafficSim;

@SuppressWarnings("serial")
public abstract class TrafficSimDisplayer extends PApplet
{

    private static TrafficSim _sim;
    private Logger            _log;
    private long              _duration;

    /**
     * Gets called first by PApplet. Creates a new simulation.
     */
    @Override
    public void setup()
    {
	_duration = System.currentTimeMillis();
	_log = Logger.getLogger(sketchPath);
	_log.info("Simulation staterd\n");
	_sim = getNewSimulation(this, _log);
	size(_sim.getWidth(), _sim.getHeight());
	frameRate(_sim.getFrameRate());
    }

    /**
     * Called by PApplet. Draws the simulation.
     */
    @Override
    public void draw()
    {
	background(100);
	if (_sim.getSimDuration() >= frameCount)
	{
	    _sim.display();
	    _sim.update();
	} else
	{
	    _sim.end();
	    _log.info("Simulation ended. Took: " + (System.currentTimeMillis() - _duration) / 1000 + "sec");
	    exit();
	}
    }

    protected abstract TrafficSim getNewSimulation(PApplet applet_, Logger log);
}
