/**
 * 
 */
package sim.app.geo;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import processing.core.PApplet;
import sim.app.agents.display.lights.TrafficLight;
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import sim.app.geo.distance.Meters;
import sim.app.processing.Displayable;
import sim.app.utils.TrafficLightState;

/**
 * @author biggie
 * 
 */
public abstract class Road implements Displayable {
    public static final Distance LAYER_SEG = new Meters(10);

    private static PApplet _parent;

    private List<Line2D> _lineList;

    private double _realLength = 0;

    private LinkedList<List<Line2D>> _segmentList;

    private HashMap<Line2D, List<Vehicle>> _vehicleOnSeg;
    
    private List<Road> _subRoads;

    private List<Vehicle> _vehiclesOnRoad;

    private Distance _geoLength;

    private TrafficLight _tf;

    /*
     * If something is this far way from something else, they are considered to
     * be in the same place.
     */
    public static final double DISTANCE_THRESHOLD = 0.001;

    public final String ID;
    private int _roadCount = 0;

    protected abstract int getStrokeWeight();

    abstract protected int getMaxVhclSeg();

    /**
     * Constructor
     */
    public Road(String id_, List<Point2D> pointList_, PApplet parent_) {
	if (_parent == null) {
	    _parent = parent_;
	}
	ID = id_+"+"+_roadCount;
	_roadCount++;
	_vehiclesOnRoad = new LinkedList<Vehicle>();
	createRoad(pointList_);
//	processRoadSegments();
    }
    /**
     * 
     * @param intersecList_
     */
    public void setSubRoad(List<Road> rdList_)
    {
	_subRoads = rdList_;
    }
    
    public Road getSubRoad(int idx_)
    {
	return _subRoads.get(idx_);
    }
    public List<Road> getSubRoadList(){
	return _subRoads;
    }
    /**
     * Get a list of the lines that make up this road
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
    public Point2D getNewLocation(Point2D curLoc_, double len_) {
	Line2D line = getLine(curLoc_);
	return getNewLocation(curLoc_, line, len_);
    }

    /**
     * Given a line, find a secant line that intersects it at length len_
     * 
     * @param curLoc_
     * @param len_
     * @param dir_
     * @return
     */
    public Point2D getNewLocation(Point2D curLoc_, Line2D l_, double len_) {
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

	Point2D secP1 = new Point2D.Double(curLoc_.getX() + dir[0] * runLen, curLoc_.getY());
	Point2D secP2 = new Point2D.Double(secP1.getX(), secP1.getY() + 10*dir[1]);
	Line2D secant = new Line2D.Double(secP1, secP2);
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
	return _tf.getState();
    }

    /**
     * @param tf
     *            the _tf to set
     */
    public void setTf(TrafficLight tf) {
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
	if(null == _subRoads || _subRoads.isEmpty()){
	for (Line2D currSeg : _lineList) {
	    _parent.stroke(0);
	    _parent.strokeWeight(getStrokeWeight());
	    _parent.line((float) currSeg.getP1().getX(), (float) currSeg.getP1().getY(),
		    (float) currSeg.getP2().getX(), (float) currSeg.getP2().getY());
	}
	}else{
	   for(Road subR: _subRoads)
	   {
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
	    int segIdx = (int) Math.ceil((currLine.getP1().distance(v.getLocation()) / LAYER_SEG.getVal()));
	    Line2D seg = _segmentList.get(v.getRoadLineIdx()).get(segIdx);
	    _vehicleOnSeg.get(seg).add(v);
	}
    }

    /**
     * 
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
    public Point2D findIntersection(Road b_) {
	Point2D intersectPoint = null;
	for (Line2D bLine : b_.getLineList()) {
	    
	    for (Line2D thisLine : this.getLineList()) {
		if (thisLine.intersectsLine(bLine)) {
		    intersectPoint = findIntersection(thisLine.getP1(), thisLine.getP2(), bLine.getP1(), bLine.getP2());
		}
	    }
	}
	return intersectPoint;
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
	if(null != _subRoads && !_subRoads.isEmpty())
	{
	    for(Road subR : _subRoads)
	    {
		subR.processRoadSegments();
	    }
	}else{
	_segmentList = new LinkedList<List<Line2D>>();
	_vehicleOnSeg = new HashMap<Line2D, List<Vehicle>>();
	for (Line2D currLine : _lineList) {
	    // Divide the line into segments and store them in a map
	    _segmentList.add(new LinkedList<Line2D>());
	    findRoadSegment(currLine);
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

    private Line2D getLine(Point2D curLoc) {
	Line2D currLine = null;

	for (Line2D line : _lineList) {
	    if (line.ptLineDist(curLoc) < DISTANCE_THRESHOLD) {
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
     * 
     * @param l_
     * @param pt_
     * @return
     */
    public int getSegIdx(Line2D l_, Point2D pt_) {
	int seg;

	if (l_.ptLineDist(pt_) <= DISTANCE_THRESHOLD) {
	    seg = (int) (l_.getP1().distance(pt_) / LAYER_SEG.getVal());
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
	if (compare1 > 0 ||compare2 > 0)
	    return null;

	// return the valid intersection
	return pt;
    }
    
    public List<Point2D> getSubPointList(Point2D p1_, Point2D p2_)
    {
	Line2D l1 = getLine(p1_);
	Line2D l2 = getLine(p2_);
	
	List<Point2D> subList = new LinkedList<Point2D>();
	int dist1 = Double.compare(DISTANCE_THRESHOLD, l1.getP1().distance(l2.getP1()));
	int dist2 = Double.compare(DISTANCE_THRESHOLD, l1.getP2().distance(l2.getP2()));	
	if(1==dist1 && 1 == dist2)
	{
	    subList.add(p1_);
	    subList.add(p2_);
	}else{
	    subList.add(p1_);
	    for(Line2D cl: getLineList())
	    {
		if(cl.equals(l2))
		{
//		    Line2D lastLine = new Line2D.Double(cl.getP1(), p2_);
		    subList.add(p2_);
		    break;
		}
		subList.add(cl.getP2());
	    }	    
	}
	return subList;	
    }
    
    public Point2D getP1()
    {
	return _lineList.get(0).getP1();
    }
    public Point2D getP2()
    {
	return _lineList.get(_lineList.size()-1).getP2();
    }

    public Line2D getLine(int i) {
	// TODO Auto-generated method stub
	return _lineList.get(i);
    }

}
