/**
 * 
 */
package sim.graph.traffic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import processing.core.PApplet;
import sim.agents.traffic.TLState;
import sim.agents.traffic.vhcl.Vehicle;
import sim.geo.distance.Distance;
import sim.geo.distance.Kilometers;
import sim.geo.distance.Meters;
import sim.util.Double2D;
import sim.utils.Orientation;
import sim.utils.TrafficLightState;

/**
 * @author biggie
 */
public abstract class Road {
    public static final Distance LAYER_SEG = new Meters(10);

    private static PApplet _parent;
    private List<Line2D> _lineList;
    private double _realLength = 0;
    private LinkedList<List<Line2D>> _segmentList;
    private HashMap<Line2D, List<Vehicle>> _vehicleOnSeg;
    private List<Road> _subRoads;
    private final List<Vehicle> _vehiclesOnRoad;
    private Distance _geoLength;
    private TLState _tf;
    private Orientation _or;

    /*
     * If something is this far way from something else, they are considered to
     * be in the same place.
     */
    public static final double DISTANCE_THRESHOLD = 0.001;

    public final String ID;

    private Double _avgSpeed = 1000.0;

    private int _numOfSegs;
    private static int _roadCount = 0;

    protected abstract int getStrokeWeight();

    abstract public int getMaxVhclSeg();

    /**
     * Constructor, Processing
     */
    public Road(String id_, List<Point2D> pointList_, PApplet parent_) {
	if (_parent == null) {
	    _parent = parent_;
	}
	ID = id_;
	_roadCount++;
	_vehiclesOnRoad = new LinkedList<Vehicle>();
	createRoad(pointList_);
    }

    /**
     * Constructor, Mason
     */
    public Road(String id_, List<Point2D> pointList_) {
	this(id_, pointList_, null);
    }

    /**
     * @param intersecList_
     */
    public void setSubRoad(List<Road> rdList_) {
	_subRoads = rdList_;
    }

    public Road getSubRoad(int idx_) {
	return _subRoads.get(idx_);
    }

    public List<Road> getSubRoadList() {
	return _subRoads;
    }

    /**
     * Get a list of the lines that make up this road
     * 
     * @return
     */
    public List<Line2D> getLineList() {
	return _lineList;
    }

    /**
     * @return the _segmentList
     */
    public LinkedList<List<Line2D>> getSegmentList() {
	return _segmentList;
    }

    public Orientation getOr() {
	return _or;
    }

    public void setOr(Orientation or_) {
	_or = or_;
    }

    public abstract Distance getMaxVelocity();

    /**
     * @return the _roadLength
     */
    public double getRoadLength() {
	return _realLength;
    }

    /**
     * Determines next location, based on current location and how much to move.
     * Not as efficient as overloaded version where the current line is also
     * known.
     * 
     * @param curLoc_
     * @param len_
     * @param dir_
     * @return
     */
    public Double2D getNewLocation(Double2D curLoc_, Double len_) {
	Line2D line = getLine(curLoc_);
	Point2D newLoc2D = getNewLocation(curLoc_, line, len_);
	Double2D newLoc = new Double2D(newLoc2D.getX(), newLoc2D.getY());
	return newLoc;
    }

