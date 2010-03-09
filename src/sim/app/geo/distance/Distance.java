/**
 * 
 */
package sim.app.geo.distance;

/**
 * @author biggie
 * 
 */
public abstract class Distance {

    protected static double MTRS = 1.0;
    protected static double KILO_M = 1000.0;

    protected double _lenght;

    public boolean equals(Distance x_) {

	boolean eq = false;
	if (x_ instanceof Meters) {
	    eq = this.toMeters() == x_.getVal();
	} else {
	    eq = this.toMeters() == x_.toMeters();
	}
	return eq;

    }

    public abstract double getVal();
    public abstract void setVal(double x_);
    public abstract double toMeters();
    public abstract String toString();

}
