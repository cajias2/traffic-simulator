/**
 * 
 */
package sim.app.geo;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import sim.app.agents.Vehicle;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import sim.app.geo.distance.Meters;

/**
 * @author biggie
 * 
 */
public abstract class Road {
    private static final Distance LAYER_SEG = new Meters(100);

    private static final double DISTANCE_THRESHOLD = 1;

    public final String ID;

    private static PApplet _parent;

    private List<Line2D> _lineList;
    private double _realLength = 0;
    private List<Double> _velocityLayer;
    private LinkedList<List<Line2D>> _segmentList;
    private List<Vehicle> _vehiclesOnRoad;

    private Distance _geoLength;

    public abstract Distance getMaxVelocity();

    protected abstract int getStrokeWeight();

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
	_velocityLayer = new ArrayList<Double>((int) Math.ceil(_realLength
		/ LAYER_SEG.getVal()));
	processRoadSegments();
    }

    /**
     * Create a a list of line segments for disaq1play
     */
    private void processRoadSegments() {
	_segmentList = new LinkedList<List<Line2D>>();

	for (Line2D currLine : _lineList) {
	    double lineLength = currLine.getP1().distance(currLine.getP2());

	    // Divide the line into segments and store them in a map
	    _segmentList.add(new LinkedList<Line2D>());
	    int totalSegments = (int) Math
		    .ceil(lineLength / LAYER_SEG.getVal());
	    findRoadSegment(currLine, totalSegments);
	}
    }

    /**
     * Helper method that breaks a line into smaller subsegments.
     * 
     * @param line_
     * @param totalSegments_
     * @return
     */
    private void findRoadSegment(Line2D line_, int totalSegments_) {

	// Select direction to move in.
	boolean dir = true;
	if (line_.getX1() > line_.getX2())
	    dir = false;
	Point2D segOrig = null;
	Point2D segDest = null;

	for (int i = 0; i < totalSegments_; i++) {
	    if (segOrig == null) { // If this is the first round...
		segOrig = line_.getP1();
	    } else {
		segOrig = segDest;
	    }
	    if ((i + 1) >= totalSegments_) { // Last Round
		segDest = line_.getP2();
	    } else {
		segDest = getNewLocation(segOrig, line_, LAYER_SEG.getVal(), dir);
	    }
	    _segmentList.getLast().add(new Line2D.Double(segOrig, segDest));
	}
    }

    /**
     * Determines next location, based on current location and how much to move.
     * Not as efficient as overloaded version where the current line is also 
     * known.  
     * @param curLoc_
     * @param len_
     * @param dir_
     * @return
     */
    public Point2D getNewLocation(Point2D curLoc_, double len_, boolean dir_) {
	Line2D line = getLine(curLoc_);
	return getNewLocation(curLoc_, line, len_, dir_);
    }
    private Line2D getLine(Point2D curLoc) {
	Line2D currLine = null;
	
	for(Line2D line : _lineList)
	{
	    if(line.ptLineDist(curLoc) < DISTANCE_THRESHOLD){
		currLine = line;
		break;
	    }
	}
	
	return currLine;
    }

    /**
     * Given a line, find a secant line that intersects it at length len_ 
     * 
     * @param curLoc_
     * @param len_
     * @param dir_
     * @return
     */
    public Point2D getNewLocation(Point2D curLoc_, Line2D line_, double len_, boolean dir_) {
	Point2D newLoc_ = null;
	
	double slope = (line_.getY2() - line_.getY1())
		/ (line_.getX2() - line_.getX1());
	double slopeDeg = Math.atan(slope);
	double runLen = Math.abs(Math.cos(slopeDeg) *len_);
	
	int direction = 1;
	if (!dir_) {
	    direction = -1;
	}
	Point2D secantOrig = new Point2D.Double(curLoc_.getX() + direction
		* runLen, curLoc_.getY());
	Point2D secantDest = new Point2D.Double(secantOrig.getX(),
		secantOrig.getY() + 1);	
	Line2D secant = new Line2D.Double(secantOrig, secantDest);
	newLoc_ = intersectionPoint(line_, secant);

	if (newLoc_ == null)
	    throw new RuntimeException(
		    "Intersection should not be null");
	return newLoc_;
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
		_realLength += orig.distance(dest);
		orig = dest;
	    }
	}
	_geoLength = new Kilometers(_realLength);
	System.out.println("Pixel Lenght:" + _realLength);
	System.out.println("Scale Length: " + _geoLength);
    }

    /**
     * @return the _roadLength
     */
    public double getRoadLength() {
	return _realLength;
    }

    /**
     * Display the road.
     */
    public void display() {
	_parent.noFill();
	_parent.smooth();

	/*
	 * Display the road by going though each line in the list that comprises
	 * it and displaying it individually.
	 */
	for (Line2D currSeg : _lineList) {
	    _parent.stroke(0);
	    _parent.strokeWeight(getStrokeWeight());
	    _parent.line((float) currSeg.getP1().getX(), (float) currSeg
		    .getP1().getY(), (float) currSeg.getP2().getX(),
		    (float) currSeg.getP2().getY());
	}
	/*
	 * Paint each line from _segment list on top of the street we just
	 * painted. These segments will vary in color/weight depending on the
	 * cars cruising it.
	 */
	// TODO
	// for(Vehicle vhcl: _vehiclesOnRoad)
	// {
	// if(vhcl.)
	// }
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

    public Point2D startLoc() {
	return _lineList.get(0).getP1();
    }

    public Point2D endLoc() {
	return _lineList.get(_lineList.size() - 1).getP2();
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
