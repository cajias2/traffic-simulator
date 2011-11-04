/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */

package test.social;

import sim.agents.Agent;
import sim.app.social.SocialSim;

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
	int snapshotInterval = 1;
	int totalSimRuns = 1;
	for (int i = 0; i < args.length; i++) {
	    if (K_FLAG.equals(args[i])) {
		kSize = Integer.parseInt(args[++i]);
	    } else if (INTERVAL_FLAG.equals(args[i])) {
		snapshotInterval = Integer.parseInt(args[++i]);
	    } else if (TOTAL_RUN_FLAG.equals(args[i])) {
		totalSimRuns = Integer.parseInt(args[++i]);
	    }
	}
	long durTime = System.currentTimeMillis();
	runSim(args, kSize, snapshotInterval, totalSimRuns);
	System.out.println("DURATION: " + (System.currentTimeMillis() - durTime) / 60000 + " mins");
    }

    /**
     * 
     * @param args
     * @param kSize
     * @param snapshotInterval
     * @param totalSimRuns
     */
    private static void runSim(String[] args, int kSize, int snapshotInterval, int totalSimRuns) {
	for (int i = 0; i < totalSimRuns; i++) {
	    System.out.println("******\n* Run: " + i + "\n******\n");
	    SocialSim<Agent, String> sim = new SocialSim<Agent, String>(SEED);
	    sim.runSim(args, snapshotInterval);
	    System.out.println("Done.");
	}
    }
}
