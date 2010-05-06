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
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import sim.app.geo.distance.Meters;
import sim.app.processing.Displayable;

/**
 * @author biggie
 * 
 */
public abstract class Road implements Displayable {
	private static final Distance LAYER_SEG = new Meters(10);

	private static PApplet _parent;

	private List<Line2D> _lineList;

	private double _realLength = 0;

	private LinkedList<List<Line2D>> _segmentList;

	private HashMap<Line2D, List<Vehicle>> _vehicleOnSeg;

	private List<Vehicle> _vehiclesOnRoad;

	private Distance _geoLength;

	/*
	 * If something is this far way from something else, they are considered to
	 * be in the same place.
	 */
	public static final double DISTANCE_THRESHOLD = 0.001;

	public final String ID;

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
		processRoadSegments();
	}

	public List<Line2D> getLineList() {
		return _lineList;
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
	public Point2D getNewLocation(Point2D curLoc_, Line2D line_, double len_) {
		// Select direction to move in.
		int[] direction = { 1, 1 };
		if (line_.getX1() > line_.getX2())
			direction[0] = -direction[0];
		if (line_.getY1() > line_.getY2())
			direction[1] = -direction[0];

		Point2D newLoc_ = null;

		double slope = (line_.getY2() - line_.getY1())
				/ (line_.getX2() - line_.getX1());
		double slopeDeg = Math.atan(slope);
		double runLen = Math.abs(Math.cos(slopeDeg) * len_);

		Point2D secantOrig = new Point2D.Double(curLoc_.getX() + direction[0]
				* runLen, curLoc_.getY());
		Point2D secantDest = new Point2D.Double(secantOrig.getX(), secantOrig
				.getY() + 1);
		Line2D secant = new Line2D.Double(secantOrig, secantDest);
		newLoc_ = intersectionPoint(line_, secant);

		// Am I over the boundary of the line?
		if (curLoc_.distance(line_.getP2()) < curLoc_.distance(newLoc_)) {
			int nextLineIdx = _lineList.indexOf(line_) + 1;
			if (nextLineIdx < _lineList.size()) {
				Line2D nextLine = _lineList.get(_lineList.indexOf(line_) + 1);
				newLoc_ = getNewLocation(nextLine.getP1(), nextLine, len_
						- (curLoc_.distance(nextLine.getP1())));
			} else {
				newLoc_ = curLoc_;
			}
		}

		if (newLoc_ == null)
			throw new RuntimeException("Intersection should not be null");
		return newLoc_;
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
		for (Entry<Line2D, List<Vehicle>> e : _vehicleOnSeg.entrySet()) {

			Line2D seg = e.getKey();
			List<Vehicle> vList = e.getValue();
			float color = 1;

			if (color >= 10)
				color = 5;
			_parent.stroke(255);
			_parent.strokeWeight(vList.size());
			_parent.line((float) seg.getX1(), (float) seg.getY1(), (float) seg
					.getX2(), (float) seg.getY2());
			color += 0.1;
		}
	}

	/**
	 * 
	 */
	public void update() {
		for (Vehicle v : _vehiclesOnRoad) {
			Line2D currLine = v.getLine();
			int segIdx = (int) Math.ceil((currLine.getP1().distance(
					v.getLocation()) / LAYER_SEG.getVal()));
			Line2D seg = _segmentList.get(v.getLineIdx()).get(segIdx);
			_vehicleOnSeg.get(seg).add(v);
		}
	}

	/**
	 * 
	 * @param v_
	 * @throws Exception
	 */
	public void updateVehicle(Vehicle v_) {
		int segIdx = getSegIdx(v_.getLine(), v_.getLocation());
		Line2D seg = _segmentList.get(v_.getLineIdx()).get(segIdx);
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
				if (v_.getLineIdx() > 0) {
					int prevLineIdx = v_.getLineIdx() - 1;
					int prevSegIdx = _segmentList.get(prevLineIdx).size() - 1;
					Line2D prevSeg = _segmentList.get(prevLineIdx).get(
							prevSegIdx);
					if (_vehicleOnSeg.get(prevSeg).contains(v_)) {
						_vehicleOnSeg.get(prevSeg).remove(v_);
					}
				}

			} else {
				Line2D prevSeg = _segmentList.get(v_.getLineIdx()).get(
						segIdx - 1);
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
		segIdx = getSegIdx(v_.getLine(), v_.getLocation());

		Line2D seg = _segmentList.get(v_.getLineIdx()).get(segIdx);
		if (_vehicleOnSeg.get(seg).contains(v_)) {
			_vehicleOnSeg.get(seg).remove(v_);
		}

	}

	/**
	 * Create a a list of line segments for disaq1play
	 */
	private void processRoadSegments() {
		_segmentList = new LinkedList<List<Line2D>>();
		_vehicleOnSeg = new HashMap<Line2D, List<Vehicle>>();

		for (Line2D currLine : _lineList) {
			// Divide the line into segments and store them in a map
			_segmentList.add(new LinkedList<Line2D>());
			findRoadSegment(currLine);
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

	private int getLineFromPoint(Point2D curLoc) {
		int idx = 0;

		for (Line2D line : _lineList) {
			if (line.ptLineDist(curLoc) < DISTANCE_THRESHOLD) {
				break;
			}
			idx++;
		}
		return idx;
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
	 * Returns the point where two lines intersects.
	 * 
	 * @param line1_
	 * @param line2_
	 * @return	Point2D
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

	private int getSegIdx(Line2D l_, Point2D pt_) {
		int seg;

		if (l_.ptLineDist(pt_) <= DISTANCE_THRESHOLD) {
			seg = (int) (l_.getP1().distance(pt_) / LAYER_SEG.getVal());
		} else
			throw new RuntimeException("Point not in line");

		return seg;
	}

	/**
	 * Find the intersection point between this and another road
	 * @param b_
	 * @return
	 */
	public Point2D findIntersection(Road b_) {
		Point2D intersectPoint = null;
		for(Line2D bLine: b_.getLineList())
		{
			for(Line2D thisLine: this.getLineList())
			{
				intersectPoint = intersectionPoint(thisLine, bLine);
				if(null != intersectPoint)
					break;
			}
		}
		return intersectPoint;
	}

}