    /**
     * 
     * Wrapper methods that changes Double2D to Point2D where all the point/line
     * logic has already been implemented.
     * 
     * @params
     * @return Point2D
     * @author biggie
     */
    private Point2D getNewLocation(Double2D currLoc_, Line2D l_, double len_) {
	return getNewLocation(new Point2D.Double(currLoc_.x, currLoc_.y), l_, len_);
    }
    /**
     * Given a line, find a secant line that intersects it at length len_
     * 
     * @param curLoc_
     * @param len_
     * @param dir_
     * @return
     */
    private Point2D getNewLocation(Point2D curLoc_, Line2D l_, double len_) {
	// Select direction to move in.
	int[] dir = { 1, 1 };
	if (l_.getX1() > l_.getX2())
	    dir[0] = -dir[0];
	if (l_.getY1() > l_.getY2())
	    dir[1] = -dir[1];

	Point2D newLoc_ = null;

	double slope = (l_.getY2() - l_.getY1()) / (l_.getX2() - l_.getX1());
	double slopeDeg = Math.atan(slope);
	double runLen = Math.abs(Math.cos(slopeDeg) * len_);

	Point2D secP1;
	Point2D secP2;
	Line2D secant;
	if (l_.getX2() - l_.getX1() == 0) {
	    secP1 = new Point2D.Double(curLoc_.getX() - 10, curLoc_.getY() + len_ * dir[1]);
	    secP2 = new Point2D.Double(secP1.getX() + 10, curLoc_.getY() + len_ * dir[1]);
	    secant = new Line2D.Double(secP1, secP2);
	} else {
	    secP1 = new Point2D.Double(curLoc_.getX() + dir[0] * runLen, curLoc_.getY());
	    secP2 = new Point2D.Double(secP1.getX(), secP1.getY() + 10 * dir[1]);
	    secant = new Line2D.Double(secP1, secP2);
	}
	newLoc_ = findIntersection(l_.getP1(), l_.getP2(), secant.getP1(), secant.getP2());

	// Am I over the boundary of the line?
	if (null == newLoc_) {
	    int nextLineIdx = _lineList.indexOf(l_) + 1;
	    if (nextLineIdx < _lineList.size()) {
		Line2D nextLine = _lineList.get(nextLineIdx);
		newLoc_ = getNewLocation(nextLine.getP1(), nextLine, len_ - (curLoc_.distance(nextLine.getP1())));
	    } else {
		newLoc_ = curLoc_;
	    }
	}

	if (newLoc_ == null)
	    throw new RuntimeException("Intersection should not be null");
	return newLoc_;
    }

    /**
     * @return the _tf
     */
    public TrafficLightState getTf() {
	if(_tf != null)
	{
	    return _tf.getState(); 
	}
	return null;
	
	
    }

    /**
     * @param tf
     *            the _tf to set
     */
    public void setTL(TLState tf) {
	_tf = tf;
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
	if (null == _subRoads || _subRoads.isEmpty()) {
	    for (Line2D currSeg : _lineList) {
		_parent.stroke(0);
		_parent.strokeWeight(getStrokeWeight());
		_parent.line((float) currSeg.getP1().getX(), (float) currSeg.getP1().getY(), (float) currSeg.getP2()
			.getX(), (float) currSeg.getP2().getY());
	    }
	} else {
	    for (Road subR : _subRoads) {
		subR.display();
	    }
	}
	for (Entry<Line2D, List<Vehicle>> e : _vehicleOnSeg.entrySet()) {

	    Line2D seg = e.getKey();
	    List<Vehicle> vList = e.getValue();
	    _parent.stroke(255);
	    _parent.strokeWeight(vList.size());
	    _parent.line((float) seg.getX1(), (float) seg.getY1(), (float) seg.getX2(), (float) seg.getY2());
	}
    }

    /**
     * Update road
     */
    public void update() {
	for (Vehicle v : _vehiclesOnRoad) {
	    Line2D currLine = v.getRoadLine();
	    Point2D pt = new Point2D.Double(v.getLocation().x, v.getLocation().y);
	    int segIdx = (int) Math.ceil((currLine.getP1().distance(pt) / LAYER_SEG.getVal()));
	    Line2D seg = _segmentList.get(v.getRoadLineIdx()).get(segIdx);
	    _vehicleOnSeg.get(seg).add(v);
	}
    }

