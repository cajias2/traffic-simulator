/*
 * @(#)SmartTFAgent.java    %I%    %G%
 * @author biggie
 * 
 */

package traffic.agents.lights;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import sim.agents.traffic.TLAgent;
import sim.engine.SimState;
import sim.graph.traffic.Road;
import sim.utils.Orientation;

/**
 * @author biggie
 * 
 */
public class SmartTFAgent extends TLAgent {

    private final Map<Orientation, Double> _flowMap = new HashMap<Orientation, Double>();

    /**
     * @author biggie SmartTFAgent
     */
    public SmartTFAgent(int duration_, double split_, Logger log_) {
	super(null, duration_, split_, log_);
	_flowMap.put(Orientation.NS, 0.0);
	_flowMap.put(Orientation.EW, 0.0);
    }

    @Override
    public void step(SimState state_) {
	double flowNS = _flowMap.get(Orientation.NS);
	double flowEW = _flowMap.get(Orientation.EW);

	if (!(flowEW == 0 && flowNS == 0)) {
	    int newSplit = 0;

	    if (flowEW == 0)
		newSplit = 0;
	    else if (flowNS == 0)
		newSplit = 100;
	    else {
		newSplit = (int) (((flowEW * 100.0) / (flowNS + flowEW)));
		// System.out.println(newSplit);
	    }
	    setSplit(newSplit);
	}

	super.step(state_);
	for (Road rd : getRoads()) {
	    double maxCars = rd.getNumOfSegs() * rd.getMaxVhclSeg();
	    _flowMap.put(rd.getOr(), (rd.getVehiclesOnRoad().size() / maxCars) * 100.0);
	}

    }

}
