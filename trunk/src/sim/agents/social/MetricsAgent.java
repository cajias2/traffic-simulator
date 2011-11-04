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
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.algorithms.metrics.LowOrderGraphMetrics;

/**
 * @author biggie
 * 
 */
public class MetricsAgent implements Steppable {

    private static final long serialVersionUID = 4508519047587954841L;
    private BufferedWriter _outWrt = null;

    public MetricsAgent() {
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
    public void step(SimState state_) {
	if (null != _outWrt) {
	    SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	    int nodeCount = socSim.network.getVertexCount();

	    double maxEdges = nodeCount * (nodeCount - 1) / 2;
	    double ts = socSim.schedule.time() + 1;
	    double avgCi = LowOrderGraphMetrics.avgClusterCoeff(socSim.network);
	    double avgDeg = LowOrderGraphMetrics.avgDeg(socSim.network);
	    double edgepnct = socSim.network.getEdgeCount() / maxEdges;
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
