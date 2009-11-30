package sim.app.MASONStupidmodels.src.stupidModel02;

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
		// build a sequence that makes all the bugs move
		Sequence bugMoveSeq = new Sequence(bugs) {
			public void step(SimState state) {
				for (int i=0; i<steps.length; i++)
					((StupidBug)steps[i]).move(state);
			}
		};
		schedule.scheduleRepeating(0,0,bugMoveSeq);
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
		schedule.scheduleRepeating(0,1,bugEatSeq);
	}
	
	public ObjectGrid2D getBugSpace() {
		return bugSpace;
	}
}
