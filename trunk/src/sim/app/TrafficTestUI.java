package sim.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import sim.app.utils.JungDisplay;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.portrayal.LocationWrapper;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.util.Bag;
import sim.util.Double2D;

public class TrafficTestUI extends GUIState {

	public JungDisplay jDisplay;


	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TrafficTestUI vid = new TrafficTestUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}

	/**
	 * 
	 */
	public TrafficTestUI() {
		super(new TrafficTest(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param state
	 */
	public TrafficTestUI(SimState state) {
		super(state);
	}

	/**
	 * 
	 * @return
	 */
	public static String getName() {
		return "Network Test";
	}

	/**
	 * 
	 */
	public void start() {
		super.start();
		setupPortrayals();
	}

	/**
	 * 
	 */
	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {

		// Reset displays.
		jDisplay.reset();

	}

	public void init(Controller c) {
		super.init(c);

		// Instantiate JungDisplay
		jDisplay = new JungDisplay(this);
		jDisplay.frame.setTitle("Preferential attachment graph");
		c.registerFrame(jDisplay.frame);
		jDisplay.frame.setVisible(true);
		// // //////// BEGIN OPTIONAL MOVEMENT CODE
		//
		// // In this code we're showing how to augment MASON to enable moving
		// // objects around with
		// // the mouse. The general idea is: when we click on a location, we
		// // gather all the objects
		// // at that location and find the one we want to move around. Then as we
		// // drag, we query
		// // MASON for the location in the field that would be equivalent to where
		// // the mouse is located,
		// // then set the object to that location and redraw everything.
		//
		// // We augment that simple approach with a little bit of niftyness in the
		// // form of 'nodeLocDelta'.
		// // What we do is: when the user depressed the mouse on the the object we
		// // compute the difference
		// // in where he clicked the mouse and the actual origin of the object. As
		// // the user drags around,
		// // we always add this difference before setting the object. This makes
		// // it appear that the user
		// // can drag the object from any spot on the object -- otherwise the
		// // object would "pop" to
		// // center itself at the mouse cursor, which doesn't feel quite right
		// // drag-and-drop-wise.
		//
		// // We also perform Selection of the object we've just dragged, for good
		// // measure.
		//
		// // A somewhat simpler example is show in HeatBugsWithUI.java, which
		// // doesn't use
		// // a nodeLocDelta and also doesn't bother to perform selection.
		//
		// // add mouse motion listener
		// MouseInputAdapter adapter = new MouseInputAdapter() {
		// Object node = null; // the object we're dragging
		// LocationWrapper nodeWrapper = null; // the wrapper for the object --
		// // useful for selection
		// Double2D nodeLocDelta = null; // our computed difference to be nifty
		//
		// // figure out what object we clicked on (if any) and what the
		// // computed difference is.
		// @Override
		// public void mousePressed(MouseEvent e) {
		// final Point point = e.getPoint();
		// Continuous2D field = (Continuous2D) (nodePortrayal.getField());
		// if (field == null)
		// return;
		// node = null;
		//
		// // go through all the objects at the clicked point. The
		// // objectsHitBy method
		// // doesn't return objects: it returns LocationWrappers. You can
		// // extract the object
		// // by calling getObject() on the LocationWrapper.
		//
		// Rectangle2D.Double rect = new Rectangle2D.Double(point.x,
		// point.y, 1, 1);
		//
		// Bag hit = new Bag();
		// nodePortrayal.hitObjects(jDisplay.getDrawInfo2D(nodePortrayal,
		// rect), hit);
		// if (hit.numObjs > 0) {
		// nodeWrapper = ((LocationWrapper) hit.objs[hit.numObjs - 1]); // grab
		// // the
		// // topmost
		// // one
		// // from
		// // the
		// // user's
		// // perspective
		// node = nodeWrapper.getObject();
		// jDisplay.performSelection(nodeWrapper);
		//
		// Double2D nodeLoc = (field.getObjectLocation(node)); // where
		// // the
		// // node
		// // is
		// // actually
		// // located
		// //System.out.println("Node location: "+ nodeLoc.x+"," +nodeLoc.y);
		// Double2D mouseLoc = nodePortrayal.getLocation(jDisplay
		// .getDrawInfo2D(nodePortrayal, point)); // where the
		// // mouse
		// // clicked
		// nodeLocDelta = new Double2D(nodeLoc.x - mouseLoc.x,
		// nodeLoc.y - mouseLoc.y);
		// }
		//
		// c.refresh(); // get the other displays and inspectors to update
		// // their locations
		// // we need to refresh here only in order to display that the
		// // node is now selected
		// // btw: c must be final.
		// }
		//
		// @Override
		// public void mouseReleased(MouseEvent e) {
		// node = null;
		// }
		//
		// // We move the node in our Field, adding in the computed difference
		// // as necessary
		// @Override
		// public void mouseDragged(MouseEvent e) {
		// final Point point = e.getPoint();
		// Continuous2D field = (Continuous2D) (nodePortrayal.getField());
		// if (node == null || field == null)
		// return;
		//
		// Double2D mouseLoc = nodePortrayal.getLocation(jDisplay
		// .getDrawInfo2D(nodePortrayal, point)); // where the
		// // mouse dragged
		// // to
		// Double2D newBallLoc = new Double2D(nodeLocDelta.x + mouseLoc.x,
		// nodeLocDelta.y + mouseLoc.y); // add in computed
		// // difference
		// field.setObjectLocation(node, newBallLoc);
		// c.refresh(); // get the other displays and inspectors to update
		// // their locations
		// // btw: c must be final.
		// }
		// };
		//
		// // We then attach our listener to the "INSIDE DISPLAY" that's part of
		// // the Display2D. The insideDisplay
		// // is the object inside the scrollview which does the actual drawing.
		// jDisplay.insideDisplay.addMouseListener(adapter);
		// jDisplay.insideDisplay.addMouseMotionListener(adapter);
		//
		// // //////// END MOVEMENT CODE
		//
		// }		
	}

	// public void init(final Controller c) {
	// super.init(c);
	//
	// // make the displayer
	// jDisplay = new Display2D(TrafficTest.XMAX, TrafficTest.YMAX, this, 1);
	//
	// displayFrame = jDisplay.createFrame();
	// displayFrame.setTitle("Traffic Test Display");
	// c.registerFrame(displayFrame); // register the frame so it appears in
	// // the "Display" list
	// displayFrame.setVisible(true);
	// jDisplay.attach(edgePortrayal, "Edges");
	// jDisplay.attach(nodePortrayal, "Nodes");
	//
	// // //////// BEGIN OPTIONAL MOVEMENT CODE
	//
	// // In this code we're showing how to augment MASON to enable moving
	// // objects around with
	// // the mouse. The general idea is: when we click on a location, we
	// // gather all the objects
	// // at that location and find the one we want to move around. Then as we
	// // drag, we query
	// // MASON for the location in the field that would be equivalent to where
	// // the mouse is located,
	// // then set the object to that location and redraw everything.
	//
	// // We augment that simple approach with a little bit of niftyness in the
	// // form of 'nodeLocDelta'.
	// // What we do is: when the user depressed the mouse on the the object we
	// // compute the difference
	// // in where he clicked the mouse and the actual origin of the object. As
	// // the user drags around,
	// // we always add this difference before setting the object. This makes
	// // it appear that the user
	// // can drag the object from any spot on the object -- otherwise the
	// // object would "pop" to
	// // center itself at the mouse cursor, which doesn't feel quite right
	// // drag-and-drop-wise.
	//
	// // We also perform Selection of the object we've just dragged, for good
	// // measure.
	//
	// // A somewhat simpler example is show in HeatBugsWithUI.java, which
	// // doesn't use
	// // a nodeLocDelta and also doesn't bother to perform selection.
	//
	// // add mouse motion listener
	// MouseInputAdapter adapter = new MouseInputAdapter() {
	// Object node = null; // the object we're dragging
	// LocationWrapper nodeWrapper = null; // the wrapper for the object --
	// // useful for selection
	// Double2D nodeLocDelta = null; // our computed difference to be nifty
	//
	// // figure out what object we clicked on (if any) and what the
	// // computed difference is.
	// @Override
	// public void mousePressed(MouseEvent e) {
	// final Point point = e.getPoint();
	// Continuous2D field = (Continuous2D) (nodePortrayal.getField());
	// if (field == null)
	// return;
	// node = null;
	//
	// // go through all the objects at the clicked point. The
	// // objectsHitBy method
	// // doesn't return objects: it returns LocationWrappers. You can
	// // extract the object
	// // by calling getObject() on the LocationWrapper.
	//
	// Rectangle2D.Double rect = new Rectangle2D.Double(point.x,
	// point.y, 1, 1);
	//
	// Bag hit = new Bag();
	// nodePortrayal.hitObjects(jDisplay.getDrawInfo2D(nodePortrayal,
	// rect), hit);
	// if (hit.numObjs > 0) {
	// nodeWrapper = ((LocationWrapper) hit.objs[hit.numObjs - 1]); // grab
	// // the
	// // topmost
	// // one
	// // from
	// // the
	// // user's
	// // perspective
	// node = nodeWrapper.getObject();
	// jDisplay.performSelection(nodeWrapper);
	//
	// Double2D nodeLoc = (field.getObjectLocation(node)); // where
	// // the
	// // node
	// // is
	// // actually
	// // located
	// //System.out.println("Node location: "+ nodeLoc.x+"," +nodeLoc.y);
	// Double2D mouseLoc = nodePortrayal.getLocation(jDisplay
	// .getDrawInfo2D(nodePortrayal, point)); // where the
	// // mouse
	// // clicked
	// nodeLocDelta = new Double2D(nodeLoc.x - mouseLoc.x,
	// nodeLoc.y - mouseLoc.y);
	// }
	//
	// c.refresh(); // get the other displays and inspectors to update
	// // their locations
	// // we need to refresh here only in order to display that the
	// // node is now selected
	// // btw: c must be final.
	// }
	//
	// @Override
	// public void mouseReleased(MouseEvent e) {
	// node = null;
	// }
	//
	// // We move the node in our Field, adding in the computed difference
	// // as necessary
	// @Override
	// public void mouseDragged(MouseEvent e) {
	// final Point point = e.getPoint();
	// Continuous2D field = (Continuous2D) (nodePortrayal.getField());
	// if (node == null || field == null)
	// return;
	//
	// Double2D mouseLoc = nodePortrayal.getLocation(jDisplay
	// .getDrawInfo2D(nodePortrayal, point)); // where the
	// // mouse dragged
	// // to
	// Double2D newBallLoc = new Double2D(nodeLocDelta.x + mouseLoc.x,
	// nodeLocDelta.y + mouseLoc.y); // add in computed
	// // difference
	// field.setObjectLocation(node, newBallLoc);
	// c.refresh(); // get the other displays and inspectors to update
	// // their locations
	// // btw: c must be final.
	// }
	// };
	//
	// // We then attach our listener to the "INSIDE DISPLAY" that's part of
	// // the Display2D. The insideDisplay
	// // is the object inside the scrollview which does the actual drawing.
	// jDisplay.insideDisplay.addMouseListener(adapter);
	// jDisplay.insideDisplay.addMouseMotionListener(adapter);
	//
	// // //////// END MOVEMENT CODE
	//
	// }

	public void quit() {
		super.quit();

		if (jDisplay.frame != null)
			jDisplay.frame.dispose();
		jDisplay.frame = null;
		jDisplay = null;
	}
}