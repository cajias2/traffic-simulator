/*
  Copyright 2006 by 
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package sim.app.social;
import java.awt.Color;
import java.util.Map;
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

    public SocialSimUI(Map<String, String> argMap_) {
	super(new SocialSimBatchRunner(System.currentTimeMillis(), argMap_));
    }

    public SocialSimUI(SimState state) {
	super(state);
    }

    public static String getName() {
	return "Social Sim";
    }

    public void setupPortrayals() {
	edgePortrayal.setField(new SpatialNetwork2D(((SocialSimBatchRunner) state).env, Agent.getTestNet()));
        SimpleEdgePortrayal2D p = new SimpleEdgePortrayal2D(Color.lightGray, Color.lightGray, Color.black);
        p.setShape(SimpleEdgePortrayal2D.SHAPE_LINE);
        p.setBaseWidth(2);
        edgePortrayal.setPortrayalForAll(p);
    
        // tell the portrayals what to portray and how to portray them
	socPortrayal.setField(((SocialSimBatchRunner) state).env);
        socPortrayal.setPortrayalForClass(Agent.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.red));
        // reschedule the displayer
        display.reset();
    
        // redraw the display
        display.repaint();
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
	display.attach(edgePortrayal, "");

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

    public static void main(String[] args) {
	Map<String, String> simArgs = SocialInputParseService.parseCmdLnArgs(args, _log);
	SocialSimUI socsimUI = new SocialSimUI(simArgs);
        Console c = new Console(socsimUI);
        c.setVisible(true);
    }
}
