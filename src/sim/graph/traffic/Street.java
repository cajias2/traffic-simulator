/**
 * 
 */
package sim.graph.traffic;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;

public class Street extends Road {
    private static int _streetCount = 0;
    private static final int TYPE = 1;
    private static final Distance MAX_VELOCITY = new Kilometers(45.0);
    private static final int MAX_VHCL_PER_SEG = 5;

    /**
     * Class constructor, Processing.
     * 
     * @author biggie
     */
    public Street(String id_, List<Point2D> pointList_, PApplet parent_, Logger log_) {

	super(id_, pointList_, parent_);
	_streetCount++;
    }

    /**
     * Class constructor, Mason
     * 
     * @author biggie
     */
    public Street(String id_, List<Point2D> pointList_, Logger log_)
    {
	this(id_, pointList_, null, log_);
    }

    @Override
    public Distance getMaxVelocity() {
	return MAX_VELOCITY;
    }


    @Override
    protected int getStrokeWeight() {
	return TYPE;
    }

    @Override
    public int getMaxVhclSeg() {
	return MAX_VHCL_PER_SEG;
    }

}
