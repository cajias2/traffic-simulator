/*
 * @(#)SmartTFAgent.java    %I%    %G%
 * @author biggie
 * 
 */

package simulations.agents.lights;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import sim.agents.lights.TrafficLightAgent;
import sim.engine.SimState;
import sim.geo.Road;
import sim.utils.Orientation;

/**
 * @author biggie
 * 
 */
public class SmartTFAgent extends TrafficLightAgent {

    private final Map<Orientation, Double> _flowMap = new HashMap<Orientation, Double>();

    /**
     * @author biggie SmartTFAgent
     */
    public SmartTFAgent(int duration_, double split_, Logger log_) {
	super(duration_, split_, log_);
	_flowMap.put(Orientation.NS, 0.0);
	_flowMap.put(Orientation.EW, 0.0);
    }

    @Override
    public void step(SimState state_) {
	double flowNS = _flowMap.get(Orientation.NS);
	double flowEW = _flowMap.get(Orientation.EW);
	int newSplit = 0;
	if (!(flowEW == 0 && flowNS == 0)) {

	    if (flowEW == 0)
		newSplit = 0;
	    else if (flowNS == 0)
		newSplit = 100;
	    else {
		newSplit = (int) (((flowNS * 100.0) / (flowNS + flowEW)));
	    }
	}
	setSplit(newSplit);

	super.step(state_);
	for (Road rd : getRoads()) {
	    double maxCars = rd.getNumOfSegs() * rd.getMaxVhclSeg();
	    _flowMap.put(rd.getOr(), (rd.getVehiclesOnRoad().size() / maxCars) * 100.0);
	}

    }

}
