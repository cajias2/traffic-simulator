package sim.app.geo.distance;


public class Meters extends Distance {

    
    public Meters(double length_)
    {
	_lenght = length_;
    }


    @Override
    public double toMeters() {
	return _lenght*MTRS;
    }
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
	return _lenght + "m";
    }

}
