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
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 * 
 */
public class MetricsAgent implements Steppable {

    private BufferedWriter _outWrt = null;

    public MetricsAgent() {
	File outDir = new File(System.getProperty("user.dir") + "/output");
	if (!outDir.exists())
	    outDir.mkdir();

	FileWriter outFileWrt;
	try {
	    outFileWrt = new FileWriter(outDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".txt");
	    _outWrt = new BufferedWriter(outFileWrt);
	    System.out.println("OUTPUT:\tTimeStep\t avgCI\t avgDeg\t Edges\n");

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
	    SocialSim socSim = (SocialSim) state_;
	    int nodeCount = socSim.network.getJGraph().getVertexCount();
	    double maxEdges = nodeCount * (nodeCount - 1) / 2;
	    double ts = socSim.schedule.time() + 1;
	    double avgCi = socSim.network.avgClusterCoeff();
	    double avgDeg = socSim.network.avgDeg();
	    double edgepnct = socSim.network.getJGraph().getEdgeCount() / maxEdges;
	    if (socSim.network.getJGraph().getEdgeCount() > maxEdges) {
		System.out.println("WTF");
	    }

	    // Remove duplicates
	    try {
		_outWrt.write(ts + "\t" + avgCi + "\t" + "\t" + avgDeg + "\t" + edgepnct + "\n");
		_outWrt.flush();
		if (socSim.SIM_TIME < ts) {
		    _outWrt.close();
		}
	    } catch (Exception e) {// Catch exception if any
		System.err.println("Error: " + e.getMessage());
	    }
	}
    }

    /**
     * 
     * @param g_
     *            Graph to measure
     * @return <code>double</code> Characteristic path length
     */
    private static double clusterIdx(Graph<Agent, FriendLink> g_) {
	boolean firstPass = true;
	double clusterIdxAvg = 0.0;
	for (Agent iNode : g_.getVertices()) {
	    double kLinks = 0.0;
	    Agent kNodes[] = new Agent[g_.getNeighborCount(iNode)];
	    kNodes = g_.getNeighbors(iNode).toArray(kNodes);
	    for (int i = 0; i < kNodes.length; i++) {
		for (int j = i + 1; j < kNodes.length; j++) {
		    if (g_.isNeighbor(kNodes[i], kNodes[j]))
			kLinks++;
		}
	    }
	    double clusterIdx = (g_.degree(iNode) >= 2) ? kLinks
		    / (g_.getNeighborCount(iNode) * (g_.getNeighborCount(iNode) - 1) / 2) : 0;
	    if (firstPass) {
		clusterIdxAvg = clusterIdx;
		firstPass = !firstPass;
	    } else {
		clusterIdxAvg = (clusterIdxAvg + clusterIdx) / 2;

	    }

	}
	return clusterIdxAvg;
    }
}
