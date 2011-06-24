package app.social.agents;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.util.Double2D;

public class TraverseAgent extends Agent {

    private static int TAcount = 0;
    private final static int DIM = 6;

    // private CardinalDirection mov_dir;
    private final double dvecx;
    private final double dvecy;
    private final int leaps = 0;
    private final int round = 0;

    public TraverseAgent(SimState state_) {
	super(state_);
	// CardinalDirection[] opts = CardinalDirection.values();
	// mov_dir = opts[_rand.nextInt(opts.length)];
	SocialSim socSim = (SocialSim) state_;
	dvecx = _rand.nextDouble() * (SocialSim.XMAX - SocialSim.XMIN) / DIM * (_rand.nextBoolean() ? 1 : -1);
	dvecy = _rand.nextDouble() * (SocialSim.YMAX - SocialSim.YMIN) / DIM * (_rand.nextBoolean() ? 1 : -1);

	_actionDim = DIM;
	TAcount++;
    }
    
    @Override
    protected Double2D move(SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	Double2D currLoc = socSim.fieldEnvironment.getObjectLocation(this);
	boolean resetx = false;
	boolean resety = false;

	if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dvecx, currLoc.y + dvecy))) {
	    if (dvecx > 0) {
		if (!socSim.acceptablePosition(this, new Double2D(
			SocialSim.XMIN + dvecx - (SocialSim.XMAX - currLoc.x), currLoc.y + dvecy))) {
		    if (dvecy > 0) {
			if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dvecx, SocialSim.YMIN + dvecy
				- (SocialSim.YMAX - currLoc.y)))) {
			    resetx = resety = true;
			} else {
			    resety = true;
			}
		    } else {
			if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dvecx, SocialSim.YMAX + dvecy
				- (currLoc.y - SocialSim.YMIN)))) {
			    resetx = resety = true;
			} else {
			    resety = true;
			}
		    }
		} else {
		    resetx = true;
		}
	    } else {
		if (!socSim.acceptablePosition(this, new Double2D(
			SocialSim.XMAX + dvecx - (currLoc.x - SocialSim.XMIN), currLoc.y + dvecy))) {
		    if (dvecy > 0) {
			if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dvecx, SocialSim.YMIN + dvecy
				- (SocialSim.YMAX - currLoc.y)))) {
			    resetx = resety = true;
			} else {
			    resety = true;
			}
		    } else {
			if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dvecx, SocialSim.YMAX + dvecy
				- (currLoc.y - SocialSim.YMIN)))) {
			    resetx = resety = true;
			} else {
			    resety = true;
			}
		    }
		} else {
		    resetx = true;
		}
	    }
	}

	currLoc = new Double2D((resetx ? dvecx
		+ ((dvecx > 0) ? SocialSim.XMIN - SocialSim.XMAX + currLoc.x : SocialSim.XMAX + SocialSim.XMIN
			- currLoc.x) : currLoc.x + dvecx), (resety ? dvecy
		+ ((dvecy > 0) ? SocialSim.YMIN - SocialSim.YMAX + currLoc.y : SocialSim.YMAX + SocialSim.YMIN
			- currLoc.y) : currLoc.y + dvecy));
	return currLoc;
    }


}
