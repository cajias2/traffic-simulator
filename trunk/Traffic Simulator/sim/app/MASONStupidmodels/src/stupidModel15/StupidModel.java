package stupidModel15;
// in this version, I hacked something together
// so that all the bugs will move before any of them
// grow.

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

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
	private int xSpaceSize;
	private int ySpaceSize;
	
	private Bag bugs;
	private HabitatCell[] cells;
	
	private PrintWriter outFile=null;
	
	private int initNumberOfBugs = 100;
	private double bugMaxDailyFoodConsumption = 1.0;
	private double cellMaxFoodProduction = .01;
	private double bugSurvivalProbability = .95;
	private double bugReproductionSize = 10.0;
	private final String dataFile = "Stupid_Cell.Data";
	
	public StupidModel(long seed) {
		super(new MersenneTwisterFast(seed), new Schedule(6));
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
	  // the cell space is read from a file		
	  // the DoubleFileGridReader is home-grown, NOT a Repast class.
	  // methods:  nextData, getX, getY, getData.
	  DoubleFileGridReader theReader = new DoubleFileGridReader(dataFile);
	  if (!theReader.nextData()) {
			System.err.println("Empty data file");
			System.exit(0);
			}
	  int x = theReader.getX();		
	  int y = theReader.getY();
	  xSpaceSize = x+1;
	  ySpaceSize = y+1;
	  int counter = 0;
  	  cells = new HabitatCell[xSpaceSize*ySpaceSize];
 	  habitatSpace = new ObjectGrid2D(xSpaceSize,ySpaceSize);
 	  HabitatCell newCell = new HabitatCell(this,x,y,theReader.getData());
 	  habitatSpace.field[x][y] = newCell;
      cells[counter++] = newCell;

      while (theReader.nextData()) {
			x = theReader.getX();
			y = theReader.getY();
			newCell = new HabitatCell(this,x,y,theReader.getData());
			habitatSpace.field[x][y] = newCell;
			cells[counter++] = newCell;
		}

	  bugSpace = new ObjectGrid2D(xSpaceSize,ySpaceSize);
	  bugs = new Bag();

      for (int i=0; i<initNumberOfBugs; i++) {
		  bugs.add(new StupidBug(this));
	  }
	}
  
	public void buildSchedule() {
		Sequence cellSeq = new Sequence(cells) {
			public void step(SimState state) {
				super.step(state);
			}
		};
		schedule.scheduleRepeating(0,0,cellSeq);

		// build a step that makes all the bugs move.
		// but first, the bugs are sorted in descending order
		// by size
		Steppable bugMoveStep = new Steppable() {
			public void step(SimState state) {
				// build a sequence that makes all the bugs move
				bugs.sort(new Comparator() {
					public int compare(Object obj1, Object obj2) {
						StupidBug bug1 = (StupidBug)obj1;
						StupidBug bug2 = (StupidBug)obj2;
						if (bug1.getSize() > bug2.getSize())
							return -1;
						else if (bug1.getSize() == bug2.getSize())
							return 0;
						else return 1;
					}
					// check to see if they're the same object
					public boolean equals(Object bug1, Object bug2) {
						return bug1 == bug2;
					}
				});
				for (int i=0; i<bugs.size(); i++)
					((StupidBug)bugs.get(i)).move(state);
			}
		};
		schedule.scheduleRepeating(0,1,bugMoveStep);

		// build a sequence that makes all the bugs grow
		// steps is the instance variable in a Sequence
		//  that stores the Steppable objects
		// this is pretty kludgy, since I'm not using the
		//  fact that they're steppable.
		Steppable bugEatStep = new Steppable () {
			public void step(SimState state) {
				// make a copy of Bugs so that Bugs can be
				// added or deleted to the original Bag
				// w/o affecting tempBugs.
				try {
					Bag tempBugList = (Bag) bugs.clone();
					for (int i=0; i<tempBugList.size(); i++)
						((StupidBug)tempBugList.get(i)).grow(state);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		schedule.scheduleRepeating(0,2,bugEatStep);

		Steppable bugSurviveStep = new Steppable () {
			public void step(SimState state) {
				// make a copy of Bugs so that Bugs can be
				// added or deleted to the original Bag
				// w/o affecting tempBugs.
				try {
					Bag tempBugList = (Bag) bugs.clone();
					for (int i=0; i<tempBugList.size(); i++)
						((StupidBug)tempBugList.get(i)).survive(state);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		schedule.scheduleRepeating(0,3,bugSurviveStep);

		schedule.scheduleRepeating(0,4,new Steppable() {
			public void step(SimState state) {
				outputData();
			}
		});

		schedule.scheduleRepeating(0,5,new Steppable() {
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
		for (int i=0; i<bugs.size(); i++) {
			double thisBugSize = ((StupidBug)bugs.get(i)).getSize();
			if (thisBugSize < minBugSize)
				minBugSize = thisBugSize;
			if (thisBugSize > maxBugSize)
				maxBugSize = thisBugSize;
			totalSize += thisBugSize;
		}
		double aveBugSize = totalSize / bugs.size();
		outFile.println(ticks + "\t" + minBugSize + "\t" +
				maxBugSize + "\t" + aveBugSize);
	}
	
	public void checkIfDone(SimState state) {
		if (bugs.size() == 0 ||
			bugs.size() >= 5000) {
				schedule.reset();
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

	public Bag getBugs() {
		return bugs;
	}

	public double getBugSurvivalProbability() {
		return bugSurvivalProbability;
	}

	public void setBugSurvivalProbability(double bugSurvivalProbability) {
		this.bugSurvivalProbability = bugSurvivalProbability;
	}

	public double getBugReproductionSize() {
		return bugReproductionSize;
	}

	public int getXSpaceSize() {
		return xSpaceSize;
	}

	public void setXSpaceSize(int spaceSize) {
		xSpaceSize = spaceSize;
	}

	public int getYSpaceSize() {
		return ySpaceSize;
	}

	public void setYSpaceSize(int spaceSize) {
		ySpaceSize = spaceSize;
	}

	public String getDataFile() {
		return dataFile;
	}
}
