/*
 * @(#)MetricsAgent.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.agents.social;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import sim.graph.algorithms.metrics.SimpleMetrics;

/**
 * @author biggie
 * 
 */
public class MetricsAgent extends Agent {

    private static final long serialVersionUID = 4508519047587954841L;
    private BufferedWriter _outWrt = null;

    public MetricsAgent(SimState state_) {
	super(state_);
	File outDir = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "output");
	if (!outDir.exists())
	    outDir.mkdir();
	try {
	    _outWrt = new BufferedWriter(new FileWriter(outDir.getAbsolutePath() + System.getProperty("file.separator")
		    + System.currentTimeMillis() + "graphMet" + ".txt"));
	    _outWrt.write("TimeStep\tCI\tDeg\t Edges\n");

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    @Override
    public void step(SimState state_) {
	if (null != _outWrt) {
	    SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	    int nodeCount = _agentList.size();

	    double maxEdges = nodeCount * (nodeCount - 1) / 2;
	    double ts = socSim.schedule.time() + 1;
	    double avgCi = SimpleMetrics.avgClusterCoeff(getSocGraph());
	    double avgDeg = SimpleMetrics.avgDeg(getSocGraph());
	    double edgepnct = getSocGraph().getEdgeCount() / maxEdges;
	    /*
	     * Print a log line
	     */
	    try {
		_outWrt.write(ts + "\t" + avgCi + "\t" + avgDeg + "\t" + edgepnct + "\n");
		_outWrt.flush();
		if (socSim.SIM_TIME < ts) {
		    _outWrt.close();
		}
	    } catch (Exception e) {// Catch exception if any
		System.err.println("Error: " + e.getMessage());
	    }
	}
    }
}
