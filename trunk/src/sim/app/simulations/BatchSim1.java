package sim.app.simulations;

import java.io.File;

import sim.app.TrafficSim;
import sim.app.processing.displayers.TrafficSimDisplayStarter;

public class BatchSim1 {

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
	if (args.length < 1) {
	    printUsageAndExit();
	}
	
	Double[] tlDelay = { 0.0, 0.0, 0.0 };
	int simDur = 1800;
	int tfDur = 100;
	String outFolder = "out";

	for (int i = 0; i < args.length; i++) {
	    if ("-outF".equals(args[i])) {
		outFolder = args[++i];
	    } else if ("-simDur".equals(args[i])) {
		simDur = Integer.parseInt(args[++i]);
	    } else if ("-tl".equals(args[i])) {
		tlDelay[0] = Double.parseDouble(args[++i]);
		tlDelay[1] = Double.parseDouble(args[++i]);
		tlDelay[2] = Double.parseDouble(args[++i]);		    
	    } else if ("-tfDur".equals(args[i])) {
		tfDur = Integer.parseInt(args[++i]);	    
	    }else {
		printUsageAndExit();
	    }
	}

	TrafficSimDisplayStarter.setParams(simDur, tlDelay, tfDur, outFolder);
	TrafficSimDisplayStarter.main(null);
    }
    
    /**
     * If somethign goes wrong, print a usage message and exit with error
     */
    private static void printUsageAndExit() {
	System.err.println("Usage: java -jar " + "[app jar name]" + ".jar -outF [outFile] -simDur [int]" +
			"-tl [double[3]] -tfDur[int]");
	System.exit(1);
    }

}
