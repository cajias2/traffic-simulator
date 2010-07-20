/*
 * @(#)SectionEndComparator.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.utils.xml.data;

import java.util.Comparator;

/**
 * @author biggie
 *
 */
public class SectionEndComparator implements Comparator<OutputSection> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(OutputSection o1_, OutputSection o2_) {
	int retVal = 0;
	if (o1_.getEnd() > o2_.getEnd()) {
	    retVal = 1;
	}
	if (o1_.getEnd() < o2_.getEnd()) {
	    retVal = -1;
	}

	return retVal;
    }

}
