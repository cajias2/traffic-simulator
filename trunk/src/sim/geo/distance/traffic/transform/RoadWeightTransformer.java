/*
 * @(#)RoadWeightTransformer.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.geo.distance.traffic.transform;

import org.apache.commons.collections15.Transformer;

import sim.graph.traffic.Road;

/**
 * @author biggie
 *
 */
public class RoadWeightTransformer implements Transformer<Road, Number> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
     */
    public Double transform(Road rd_) {
	return rd_.getRoadLength() / rd_.getAvgSpeed();
    }

}
