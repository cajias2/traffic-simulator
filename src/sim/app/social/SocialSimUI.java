/*
  Copyright 2006 by Daniel Kuebrich
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package sim.app.social;
import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JFrame;

import sim.agents.Agent;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.utils.xml.social.SocialInputParseService;

public class SocialSimUI extends GUIState {
    private static Logger _log = Logger.getLogger("SimUILogger");
    public Display2D display;
    public JFrame displayFrame;

    NetworkPortrayal2D edgePortrayal = new NetworkPortrayal2D();
    ContinuousPortrayal2D socPortrayal = new ContinuousPortrayal2D();

    public static void main(String[] args) {
	String simXml = SocialInputParseService.parseCmdLnArgs(args, _log);
	SocialSimUI socsimUI = new SocialSimUI(simXml);
	Console c = new Console(socsimUI);
	c.setVisible(true);
    }

    public SocialSimUI(String simXml_) {
	super(new SocialSim(System.currentTimeMillis(), simXml_));
    }

    public SocialSimUI(SimState state) {
	super(state);
    }

    public static String getName() {
	return "Social Sim";
    }

    @Override
    public void start() {
	super.start();
	// set up our portrayals
	setupPortrayals();
    }

    @Override
    public void load(SimState state) {
	super.load(state);
	// we now have new grids. Set up the portrayals to reflect that
	setupPortrayals();
    }

    public void setupPortrayals() {
	edgePortrayal.setField(new SpatialNetwork2D(((SocialSim) state).fieldEnvironment, ((SocialSim) state).network));
	SimpleEdgePortrayal2D p = new SimpleEdgePortrayal2D(Color.lightGray, Color.lightGray, Color.black);
	p.setShape(SimpleEdgePortrayal2D.SHAPE_LINE);
	p.setBaseWidth(2);
	edgePortrayal.setPortrayalForAll(p);

	// tell the portrayals what to portray and how to portray them
	socPortrayal.setField(((SocialSim) state).fieldEnvironment);
	socPortrayal.setPortrayalForClass(Agent.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.red));
	// reschedule the displayer
	display.reset();

	// redraw the display
	display.repaint();
    }

    @Override
    public void init(Controller c) {
	super.init(c);

	// Make the Display2D. We'll have it display stuff later.
	display = new Display2D(800, 800, this, 1); // at 400x400, we've got 4x4
						    // per array position
	displayFrame = display.createFrame();
	c.registerFrame(displayFrame); // register the frame so it appears in
				       // the "Display" list
	displayFrame.setVisible(true);

        // attach the portrayals
	display.attach(socPortrayal, "Agents");
	display.attach(edgePortrayal, "Edges");

        // specify the backdrop color -- what gets painted behind the displays
	display.setBackdrop(new Color(0, 80, 0)); // a dark green
    }

    @Override
    public void quit() {
	super.quit();

	if (displayFrame != null)
	    displayFrame.dispose();
	displayFrame = null; // let gc
	display = null; // let gc
    }
}