    /**
     * @param v_
     * @throws Exception
     */
    public void updateVehicle(Vehicle v_) {
	int segIdx = getSegIdx(v_.getRoadLine(), v_.getLocation());
	Line2D seg = _segmentList.get(v_.getRoadLineIdx()).get(segIdx);
	// Check if the car is not on this segment yet
	if (!_vehicleOnSeg.get(seg).contains(v_)) {
	    // Remove it from the previous seg
	    /*
	     * If the car is on the first segment of a line, add it to that
	     * segment. For all cases other than base case, we need to remove it
	     * from the last segment of the previous line.
	     */
	    if (segIdx == 0) {
		_vehicleOnSeg.get(seg).add(v_);
		if (v_.getRoadLineIdx() > 0) {
		    int prevLineIdx = v_.getRoadLineIdx() - 1;
		    int prevSegIdx = _segmentList.get(prevLineIdx).size() - 1;
		    Line2D prevSeg = _segmentList.get(prevLineIdx).get(prevSegIdx);
		    if (_vehicleOnSeg.get(prevSeg).contains(v_)) {
			_vehicleOnSeg.get(prevSeg).remove(v_);
		    }
		}

	    } else {
		Line2D prevSeg = _segmentList.get(v_.getRoadLineIdx()).get(segIdx - 1);
		if (_vehicleOnSeg.get(prevSeg).contains(v_)) {
		    _vehicleOnSeg.get(prevSeg).remove(v_);
		    _vehicleOnSeg.get(seg).add(v_);
		}
	    }
	}

    }

    /**
     * Returns the starting point of the current line.
     * 
     * @params
     * @return Double2D
     * @author biggie
     */
    public Double2D startLoc() {
	return new Double2D(_lineList.get(0).getP1());
    }

    /**
     * Returns the ending point of the current line.
     * 
     * @params
     * @return Point2D
     * @author biggie
     */
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
    @Override
    public String toString() {
	return ID + "_v" + _vehiclesOnRoad.size();
    }

    /**
     * Remove vehicle from this road
     * 
     * @param v_
     *            Vehicle instance
     */
    public void removeVFromRoad(Vehicle v_) {
	_vehiclesOnRoad.remove(v_);

	int segIdx;
	segIdx = getSegIdx(v_.getRoadLine(), v_.getLocation());

	Line2D seg = _segmentList.get(v_.getRoadLineIdx()).get(segIdx);
	if (_vehicleOnSeg.get(seg).contains(v_)) {
	    _vehicleOnSeg.get(seg).remove(v_);
	}

    }

    /**
     * Find the intersection point between this and another road
     * 
     * @param b_
     * @return
     */
    public Double2D findIntersection(Road b_) {
	Point2D intersectPoint = null;
	for (Line2D bLine : b_.getLineList()) {

	    for (Line2D thisLine : getLineList()) {
		if (thisLine.intersectsLine(bLine)) {
		    intersectPoint = findIntersection(thisLine.getP1(), thisLine.getP2(), bLine.getP1(), bLine.getP2());
		}
	    }
	}
	return new Double2D(intersectPoint.getX(), intersectPoint.getY());
    }

    /**
     * Return true if segment has reached maximum capacity TODO: make this a
     * function of vhcl size, not vhcl number.
     * 
     * @param roadSeg_
     * @return
     */
    public boolean isSegSaturated(Line2D roadSeg_) {
	return _vehicleOnSeg.get(roadSeg_).size() >= getMaxVhclSeg();
    }

    /**
     * Create a a list of line segments for disaq1play
     */
    public void processRoadSegments() {
	if (null != _subRoads && !_subRoads.isEmpty()) {
	    for (Road subR : _subRoads) {
		subR.processRoadSegments();
	    }
	} else {
	    _segmentList = new LinkedList<List<Line2D>>();
	    _vehicleOnSeg = new HashMap<Line2D, List<Vehicle>>();
	    for (Line2D currLine : _lineList) {
		// Divide the line into segments and store them in a map
		_segmentList.add(new LinkedList<Line2D>());
		findRoadSegment(currLine);
	    }

	    // Find how many segments we created
	    for (List<Line2D> lineList : _segmentList) {
		_numOfSegs += lineList.size();
	    }

	}
    }

