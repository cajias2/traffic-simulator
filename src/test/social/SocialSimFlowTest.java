/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */

package test.social;

import java.util.List;

/**
 * TODO Purpose
 * 
 * @author biggie
 */
public class SocialSimFlowTest {
    private static final long SEED = 320320486;
    private static final String K_FLAG = "-k";
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
	List<Integer> simIDs = SimulationTest.runSim(args, totalSimRuns, SEED);
	System.out.println("New Simulations: " + simIDs);
	System.out.println("DURATION: " + (System.currentTimeMillis() - durTime) / 60000 + " mins");
	System.out.println("Clustering Snapshots....");
	System.out.println("Running CommTracker....");
	for (Integer simID : simIDs) {
	    ClusterGraphTest.cluster(simID, kSize);
	}
	System.out.println("All done. Buh-bye!");
    }



}
