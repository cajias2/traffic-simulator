/**
 * 
 */
package sim.agents;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

import sim.app.networktest.NetworkTest;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.mason.AgentNetwork;
import sim.portrayal.DrawInfo2D;
import sim.util.Bag;
import sim.util.Double2D;
import ec.util.MersenneTwisterFast;

/**
 * @author biggie
 * 
 */
public class Agent implements Steppable {

    public Font nodeFont = new Font("SansSerif", Font.PLAIN, 9);
    private int steps = 0;
    private Double2D desiredLocation = null;
    protected MersenneTwisterFast _rand = null;
    protected AgentNetwork _net;
    private static int _agentCount = 0;



    protected int _id;
    protected int _actionDim = 0;

    public Agent(final SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	_net = socSim.network;
	_rand = socSim.random;
	_id = _agentCount;
	_agentCount++;
    }

    /**
     * 
     */
    public void step(final SimState state_) {
	
	SocialSim socSim = (SocialSim) state_;
	beforeStep(socSim);
	Double2D currLoc = socSim.fieldEnvironment.getObjectLocation(this);
	Bag objs = socSim.fieldEnvironment.getObjectsExactlyWithinDistance(new Double2D(currLoc.x, currLoc.y),
		_actionDim);

	Iterator<Agent> iter = objs.iterator();
	while (iter.hasNext()) {
	    Agent ag = iter.next();
	    // make sure not the same obj, and an edge does not already exist.
	    if (this != ag){
		interactWithAgent(ag);
	    }
	}

	Double2D newLoc = move(state_);
	socSim.fieldEnvironment.setObjectLocation(this, newLoc);
	afterStep(socSim);
    }

    /**
     * @author biggie
     * @name interactWithAgent Purpose TODO
     * 
     * @param
     * @return void
     */
    protected void interactWithAgent(Agent ag_) {
    }

    /**
     * @author biggie
     * @name afterStep Purpose: Hook method to prepare child agents for step
     * 
     * @param SimState
     *            The state of the simulation
     * @return void
     */
    protected void afterStep(SocialSim state_) {
    }

    /**
     * @author biggie
     * @name beforeStep Purpose:Hook method to perform actions after step
     * 
     * @param SimState
     *            The state of the simulation
     * @return void
     */
    protected void beforeStep(SocialSim state_) {
    }

    /**
     * @author biggie
     * @name setRandomEngine Purpose TODO
     * 
     * @param
     * @return void
     */
    protected void setRandomEngine(MersenneTwisterFast random_) {
	_rand = random_;
    }

    /**
     * @return
     */
    protected boolean isNewFriend(Agent ag_) {
	return false;
    }

    /**
     * 
     * @author biggie
     * @name move Purpose TODO
     * 
     * @param
     * @return Double2D
     */
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

    /**
     * @param object
     * @param graphics
     * @param info
     */
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
	double diamx = info.draw.width * SocialSim.DIAMETER;
	double diamy = info.draw.height * SocialSim.DIAMETER;

	graphics.setColor(Color.red);
	graphics.fillOval((int) (info.draw.x - diamx / 2), (int) (info.draw.y - diamy / 2), (int) (diamx),
		(int) (diamy));
	graphics.drawOval((int) (info.draw.x - diamx / 2), (int) (info.draw.y - diamy / 2),
		(int) (diamx + _actionDim / 2), (int) (diamy + _actionDim / 2));
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

    public final int getID() {
	return _id;
    }

    /**
     * 
     * @author biggie
     * @name befriend Purpose TODO
     * 
     * @param
     * @return void
     */
    protected void befriend(Agent ag_) {
	_net.addEdge(this, ag_, new Integer(1));
    }

    /**
     * @param ag_
     */
    protected void unfriend(Agent ag_) {
	_net.removeEdge(this, ag_);
    }
}
