package sim.app.MASONStupidmodels.src.stupidModel01;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.Sequence;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class StupidModel extends SimState {
	
	private ObjectGrid2D bugSpace;
	private StupidBug[] bugs;
	
	public StupidModel(long seed) {
		super(new MersenneTwisterFast(seed), new Schedule());
	}
  
	public void start() {
	  buildModel();
	  buildSchedule();
	}
  
	public void buildModel() {
	  bugSpace = new ObjectGrid2D(100,100);
	  bugs = new StupidBug[100];
	  for (int i=0; i<100; i++) {
		  bugs[i] = new StupidBug(this);
	  }
	}
  
	public void buildSchedule() {
		Sequence bugSeq = new Sequence(bugs);
		schedule.scheduleRepeating(bugSeq);
	}

	public ObjectGrid2D getBugSpace() {
		return bugSpace;
	}
}
