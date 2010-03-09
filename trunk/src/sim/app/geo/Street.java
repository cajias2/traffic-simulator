/**
 * 
 */
package sim.app.geography;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.geography.distance.Distance;
import sim.app.geography.distance.Kilometers;

public class Street extends Road {
    private static int _streetCount = 0;
    private static final int TYPE = 1;
    private static final Distance MAX_VELOCITY = new Kilometers(45.0);


    /**
     * Class constructor
     * 
     * @author biggie
     */
    public Street(String id_, List<Point2D> pointList_, PApplet parent_,
	    Logger log_) {

	super(id_, pointList_, parent_);
	_streetCount++;
    }

    /**
     * Road type.
     * 
     * @return
     */
    public int getType() {
	return TYPE;
    }

    public Distance getMaxVelocity() {
	return MAX_VELOCITY;
    }

}