    /**
     * Helper method that breaks a line into smaller subsegments.
     * 
     * @param line_
     * @param totalSegments_
     * @return
     */
    private void findRoadSegment(Line2D line_) {
	Point2D segOrig = null;
	Point2D segDest = null;

	double lineLength = line_.getP1().distance(line_.getP2());
	int totalSegments = (int) Math.ceil(lineLength / LAYER_SEG.getVal());

	for (int i = 0; i < totalSegments; i++) {
	    if (segOrig == null) { // If this is the first round...
		segOrig = line_.getP1();
	    } else {
		segOrig = segDest;
	    }
	    if ((i + 1) >= totalSegments) { // Last Round
		segDest = line_.getP2();
	    } else {
		segDest = getNewLocation(segOrig, line_, LAYER_SEG.getVal());
	    }
	    Line2D segment = new Line2D.Double(segOrig, segDest);
	    _segmentList.getLast().add(segment);
	    _vehicleOnSeg.put(segment, new LinkedList<Vehicle>());
	}
    }


    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return Line2D
     * @author biggie
     */
    private Line2D getLine(Double2D curLoc_){
	return getLine(new Point2D.Double(curLoc_.x, curLoc_.y));
    }
    /**
     * Return the line segment to which this point belongs to.
     * 
     * @param curLoc
     * @return
     */
    private Line2D getLine(Point2D curLoc_) {
	Line2D currLine = null;
	for (Line2D line : _lineList) {
	    if (line.ptLineDist(curLoc_) < DISTANCE_THRESHOLD) {
		currLine = line;
		break;
	    }
	}

	return currLine;
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
    }

    /**
     * @param l_
     * @param currLocation_
     * @return
     */
    public int getSegIdx(Line2D l_, Double2D currLocation_) {
	int seg;
	Point2D pt = new Point2D.Double(currLocation_.x, currLocation_.y);

	if (l_.ptLineDist(pt) <= DISTANCE_THRESHOLD) {
	    seg = (int) (l_.getP1().distance(pt) / LAYER_SEG.getVal());
	    // Hack in cases that the point is right on the border
	    if (0 == Double.compare(currLocation_.getX(), l_.getX2())
		    && 0 == Double.compare(currLocation_.getY(), l_.getY2())) {
		seg--;
	    }
	} else
	    throw new RuntimeException("Point not in line");

	return seg;
    }

    /**
     * Find the intersection between two lines. </p> taken from {@link http
     * ://workshop.evolutionzone.com/2007/09/10/code-2d-line-intersection/}
     * 
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public static Point2D findIntersection(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
	double xD1, yD1, xD2, yD2, xD3, yD3;
	double dot, deg, len1, len2;
	double segmentLen1, segmentLen2;
	double ua, div;

	// calculate differences
	xD1 = p2.getX() - p1.getX();
	xD2 = p4.getX() - p3.getX();
	yD1 = p2.getY() - p1.getY();
	yD2 = p4.getY() - p3.getY();
	xD3 = p1.getX() - p3.getX();
	yD3 = p1.getY() - p3.getY();

	// calculate the lengths of the two lines
	len1 = Math.sqrt(xD1 * xD1 + yD1 * yD1);
	len2 = Math.sqrt(xD2 * xD2 + yD2 * yD2);

	// calculate angle between the two lines.
	dot = (xD1 * xD2 + yD1 * yD2); // dot product
	deg = dot / (len1 * len2);

	// if abs(angle)==1 then the lines are parallell,
	// so no intersection is possible
	if (Math.abs(deg) == 1)
	    return null;

	// find intersection Pt between two lines
	Point2D pt = new Point2D.Double();
	div = yD2 * xD1 - xD2 * yD1;
	ua = (xD2 * yD3 - yD2 * xD3) / div;
	pt.setLocation(p1.getX() + ua * xD1, p1.getY() + ua * yD1);

	// calculate the combined length of the two segments
	// between Pt-p1 and Pt-p2
	xD1 = pt.getX() - p1.getX();
	xD2 = pt.getX() - p2.getX();
	yD1 = pt.getY() - p1.getY();
	yD2 = pt.getY() - p2.getY();
	segmentLen1 = Math.sqrt(xD1 * xD1 + yD1 * yD1) + Math.sqrt(xD2 * xD2 + yD2 * yD2);

	// calculate the combined length of the two segments
	// between Pt-p3 and Pt-p4
	xD1 = pt.getX() - p3.getX();
	xD2 = pt.getX() - p4.getX();
	yD1 = pt.getY() - p3.getY();
	yD2 = pt.getY() - p4.getY();
	segmentLen2 = Math.sqrt(xD1 * xD1 + yD1 * yD1) + Math.sqrt(xD2 * xD2 + yD2 * yD2);

	// if the lengths of both sets of segments are the same as
	// the lengths of the two lines the point is actually
	// on the line segment.

	// if the point isn't on the line, return null
	int compare1 = Double.compare(Math.abs(len1 - segmentLen1), DISTANCE_THRESHOLD);
	int compare2 = Double.compare(Math.abs(len2 - segmentLen2), DISTANCE_THRESHOLD);
	if (compare1 > 0 || compare2 > 0)
	    return null;

	// return the valid intersection
	return pt;
    }

    /**
     * Return the list of points that make up a Road, from {@code p1_} to
     * {@code p2_} This can be used to draw a subsection of a road accurately.
     * 
     * @param p1_
     *            Begining of section
     * @param p2_
     *            End of section
     * @return Point list.
     */
    public List<Point2D> getSubPointList(Double2D p1_, Double2D p2_) {
	return getSubPointList(new Point2D.Double(p1_.x, p1_.y), new Point2D.Double(p2_.x, p2_.y));
    }

