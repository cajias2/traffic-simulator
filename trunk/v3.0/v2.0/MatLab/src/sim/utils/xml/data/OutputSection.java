/*
 * @(#)OutputSection.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.utils.xml.data;

/**
 * @author biggie
 *
 */
public class OutputSection {

    private final Double _startStep;
    private final Double _endStep;
    private final Double _speed;
    private final String _name;
    /**
     * @author biggie
     * OutputSection
     */
    public OutputSection(String name_, Double startStep_, Double endStep_, Double speed_) {
	_name = name_;
	_startStep = startStep_;
	_endStep = endStep_;
	_speed = speed_;
    }
    /**
     * @return the startStep
     */
    public Double getStart() {
	return _startStep;
    }
    /**
     * @return the endStep
     */
    public Double getEnd() {
	return _endStep;
    }
    /**
     * @return the speed
     */
    public Double getSpeed() {
	return _speed;
    }
    /**
     * @return the name
     */
    public String getName() {
	return _name;
    }

}
