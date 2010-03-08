/**
 * 
 */
package sim.app.graph;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Logger;

import processing.core.PApplet;

public class Street extends Road {
    private static int _streetCount = 0;
    private static final int TYPE = 1;
    private static final int MAX_VELOCITY = 45;

    private static Logger _logger;

    /**
     * Class constructor
     * 
     * @author biggie
     */
    public Street(String id_, List<Point2D> pointList_, PApplet parent_,
	    Logger log_) {

	super(id_, pointList_, parent_);
	_streetCount++;
	_logger = log_;
    }

    /**
     * Road type.
     * 
     * @return
     */
    public int getType() {
	return TYPE;
    }

    public int getMaxVelocity() {
	// TODO Auto-generated method stub
	return MAX_VELOCITY;
    }

}
