/**
 * 
 */
package social.agents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import sim.agents.Agent;
import sim.app.networktest.NetworkTest;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;

/**
 * @author biggie
 *
 */
public class RandomAgent extends Agent {

    private static int COUNT = 0;
    private final int ID;

    public RandomAgent() {
	super();
	ID = COUNT;
	COUNT++;
    }

    public final int getID() {
	return ID;
    }

    Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    int steps = 0;

    /**
     * 	
     */
    public void step(final SimState state) {
	SocialSim nt = (SocialSim) state;
	Double2D location = nt.environment.getObjectLocation(this);

	steps--;
	if (desiredLocation == null || steps <= 0) {
	    desiredLocation = new Double2D((state.random.nextDouble() - 0.5)
		    * ((NetworkTest.XMAX - NetworkTest.XMIN) / 5 - NetworkTest.DIAMETER) + location.x,
		    (state.random.nextDouble() - 0.5)
			    * ((NetworkTest.YMAX - NetworkTest.YMIN) / 5 - NetworkTest.DIAMETER) + location.y);
	    steps = 50 + state.random.nextInt(50);
	}

	double dx = desiredLocation.x - location.x;
	double dy = desiredLocation.y - location.y;

	{
	    double temp = /* Strict */Math.sqrt(dx * dx + dy * dy);
	    if (temp < 1) {
		steps = 0;
	    } else {
		dx /= temp;
		dy /= temp;
	    }
	}

	if (!nt.acceptablePosition(this, new Double2D(location.x + dx, location.y + dy))) {
	    steps = 0;
	} else {
	    nt.environment.setObjectLocation(this, new Double2D(location.x + dx, location.y + dy));
	}

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
	graphics.drawString(id, (int) (info.draw.x - diamx / 2), (int) (info.draw.y - diamy / 2));
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


    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#move(sim.engine.SimState)
     */
     @Override
    public void move(SimState state_) {
	// TODO Auto-generated method stub

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
    public boolean makeFriend(SimState state_) {
	// TODO Auto-generated method stub
	return false;
    }

}
