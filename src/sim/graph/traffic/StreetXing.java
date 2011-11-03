package sim.graph.traffic;

/*
 * @author Raul Cajias
 */
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import sim.agents.traffic.TLAgent;
import sim.util.Double2D;
import sim.utils.Orientation;

public class StreetXing {

    private final String ID;
    private static Logger _log;
    private TLAgent _trafficLight;
    private static int _xingCount = 0;
    private double _startOdds = 0;
    private double _endOdds = 0;
    private Double2D _location;
    private final List<Road> _roads;
    private Collection<Road> _subRoads = new ArrayList<Road>();

    public Font nodeFont = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * Constructor for class StreetXingNode
     * 
     * @author biggie
     * @param a_
     * @param b_
     */
    public StreetXing(Road a_, Road b_) {
	_roads = new ArrayList<Road>();
	_roads.add(a_);
	if (null != b_) {
	    ID = a_.ID + "X" + b_.ID;
	    _roads.add(b_);
	    _location = a_.findIntersection(b_);
	} else {
	    ID = a_.ID;
	}
	_xingCount++;
    }

    /**
     * Constructor for road star/end xings
     * 
     * @param p_
     * @param road_
     */
    public StreetXing(Double2D p_, Road road_) {
	this(road_, null);
	_location = p_;

    }

    /**
     * True if xing has a traffic light. False otherwise.
     * 
     * @return boolean
     */
    public boolean hasTrafficLight() {
	for (Road rd : _subRoads)
	{
	    if (rd.getTf() != null)
	    {
		return true;
	    }
	}
	return false;
    }

    public String getId() {
	return ID;
    }

    /**
     * @return the _startOdds
     */
    public double getStartOdds() {
	return _startOdds;
    }

    /**
     * @return the _endOdds
     */
    public double getEndOdds() {
	return _endOdds;
    }

    /**
     * @return the _location
     */
    public Double2D getLocation() {
	return _location;
    }

    public List<Road> getRoads()
    {
        return _roads;
    }

    /**
     * @param _subRoad_
     */
    public void setSubRoads(Collection<Road> subRoads_) {
	_subRoads = subRoads_;
    }

    /**
     * @param startOdds_
     *            the _startOdds to set
     */
    public void setStartOdds(double startOdds_) {
	_startOdds = startOdds_;
    }

    /**
     * @param endOdds_
     *            the _endOdds to set
     */
    public void setEndOdds(double endOdds_) {
	_endOdds = endOdds_;
    }

    /**
     * @author biggie
     */
    @Override
    public String toString() {

	StringBuffer echo = new StringBuffer(ID);
	if (hasTrafficLight()) {
	    for (Road rd : _subRoads) {
		if (rd.getTf() != null && rd.getOr() == Orientation.NS) {
		    echo.append("(" + rd.getOr() + ":" + rd.getTf() + ")");
		    break;
		}
	    }
	}
	return echo.toString();
    }

}
