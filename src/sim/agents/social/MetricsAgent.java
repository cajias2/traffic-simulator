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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.FloydWarshallShortestPaths;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.social.link.FriendLink;
import sim.graph.social.metrics.BronKerboschKCliqueFinder;
import sim.graph.social.metrics.CPMCommunityFinder;

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
	    System.out.println("OUTPUT:\tTimeStep\t avgPL\t avgCI\t avgDeg\t Edges\tKCliques\n");

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
	    
	    
	    DijkstraDistance<Agent, FriendLink> dijkstra = new DijkstraDistance(socSim.network.getJGraph(), true);
	    Collection<Agent> nodes = socSim.network.getJungNodes();
	    
	    int totalLength = 0;
	    int totalPaths = 0;
	    for(Agent agent : nodes){
	    	Map<Agent, Number> map = dijkstra.getDistanceMap(agent);
	    	
	    	Set<Agent> agents = map.keySet();
	    	
	    	for(Agent dest : agents){
	    		if(dest != agent){
	    			if(map.get(dest)!=null){
		    			totalLength += (Double)map.get(dest);
		    			totalPaths++;
	    			}
	    		}
	    	}	    	
	    }
	    	    
	    double avgPL = 0;
	    if(totalPaths != 0){
	    	avgPL = (totalLength/totalPaths);
	    }
	    
	    Collection<Set<Agent>> kcliques = null;
	    Collection<Set<Agent>> cpmCom = null;
	    if (ts % 10 == 0) {
		kcliques = findKCliques(socSim.network.getJgraphT(), 4);
		cpmCom = findCPM(kcliques);
	    }

	    /*
	     * Print a log line
	     */
	    try {
		_outWrt.write(ts + "\t" + avgPL + "\t" + avgCi + "\t" + "\t" + avgDeg + "\t" + edgepnct + "\t");
		if (ts % 10 == 0) {
		    if (kcliques != null) {
			_outWrt.write(kcliques.size());
		    }
		    _outWrt.write("\t");

		    if (cpmCom != null) {
			_outWrt.write(cpmCom.size());
		    }

		}
		_outWrt.write("\n");
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
     * @author biggie
     * @name findCPM Purpose TODO
     * 
     * @param
     * @return Collection<Set<Agent>>
     */
    private Collection<Set<Agent>> findCPM(Collection<Set<Agent>> kcliques_) {

	CPMCommunityFinder<Agent> cpm = new CPMCommunityFinder<Agent>(kcliques_);
	return cpm.findCommunities(4);
    }

    /**
     * @author biggie
     * @name findKCliques Purpose TODO
     * 
     * @param
     * @return Collection<Set<V>>
     */
    private Collection<Set<Agent>> findKCliques(UndirectedGraph<Agent, FriendLink> graph_, int k_) {
	BronKerboschKCliqueFinder<Agent, FriendLink> clique = new BronKerboschKCliqueFinder<Agent, FriendLink>(graph_);
	return clique.getKMaxClique(k_);
    }
}
