/**
 * 
 */
package social.agents.utils;

import static java.lang.Math.sqrt;
import sim.util.Double2D;
import ec.util.MersenneTwisterFast;

/**
 * @author biggie
 *
 */
public class Movements {

    /**
     * 
     * @param t
     * @param n
     * @param currLoc_
     * @return
     */
    public static Double2D brownianMotion(double t, double n, Double2D currLoc_, MersenneTwisterFast rand_) {
	double dt = t / n;
	double deltaX = sqrt(dt) * rand_.nextGaussian();
	double deltaY = sqrt(dt) * rand_.nextGaussian();
	return new Double2D(currLoc_.x + deltaX, currLoc_.y + deltaY);
    }

}
