package test.social;

import sim.app.social.db.DBManager;
import sim.graph.algorithms.ClusterFinder;

/**
 * TODO Purpose
 * 
 * @author biggie
 * @date Nov 28, 2011
 */
public class ClusterGraphTest {
    private static final long SEED = 320320486;
    private static final String K_FLAG = "-k";
    private static final String SIM_ID = "-id";

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    public static void main(String[] args) {
	int kSize = 4;
	int simId = -1;
	for (int i = 0; i < args.length; i++) {
	    if (K_FLAG.equals(args[i])) {
		kSize = Integer.parseInt(args[++i]);
	    } else if (SIM_ID.equals(args[i])) {
		simId = Integer.parseInt(args[++i]);
	    }
	}
	long durTime = System.currentTimeMillis();
	System.out.println("Running CommTracker....");
	cluster(simId, kSize);
	System.out.println("DURATION: " + (System.currentTimeMillis() - durTime) / 60000 + " mins");
	System.out.println("All done. Buh-bye!");
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    public static void cluster(int simId_, int kSize_) {
	ClusterFinder cf = new ClusterFinder(new DBManager(), kSize_);
	cf.clusterSim(simId_);
	cf.writeMetrics();
    }

}
