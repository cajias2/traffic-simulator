package stupidModel07;
// in this version, I hacked something together
// so that all the bugs will move before any of them
// grow.

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class StupidModel extends SimState {
	
	private ObjectGrid2D bugSpace;
	private ObjectGrid2D habitatSpace;
		
	private StupidBug[] bugs;
	private HabitatCell[] cells;
	
	private int initNumberOfBugs = 100;
	private double bugMaxDailyFoodConsumption = 1.0;
	private double cellMaxFoodProduction = .01;
	
	public StupidModel(long seed) {
		super(new MersenneTwisterFast(seed), new Schedule(4));
	}
  
	public void start() {
	  super.start();
	  buildModel();
	  buildSchedule();
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
		Sequence cellSeq = new Sequence(cells);
		schedule.scheduleRepeating(0,0,cellSeq);
		// build a sequence that makes all the bugs move
		Sequence bugMoveSeq = new Sequence(bugs) {
			public void step(SimState state) {
				for (int i=0; i<steps.length; i++)
					((StupidBug)steps[i]).move(state);
			}
		};
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
				checkIfDone(state);
			}
		});
	}

	public void checkIfDone(SimState state) {
		StupidModel theModel = (StupidModel) state;
		for (int i=0; i<bugs.length; i++)
			if (bugs[i].getSize() >= 100) {
				finish();
				theModel.schedule.reset();
				break;
			}
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
