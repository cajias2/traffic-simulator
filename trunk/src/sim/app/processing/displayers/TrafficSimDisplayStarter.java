package sim.app.processing.displayers;

import java.io.File;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.TrafficSim;
import sim.app.simulations.MainSimulation;

public class TrafficSimDisplayStarter extends PApplet {

    /**
     * 
     */
    private static final long serialVersionUID = 4667148180915457828L;
    private static TrafficSim _sim;
    private Logger _log;
    private long _totalTime;
    protected static int _simDur;
    protected static Double[] _tlDelay;
    protected static int _tfDur;
    protected static String _outFolder;

    
    public static void setParams(int simDur_, Double[] tlDelay_, int tfDur_, String outFolder_) {
	_simDur = simDur_;
	_tlDelay = tlDelay_;
	_tfDur = tfDur_;
	_outFolder = outFolder_;

    }

    /**
     * Gets called first by PApplet. Creates a new simulation.
     */
    public void setup() {
	_totalTime = System.currentTimeMillis();
	_log = Logger.getLogger(sketchPath);
	_log.info("Simulation staterd\n");
	_sim = getNewSimulation(this,_simDur,_tlDelay,_tfDur, _outFolder, _log);
	size(_sim.getWidth(), _sim.getHeight());
	frameRate(_sim.getFrameRate());
    }

    /**
     * Called by PApplet. Draws the simulation.
     */
    public void draw() {
	background(100);
	if (_sim.getSimDuration() >= this.frameCount) {
	    _sim.display();
	    _sim.update();
	} else {
	    _sim.end();
	    _log.info("Simulation ended. Took: " + (System.currentTimeMillis() - _totalTime) / 1000 + "sec");
	    this.exit();
	}
    }

    protected TrafficSim getNewSimulation(PApplet applet_, int simDur_, Double[] tfDlay_, int tfDur_,
	    String outFolder_, Logger log_) {
	return new MainSimulation(applet_, simDur_, tfDlay_, tfDur_, outFolder_, log_);

    }

    
    public static void main(String[] args) {	
	PApplet.main(new String[] { TrafficSimDisplayStarter.class.getName() });
    }


    


}
