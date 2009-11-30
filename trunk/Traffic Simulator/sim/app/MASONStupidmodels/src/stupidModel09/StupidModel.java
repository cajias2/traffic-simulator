package stupidModel09;
// in this version, I hacked something together
// so that all the bugs will move before any of them
// grow.

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import sim.engine.RandomSequence;
import sim.engine.Schedule;
import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import ec.util.MersenneTwisterFast;

public class StupidModel extends SimState {
	
	private ObjectGrid2D bugSpace;
	private ObjectGrid2D habitatSpace;
		
	private StupidBug[] bugs;
	private HabitatCell[] cells;
	
	private PrintWriter outFile=null;
	
	private int initNumberOfBugs = 100;
	private double bugMaxDailyFoodConsumption = 1.0;
	private double cellMaxFoodProduction = .01;
	
	public StupidModel(long seed) {
		super(new MersenneTwisterFast(seed), new Schedule(5));
	}
  
	public void start() {
	  super.start();
	  buildModel();
	  buildSchedule();
	  // open the output file so that it appends if it already exists
	  try {
		outFile = new PrintWriter(new FileWriter("Stupid_output.txt", true));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
  
	public void buildModel() {
	  bugSpace = new ObjectGrid2D(100,100);
	  bugs = new StupidBug[initNumberOfBugs];
	  habitatSpace = new ObjectGrid2D(100,100);
	  cells = new HabitatCell[10000];

	  for (int i=0; i<initNumberOfBugs; i++) {
		  bugs[i] = new StupidBug(this);
	  }
	  int cellCount = 0;
      for (int i=0; i<100; i++)
    	  for (int j=0; j<100; j++) {
    		  HabitatCell h = new HabitatCell(this,i,j);
    		  habitatSpace.field[i][j] = h;
    		  cells[cellCount++] = h;
    	  }	  
	}
  
	public void buildSchedule() {
		Sequence cellSeq = new Sequence(cells) {
			public void step(SimState state) {
				super.step(state);
			}
		};
		schedule.scheduleRepeating(0,0,cellSeq);
		
		// this moves the bugs in random order.
		// I've written the StupidBug step method
		// to call move.  If we wanted to randomize
		// 2 actions, it would be much more complicated,
		// because then we'd have to specify which method
		// we called.
		Sequence bugMoveSeq = new RandomSequence(bugs);
		schedule.scheduleRepeating(0,1,bugMoveSeq);

		// build a sequence that makes all the bugs grow
		// steps is the instance variable in a Sequence
		//  that stores the Steppable objects
		// this is pretty kludgy, since I'm not using the
		//  fact that they're steppable.
		Sequence bugEatSeq = new Sequence(bugs) {
			public void step(SimState state) {
				for (int i=0; i<steps.length; i++)
					((StupidBug)steps[i]).grow(state);
			}
		};
		schedule.scheduleRepeating(0,2,bugEatSeq);

		schedule.scheduleRepeating(0,3,new Steppable() {
			public void step(SimState state) {
				outputData();
			}
		});
		schedule.scheduleRepeating(0,4,new Steppable() {
			public void step(SimState state) {
				checkIfDone(state);
			}
		});
	}

	public void outputData() {
		int ticks = (int) schedule.time();
		double minBugSize = Double.MAX_VALUE;
		double maxBugSize = 0.0;
		double totalSize = 0.0;
		for (int i=0; i<bugs.length; i++) {
			double thisBugSize = ((StupidBug)bugs[i]).getSize();
			if (thisBugSize < minBugSize)
				minBugSize = thisBugSize;
			if (thisBugSize > maxBugSize)
				maxBugSize = thisBugSize;
			totalSize += thisBugSize;
		}
		double aveBugSize = totalSize / bugs.length;
		outFile.println(ticks + "\t" + minBugSize + "\t" +
				maxBugSize + "\t" + aveBugSize);
	}
	
	public void checkIfDone(SimState state) {
		StupidModel theModel = (StupidModel) state;
		for (int i=0; i<bugs.length; i++)
			if (((StupidBug)bugs[i]).getSize() > 100.0) {
				//finish();
				theModel.schedule.reset();
				break;
			}
	}
	
	public void finish() {
		super.finish();
		outFile.close();
	}
	
	public ObjectGrid2D getBugSpace() {
		return bugSpace;
	}

	public ObjectGrid2D getHabitatSpace() {
		return habitatSpace;
	}

	public double getBugMaxDailyFoodConsumption() {
		return bugMaxDailyFoodConsumption;
	}

	public void setBugMaxDailyFoodConsumption(double bugMaxDailyFoodConsumption) {
		this.bugMaxDailyFoodConsumption = bugMaxDailyFoodConsumption;
	}

	public double getCellMaxFoodProduction() {
		return cellMaxFoodProduction;
	}

	public void setCellMaxFoodProduction(double cellMaxFoodProduction) {
		this.cellMaxFoodProduction = cellMaxFoodProduction;
	}

	public int getInitNumberOfBugs() {
		return initNumberOfBugs;
	}

	public void setInitNumberOfBugs(int initNumberOfBugs) {
		this.initNumberOfBugs = initNumberOfBugs;
	}

	public StupidBug[] getBugs() {
		return bugs;
	}
}