    /**
     * Handles point arithmetic with Java.awt
     */
    private List<Point2D> getSubPointList(Point2D p1_, Point2D p2_) {
	Line2D l1 = getLine(p1_);
	Line2D l2 = getLine(p2_);

	List<Point2D> subList = new LinkedList<Point2D>();
	int dist1 = Double.compare(DISTANCE_THRESHOLD, l1.getP1().distance(l2.getP1()));
	int dist2 = Double.compare(DISTANCE_THRESHOLD, l1.getP2().distance(l2.getP2()));
	if (1 == dist1 && 1 == dist2) {
	    subList.add(p1_);
	    subList.add(p2_);
	} else {
	    subList.add(p1_);
	    for (Line2D cl : getLineList()) {
		if (cl.equals(l2)) {
		    // Line2D lastLine = new Line2D.Double(cl.getP1(), p2_);
		    subList.add(p2_);
		    break;
		}
		subList.add(cl.getP2());
	    }
	}
	return subList;
    }

    /**
     * @return The coordinates of the road origin.
     */
    public Double2D getP1() {
	Point2D p1Pt = _lineList.get(0).getP1();
	Double2D p1 = new Double2D(p1Pt.getX(), p1Pt.getY());
	return p1;
    }

    /**
     * @return The coordibates of the road end.
     */
    public Double2D getP2() {
	Point2D p2Pt = _lineList.get(_lineList.size() - 1).getP2();
	return new Double2D(p2Pt.getX(), p2Pt.getY());
    }

    /**
     * Get the ith line segment of the Road.
     * 
     * @param i
     * @return Line segment.
     */
    public Line2D getLine(int i) {
	// TODO Auto-generated method stub
	return _lineList.get(i);
    }

    /**
     * @return the numOfSegs
     */
    public int getNumOfSegs() {
	return _numOfSegs;
    }

    /**
     * @author biggie
     * @name setAvgSpeed Purpose TODO
     * 
     * @param
     * @return void
     */
    public void setAvgSpeed(Double currSpeed_) {
	_avgSpeed = currSpeed_;

    }

    /**
     * 
     * @author biggie
     * @name getAvgSpeed Purpose TODO
     * 
     * @param
     * @return Double
     */
    public Double getAvgSpeed() {
	return _avgSpeed;
    }

}
