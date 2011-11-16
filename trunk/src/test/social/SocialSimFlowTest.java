/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */

package test.social;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.app.social.db.DBManager;
import sim.graph.UndirectedSparseDynamicGraph;
import sim.graph.algorithms.BronKerboschKCliqueFinder;
import sim.graph.algorithms.CPMCommunityFinder;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.Graph;

/**
 * TODO Purpose
 * 
 * @author biggie
 */
public class SocialSimFlowTest {
    private static final long SEED = 320320486;
    private static final String K_FLAG = "-k";
    private static final String INTERVAL_FLAG = "-i";
    private static final String TOTAL_RUN_FLAG = "-rep";

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    public static void main(String[] args) {
	int kSize = 4;
	int totalSimRuns = 1;
	for (int i = 0; i < args.length; i++) {
	    if (K_FLAG.equals(args[i])) {
		kSize = Integer.parseInt(args[++i]);
	    } else if (TOTAL_RUN_FLAG.equals(args[i])) {
		totalSimRuns = Integer.parseInt(args[++i]);
	    }
	}
	long durTime = System.currentTimeMillis();
	List<Integer> simIDs = runSim(args, kSize, totalSimRuns);
	System.out.println("DURATION: " + (System.currentTimeMillis() - durTime) / 60000 + " mins");
	System.out.println("Clustering Snapshots....");

	clusterGraphs(simIDs, kSize);
    }

    /**
     * @param simIDs_
     */
    private static void clusterGraphs(List<Integer> simIDs_, int k_) {
	DBManager dbMgr = new DBManager();
	for (Integer simID : simIDs_) {
	    UndirectedSparseDynamicGraph dynGraph = new UndirectedSparseDynamicGraph(simID, dbMgr);
	    dynGraph.init();
	    Collection<Set<Integer>> communities = clusterGraph((Graph) dynGraph, k_);
	}
    }

    /**
     * @param dynGraph_
     */
    private static Collection<Set<Integer>> clusterGraph(Graph<Integer, Edge> dynGraph_, int k_) {
	BronKerboschKCliqueFinder<Integer, Edge> kFinder = new BronKerboschKCliqueFinder<Integer, Edge>(dynGraph_);
	Collection<Set<Integer>> maxKClique = kFinder.getAllMaxKCliques(k_);
	CPMCommunityFinder<Integer> cpmFinder = new CPMCommunityFinder<Integer>(maxKClique);
	return cpmFinder.findCommunities(k_);
    }

    /**
     * @param args
     * @param kSize
     * @param totalSimRuns
     */
    private static List<Integer> runSim(String[] args, int kSize, int totalSimRuns) {
	List<Integer> simIDs = new LinkedList<Integer>();
	for (int i = 0; i < totalSimRuns; i++) {
	    System.out.println("******\n* Run: " + i + "\n******\n");
	    SocialSim<Agent, String> sim = new SocialSim<Agent, String>(SEED);
	    sim.runSim(args);
	    simIDs.add(sim.getSimID());
	    System.out.println("Done.");
	}
	return simIDs;
    }
    
    
}
