/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */

package test.social;

import java.util.LinkedList;
import java.util.List;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.graph.algorithms.ClusterFinder;

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
	ClusterFinder.findSimClusters(simIDs, kSize);
	System.out.println("Running CommTracker....");
	System.out.println("** TODO **");
	System.out.println("All done. Buh-bye!");
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
