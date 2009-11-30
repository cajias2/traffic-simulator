package stupidModel06;

import sim.portrayal.Inspector;
import sim.util.gui.MiniHistogram;

public class StupidInspector extends Inspector {

	private StupidBug[] bugs;
	private MiniHistogram bugSizeHistogram;
	private double[] bugSizeArray;
	
	public StupidInspector(StupidBug[] b) {
		setVolatile(false);
		bugs = b;
		bugSizeArray = new double[bugs.length];
		bugSizeHistogram = new MiniHistogram();
        bugSizeHistogram.setBucketsAndLabels(MiniHistogram.makeBuckets(makeBugSizeArray(),10,0,100,false),
        		MiniHistogram.makeBucketLabels(10,0.0,100.0,false));
        add(bugSizeHistogram);
 	}

	public double[] makeBugSizeArray() {
		for (int i=0; i<bugs.length; i++)
			bugSizeArray[i] = bugs[i].getSize();
		return bugSizeArray;
	}
	
	public void updateInspector() {
        bugSizeHistogram.setBucketsAndLabels(MiniHistogram.makeBuckets(makeBugSizeArray(),10,0,100,false),
        		MiniHistogram.makeBucketLabels(10,0.0,100.0,false));
	}

}
