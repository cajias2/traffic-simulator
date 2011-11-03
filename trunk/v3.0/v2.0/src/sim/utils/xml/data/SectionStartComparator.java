/*
 * @(#)SectionStartComparator.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.utils.xml.data;

import java.util.Comparator;


/**
 * @author biggie
 *
 */
public class SectionStartComparator implements Comparator<OutputSection> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(OutputSection arg0_, OutputSection arg1_) {
	int retVal = 0;
	if (arg0_.getStart() > arg1_.getStart()) {
	    retVal = 1;
	}
	if (arg0_.getStart() < arg1_.getStart()) {
	    retVal = -1;
	}

	return retVal;
    }

}
