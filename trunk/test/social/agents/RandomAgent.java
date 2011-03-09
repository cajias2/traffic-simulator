/**
 * 
 */
package social.agents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

import sim.agents.Agent;
import sim.app.networktest.NetworkTest;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Bag;
import sim.util.Double2D;
import social.links.SimpleFriendLink;

/**
 * @author biggie
 * 
 */
public class RandomAgent extends Agent {

    private static int COUNT = 0;
    private final int ID;
    private static int ACTION_DIM = 1;
    private Double2D desiredLocation = null;
    private final Double2D suggestedLocation = null;
    int steps = 0;

    public RandomAgent() {
	super();
	ID = COUNT;
	COUNT++;
    }

    public final int getID() {
	return ID;
    }

    /**
     * 	
     */
    public void step(final SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	Double2D currLoc = socSim.fieldEnvironment.getObjectLocation(this);
	Bag objs = socSim.fieldEnvironment.getObjectsWithinDistance(new Double2D(currLoc.x, currLoc.y), ACTION_DIM);

	Iterator<Agent> iter = objs.iterator();
	while(iter.hasNext())
	{
	    Agent ag  = iter.next();
	    if (makeFriend(ag, state_)) {
		socSim.network.addEdge(this, ag, new SimpleFriendLink(state_.random.nextDouble()));
	    }
	}

	Double2D newLoc = move(state_);
	socSim.fieldEnvironment.setObjectLocation(this, newLoc);
    }

    public Font nodeFont = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * @param object
     * @param graphics
     * @param info
     */
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
	double diamx = info.draw.width * NetworkTest.DIAMETER;
	double diamy = info.draw.height * NetworkTest.DIAMETER;

	graphics.setColor(Color.red);
	graphics.fillOval((int) (info.draw.x - diamx / 2), (int) (info.draw.y - diamy / 2), (int) (diamx),
		(int) (diamy));
	graphics.setFont(nodeFont.deriveFont(nodeFont.getSize2D() * (float) info.draw.width));
	graphics.setColor(Color.blue);
	// graphics.drawString(toString(), (int) (info.draw.x - diamx / 2),
	// (int) (info.draw.y - diamy / 2));
    }

    /**
     * @param object
     * @param info
     * @return
     */
    public boolean hitObject(Object object, DrawInfo2D info) {
	double diamx = info.draw.width * NetworkTest.DIAMETER;
	double diamy = info.draw.height * NetworkTest.DIAMETER;

	Ellipse2D.Double ellipse = new Ellipse2D.Double((int) (info.draw.x - diamx / 2),
		(int) (info.draw.y - diamy / 2), (int) (diamx), (int) (diamy));
	return (ellipse.intersects(info.clip.x, info.clip.y, info.clip.width, info.clip.height));
    }

    @Override
    public String toString() {
	return "Rand_" + ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#makeFriend(sim.engine.SimState)
     */
    @Override
    protected boolean makeFriend(Agent ag_, SimState state_) {
	if (!((SocialSim) state_).network.hasEdge(this, ag_))
	    return state_.random.nextBoolean();
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#move(sim.engine.SimState)
     */
    @Override
    protected Double2D move(SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	Double2D currLoc = socSim.fieldEnvironment.getObjectLocation(this);
	steps--;
	if (desiredLocation == null || steps <= 0) {
	    desiredLocation = new Double2D((state_.random.nextDouble() - 0.5)
		    * ((SocialSim.XMAX - SocialSim.XMIN) / 5 - SocialSim.DIAMETER) + currLoc.x,
		    (state_.random.nextDouble() - 0.5) * ((SocialSim.YMAX - SocialSim.YMIN) / 5 - SocialSim.DIAMETER)
			    + currLoc.y);
	    steps = 50 + state_.random.nextInt(50);
	}

	double dx = desiredLocation.x - currLoc.x;
	double dy = desiredLocation.y - currLoc.y;
	double temp = /* Strict */Math.sqrt(dx * dx + dy * dy);
	if (temp < 1) {
	    steps = 0;
	} else {
	    dx /= temp;
	    dy /= temp;
	}

	if (!socSim.acceptablePosition(this, new Double2D(currLoc.x + dx, currLoc.y + dy))) {
	    steps = 0;
	} else {
	    currLoc = new Double2D(currLoc.x + dx, currLoc.y + dy);
	}

	return currLoc;
    }

}
