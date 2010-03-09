/**
 * 
 */
package sim.app.geography;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import sim.app.agents.Vehicle;
import sim.app.geography.distance.Distance;

/**
 * @author biggie
 * 
 */
public abstract class Road {
    private static final double LAYER_SEG = 10;

    private final String ID;

    private static PApplet _parent;

    private List<Line2D> _lineList;
    private double _roadLength = 0;
    private List<Double> _velocityLayer;
    private LinkedList<List<Line2D>> _segmentList;
    private List<Vehicle> _vehiclesOnRoad;

    public abstract Distance getMaxVelocity();

    /**
     * Constructor
     */
    public Road(String id_, List<Point2D> pointList_, PApplet parent_) {
	if (_parent == null) {
	    _parent = parent_;
	}
	ID = id_;
	_vehiclesOnRoad = new LinkedList<Vehicle>();
	createRoad(pointList_);
	// Use total street length to determine layer size.
	_velocityLayer = new ArrayList<Double>((int) Math.ceil(_roadLength
		/ LAYER_SEG));
	processRoadSegments();
    }

    /**
     * 
     */
    private void processRoadSegments() {
	/*
	 * Create a a list of line segments for display
	 */
	_segmentList = new LinkedList<List<Line2D>>();

	for (Line2D currLine : _lineList) {
	    double slope = (currLine.getY2() - currLine.getY1())
		    / (currLine.getX2() - currLine.getX1());
	    double slopeDeg = Math.atan(slope);
	    double lineLength = currLine.getP1().distance(currLine.getP2());
	    double runLen = Math.abs(Math.cos(slopeDeg) * LAYER_SEG);
	    // Select direction to move in.
	    int direction = 1;
	    if (currLine.getX1() > currLine.getX2())
		direction = -direction;

	    // Divide the line into segments and store them in a map
	    _segmentList.add(new LinkedList<Line2D>());
	    int totalSegments = (int) Math.ceil(lineLength / LAYER_SEG);

	    Point2D segOrig = null;
	    Point2D segDest = null;

	    for (int i = 0; i < totalSegments; i++) {
		if (segOrig == null) { // If this is the first round...
		    segOrig = currLine.getP1();
		} else {
		    segOrig = segDest;
		}
		if ((i + 1) >= totalSegments) { // Last Round
		    segDest = currLine.getP2();
		} else {
		    // Do some euclidean magic to calculate next segment.
		    // TODO can this be optimized?
		    Point2D secantOrig = new Point2D.Double(segOrig.getX()
			    + direction * runLen, segOrig.getY());
		    Point2D secantDest = new Point2D.Double(secantOrig.getX(),
			    secantOrig.getY() + 1);
		    Line2D secant = new Line2D.Double(secantOrig, secantDest);
		    segDest = intersectionPoint(currLine, secant);

		    if (segDest == null)
			throw new RuntimeException(
				"Intersection should not be null");
		}
		Line2D segment = new Line2D.Double(segOrig, segDest);
		_segmentList.getLast().add(segment);
	    }
	}
    }

    /**
     * @param pointList_
     */
    private void createRoad(List<Point2D> pointList_) {
	_lineList = new LinkedList<Line2D>();
	/*
	 * Create the segments based on point list.
	 */
	Point2D orig = null, dest = null;
	for (Point2D current : pointList_) {
	    dest = current;
	    if (orig == null) { // handle base case
		orig = current;
	    } else {
		_lineList.add(new Line2D.Float(orig, dest));
		_roadLength += orig.distance(dest);
		orig = dest;
	    }
	}
    }

    /**
     * @return the _roadLength
     */
    public double getRoadLength() {
	return _roadLength;
    }

    /**
     * TODO javadoc
     */
    public void display() {
	_parent.noFill();
	// _parent.noStroke();
	_parent.smooth();

	for (Line2D currSeg : _lineList) {
	    _parent.stroke(0);
	    _parent.strokeWeight(1);
	    _parent.line((float) currSeg.getP1().getX(), (float) currSeg
		    .getP1().getY(), (float) currSeg.getP2().getX(),
		    (float) currSeg.getP2().getY());
	}
	for (List<Line2D> line : _segmentList) {
	    Iterator<Line2D> iter = line.iterator();
	    float color = 1;

	    while (iter.hasNext()) {
		Line2D seg = (Line2D) iter.next();
		if (color >= 10)
		    color = 1;
		_parent.stroke(255);
		_parent.strokeWeight(color);
		_parent.point((float) seg.getX1(), (float) seg.getY1());
		_parent.point((float) seg.getX2(), (float) seg.getY2());
		color += 0.1;
	    }
	}
    }

    /**
     * TODO javadoc
     * 
     * @param line1_
     * @param line2_
     * @return
     */
    private static Point2D intersectionPoint(Line2D line1_, Line2D line2_) {
	Point2D p = null;
	double x1 = line1_.getX1();
	double y1 = line1_.getY1();
	double x2 = line1_.getX2();
	double y2 = line1_.getY2();

	double x3 = line2_.getX1();
	double y3 = line2_.getY1();
	double x4 = line2_.getX2();
	double y4 = line2_.getY2();

	double det = (x1 - x2) * (y3 - y4) - (y1 * y2) * (x3 - x4);
	double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2)
		* (x3 * y4 - y3 * x4))
		/ det;
	double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2)
		* (x3 * y4 - y3 * x4))
		/ det;
	p = new Point2D.Double(px, py);
	return p;
    }

    /**
     * @return the _carsOnRoad
     */
    public List<Vehicle> getVehiclesOnRoad() {
	return _vehiclesOnRoad;
    }

    /**
     * Class toString
     */
    public String toString() {
	return ID + "__" + _vehiclesOnRoad.size();
    }
}
