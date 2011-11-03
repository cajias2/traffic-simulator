/**
 * 
 */
package sim.geo.distance;

/**
 * @author biggie
 * 
 */
public class Kilometers extends Distance {

    public Kilometers(Double x_) {
	_lenght = x_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.app.geo.distance.Distance#toMeters()
     */
    @Override
    public double toMeters() {
	return _lenght * KILO_M;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.app.geo.distance.Distance#val()
     */
    @Override
    public double getVal() {
	return _lenght;
    }

    @Override
    public void setVal(double x_) {
	_lenght = x_;
    }

    @Override
    public String toString() {
	return _lenght + "K";
    }

}
